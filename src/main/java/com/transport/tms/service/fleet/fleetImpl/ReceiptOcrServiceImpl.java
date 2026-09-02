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

    @Override
    public com.transport.tms.dto.fleet.response.OcrMissionResult extractMissionData(MultipartFile image) {
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
            textContent.put("text",
                "Analyse ce document de transport (ordre de mission, bon de commande, fiche Amazon Relay, email de transport, screenshot d'application logistique, etc.). " +
                "Extrais toutes les informations disponibles et renvoie UNIQUEMENT un objet JSON valide, sans markdown, avec exactement ces clés : " +
                "'title' (chaîne, ex: numéro de chargement ou référence, comme '1135PDCHP' ou 'Mission Paris-Lyon'), " +
                "'departureLocation' (chaîne, ville ou adresse complète de départ, ex: 'XOR4 Saint Sauveur, Hauts-de-France'), " +
                "'arrivalLocation' (chaîne, ville ou adresse complète d'arrivée, ex: 'BCN8 Sabadell, Barcelona'), " +
                "'plannedDeparture' (chaîne au format ISO 8601 YYYY-MM-DDTHH:mm:ss, date et heure de départ), " +
                "'plannedReturn' (chaîne au format ISO 8601 YYYY-MM-DDTHH:mm:ss si date de retour/arrivée visible, sinon null), " +
                "'revenue' (nombre décimal avec point comme séparateur décimal — ATTENTION : si tu vois '€2698,99' ou '2698,99' le montant est 2698.99 (deux-mille-six-cent-quatre-vingt-dix-huit euros quatre-vingt-dix-neuf cents), si tu vois '€698,99' c'est 698.99. La virgule est le séparateur décimal en format européen. Lis TOUS les chiffres avant la virgule, ne coupe pas le montant. Exemple Amazon Relay: '€2698,99' => 2698.99, '€1 234,56' => 1234.56), " +
                "'cargoDescription' (chaîne décrivant le type de fret ou de transport, ex: 'Semi-remorque', 'Palettes', null si inconnu), " +
                "'notes' (chaîne avec toutes autres informations utiles comme le nom des chauffeurs ou commentaires, null si rien). " +
                "IMPORTANT pour le champ 'revenue': recopie l'intégralité du montant visible sur le document, chiffre par chiffre, avant de le convertir."
            );

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

            log.info("Anthropic raw response (Mission): {}", assistantReply);

            JsonNode jsonResult = objectMapper.readTree(assistantReply);

            return com.transport.tms.dto.fleet.response.OcrMissionResult.builder()
                    .title(jsonResult.path("title").asText(null))
                    .departureLocation(jsonResult.path("departureLocation").asText(null))
                    .arrivalLocation(jsonResult.path("arrivalLocation").asText(null))
                    .plannedDeparture(getLocalDateTimeNodeFromField(jsonResult, "plannedDeparture"))
                    .plannedReturn(getLocalDateTimeNodeFromField(jsonResult, "plannedReturn"))
                    .revenue(getBigDecimalNode(jsonResult, "revenue"))
                    .cargoDescription(jsonResult.path("cargoDescription").asText(null))
                    .notes(jsonResult.path("notes").asText(null))
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'extraction OCR via Anthropic (Mission)", e);
            throw new InvalidOperationException("Erreur lors de l'analyse du document de mission: " + e.getMessage());
        }
    }

    private LocalDateTime getLocalDateTimeNodeFromField(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull() || fieldNode.asText().isEmpty() || "null".equals(fieldNode.asText())) {
            return null;
        }
        String raw = fieldNode.asText().trim();
        try {
            // Try full ISO datetime
            return LocalDateTime.parse(raw, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e1) {
            try {
                // Try date only
                LocalDate d = LocalDate.parse(raw, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
                return d.atStartOfDay();
            } catch (Exception e2) {
                log.warn("Impossible de parser la date/heure '{}', champ ignoré.", raw);
                return null;
            }
        }
    }

    @Override
    public com.transport.tms.dto.fleet.response.OcrDocumentResult extractDocumentData(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidOperationException("L'image fournie est vide ou nulle.");
        }

        if (anthropicApiKey == null || anthropicApiKey.isEmpty()) {
            throw new InvalidOperationException("La clé API Anthropic n'est pas configurée.");
        }

        try {
            String mimeType = image.getContentType();
            if (mimeType == null) mimeType = "image/jpeg";

            String base64Data = Base64.getEncoder().encodeToString(image.getBytes());

            // Anthropic supporte images ET PDF (type "document")
            boolean isPdf = mimeType.equals("application/pdf");

            Map<String, Object> fileSource = new HashMap<>();
            fileSource.put("type", "base64");
            fileSource.put("media_type", isPdf ? "application/pdf" : mimeType);
            fileSource.put("data", base64Data);

            Map<String, Object> fileContent = new HashMap<>();
            fileContent.put("type", isPdf ? "document" : "image");
            fileContent.put("source", fileSource);

            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text",
                "Analyse ce document administratif (carte grise, assurance, permis de conduire, visite technique, etc.). " +
                "Extrais toutes les informations disponibles et renvoie UNIQUEMENT un objet JSON valide, sans markdown, avec exactement ces clés : " +
                "'typeDocument' (chaîne: INSURANCE, TECHNICAL_CONTROL, REGISTRATION, PERMIT, CONTRACT, PAYSLIP, ou OTHER selon ce qui est détecté), " +
                "'referenceNumber' (chaîne, ex: numéro de permis, numéro de série, numéro de police d'assurance), " +
                "'issueDate' (chaîne au format YYYY-MM-DD, date de délivrance ou d'émission), " +
                "'expiryDate' (chaîne au format YYYY-MM-DD, date de fin de validité ou d'expiration), " +
                "'issuer' (chaîne, organisme émetteur, ex: Préfecture, nom de l'assurance), " +
                "'amount' (nombre décimal avec point comme séparateur, ex: coût de la carte grise ou de l'assurance, ou null si non applicable), " +
                "'notes' (chaîne, toutes autres informations pertinentes, null si rien)."
            );

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", List.of(fileContent, textContent));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");
            // PDF support requires this beta header
            if (isPdf) {
                headers.set("anthropic-beta", "pdfs-2024-09-25");
            }

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

            log.info("Anthropic HTTP status (Document Flotte): {}", response.getStatusCode());
            log.info("Anthropic raw body (Document Flotte): {}", response.getBody());

            JsonNode rootNode = objectMapper.readTree(response.getBody());

            // Vérifier si la réponse contient une erreur Anthropic
            if (rootNode.has("error")) {
                String errMsg = rootNode.path("error").path("message").asText("Erreur inconnue de l'API");
                log.error("Anthropic API error: {}", errMsg);
                throw new InvalidOperationException("Erreur API IA : " + errMsg);
            }

            JsonNode contentNode = rootNode.path("content");
            if (contentNode.isMissingNode() || contentNode.isEmpty()) {
                log.error("Anthropic response has no content: {}", response.getBody());
                throw new InvalidOperationException("Réponse vide de l'IA.");
            }

            String assistantReply = contentNode.get(0).path("text").asText();

            assistantReply = assistantReply.replaceAll("(?s)^```json\\s*", "");
            assistantReply = assistantReply.replaceAll("(?s)\\s*```$", "");
            assistantReply = assistantReply.trim();

            log.info("Anthropic parsed reply (Document Flotte): {}", assistantReply);

            return objectMapper.readValue(assistantReply, com.transport.tms.dto.fleet.response.OcrDocumentResult.class);

        } catch (InvalidOperationException e) {
            throw e;
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Anthropic HTTP error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new InvalidOperationException("Erreur API IA (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'extraction du document: {}", e.getMessage(), e);
            throw new InvalidOperationException("Erreur de traitement : " + e.getMessage());
        }
    }
}