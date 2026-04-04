package com.example.ai_backend_lab.entities;

import java.time.LocalDateTime;

import com.example.ai_backend_lab.enums.Provider;
import com.example.ai_backend_lab.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "messages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private String content;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String model;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private LocalDateTime createdAt;
}