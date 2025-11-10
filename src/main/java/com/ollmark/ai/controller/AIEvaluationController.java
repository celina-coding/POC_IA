package com.ollmark.ai.controller;

import com.ollmark.ai.service.AIEvaluationService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/evaluation")
public class AIEvaluationController {

    private final AIEvaluationService evaluationService;

    public AIEvaluationController(AIEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/test-rectangle")
    public Map<String, Object> testRectangleGeneration(@RequestBody Map<String, String> request) {
        String description = request.get("description");
        var result = evaluationService.testRectangleGeneration(description);
        
        return createResponse(result);
    }

    @PostMapping("/test-text") 
    public Map<String, Object> testTextGeneration(@RequestBody Map<String, String> request) {
        String description = request.get("description");
        var result = evaluationService.testTextGeneration(description);
        
        return createResponse(result);
    }

    @PostMapping("/test-page")
    public Map<String, Object> testPageGeneration(@RequestBody Map<String, String> request) {
        String description = request.get("description");
        var result = evaluationService.testPageGeneration(description);
        
        return createResponse(result);
    }

    @PostMapping("/batch-test")
    public Map<String, Object> batchTest(@RequestBody Map<String, List<String>> request) {
        List<String> descriptions = request.get("descriptions");
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (String desc : descriptions) {
            // Test avec les 3 types d'éléments
            var rectResult = evaluationService.testRectangleGeneration(desc);
            var textResult = evaluationService.testTextGeneration(desc); 
            var pageResult = evaluationService.testPageGeneration(desc);
            
            results.addAll(Arrays.asList(
                createResponse(rectResult),
                createResponse(textResult),
                createResponse(pageResult)
            ));
        }
        
        return Map.of(
            "status", "completed",
            "totalTests", results.size(),
            "results", results
        );
    }

    private Map<String, Object> createResponse(AIEvaluationService.EvaluationResult result) {
        return Map.of(
            "elementType", result.getElementType(),
            "description", result.getDescription(), 
            "successRate", result.getSuccessRate(),
            "summary", result.getSummary(),
            "responses", result.getResponses(),
            "validations", result.getValidations(),
            "timestamp", new Date()
        );
    }

    @GetMapping("/test-scenarios")
    public Map<String, List<String>> getTestScenarios() {
        return Map.of(
            "scenarios", Arrays.asList(
                "Crée un rectangle bleu en haut à gauche",
                "Ajoute un texte 'Hello World' au centre", 
                "Crée une page nommée 'Ma Première Page'",
                "Rectangle rouge de 300x200 pixels",
                "Texte de titre en gros caractères",
                "Page pour le dashboard marketing"
            )
        );
    }
}