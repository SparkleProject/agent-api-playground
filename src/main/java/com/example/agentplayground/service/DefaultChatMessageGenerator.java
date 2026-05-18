package com.example.agentplayground.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultChatMessageGenerator implements ChatMessageGenerator<List<ChatMessage>> {
    @Override
    public List<ChatMessage> generate(String message, String previousRequest, String previousResponse) {
        return List.of(
            SystemMessage.from("Organise the response nice for reading"),
            UserMessage.from(message));
    }
}
