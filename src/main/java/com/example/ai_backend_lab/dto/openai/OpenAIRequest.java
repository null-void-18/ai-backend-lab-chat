package com.example.ai_backend_lab.dto.openai;

import java.util.ArrayList;
import java.util.List;

import com.example.ai_backend_lab.entities.Chat;
import com.example.ai_backend_lab.entities.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OpenAIRequest {
    private String model;
    private List<Message> messages;


    public static OpenAIRequest fromPrompt(String model,String systemPrompt,String userMessage) {
        OpenAIRequest openAIRequest = new OpenAIRequest();
        List<Message> messages = List.of(
            new Message("system",systemPrompt),
            new Message("user", userMessage)
        );
        openAIRequest.setMessages(messages);
        openAIRequest.setModel(model);

        return openAIRequest;
    }

    public static OpenAIRequest fromPrompt(Chat chat) {

        List<ChatMessage> chatMessages = chat.getMessages();
        OpenAIRequest openAIRequest = new OpenAIRequest();
        openAIRequest.setMessages(new ArrayList<>());
        openAIRequest.setModel(chat.getModel());

        List<Message> messages = openAIRequest.getMessages();

        for(ChatMessage chatMessage : chatMessages) {
                String role = chatMessage.getRole().toString();
                messages.add(
                            new Message(role,chatMessage.getContent())
                    );
        }

        return openAIRequest;
    }
}
