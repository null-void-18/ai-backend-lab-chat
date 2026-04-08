package com.example.ai_backend_lab.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.ai_backend_lab.client.AiClient;
import com.example.ai_backend_lab.dto.openai.ChatMessageResponse;
import com.example.ai_backend_lab.dto.user.CreateChatRequest;
import com.example.ai_backend_lab.entities.Chat;
import com.example.ai_backend_lab.entities.ChatMessage;
import com.example.ai_backend_lab.entities.User;
import com.example.ai_backend_lab.enums.Role;
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

    public ChatService(Map<String,AiClient> clients, UserRepository userRepository, ChatRepository chatRepository,MessageRepository messageRepository) {
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

    public String getReplyForChat(Integer id,String message) throws Exception {

        Chat chat = chatRepository.findById(id).orElse(null);

        if(chat == null) {
            throw new Exception("Chat not found!");
        }

        List<ChatMessage> messages = chat.getMessages();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chat);
        chatMessage.setContent(message);
        chatMessage.setProvider(chat.getProvider());
        chatMessage.setModel(chat.getModel());
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setRole(Role.USER);

        messages.add(chatMessage);


        AiClient client = clients.get(chat.getProvider().toString().toLowerCase());

        if (client == null) {
            log.error("Invalid AI provider: {}", chat.getProvider());
            return "AI provider is not configured correctly";
        }

        log.info("Processing user message: {}", chat);

        try {
            String response = client.getChatResponse(chat);

            ChatMessage chatMessageResponse = new ChatMessage();
            chatMessageResponse.setChat(chat);
            chatMessageResponse.setContent(response);
            chatMessageResponse.setProvider(chat.getProvider());
            chatMessageResponse.setModel(chat.getModel());
            chatMessageResponse.setCreatedAt(LocalDateTime.now());
            chatMessageResponse.setRole(Role.MODEL);

            messages.add(chatMessageResponse);

            chatRepository.save(chat);

            return response;
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

        AiClient client = clients.get(createChatRequest.getProvider().toString().toLowerCase());

        if (client == null) {
            throw new RuntimeException("Invalid AI provider");
        }

        Chat chat = new Chat();

        chat.setModel(createChatRequest.getModel());
        chat.setProvider(createChatRequest.getProvider());
        chat.setUser(user);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setMessages(new ArrayList<>());

        client.initializeChat(chat);
        
        chatRepository.save(chat);
        
        return chat.getId();
    }


    public Page<ChatMessageResponse> getChatMessages(Integer chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ChatMessage> messages = messageRepository.findByChatId(chatId, pageable);

        Page<ChatMessageResponse> messageResponse = messages.map(this::mapToResponse);

        return messageResponse;
    }


    private ChatMessageResponse mapToResponse(ChatMessage chatMessage) {
        return new ChatMessageResponse(
            chatMessage.getContent(),
            chatMessage.getRole(),
            chatMessage.getCreatedAt()
        );
    }
}
