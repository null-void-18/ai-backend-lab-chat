package com.example.ai_backend_lab.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_backend_lab.dto.user.CreateUserRequest;
import com.example.ai_backend_lab.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/create")
    public UUID createUser(@RequestBody CreateUserRequest createUserRequest) {
        UUID id = null;

        try {
            id = userService.createUser(createUserRequest);
        } catch (Exception ex) {
            log.error("Failed to create user" + ex);
        }
    
        return id;
    }
    
}
