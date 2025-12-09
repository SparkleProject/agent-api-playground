package com.example.agentplayground.service;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AzureAiService {

    private final Map<String, AzureOpenAiChatModel> models;
    private final com.example.agentplayground.config.AzureMultiModelProperties properties;

    @Autowired
    public AzureAiService(
            @org.springframework.beans.factory.annotation.Value("#{springAiChatModels}") Map<String, AzureOpenAiChatModel> models,
            com.example.agentplayground.config.AzureMultiModelProperties properties) {
        this.models = models;
        this.properties = properties;
    }

    public Set<String> getSupportedModels() {
        return properties.getModels().values().stream()
                .map(com.example.agentplayground.config.AzureMultiModelProperties.AzureModelConfig::getDeploymentName)
                .collect(java.util.stream.Collectors.toSet());
    }

    public String chat(String modelName, String message) {
        AzureOpenAiChatModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model or missing credentials: " + modelName);
        }
        return model.call(message);
    }
}
