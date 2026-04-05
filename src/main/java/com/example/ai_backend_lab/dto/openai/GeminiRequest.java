package com.example.ai_backend_lab.dto.openai;

import java.util.ArrayList;
import java.util.List;

import com.example.ai_backend_lab.entities.Chat;
import com.example.ai_backend_lab.entities.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GeminiRequest {
    List<Content> contents;

    public static GeminiRequest fromPrompt(String message) {
        return new GeminiRequest(
                List.of(
                        new Content(
                               "user", List.of(new Part(message))
                        )
                )
        );
    }

     public static GeminiRequest fromPrompt(Chat chat) {
        List<ChatMessage> messages = chat.getMessages();

        GeminiRequest geminiRequest = new GeminiRequest();

        geminiRequest.setContents(new ArrayList<>());

        List<Content> content = geminiRequest.getContents();

        for(ChatMessage message : messages) {
                String role = message.getRole().toString();
                content.add(
                        new Content(
                              role, List.of(new Part(message.getContent()))
                        )
                );
        }

        return geminiRequest;
    }
}
