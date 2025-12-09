package com.example.agentplayground.service;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AzureLangChainService {

    private final Map<String, ChatLanguageModel> models;
    private final com.example.agentplayground.config.AzureMultiModelProperties properties;

    @Autowired
    public AzureLangChainService(
            @org.springframework.beans.factory.annotation.Value("#{azureOpenAiChatModels}") Map<String, AzureOpenAiChatModel> azureOpenAiChatModels,
            com.example.agentplayground.config.AzureMultiModelProperties properties) {
        this.models = new HashMap<>(azureOpenAiChatModels);
        this.properties = properties;
    }

    public Set<String> getSupportedModels() {
        return properties.getModels().keySet();
    }

    public String chat(String modelName, String message) {
        ChatLanguageModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model or missing credentials: " + modelName);
        }
        return model.generate(message);
    }
}
