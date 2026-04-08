package com.example.ai_backend_lab.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ai_backend_lab.entities.ChatMessage;

public interface MessageRepository extends JpaRepository<ChatMessage, Integer>{
    Page<ChatMessage> findByChatId(Integer chat, Pageable pageable);

}
