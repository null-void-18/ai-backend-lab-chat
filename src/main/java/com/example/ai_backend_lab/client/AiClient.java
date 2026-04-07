package com.example.ai_backend_lab.client;

import com.example.ai_backend_lab.entities.Chat;

public interface AiClient {
    String getChatResponse(String input);

    String getChatResponse(Chat chat);

    void initializeChat(Chat chat); 
}
