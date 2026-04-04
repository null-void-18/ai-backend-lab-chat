package com.example.ai_backend_lab.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.ai_backend_lab.dto.user.CreateUserRequest;
import com.example.ai_backend_lab.entities.User;
import com.example.ai_backend_lab.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID createUser(CreateUserRequest createUserRequest) throws Exception {
        User user = new User();

        if(userRepository.findByEmail(createUserRequest.getEmail()) != null) {
            throw new Exception("User already exists!");
        }

        user.setEmail(createUserRequest.getEmail());

        userRepository.save(user);

        return user.getId();
    }
    
}
