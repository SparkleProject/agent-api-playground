package com.example.agentplayground.service;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AzureLangChainService {

    private final Map<String, ChatLanguageModel> models;

    @Autowired
    public AzureLangChainService(@Autowired(required = false) AzureOpenAiChatModel azureOpenAiChatModel) {
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
        ChatLanguageModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model: " + modelName);
        }
        return model.generate(message);
    }
}
