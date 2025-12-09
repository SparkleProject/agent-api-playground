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

    private String apiKey;
    private String endpoint;
    private String deploymentName;
    private Map<String, AzureModelConfig> models = new HashMap<>();

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public Map<String, AzureModelConfig> getModels() {
        return models;
    }

    public void setModels(Map<String, AzureModelConfig> models) {
        this.models = models;
    }

    @org.springframework.context.annotation.Bean
    public Map<String, dev.langchain4j.model.azure.AzureOpenAiChatModel> azureOpenAiChatModels() {
        Map<String, dev.langchain4j.model.azure.AzureOpenAiChatModel> chatModels = new HashMap<>();
        models.forEach((key, config) -> {
            String effectiveApiKey = (config.getApiKey() != null && !config.getApiKey().isBlank()) ? config.getApiKey()
                    : this.apiKey;
            String effectiveEndpoint = (config.getEndpoint() != null && !config.getEndpoint().isBlank())
                    ? config.getEndpoint()
                    : this.endpoint;
            String effectiveDeploymentName = (config.getDeploymentName() != null
                    && !config.getDeploymentName().isBlank()) ? config.getDeploymentName() : this.deploymentName;

            if (effectiveApiKey != null && !effectiveApiKey.isBlank() &&
                    effectiveEndpoint != null && !effectiveEndpoint.isBlank() &&
                    effectiveDeploymentName != null && !effectiveDeploymentName.isBlank()) {
                chatModels.put(key, dev.langchain4j.model.azure.AzureOpenAiChatModel.builder()
                        .apiKey(effectiveApiKey)
                        .endpoint(effectiveEndpoint)
                        .deploymentName(effectiveDeploymentName)
                        .logRequestsAndResponses(true)
                        .build());
            }
        });
        return chatModels;
    }

    @org.springframework.context.annotation.Bean
    public Map<String, org.springframework.ai.azure.openai.AzureOpenAiChatModel> springAiChatModels() {
        Map<String, org.springframework.ai.azure.openai.AzureOpenAiChatModel> chatModels = new HashMap<>();
        models.forEach((key, config) -> {
            String effectiveApiKey = (config.getApiKey() != null && !config.getApiKey().isBlank()) ? config.getApiKey()
                    : this.apiKey;
            String effectiveEndpoint = (config.getEndpoint() != null && !config.getEndpoint().isBlank())
                    ? config.getEndpoint()
                    : this.endpoint;
            String effectiveDeploymentName = (config.getDeploymentName() != null
                    && !config.getDeploymentName().isBlank()) ? config.getDeploymentName() : this.deploymentName;

            if (effectiveApiKey != null && !effectiveApiKey.isBlank() &&
                    effectiveEndpoint != null && !effectiveEndpoint.isBlank() &&
                    effectiveDeploymentName != null && !effectiveDeploymentName.isBlank()) {

                com.azure.ai.openai.OpenAIClient openAIClient = new com.azure.ai.openai.OpenAIClientBuilder()
                        .endpoint(effectiveEndpoint)
                        .credential(new com.azure.core.credential.AzureKeyCredential(effectiveApiKey))
                        .buildClient();

                org.springframework.ai.azure.openai.AzureOpenAiChatOptions options = org.springframework.ai.azure.openai.AzureOpenAiChatOptions
                        .builder()
                        .withDeploymentName(effectiveDeploymentName)
                        .build();

                chatModels.put(key,
                        new org.springframework.ai.azure.openai.AzureOpenAiChatModel(openAIClient, options));
            }
        });
        return chatModels;
    }

    @Data
    public static class AzureModelConfig {
        private String apiKey;
        private String endpoint;
        private String deploymentName;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getDeploymentName() {
            return deploymentName;
        }

        public void setDeploymentName(String deploymentName) {
            this.deploymentName = deploymentName;
        }
    }
}
