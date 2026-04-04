package com.example.ai_backend_lab.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.ai_backend_lab.client.AiClient;
import com.example.ai_backend_lab.dto.user.CreateChatRequest;
import com.example.ai_backend_lab.entities.Chat;
import com.example.ai_backend_lab.entities.User;
import com.example.ai_backend_lab.repository.ChatRepository;
import com.example.ai_backend_lab.repository.MessageRepository;
import com.example.ai_backend_lab.repository.UserRepository;

@Service
public class ChatService {
    
    private final Map<String,AiClient> clients;
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Value("${ai.provider}")
    private String provider;

    public ChatService(Map<String,AiClient> clients, UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository) {
        this.clients = clients;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    public String getReply(String message) {
        if (message == null || message.isBlank()) {
            return "Message cannot be empty";
        }

        AiClient client = clients.get(provider);

        if (client == null) {
            log.error("Invalid AI provider: {}", provider);
            return "AI provider is not configured correctly";
        }

        log.info("Processing user message: {}", message);

        try {
            return client.getChatResponse(message);
        }catch(Exception ex) {
            log.error("AI service failed", ex);
            return "Sorry AI is not available right now. Please try after sometime";
        }
    }


    public Integer createChat(CreateChatRequest createChatRequest) throws Exception {

        User user = userRepository.findById(createChatRequest.getUserId()).orElse(null);

        if(user == null) {
            throw new Exception("User not found!");
        }

        Chat chat = new Chat();

        chat.setModel(createChatRequest.getModel());
        chat.setProvider(createChatRequest.getProvider());
        chat.setUser(user);
        chat.setCreatedAt(LocalDateTime.now());
        
        chatRepository.save(chat);
        
        return chat.getId();
    }
}
