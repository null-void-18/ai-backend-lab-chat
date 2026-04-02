package com.example.ai_backend_lab.dto.openai;

import java.util.List;

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
                                List.of(new Part(message))
                        )
                )
        );
    }
}
