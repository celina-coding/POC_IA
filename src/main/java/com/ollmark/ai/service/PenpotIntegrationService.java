package com.ollmark.ai.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class PenpotIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(PenpotIntegrationService.class);
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String PENPOT_BASE_URL = "http://localhost:9001/api/rpc/command/update-file";
    
    // Token d'authentification (à configurer)
    private final String AUTH_TOKEN = "your-auth-token-here";

    public boolean sendToPenpot(String jsonPayload, String sessionId, String fileId) {
        // À faire 
    
    }
}