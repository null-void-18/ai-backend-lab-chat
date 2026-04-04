package com.example.ai_backend_lab.dto.user;

import java.util.UUID;

import com.example.ai_backend_lab.enums.Provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class CreateChatRequest {
    private UUID userId;
    private Provider provider;
    private String model;
}
