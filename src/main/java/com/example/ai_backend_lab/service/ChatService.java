package com.example.ai_backend_lab.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.ai_backend_lab.client.AiClient;

@Service
public class ChatService {
    
    private final Map<String,AiClient> clients;
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @Value("${ai.provider}")
    private String provider;

    public ChatService(Map<String,AiClient> clients) {
        this.clients = clients;
    }

    public String getReply(String message) {
        if (message == null || message.isBlank()) {
            return "Message cannot be empty";
        }

        AiClient client = clients.get(provider);

        if (client == null) {
            log.error("Invalid AI provider: {}", provider);
            return "AI provider is not configured correctly";
        }

        log.info("Processing user message: {}", message);

        try {
            return client.getChatResponse(message);
        }catch(Exception ex) {
            log.error("AI service failed", ex);
            return "Sorry AI is not available right now. Please try after sometime";
        }
    }
}
