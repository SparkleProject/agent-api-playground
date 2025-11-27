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

    @Autowired
    public AzureAiService(@Autowired(required = false) AzureOpenAiChatModel azureOpenAiChatModel) {
        this.models = new HashMap<>();
        if (azureOpenAiChatModel != null) {
            this.models.put("gpt-4.1-mini", azureOpenAiChatModel);
            this.models.put("gpt-4.1", azureOpenAiChatModel);
        }
    }

    public Set<String> getSupportedModels() {
        return models.keySet();
    }

    public String chat(String modelName, String message) {
        AzureOpenAiChatModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model: " + modelName);
        }
        return model.call(message);
    }
}
