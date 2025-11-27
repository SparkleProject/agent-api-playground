package com.example.agentplayground.service;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class LangChainService {

    private final Map<String, ChatLanguageModel> models;

    @Autowired
    public LangChainService(@Autowired(required = false) OpenAiChatModel openAiChatModel,
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
        ChatLanguageModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model: " + modelName);
        }
        return model.generate(message);
    }
}
