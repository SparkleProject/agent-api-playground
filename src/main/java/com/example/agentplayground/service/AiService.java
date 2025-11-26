package com.example.agentplayground.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiService {

    private final OpenAiChatModel openAiChatModel;
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final AnthropicChatModel anthropicChatModel;

    @Autowired
    public AiService(OpenAiChatModel openAiChatModel,
            VertexAiGeminiChatModel vertexAiGeminiChatModel,
            AnthropicChatModel anthropicChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.vertexAiGeminiChatModel = vertexAiGeminiChatModel;
        this.anthropicChatModel = anthropicChatModel;
    }

    public String chatWithOpenAi(String message) {
        return openAiChatModel.call(message);
    }

    public String chatWithGemini(String message) {
        return vertexAiGeminiChatModel.call(message);
    }

    public String chatWithClaude(String message) {
        return anthropicChatModel.call(message);
    }
}
