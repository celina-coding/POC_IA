package com.ollmark.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/health")
    public String health() {
        return "✅ Spring AI + Penpot - Service actif!";
    }
    
    @GetMapping("/test")
    public String test() {
        return "API de test opérationnelle";
    }
}
