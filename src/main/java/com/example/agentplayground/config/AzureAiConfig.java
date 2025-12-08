package com.example.agentplayground.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AzureAiConfig {

    @Bean
    public Map<String, AzureOpenAiChatModel> azureOpenAiChatModels(AzureMultiModelProperties properties) {
        Map<String, AzureOpenAiChatModel> models = new HashMap<>();

        properties.getModels().forEach((modelName, config) -> {
            if (config.getApiKey() == null || config.getApiKey().isBlank()) {
                return;
            }
            OpenAIClient openAIClient = new OpenAIClientBuilder()
                    .credential(new AzureKeyCredential(config.getApiKey()))
                    .endpoint(config.getEndpoint())
                    .buildClient();

            AzureOpenAiChatOptions options = AzureOpenAiChatOptions.builder()
                    .withDeploymentName(config.getDeploymentName())
                    .build();

            AzureOpenAiChatModel chatModel = new AzureOpenAiChatModel(openAIClient, options);
            models.put(modelName, chatModel);
        });

        return models;
    }
}
