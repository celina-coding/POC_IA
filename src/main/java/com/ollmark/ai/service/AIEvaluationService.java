package com.ollmark.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class AIEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(AIEvaluationService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // Schémas de réponse structurée
    private static final String RECTANGLE_SCHEMA = """
        {
            "type": "add-obj",
            "id": "UUID_GENERATED",
            "page-id": "PAGE_ID",
            "obj": {
                "type": "rect",
                "name": "Nom du rectangle",
                "x": 100,
                "y": 100,
                "width": 200,
                "height": 150,
                "selrect": {
                    "x": 100, "y": 100, "width": 200, "height": 150,
                    "x1": 100, "y1": 100, "x2": 300, "y2": 250
                },
                "points": [
                    {"x": 100, "y": 100}, {"x": 300, "y": 100},
                    {"x": 300, "y": 250}, {"x": 100, "y": 250}
                ],
                "transform": {"a": 1, "b": 0, "c": 0, "d": 1, "e": 0, "f": 0},
                "fills": [{"fill-color": "#HEX_COLOR"}]
            }
        }
        """;

    private static final String TEXT_SCHEMA = """
        {
            "type": "add-obj",
            "id": "UUID_GENERATED", 
            "page-id": "PAGE_ID",
            "obj": {
                "type": "text",
                "name": "Nom du texte",
                "x": 150,
                "y": 120,
                "width": 200,
                "height": 50,
                "content": {
                    "type": "root",
                    "children": [{
                        "type": "paragraph-set",
                        "children": [{
                            "type": "paragraph", 
                            "children": [{
                                "text": "TEXTE_CONTENU",
                                "fill-color": "#000000",
                                "font-size": "24",
                                "font-weight": "700",
                                "font-family": "Inter"
                            }]
                        }]
                    }]
                },
                "selrect": {"x": 150, "y": 120, "width": 200, "height": 50, "x1": 150, "y1": 120, "x2": 350, "y2": 170},
                "points": [
                    {"x": 150, "y": 120}, {"x": 350, "y": 120},
                    {"x": 350, "y": 170}, {"x": 150, "y": 170}
                ],
                "transform": {"a": 1, "b": 0, "c": 0, "d": 1, "e": 0, "f": 0},
                "fills": [{"fill-color": "#000000"}]
            }
        }
        """;

    private static final String PAGE_SCHEMA = """
        {
            "type": "add-page",
            "id": "UUID_GENERATED",
            "name": "NOM_PAGE"
        }
        """;

    public EvaluationResult testRectangleGeneration(String description) {
        String prompt = """
            Tu es un assistant spécialisé dans la génération de code JSON pour des éléments graphiques.
            Ta tâche est de créer un rectangle Penpot basé sur la description fournie.
            
            DESCRIPTION: %s
            
            SCHEMA_ATTENDU:
            %s
            
            RÈGLES:
            - Génère UNIQUEMENT du JSON valide
            - Utilise des UUID fictifs (ex: "123e4567-e89b-12d3-a456-426614174000")
            - Pour page-id, utilise "00000000-0000-0000-0000-000000000000"
            - Adapte les coordonnées (x, y, width, height) selon la description
            - Choisis une couleur hexadécimale appropriée
            - Ne génère QUE le JSON, sans commentaires
            
            Réponds uniquement avec le JSON:
            """.formatted(description, RECTANGLE_SCHEMA);

        return evaluateAIModel("Rectangle", prompt, description);
    }

    public EvaluationResult testTextGeneration(String description) {
        String prompt = """
            Tu es un assistant spécialisé dans la génération de code JSON pour des éléments graphiques.
            Ta tâche est de créer un texte Penpot basé sur la description fournie.
            
            DESCRIPTION: %s
            
            SCHEMA_ATTENDU:
            %s
            
            RÈGLES:
            - Génère UNIQUEMENT du JSON valide  
            - JAMAIS utiliser ```json, ```, ou backticks
            - JAMAIS de commentaires //, /*, ou #
            - COMMENCER directement par {
            - TERMINER par }
            - Utilise des UUID fictifs
            - Pour page-id, utilise "00000000-0000-0000-0000-000000000000"
            - Adapte le contenu texte et la position selon la description
            - Ne génère QUE le JSON, sans commentaires
            
            Réponds uniquement avec le JSON:
            """.formatted(description, TEXT_SCHEMA);

        return evaluateAIModel("Texte", prompt, description);
    }

    public EvaluationResult testPageGeneration(String description) {
        String prompt = """
            Tu es un assistant spécialisé dans la génération de code JSON pour des éléments graphiques.
            Ta tâche est de créer une page Penpot basé sur la description fournie.
            
            DESCRIPTION: %s
            
            SCHEMA_ATTENDU:
            %s
            
            RÈGLES:
            - Génère UNIQUEMENT du JSON valide
            - Utilise un UUID fictif pour l'id
            - Adapte le nom de la page selon la description
            - Ne génère QUE le JSON, sans commentaires
            
            Réponds uniquement avec le JSON:
            """.formatted(description, PAGE_SCHEMA);

        return evaluateAIModel("Page", prompt, description);
    }

    private EvaluationResult evaluateAIModel(String elementType, String prompt, String originalDescription) {
        List<String> responses = new ArrayList<>();
        List<Boolean> validations = new ArrayList<>();
        
        // Test multiple (3 essais)
        for (int i = 0; i < 1; i++) {
            try {
                log.info("Test {} pour {}: {}", i + 1, elementType, originalDescription);
                
                // Appel direct à l'API Ollama
                String response = callOllamaAPI(prompt);
                
                responses.add(response);
                boolean isValid = validateJSON(response);
                validations.add(isValid);
                
                log.info("Réponse {}: {}", i + 1, response);
                log.info("JSON valide: {}", isValid);
                
            } catch (Exception e) {
                log.error("Erreur lors du test {}: {}", i + 1, e.getMessage());
                responses.add("ERREUR: " + e.getMessage());
                validations.add(false);
            }
        }
        
        return new EvaluationResult(elementType, originalDescription, responses, validations);
    }

    private String callOllamaAPI(String prompt) {
        try {
            String url = "http://localhost:11434/api/generate";
            
            Map<String, Object> request = new HashMap<>();
            request.put("model", "phi3:3.8b");
            request.put("prompt", prompt);
            request.put("stream", false);
            
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            if (response != null && response.containsKey("response")) {
                return response.get("response").toString();
            } else {
                return "ERREUR: Pas de réponse de l'API Ollama";
            }
            
        } catch (Exception e) {
            log.error("Erreur API Ollama: {}", e.getMessage());
            return "ERREUR: " + e.getMessage();
        }
    }

   private boolean validateJSON(String response) {
    if (response == null || response.trim().isEmpty()) {
        return false;
    }
    
    String cleaned = response.trim();
    
    cleaned = cleaned.replaceAll("```json|```", "").trim();
    
    // Vérification basique de structure JSON
    if (!cleaned.startsWith("{") || !cleaned.endsWith("}")) {
        return false;
    }
    
    try {
        // Vérification des champs essentiels selon le type
        if (cleaned.contains("\"type\": \"add-page\"")) {
            return cleaned.contains("\"id\"") && cleaned.contains("\"name\"");
        } else if (cleaned.contains("\"type\": \"add-obj\"")) {
            return cleaned.contains("\"id\"") && 
                   cleaned.contains("\"page-id\"") && 
                   cleaned.contains("\"obj\"");
        }
    } catch (Exception e) {
        return false;
    }
    
    return true;
}

    // Classe interne avec getters/setters manuels
    public static class EvaluationResult {
        private final String elementType;
        private final String description;
        private final List<String> responses;
        private final List<Boolean> validations;
        
        public EvaluationResult(String elementType, String description, List<String> responses, List<Boolean> validations) {
            this.elementType = elementType;
            this.description = description;
            this.responses = responses;
            this.validations = validations;
        }
        
        public String getElementType() { return elementType; }
        public String getDescription() { return description; }
        public List<String> getResponses() { return responses; }
        public List<Boolean> getValidations() { return validations; }
        
        public double getSuccessRate() {
            long validCount = validations.stream().filter(Boolean::booleanValue).count();
            return (double) validCount / validations.size();
        }
        
        public String getSummary() {
            return String.format("%s - Taux de réussite: %.2f%% (%d/%d valides)", 
                elementType, getSuccessRate() * 100, 
                validations.stream().filter(Boolean::booleanValue).count(),
                validations.size());
        }
    }
}