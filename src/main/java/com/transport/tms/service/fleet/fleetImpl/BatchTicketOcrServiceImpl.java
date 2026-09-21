package com.transport.tms.service.fleet.fleetImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.tms.dto.fleet.request.BatchTicketSaveItem;
import com.transport.tms.dto.fleet.request.BatchTicketSaveRequest;
import com.transport.tms.dto.fleet.request.PeageRequest;
import com.transport.tms.dto.fleet.request.PleinCarburantRequest;
import com.transport.tms.dto.fleet.response.BatchTicketItemResult;
import com.transport.tms.dto.fleet.response.BatchTicketSaveResult;
import com.transport.tms.dto.fleet.response.PeageResponse;
import com.transport.tms.dto.fleet.response.PleinCarburantResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.service.fleet.BatchTicketOcrService;
import com.transport.tms.service.fleet.PeageService;
import com.transport.tms.service.fleet.PleinCarburantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchTicketOcrServiceImpl implements BatchTicketOcrService {

    @Value("${anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${tms.anthropic.model:claude-haiku-4-5-20251001}")
    private String anthropicModel;

    private final PeageService peageService;
    private final PleinCarburantService pleinCarburantService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<BatchTicketItemResult> analyzeBatch(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new InvalidOperationException("Aucun fichier fourni pour le traitement en masse.");
        }
        if (files.size() > 20) {
            throw new InvalidOperationException("Vous ne pouvez pas traiter plus de 20 tickets simultanément.");
        }
        if (anthropicApiKey == null || anthropicApiKey.trim().isEmpty()) {
            throw new InvalidOperationException("La clé API Anthropic n'est pas configurée.");
        }

        List<BatchTicketItemResult> results = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                BatchTicketItemResult item = analyzeSingleTicket(file, i + 1);
                results.add(item);
            } catch (Exception e) {
                log.error("Erreur lors de l'analyse du ticket batch index {}", i + 1, e);
                results.add(BatchTicketItemResult.builder()
                        .ticketIndex(i + 1)
                        .fileName(file.getOriginalFilename())
                        .ticketType("UNKNOWN")
                        .typeConfidence("UNKNOWN")
                        .processingStatus("ERROR")
                        .errorMessage("Échec de l'analyse IA: " + e.getMessage())
                        .dateConfidence("NONE")
                        .build());
            }
        }

        return results;
    }

    private BatchTicketItemResult analyzeSingleTicket(MultipartFile file, int index) throws Exception {
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String mimeType = file.getContentType();
        boolean isPdf = "application/pdf".equalsIgnoreCase(mimeType);
        if (mimeType == null || (!mimeType.startsWith("image/") && !isPdf)) {
            mimeType = "image/jpeg";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", anthropicApiKey);
        headers.set("anthropic-version", "2023-06-01");
        if (isPdf) {
            headers.set("anthropic-beta", "pdfs-2024-09-25");
        }

        Map<String, Object> source = new HashMap<>();
        source.put("type", "base64");
        source.put("media_type", isPdf ? "application/pdf" : mimeType);
        source.put("data", base64Image);

        Map<String, Object> fileContent = new HashMap<>();
        fileContent.put("type", isPdf ? "document" : "image");
        fileContent.put("source", source);

        String prompt = "Analyse ce document (ticket de péage, de carburant ou autre) et renvoie UNIQUEMENT un objet JSON valide, sans markdown, avec exactement ces clés :\n" +
                "- 'documentType' : 'PEAGE' si c'est un ticket de péage/autoroute (mentions: gare, péage, autoroute, ASF, VINCI, SANEF, APRR), 'CARBURANT' si c'est un ticket de carburant/essence (mentions: litres, liters, diesel, essence, station, gazole), 'UNKNOWN' sinon.\n" +
                "- 'typeConfidence' : 'HIGH' si tu es certain du type (indices clairs), 'LOW' si tu as un doute.\n" +
                "- 'operationDate' : la date de la transaction/opération au format YYYY-MM-DD. C'est la date à laquelle le paiement ou le passage a eu lieu. Ignore les dates d'impression de reçu, de validité de carte ou autres dates secondaires. Si plusieurs dates sont présentes, détermine quelle date correspond réellement à la date de l'opération. Si aucune date trouvée, mets null.\n" +
                "- 'operationTime' : l'heure de la transaction au format HH:mm. Si absente, mets '00:00'.\n" +
                "- 'dateConfidence' : 'HIGH' si une seule date de transaction évidente, 'LOW' si plusieurs dates ambiguës ou incertitude sur laquelle est la bonne, 'NONE' si aucune date détectée.\n" +
                "- 'dateWarning' : message explicatif en français si dateConfidence est LOW ou NONE (ex: 'Plusieurs dates détectées: 15/09 et 20/09. Date de transaction retenue: 20/09'), null sinon.\n" +
                "- 'amountTTC' : montant TTC total payé (nombre), null si non trouvé.\n" +
                "- 'amountHT' : montant HT (nombre), null si non trouvé.\n" +
                "- 'tvaRate' : taux TVA en % (nombre, ex: 20.0), null si non trouvé.\n" +
                "- 'tvaAmount' : montant TVA (nombre), null si non trouvé.\n" +
                "- 'gareEntree' : gare d'entrée (chaîne), null si non trouvé (uniquement pour PEAGE).\n" +
                "- 'gareSortie' : gare de sortie (chaîne), null si non trouvé (uniquement pour PEAGE).\n" +
                "- 'receiptNumber' : numéro de reçu/ticket/transaction (chaîne), null si non trouvé.\n" +
                "- 'operatorName' : société opérateur (ex: ASF, VINCI, Total, Shell), null si non trouvé.\n" +
                "- 'quantityLiters' : quantité de carburant en litres (nombre), null si non trouvé (uniquement pour CARBURANT).\n" +
                "- 'pricePerLiter' : prix par litre (nombre), null si non trouvé (uniquement pour CARBURANT).\n" +
                "- 'fuelType' : type de carburant ('DIESEL', 'ESSENCE', 'GPL', 'ELECTRIQUE'), null si non trouvé (uniquement pour CARBURANT).";

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", List.of(fileContent, textContent));

        Map<String, Object> body = new HashMap<>();
        body.put("model", anthropicModel);
        body.put("max_tokens", 1024);
        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages",
                requestEntity,
                String.class
        );

        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String assistantReply = rootNode.path("content").get(0).path("text").asText();
        assistantReply = assistantReply.replaceAll("(?s)^```json\\s*", "").replaceAll("(?s)\\s*```$", "").trim();

        JsonNode json = objectMapper.readTree(assistantReply);

        String docType = json.path("documentType").asText("UNKNOWN").toUpperCase();
        String typeConfidence = json.path("typeConfidence").asText("LOW").toUpperCase();
        String dateConfidence = json.path("dateConfidence").asText("NONE").toUpperCase();
        String dateWarning = json.path("dateWarning").asText(null);

        // Date extraction & validation logic
        String rawDateStr = json.path("operationDate").asText(null);
        String rawTimeStr = json.path("operationTime").asText("00:00");
        String isoDateTimeStr = null;

        if (rawDateStr != null && !rawDateStr.isEmpty() && !"null".equalsIgnoreCase(rawDateStr)) {
            try {
                LocalDate date = LocalDate.parse(rawDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalTime time = LocalTime.MIDNIGHT;
                try {
                    time = LocalTime.parse(rawTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                } catch (Exception te) {
                    try {
                        time = LocalTime.parse(rawTimeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
                    } catch (Exception ignored) {}
                }
                LocalDateTime ldt = LocalDateTime.of(date, time);

                // Date consistency checks
                if (date.isAfter(LocalDate.now())) {
                    dateConfidence = "LOW";
                    dateWarning = (dateWarning != null ? dateWarning + " | " : "") + "Date dans le futur détectée (" + rawDateStr + ").";
                } else if (date.isBefore(LocalDate.of(2010, 1, 1))) {
                    dateConfidence = "LOW";
                    dateWarning = (dateWarning != null ? dateWarning + " | " : "") + "Date trop ancienne détectée (" + rawDateStr + ").";
                }

                isoDateTimeStr = ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                dateConfidence = "NONE";
                dateWarning = "Impossible de parser la date d'opération: " + rawDateStr;
            }
        } else {
            dateConfidence = "NONE";
            dateWarning = "Aucune date d'opération détectée sur le ticket.";
        }

        BigDecimal amountTTC = getBigDecimalNode(json, "amountTTC");
        BigDecimal amountHT = getBigDecimalNode(json, "amountHT");
        BigDecimal tvaAmount = getBigDecimalNode(json, "tvaAmount");
        BigDecimal tvaRate = getBigDecimalNode(json, "tvaRate");

        // Compute status
        String processingStatus = "OK";
        if ("UNKNOWN".equals(docType)) {
            processingStatus = "ERROR";
        } else if ("LOW".equals(typeConfidence) || "LOW".equals(dateConfidence) || "NONE".equals(dateConfidence) || amountTTC == null) {
            processingStatus = "PARTIAL";
        }

        return BatchTicketItemResult.builder()
                .ticketIndex(index)
                .fileName(file.getOriginalFilename())
                .ticketType(docType)
                .typeConfidence(typeConfidence)
                .processingStatus(processingStatus)
                .operationDate(isoDateTimeStr)
                .dateConfidence(dateConfidence)
                .dateWarning(dateWarning)
                .amountTTC(amountTTC)
                .amountHT(amountHT)
                .tvaAmount(tvaAmount)
                .tvaRate(tvaRate)
                .gareEntree(json.path("gareEntree").asText(null))
                .gareSortie(json.path("gareSortie").asText(null))
                .receiptNumber(json.path("receiptNumber").asText(null))
                .societeAutoroute(json.path("operatorName").asText(null))
                .quantityLiters(getBigDecimalNode(json, "quantityLiters"))
                .totalCost(amountTTC != null ? amountTTC : getBigDecimalNode(json, "totalCost"))
                .pricePerLiter(getBigDecimalNode(json, "pricePerLiter"))
                .fuelType(json.path("fuelType").asText(null))
                .build();
    }

    private BigDecimal getBigDecimalNode(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        if (field.isMissingNode() || field.isNull() || "null".equalsIgnoreCase(field.asText())) {
            return null;
        }
        try {
            return new BigDecimal(field.asText().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public BatchTicketSaveResult saveBatch(BatchTicketSaveRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOperationException("Aucun élément à enregistrer.");
        }

        List<BatchTicketSaveResult.BatchTicketSaveItemResult> results = new ArrayList<>();
        int savedCount = 0;
        int failedCount = 0;

        for (BatchTicketSaveItem item : request.getItems()) {
            try {
                if (item.getVehiculeId() == null) {
                    throw new InvalidOperationException("Le véhicule est obligatoire pour le ticket #" + item.getTicketIndex());
                }

                LocalDateTime opDate = LocalDateTime.now();
                if (item.getOperationDate() != null && !item.getOperationDate().isEmpty()) {
                    try {
                        opDate = LocalDateTime.parse(item.getOperationDate());
                    } catch (Exception e) {
                        try {
                            opDate = LocalDate.parse(item.getOperationDate()).atStartOfDay();
                        } catch (Exception ignored) {}
                    }
                }

                String receiptNum = (item.getReceiptNumber() != null && !item.getReceiptNumber().isBlank() && !"null".equalsIgnoreCase(item.getReceiptNumber()))
                        ? item.getReceiptNumber().trim() : null;

                if ("PEAGE".equalsIgnoreCase(item.getTicketType())) {
                    BigDecimal ttc = item.getAmountTTC() != null && item.getAmountTTC().compareTo(BigDecimal.ZERO) > 0 
                            ? item.getAmountTTC() : BigDecimal.valueOf(0.01);

                    PeageRequest pr = new PeageRequest(
                            item.getVehiculeId(),
                            item.getChauffeurId(),
                            item.getMissionId(),
                            opDate,
                            item.getAmountHT(),
                            item.getTvaRate(),
                            item.getTvaAmount(),
                            ttc,
                            item.getGareEntree(),
                            item.getGareSortie(),
                            receiptNum,
                            item.getSocieteAutoroute(),
                            item.getNotes()
                    );
                    PeageResponse response = peageService.create(pr, null);
                    results.add(BatchTicketSaveResult.BatchTicketSaveItemResult.builder()
                            .ticketIndex(item.getTicketIndex())
                            .ticketType("PEAGE")
                            .savedId(response.id())
                            .success(true)
                            .build());
                    savedCount++;

                } else if ("CARBURANT".equalsIgnoreCase(item.getTicketType())) {
                    BigDecimal qty = (item.getQuantityLiters() != null && item.getQuantityLiters().compareTo(BigDecimal.ZERO) > 0)
                            ? item.getQuantityLiters() : BigDecimal.ONE;
                    BigDecimal total = (item.getAmountTTC() != null && item.getAmountTTC().compareTo(BigDecimal.ZERO) > 0)
                            ? item.getAmountTTC() 
                            : ((item.getTotalCost() != null && item.getTotalCost().compareTo(BigDecimal.ZERO) > 0) ? item.getTotalCost() : BigDecimal.valueOf(0.01));
                    BigDecimal pricePerL = item.getPricePerLiter();
                    if (pricePerL == null && qty.compareTo(BigDecimal.ZERO) > 0 && total.compareTo(BigDecimal.ZERO) > 0) {
                        pricePerL = total.divide(qty, 3, RoundingMode.HALF_UP);
                    }
                    if (pricePerL == null || pricePerL.compareTo(BigDecimal.ZERO) <= 0) {
                        pricePerL = BigDecimal.ONE;
                    }

                    String fuelType = item.getFuelType() != null && !item.getFuelType().isEmpty() ? item.getFuelType() : "DIESEL";

                    PleinCarburantRequest pcr = new PleinCarburantRequest(
                            item.getVehiculeId(),
                            item.getChauffeurId(),
                            item.getMissionId(),
                            opDate,
                            fuelType,
                            qty,
                            pricePerL,
                            null, null, true,
                            receiptNum,
                            item.getNotes(),
                            item.getAmountHT(),
                            total,
                            item.getTvaRate(),
                            item.getTvaAmount(),
                            false, null, null
                    );

                    PleinCarburantResponse response = pleinCarburantService.create(pcr, null);
                    results.add(BatchTicketSaveResult.BatchTicketSaveItemResult.builder()
                            .ticketIndex(item.getTicketIndex())
                            .ticketType("CARBURANT")
                            .savedId(response.id())
                            .success(true)
                            .build());
                    savedCount++;
                } else {
                    throw new InvalidOperationException("Type de ticket invalide ou inconnu pour le ticket #" + item.getTicketIndex());
                }

            } catch (Exception e) {
                log.error("Erreur enregistrement ticket batch #{}: {}", item.getTicketIndex(), e.getMessage());
                results.add(BatchTicketSaveResult.BatchTicketSaveItemResult.builder()
                        .ticketIndex(item.getTicketIndex())
                        .ticketType(item.getTicketType())
                        .savedId(null)
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build());
                failedCount++;
            }
        }

        return BatchTicketSaveResult.builder()
                .savedCount(savedCount)
                .failedCount(failedCount)
                .results(results)
                .build();
    }
}
