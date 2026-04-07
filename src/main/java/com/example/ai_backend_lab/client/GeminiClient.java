package com.example.ai_backend_lab.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ai_backend_lab.dto.openai.GeminiRequest;
import com.example.ai_backend_lab.dto.openai.GeminiResponse;
import com.example.ai_backend_lab.entities.Chat;

@Component("gemini")
public class GeminiClient implements AiClient {

    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    @Value("${api.key}")
    private String apiKey;

    @Value("${ai.system.prompt}")
    private String systemPrompt;

    public GeminiClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    @Override
    public String getChatResponse(String userMessage) {

        GeminiRequest geminiRequest = GeminiRequest.fromPrompt(systemPrompt,userMessage);

        log.info("Sending request to Gemini with message: {}", userMessage);

        GeminiResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(geminiRequest)
                .retrieve()
                .onStatus(status -> status.isError(), res ->
                        res.bodyToMono(String.class)
                                .map(body -> new RuntimeException("API Error: " + body))
                )
                .bodyToMono(GeminiResponse.class)
                .block();

        log.info("Received response from Gemini");

        return response.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }

    @Override
    public String getChatResponse(Chat chat) {

        GeminiRequest geminiRequest = GeminiRequest.fromPrompt(chat);

        GeminiResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(geminiRequest)
                .retrieve()
                .onStatus(status -> status.isError(), res ->
                        res.bodyToMono(String.class)
                                .map(body -> new RuntimeException("API Error: " + body))
                )
                .bodyToMono(GeminiResponse.class)
                .block();

        log.info("Received response from Gemini");

        return response.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }


    @Override
    public void initializeChat(Chat chat) {
        //doesn't need initialization for now as Gemini does not have a system role
    }

}