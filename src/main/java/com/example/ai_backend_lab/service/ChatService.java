package com.example.ai_backend_lab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.ai_backend_lab.client.ChatClient;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getReply(String message) {
        if (message == null || message.isBlank()) {
            return "Message cannot be empty";
        }

        log.info("Processing user message: {}", message);

        try {
            return chatClient.getChatResponse(message);
        }catch(Exception ex) {
            log.error("AI service failed", ex);
            return "Sorry AI is not available right now. Please try after sometime";
        }
    }
}
