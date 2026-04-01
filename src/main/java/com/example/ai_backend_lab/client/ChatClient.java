package com.example.ai_backend_lab.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ai_backend_lab.dto.openai.ChatRequest;
import com.example.ai_backend_lab.dto.openai.Message;
import com.example.ai_backend_lab.dto.openai.OpenAIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ChatClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(ChatClient.class);

    @Value("${ai.system.prompt}")
    private String systemPrompt;

    @Value("${openai.api.key}")
    private String apiKey;
    
    public ChatClient(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = objectMapper;
    }

    public String getChatResponse(String userMessage) {

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");

        List<Message> messages = List.of(
            new Message("system",systemPrompt),
            new Message("user", userMessage)
        );

        chatRequest.setMessages(messages);

        log.info("Sending request to OpenAI with message: {}", userMessage);

        String rawResponse = webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(chatRequest)
            .retrieve()
            .onStatus(status -> status.isError(), response -> {
                return response.bodyToMono(String.class)
                        .map(body -> new RuntimeException("API Error: " + body));
            })
            .bodyToMono(String.class)
            .block();

        log.info("Received response from OpenAI");
        
        try {
            OpenAIResponse response =
                    objectMapper.readValue(rawResponse, OpenAIResponse.class);

            if (response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("No choices returned from API");
            }

            return response.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            log.error("Failed to parse response from OpenAI: {}", e.getMessage());
            throw new RuntimeException("Parsing failed", e);
        }
    }
}
