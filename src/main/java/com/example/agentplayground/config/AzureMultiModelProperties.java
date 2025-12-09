package com.example.agentplayground.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring.ai.azure.openai")
@Data
public class AzureMultiModelProperties {

    private Map<String, AzureModelConfig> models = new HashMap<>();

    @org.springframework.context.annotation.Bean
    public Map<String, dev.langchain4j.model.azure.AzureOpenAiChatModel> azureOpenAiChatModels() {
        Map<String, dev.langchain4j.model.azure.AzureOpenAiChatModel> chatModels = new HashMap<>();
        models.forEach((key, config) -> {
            chatModels.put(config.getDeploymentName(), dev.langchain4j.model.azure.AzureOpenAiChatModel.builder()
                    .apiKey(config.getApiKey())
                    .endpoint(config.getEndpoint())
                    .deploymentName(config.getDeploymentName())
                    .logRequestsAndResponses(true)
                    .build());
        });
        return chatModels;
    }

    @Data
    public static class AzureModelConfig {
        private String apiKey;
        private String endpoint;
        private String deploymentName;
    }
}
