package com.ollmark.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollmark.ai.models.OpenRouterRequest;
import com.ollmark.ai.models.OpenRouterResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class PenpotAIService {

    private static final Logger log = LoggerFactory.getLogger(PenpotAIService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openrouter.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String openRouterUrl;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${openrouter.model:mistralai/mistral-7b-instruct:free}")
    private String model;

    private static final String TRAINING_EXAMPLES = """
        EXEMPLES DE JSON PENPOT VALIDES:
        
        EXEMPLE 1 - PAYSAGE:
        {
          "id": "fffce8d7-4b40-8153-8007-1bc85a708aa8",
          "session-id": "ccbd35e7-e620-4a64-83dc-c0c45f681933",
          "page-id": "fffce8d7-4b40-8153-8007-1bc85a708aa9",
          "revn": 0,
          "vern": 0,
          "features": ["fdata/path-data", "plugins/runtime", "layout/grid", "styles/v2", "fdata/pointer-map", "components/v2", "design-tokens/v1", "variants/v1", "fdata/shape-data-type"],
          "changes": [
            {
              "type": "add-obj",
              "id": "8b2c1d4e-5f6a-4789-a012-3456789abcde",
              "page-id": "fffce8d7-4b40-8153-8007-1bc85a708aa9",
              "frame-id": "fffce8d7-4b40-8153-8007-198d755ef0af",
              "parent-id": "fffce8d7-4b40-8153-8007-198d755ef0af",
              "obj": {
                "id": "8b2c1d4e-5f6a-4789-a012-3456789abcde",
                "type": "rect",
                "name": "Ciel",
                "x": 0.0,
                "y": 0.0,
                "width": 800.0,
                "height": 600.0,
                "selrect": {
                  "x": 0.0, "y": 0.0, "width": 800.0, "height": 600.0,
                  "x1": 0.0, "y1": 0.0, "x2": 800.0, "y2": 600.0
                },
                "points": [
                  {"x": 0.0, "y": 0.0}, {"x": 800.0, "y": 0.0},
                  {"x": 800.0, "y": 600.0}, {"x": 0.0, "y": 600.0}
                ],
                "transform": {"a": 1.0, "b": 0.0, "c": 0.0, "d": 1.0, "e": 0.0, "f": 0.0},
                "transform-inverse": {"a": 1.0, "b": 0.0, "c": 0.0, "d": 1.0, "e": 0.0, "f": 0.0},
                "fills": [{"fill-color": "#87CEEB", "fill-opacity": 1.0}],
                "strokes": [],
                "parent-id": "fffce8d7-4b40-8153-8007-198d755ef0af",
                "frame-id": "fffce8d7-4b40-8153-8007-198d755ef0af"
              }
            }
          ]
        }
        
        EXEMPLE 2 - MAISON:
        {
          "id": "fffce8d7-4b40-8153-8007-1bc85a708aa8",
          "session-id": "ccbd35e7-e620-4a64-83dc-c0c45f681933",
          "page-id": "fffce8d7-4b40-8153-8007-1bc85a708aa9",
          "revn": 0,
          "vern": 0,
          "features": ["fdata/path-data", "plugins/runtime", "layout/grid", "styles/v2", "fdata/pointer-map", "components/v2", "design-tokens/v1", "variants/v1", "fdata/shape-data-type"],
          "changes": [
            {
              "type": "add-obj",
              "id": "8b2c1d4e-5f6a-4789-a012-3456789abcde",
              "page-id": "fffce8d7-4b40-8153-8007-1bc85a708aa9",
              "frame-id": "fffce8d7-4b40-8153-8007-198d755ef0af",
              "parent-id": "fffce8d7-4b40-8153-8007-198d755ef0af",
              "obj": {
                "id": "8b2c1d4e-5f6a-4789-a012-3456789abcde",
                "type": "rect",
                "name": "Corps de la maison",
                "x": 200.0,
                "y": 200.0,
                "width": 200.0,
                "height": 200.0,
                "selrect": {
                  "x": 200.0, "y": 200.0, "width": 200.0, "height": 200.0,
                  "x1": 200.0, "y1": 200.0, "x2": 400.0, "y2": 400.0
                },
                "points": [
                  {"x": 200.0, "y": 200.0}, {"x": 400.0, "y": 200.0},
                  {"x": 400.0, "y": 400.0}, {"x": 200.0, "y": 400.0}
                ],
                "transform": {"a": 1.0, "b": 0.0, "c": 0.0, "d": 1.0, "e": 0.0, "f": 0.0},
                "transform-inverse": {"a": 1.0, "b": 0.0, "c": 0.0, "d": 1.0, "e": 0.0, "f": 0.0},
                "fills": [{"fill-color": "#8B4513", "fill-opacity": 1.0}],
                "strokes": [{"stroke-color": "#000000", "stroke-opacity": 1.0, "stroke-width": 2.0, "stroke-style": "solid"}],
                "parent-id": "fffce8d7-4b40-8153-8007-198d755ef0af",
                "frame-id": "fffce8d7-4b40-8153-8007-198d755ef0af"
              }
            }
          ]
        }
        """;

    private static final String SYSTEM_PROMPT = """
    Tu es un expert en génération de JSON Penpot. Ta tâche est de créer des designs PRÉCIS basés sur les descriptions.
    
    RÈGLES ABSOLUES :
    1. UTILISEZ TOUJOURS CES UUIDs FIXES :
       - "id": "fffce8d7-4b40-8153-8007-1bc85a708aa8"
       - "session-id": "ccbd35e7-e620-4a64-83dc-c0c45f681933" 
       - "page-id": "fffce8d7-4b40-8153-8007-1bc85a708aa9"
       - "frame-id": "fffce8d7-4b40-8153-8007-198d755ef0af"
       - "parent-id": "fffce8d7-4b40-8153-8007-198d755ef0af"

    2. Pour les objets dans "changes", utilisez CES UUIDs VALIDES SEULEMENT :
       - "8b2c1d4e-5f6a-4789-a012-3456789abcde"
       - "9c3d4e5f-6a7b-8c9d-0123-456789abcdef" 
       - "a1b2c3d4-e5f6-7890-1234-567890abcdef"
       - "b2c3d4e5-f6a7-8901-2345-6789abcdef01"
       - "c3d4e5f6-a7b8-9012-3456-789abcdef012"
       - "d4e5f6a7-b8c9-0123-4567-89abcdef0123"

    3. Sois PRÉCIS et LITTÉRAL - génère exactement ce qui est demandé
    4. Ne PAS ajouter d'éléments supplémentaires non demandés
    5. Utilise 3-6 objets maximum dans "changes"
    6. Pour les cercles: "points" doit être un tableau vide []
    7. Pour les paths: "content" doit contenir une chaîne SVG valide
    
    STRUCTURE OBLIGATOIRE :
    {
      "id": "fffce8d7-4b40-8153-8007-1bc85a708aa8",
      "session-id": "ccbd35e7-e620-4a64-83dc-c0c45f681933",
      "page-id": "fffce8d7-4b40-8153-8007-1bc85a708aa9",
      "revn": 0,
      "vern": 0,
      "features": ["fdata/path-data", "plugins/runtime", "layout/grid", "styles/v2", "fdata/pointer-map", "components/v2", "design-tokens/v1", "variants/v1", "fdata/shape-data-type"],
      "changes": [
        // 3-6 objets maximum avec les UUIDs autorisés
      ]
    }
    
    NE CHANGEZ JAMAIS LES UUIDs FIXES !
    """ + TRAINING_EXAMPLES;

    public GenerationResult generatePenpotDesign(String description) {
        String userPrompt = """
            DESCRIPTION: %s
            
            CRÉEZ un design Penpot en utilisant EXCLUSIVEMENT les UUIDs fixes fournis.
            
            UUIDs OBLIGATOIRES :
            - id: "fffce8d7-4b40-8153-8007-1bc85a708aa8"
            - session-id: "ccbd35e7-e620-4a64-83dc-c0c45f681933"
            - page-id: "fffce8d7-4b40-8153-8007-1bc85a708aa9" 
            - frame-id: "fffce8d7-4b40-8153-8007-198d755ef0af"
            - parent-id: "fffce8d7-4b40-8153-8007-198d755ef0af"
            
            UUIDs pour les objets (utilisez dans cet ordre) :
            1. "8b2c1d4e-5f6a-4789-a012-3456789abcde"
            2. "9c3d4e5f-6a7b-8c9d-0123-456789abcdef"
            3. "a1b2c3d4-e5f6-7890-1234-567890abcdef"
            4. "b2c3d4e5-f6a7-8901-2345-6789abcdef01"
            5. "c3d4e5f6-a7b8-9012-3456-789abcdef012"
            
            Utilisez 3-5 objets maximum.
            Réponds UNIQUEMENT avec le JSON valide:
            """.formatted(description);

        try {
            log.info("Génération pour: {}", description);
            
            String response = callOpenRouterAPI(userPrompt);
            String fixedResponse = forceFixedUUIDs(response);
            boolean isValid = validateJSON(fixedResponse);
            String validationMessage = isValid ? validateStructure(fixedResponse) : "JSON invalide";
            
            Object jsonObject = null;
            if (isValid) {
                try {
                    jsonObject = objectMapper.readValue(fixedResponse, Object.class);
                } catch (Exception e) {
                    log.warn("Erreur lors du parsing du JSON, retour en string: {}", e.getMessage());
                    jsonObject = fixedResponse;
                }
            }
            
            return new GenerationResult(description, jsonObject, isValid, 
                isValid ? "SUCCÈS: " + validationMessage : "ERREUR: " + validationMessage);
                
        } catch (Exception e) {
            log.error("Erreur lors de la génération: {}", e.getMessage());
            return new GenerationResult(description, "ERREUR: " + e.getMessage(), false, "ÉCHEC");
        }
    }

    private String forceFixedUUIDs(String json) {
        // Forcer les UUIDs fixes principaux
        String result = json;
        
        // UUIDs fixes principaux
        result = result.replaceAll("\"id\"\\s*:\\s*\"[^\"]*\"", "\"id\": \"fffce8d7-4b40-8153-8007-1bc85a708aa8\"");
        result = result.replaceAll("\"session-id\"\\s*:\\s*\"[^\"]*\"", "\"session-id\": \"ccbd35e7-e620-4a64-83dc-c0c45f681933\"");
        result = result.replaceAll("\"page-id\"\\s*:\\s*\"[^\"]*\"", "\"page-id\": \"fffce8d7-4b40-8153-8007-1bc85a708aa9\"");
        result = result.replaceAll("\"frame-id\"\\s*:\\s*\"[^\"]*\"", "\"frame-id\": \"fffce8d7-4b40-8153-8007-198d755ef0af\"");
        result = result.replaceAll("\"parent-id\"\\s*:\\s*\"[^\"]*\"", "\"parent-id\": \"fffce8d7-4b40-8153-8007-198d755ef0af\"");
        
        // UUIDs valides pour les objets (dans l'ordre)
        String[] validObjectUUIDs = {
            "8b2c1d4e-5f6a-4789-a012-3456789abcde",
            "9c3d4e5f-6a7b-8c9d-0123-456789abcdef", 
            "a1b2c3d4-e5f6-7890-1234-567890abcdef",
            "b2c3d4e5-f6a7-8901-2345-6789abcdef01",
            "c3d4e5f6-a7b8-9012-3456-789abcdef012",
            "d4e5f6a7-b8c9-0123-4567-89abcdef0123"
        };
        
        // Remplacer les UUIDs d'objets invalides par les valides dans l'ordre
        Pattern objIdPattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]*)\"", Pattern.MULTILINE);
        Matcher matcher = objIdPattern.matcher(result);
        
        StringBuffer sb = new StringBuffer();
        int objCount = 0;
        
        while (matcher.find()) {
            String currentId = matcher.group(1);
            // Ne remplacer que si c'est un UUID d'objet (pas les IDs principaux)
            if (!currentId.equals("fffce8d7-4b40-8153-8007-1bc85a708aa8") &&
                !currentId.equals("ccbd35e7-e620-4a64-83dc-c0c45f681933") &&
                !currentId.equals("fffce8d7-4b40-8153-8007-1bc85a708aa9") &&
                !currentId.equals("fffce8d7-4b40-8153-8007-198d755ef0af")) {
                
                if (objCount < validObjectUUIDs.length) {
                    String replacement = "\"id\": \"" + validObjectUUIDs[objCount] + "\"";
                    matcher.appendReplacement(sb, replacement);
                    objCount++;
                } else {
                    matcher.appendReplacement(sb, matcher.group(0));
                }
            } else {
                matcher.appendReplacement(sb, matcher.group(0));
            }
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    private String callOpenRouterAPI(String userPrompt) {
        try {
            if (openRouterApiKey == null || openRouterApiKey.trim().isEmpty()) {
                throw new RuntimeException("Clé API OpenRouter non configurée");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openRouterApiKey);
            headers.set("HTTP-Referer", "http://localhost:8081");
            headers.set("X-Title", "Penpot AI Generator");

            List<OpenRouterRequest.Message> messages = Arrays.asList(
                new OpenRouterRequest.Message("system", SYSTEM_PROMPT),
                new OpenRouterRequest.Message("user", userPrompt)
            );

            OpenRouterRequest request = new OpenRouterRequest();
            request.setModel(model);
            request.setMessages(messages);
            request.setTemperature(0.7);
            request.setMax_tokens(4000);

            HttpEntity<OpenRouterRequest> entity = new HttpEntity<>(request, headers);

            log.debug("Envoi requête à OpenRouter avec modèle: {}", model);

            ResponseEntity<OpenRouterResponse> response = restTemplate.postForEntity(
                openRouterUrl, entity, OpenRouterResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OpenRouterResponse apiResponse = response.getBody();
                if (apiResponse.getChoices() != null && !apiResponse.getChoices().isEmpty()) {
                    String content = apiResponse.getChoices().get(0).getMessage().getContent();
                    log.debug("Réponse reçue, tokens utilisés: {}", 
                             apiResponse.getUsage() != null ? apiResponse.getUsage().getTotal_tokens() : "N/A");
                    
                    return cleanJSONResponse(content);
                }
            }
            
            throw new RuntimeException("Réponse API vide ou invalide - Status: " + response.getStatusCode());
            
        } catch (Exception e) {
            log.error("Erreur API OpenRouter: {}", e.getMessage());
            throw new RuntimeException("Erreur API OpenRouter: " + e.getMessage());
        }
    }

    private String cleanJSONResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "{}";
        }
        
        String cleaned = response.trim()
            .replaceAll("```json|```", "")
            .trim();
        
        if (!cleaned.startsWith("{")) {
            int start = cleaned.indexOf('{');
            if (start != -1) {
                cleaned = cleaned.substring(start);
            } else {
                return "{}";
            }
        }
        
        if (!cleaned.endsWith("}")) {
            int end = cleaned.lastIndexOf('}');
            if (end != -1) {
                cleaned = cleaned.substring(0, end + 1);
            } else {
                return "{}";
            }
        }
        
        return cleaned;
    }

    private boolean validateJSON(String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }
        
        String cleaned = response.trim();
        
        if (!cleaned.startsWith("{") || !cleaned.endsWith("}")) {
            return false;
        }
        
        try {
            return cleaned.contains("\"id\"") && 
                   cleaned.contains("\"session-id\"") && 
                   cleaned.contains("\"page-id\"") && 
                   cleaned.contains("\"changes\"") &&
                   cleaned.contains("\"features\"");
        } catch (Exception e) {
            return false;
        }
    }

    private String validateStructure(String json) {
        try {
            List<String> issues = new ArrayList<>();
            
            if (!json.contains("\"id\"")) issues.add("ID manquant");
            if (!json.contains("\"session-id\"")) issues.add("Session ID manquant");
            if (!json.contains("\"page-id\"")) issues.add("Page ID manquant");
            
            if (!json.contains("\"changes\"")) {
                issues.add("Tableau changes manquant");
            } else {
                int changeCount = countOccurrences(json, "\"type\": \"add-obj\"");
                if (changeCount < 1) {
                    issues.add("Aucun objet dans changes");
                } else if (changeCount > 10) {
                    issues.add("Trop d'objets dans changes: " + changeCount);
                }
            }
            
            return issues.isEmpty() ? "Structure valide (" + countOccurrences(json, "\"type\": \"add-obj\"") + " objets)" 
                                   : "Problèmes: " + String.join(", ", issues);
            
        } catch (Exception e) {
            return "Erreur de validation: " + e.getMessage();
        }
    }
    
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    public static class GenerationResult {
        private final String description;
        private final Object jsonResponse;
        private final boolean valid;
        private final String status;
        private final Date timestamp;

        public GenerationResult(String description, Object jsonResponse, boolean valid, String status) {
            this.description = description;
            this.jsonResponse = jsonResponse;
            this.valid = valid;
            this.status = status;
            this.timestamp = new Date();
        }

        public String getDescription() { return description; }
        public Object getJsonResponse() { return jsonResponse; }
        public boolean isValid() { return valid; }
        public String getStatus() { return status; }
        public Date getTimestamp() { return timestamp; }
    }
}