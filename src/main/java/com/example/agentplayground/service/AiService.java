package com.example.agentplayground.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AiService {

    private final Map<String, ChatModel> models;

    @Autowired
    public AiService(@Autowired(required = false) OpenAiChatModel openAiChatModel,
            @Autowired(required = false) VertexAiGeminiChatModel vertexAiGeminiChatModel,
            @Autowired(required = false) AnthropicChatModel anthropicChatModel) {
        this.models = new HashMap<>();
        if (openAiChatModel != null) {
            this.models.put("openai", openAiChatModel);
        }
        if (vertexAiGeminiChatModel != null) {
            this.models.put("gemini", vertexAiGeminiChatModel);
        }
        if (anthropicChatModel != null) {
            this.models.put("claude", anthropicChatModel);
        }
    }

    public Set<String> getSupportedModels() {
        return models.keySet();
    }

    public String chat(String modelName, String message) {
        ChatModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model: " + modelName);
        }
        return model.call(message);
    }
}
