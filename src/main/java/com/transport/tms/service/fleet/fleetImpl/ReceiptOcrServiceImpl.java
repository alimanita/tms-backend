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
            textContent.put("text", "Extrais les données de ce ticket de carburant. Renvoie UNIQUEMENT un objet JSON valide, sans markdown, sans commentaires, avec exactement ces clés : 'quantityLiters' (nombre), 'totalCost' (nombre), 'tvaAmount' (nombre, 0 si non trouvé), 'fillingDate' (chaîne de caractères au format YYYY-MM-DD), 'fuelType' (chaîne de caractères, ex: 'DIESEL', 'SANS PLOMB').");

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", List.of(imageContent, textContent));

            Map<String, Object> body = new HashMap<>();
            body.put("model", "claude-3-haiku-20240307");
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
                    .fillingDate(getLocalDateNode(jsonResult, "fillingDate"))
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

    private LocalDate getLocalDateNode(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        if (field.isMissingNode() || field.isNull() || field.asText().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(field.asText(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }
}
