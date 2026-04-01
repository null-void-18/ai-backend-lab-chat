package com.example.ai_backend_lab.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_backend_lab.dto.user.UserRequest;
import com.example.ai_backend_lab.dto.user.UserResponse;
import com.example.ai_backend_lab.service.ChatService;


@RestController
@RequestMapping("/chat")
public class ChatController {
    
    private final ChatService chatService;
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public UserResponse askQuestion(@RequestBody UserRequest userRequest) {
        log.info("Received /chat request");
        
        String reply = chatService.getReply(userRequest.getMessage());
        return new UserResponse(reply);
    }
    
}
