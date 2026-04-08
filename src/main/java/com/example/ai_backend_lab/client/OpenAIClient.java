package com.example.ai_backend_lab.client;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ai_backend_lab.dto.openai.OpenAIRequest;
import com.example.ai_backend_lab.dto.openai.OpenAIResponse;
import com.example.ai_backend_lab.entities.Chat;
import com.example.ai_backend_lab.entities.ChatMessage;
import com.example.ai_backend_lab.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("openai")
public class OpenAIClient implements AiClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(OpenAIClient.class);

    @Value("${ai.system.prompt}")
    private String systemPrompt;

    @Value("${api.key}")
    private String apiKey;
    
    public OpenAIClient(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String getChatResponse(String userMessage) {

        OpenAIRequest openAIRequest = OpenAIRequest.fromPrompt("gpt-5.3",systemPrompt,userMessage);

        log.info("Sending request to OpenAI with message: {}", userMessage);

        String rawResponse = webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(openAIRequest)
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

        } catch (Exception ex) {
            log.error("Failed to parse response from OpenAI: {}", ex.getMessage());
            throw new RuntimeException("Parsing failed", ex);
        }
    }

    @Override
    public String getChatResponse(Chat chat) {

        OpenAIRequest openAIRequest = OpenAIRequest.fromPrompt(chat);

        OpenAIResponse response = webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(openAIRequest)
            .retrieve()
            .onStatus(status -> status.isError(), res ->
                        res.bodyToMono(String.class)
                                .map(body -> new RuntimeException("API Error: " + body))
                )
            .bodyToMono(OpenAIResponse.class)
            .block();

        log.info("Received response from OpenAI");

        return response
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }

    @Override
    public void initializeChat(Chat chat) {
            
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setChat(chat);
        systemMessage.setContent("You are a helpful assistant.");
        systemMessage.setRole(Role.SYSTEM);
        systemMessage.setCreatedAt(LocalDateTime.now());
        systemMessage.setProvider(chat.getProvider());
        systemMessage.setModel(chat.getModel());

        chat.getMessages().add(systemMessage);
    }
}
