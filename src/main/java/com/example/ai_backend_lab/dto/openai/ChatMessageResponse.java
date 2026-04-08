package com.example.ai_backend_lab.dto.openai;

import java.time.LocalDateTime;

import com.example.ai_backend_lab.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ChatMessageResponse {
    
    private String content;

    private Role role;

    private LocalDateTime createdAt;
}
