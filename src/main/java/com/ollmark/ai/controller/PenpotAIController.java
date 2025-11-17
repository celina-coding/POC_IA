package com.ollmark.ai.controller;

import com.ollmark.ai.service.PenpotAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/penpot")
public class PenpotAIController {

    private final PenpotAIService penpotAIService;

    public PenpotAIController(PenpotAIService penpotAIService) {
        this.penpotAIService = penpotAIService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateDesign(@RequestBody Map<String, String> request) {
        try {
            String description = request.get("description");
            
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "La description est requise"
                ));
            }
            
            var result = penpotAIService.generatePenpotDesign(description);
            
            // Retourne directement l'objet JSON
            return ResponseEntity.ok(result.getJsonResponse());
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Erreur interne: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/generate-with-info")
    public ResponseEntity<Map<String, Object>> generateDesignWithInfo(@RequestBody Map<String, String> request) {
        try {
            String description = request.get("description");
            
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "La description est requise"
                ));
            }
            
            var result = penpotAIService.generatePenpotDesign(description);
            
            return ResponseEntity.ok(Map.of(
                "description", result.getDescription(),
                "design", result.getJsonResponse(),
                "valid", result.isValid(),
                "status", result.getStatus(),
                "timestamp", result.getTimestamp()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Erreur interne: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/batch-generate")
    public ResponseEntity<Map<String, Object>> batchGenerate(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> descriptions = request.get("descriptions");
            
            if (descriptions == null || descriptions.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "La liste des descriptions est requise"
                ));
            }
            
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (String desc : descriptions) {
                var result = penpotAIService.generatePenpotDesign(desc);
                results.add(Map.of(
                    "description", result.getDescription(),
                    "design", result.getJsonResponse(),
                    "valid", result.isValid(),
                    "status", result.getStatus()
                ));
            }
            
            long successCount = results.stream().filter(r -> (Boolean)r.get("valid")).count();
            
            return ResponseEntity.ok(Map.of(
                "total", results.size(),
                "successCount", successCount,
                "successRate", String.format("%.1f%%", (successCount * 100.0 / results.size())),
                "results", results,
                "timestamp", new Date()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Erreur interne: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Penpot AI Generator",
            "timestamp", new Date()
        ));
    }

    @GetMapping("/test-prompts")
    public ResponseEntity<Map<String, Object>> getTestPrompts() {
        return ResponseEntity.ok(Map.of(
            "prompts", Arrays.asList(
                "Crée un paysage de montagne avec un lac et des arbres",
                "Dessine une maison moderne avec un jardin",
                "Crée un soleil avec des nuages dans le ciel",
                "Dessine une voiture avec des roues et des fenêtres",
                "Crée un arbre avec un tronc et du feuillage",
                "Dessine un visage souriant avec des yeux et une bouche",
                "Crée un bateau sur l'eau avec des voiles",
                "Dessine un oiseau en vol avec des ailes déployées"
            ),
            "timestamp", new Date()
        ));
    }
}