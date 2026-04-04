package com.example.ai_backend_lab.enums;

public enum Provider {
    OPENAI,
    GEMINI;

    public String getDisplayName() {
        return this.name();
    }
}