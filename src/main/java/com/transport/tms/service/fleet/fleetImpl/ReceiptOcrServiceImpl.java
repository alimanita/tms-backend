package com.transport.tms.service.fleet.fleetImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.tms.dto.fleet.response.OcrFuelResult;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.service.fleet.ReceiptOcrService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReceiptOcrServiceImpl implements ReceiptOcrService {

    @Value("${anthropic.api-key:}")
    private String anthropicApiKey;

    // Nom du modèle externalisé, avec une valeur par défaut à jour (Haiku 4.5, supporte la vision)
    @Value("${tms.anthropic.model:claude-haiku-4-5-20251001}")
    private String anthropicModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OcrFuelResult extractFuelData(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidOperationException("L'image fournie est vide ou nulle.");
        }

        if (anthropicApiKey == null || anthropicApiKey.isEmpty()) {
            throw new InvalidOperationException("La clé API Anthropic n'est pas configurée.");
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String mimeType = image.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg"; // Default fallback
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> source = new HashMap<>();
            source.put("type", "base64");
            source.put("media_type", mimeType);
            source.put("data", base64Image);

            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image");
            imageContent.put("source", source);

            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Extrais les données de ce ticket de carburant. Renvoie UNIQUEMENT un objet JSON valide, sans markdown, sans commentaires, avec exactement ces clés : 'quantityLiters' (nombre), 'totalCost' (nombre), 'tvaAmount' (nombre, 0 si non trouvé), 'fillingDate' (chaîne de caractères au format YYYY-MM-DD), 'fillingTime' (chaîne de caractères au format HH:mm correspondant à l'heure exacte de la transaction indiquée sur le ticket, par exemple dans un champ 'Fecha/Hora' ou 'Date/Heure' ; si aucune heure n'est visible, renvoie '00:00'), 'fuelType' (chaîne de caractères, ex: 'DIESEL', 'SANS PLOMB').");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", List.of(imageContent, textContent));

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

            // Cleanup response in case Claude added markdown like ```json
            assistantReply = assistantReply.replaceAll("(?s)^```json\\s*", "");
            assistantReply = assistantReply.replaceAll("(?s)\\s*```$", "");

            log.info("Anthropic raw response: {}", assistantReply);

            JsonNode jsonResult = objectMapper.readTree(assistantReply);

            return OcrFuelResult.builder()
                    .quantityLiters(getBigDecimalNode(jsonResult, "quantityLiters"))
                    .totalCost(getBigDecimalNode(jsonResult, "totalCost"))
                    .tvaAmount(getBigDecimalNode(jsonResult, "tvaAmount"))
                    .fillingDate(getLocalDateTimeNode(jsonResult, "fillingDate", "fillingTime"))
                    .fuelType(jsonResult.path("fuelType").asText(null))
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'extraction OCR via Anthropic", e);
            throw new InvalidOperationException("Erreur lors de l'analyse du ticket: " + e.getMessage());
        }
    }

    private BigDecimal getBigDecimalNode(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        if (field.isMissingNode() || field.isNull()) {
            return null;
        }
        try {
            return new BigDecimal(field.asText().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Combine la date et l'heure extraites du ticket en un seul LocalDateTime.
     * Si l'heure est absente ou invalide, on retombe sur minuit (comportement précédent).
     */
    private LocalDateTime getLocalDateTimeNode(JsonNode node, String dateField, String timeField) {
        JsonNode dateNode = node.path(dateField);
        if (dateNode.isMissingNode() || dateNode.isNull() || dateNode.asText().isEmpty()) {
            return null;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateNode.asText(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            log.warn("Impossible de parser la date extraite '{}', champ ignoré.", dateNode.asText());
            return null;
        }

        LocalTime time = LocalTime.MIDNIGHT;
        JsonNode timeNode = node.path(timeField);
        if (!timeNode.isMissingNode() && !timeNode.isNull() && !timeNode.asText().isEmpty()) {
            String rawTime = timeNode.asText().trim();
            try {
                time = LocalTime.parse(rawTime, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                try {
                    // Au cas où le modèle renvoie HH:mm:ss
                    time = LocalTime.parse(rawTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
                } catch (Exception ex) {
                    log.warn("Impossible de parser l'heure extraite '{}', 00:00 utilisé par défaut.", rawTime);
                }
            }
        }

        return LocalDateTime.of(date, time);
    }

    @Override
    public com.transport.tms.dto.fleet.response.OcrTollResult extractTollData(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidOperationException("L'image fournie est vide ou nulle.");
        }

        if (anthropicApiKey == null || anthropicApiKey.isEmpty()) {
            throw new InvalidOperationException("La clé API Anthropic n'est pas configurée.");
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String mimeType = image.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> source = new HashMap<>();
            source.put("type", "base64");
            source.put("media_type", mimeType);
            source.put("data", base64Image);

            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image");
            imageContent.put("source", source);

            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Extrais les données de ce ticket de péage. Renvoie UNIQUEMENT un objet JSON valide, sans markdown, avec exactement ces clés : 'amountTTC' (nombre), 'amountHT' (nombre, 0 si non trouvé), 'tvaAmount' (nombre, 0 si non trouvé), 'tvaRate' (nombre, ex: 20.0, 0 si non trouvé), 'receiptDate' (chaîne YYYY-MM-DD), 'receiptTime' (chaîne HH:mm), 'entree' (gare d'entrée, chaîne), 'sortie' (gare de sortie, chaîne), 'receiptNumber' (numéro de reçu/ticket, chaîne), 'operator' (société ex: ASF, VINCI, SANEF, APRR, chaîne).");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", List.of(imageContent, textContent));

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

            assistantReply = assistantReply.replaceAll("(?s)^```json\\s*", "");
            assistantReply = assistantReply.replaceAll("(?s)\\s*```$", "");

            log.info("Anthropic raw response (Toll): {}", assistantReply);

            JsonNode jsonResult = objectMapper.readTree(assistantReply);

            return com.transport.tms.dto.fleet.response.OcrTollResult.builder()
                    .amountTTC(getBigDecimalNode(jsonResult, "amountTTC"))
                    .amountHT(getBigDecimalNode(jsonResult, "amountHT"))
                    .tvaAmount(getBigDecimalNode(jsonResult, "tvaAmount"))
                    .tvaRate(getBigDecimalNode(jsonResult, "tvaRate"))
                    .receiptDate(getLocalDateTimeNode(jsonResult, "receiptDate", "receiptTime"))
                    .entree(jsonResult.path("entree").asText(null))
                    .sortie(jsonResult.path("sortie").asText(null))
                    .receiptNumber(jsonResult.path("receiptNumber").asText(null))
                    .operator(jsonResult.path("operator").asText(null))
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'extraction OCR via Anthropic (Péage)", e);
            throw new InvalidOperationException("Erreur lors de l'analyse du ticket de péage: " + e.getMessage());
        }
    }

    @Override
    public com.transport.tms.dto.fleet.response.OcrPieceResult extractPieceData(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidOperationException("L'image fournie est vide ou nulle.");
        }

        if (anthropicApiKey == null || anthropicApiKey.isEmpty()) {
            throw new InvalidOperationException("La clé API Anthropic n'est pas configurée.");
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String mimeType = image.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> source = new HashMap<>();
            source.put("type", "base64");
            source.put("media_type", mimeType);
            source.put("data", base64Image);

            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image");
            imageContent.put("source", source);

            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Extrais les données de cette facture/ticket de pièce de rechange. Renvoie UNIQUEMENT un objet JSON valide, sans markdown, avec exactement ces clés : 'name' (chaîne de caractères, désignation de la pièce), 'brand' (chaîne de caractères, marque, ex: Bosch), 'unitCost' (nombre, coût unitaire, 0 si non trouvé), 'amountHT' (nombre, montant HT, 0 si non trouvé), 'tvaAmount' (nombre, montant TVA, 0 si non trouvé), 'tvaRate' (nombre, taux TVA en %, ex: 19.0, 0 si non trouvé).");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", List.of(imageContent, textContent));

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

            assistantReply = assistantReply.replaceAll("(?s)^```json\\s*", "");
            assistantReply = assistantReply.replaceAll("(?s)\\s*```$", "");

            log.info("Anthropic raw response (PieceRechange): {}", assistantReply);

            JsonNode jsonResult = objectMapper.readTree(assistantReply);

            return com.transport.tms.dto.fleet.response.OcrPieceResult.builder()
                    .name(jsonResult.path("name").asText(null))
                    .brand(jsonResult.path("brand").asText(null))
                    .unitCost(getBigDecimalNode(jsonResult, "unitCost"))
                    .amountHT(getBigDecimalNode(jsonResult, "amountHT"))
                    .tvaAmount(getBigDecimalNode(jsonResult, "tvaAmount"))
                    .tvaRate(getBigDecimalNode(jsonResult, "tvaRate"))
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'extraction OCR via Anthropic (PieceRechange)", e);
            throw new InvalidOperationException("Erreur lors de l'analyse du ticket: " + e.getMessage());
        }
    }
}