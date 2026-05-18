package com.example.agentplayground.service;

import dev.langchain4j.data.message.ChatMessage;

import java.util.List;

public interface ChatMessageResolver {
    List<ChatMessage> resolve(String modelName, String message, String previousRequest, String previousResponse);
}
