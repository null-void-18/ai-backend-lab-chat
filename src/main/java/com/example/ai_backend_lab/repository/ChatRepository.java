package com.example.ai_backend_lab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ai_backend_lab.entities.Chat;

public interface  ChatRepository extends JpaRepository<Chat, Integer>{
    
}
