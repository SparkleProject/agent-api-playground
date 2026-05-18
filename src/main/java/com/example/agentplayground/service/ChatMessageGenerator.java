package com.example.agentplayground.service;

import dev.langchain4j.data.message.ChatMessage;

import java.util.List;

public interface ChatMessageGenerator<T> {
    T generate(String message, String previousRequest, String previousResponse);
}
