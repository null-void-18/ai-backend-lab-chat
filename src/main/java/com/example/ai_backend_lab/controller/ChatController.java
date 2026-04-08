package com.example.ai_backend_lab.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.ai_backend_lab.dto.openai.ChatMessageResponse;
import com.example.ai_backend_lab.dto.openai.PageableResponse;
import com.example.ai_backend_lab.dto.user.CreateChatRequest;
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

    @PostMapping("/ask-ai")
    public UserResponse askQuestion(@RequestBody UserRequest userRequest) {
        log.info("Received /chat request");
        
        String reply = chatService.getReply(userRequest.getMessage());
        return new UserResponse(reply);
    }

     @PostMapping("/{id}/message")
    public UserResponse chatMessage(@PathVariable Integer id,@RequestBody UserRequest userRequest) {
        log.info("Received /chat/{}/message request",id);
        
        String reply = null;

        try {
            reply = chatService.getReplyForChat(id, userRequest.getMessage());
        } catch (Exception ex) {
            log.error("Failed to get a response from AI " + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI failed");
        }
        
        return new UserResponse(reply);
    }


    @PostMapping("/start")
    public Integer startChat(@RequestBody CreateChatRequest createChatRequest) {
        Integer chatId = -1;
        try {
            chatId = chatService.createChat(createChatRequest);
        }catch(Exception ex) {
            log.error("Failed to create chat", ex.getMessage());
        }

        return chatId;
    }
    

    @GetMapping("/{id}/messages")
    public PageableResponse<ChatMessageResponse> getMethodName(@PathVariable Integer id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size
    ) {
        return chatService.getChatMessages(id, page, size);
    }
    
    
}
