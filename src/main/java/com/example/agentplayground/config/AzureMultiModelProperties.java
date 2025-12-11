package com.example.agentplayground.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import lombok.Data;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
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
    public Map<String, AzureOpenAiChatModel> springAiChatModels() {
        Map<String, AzureOpenAiChatModel> chatModels = new HashMap<>();
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

                OpenAIClient openAIClient = new OpenAIClientBuilder()
                        .endpoint(effectiveEndpoint)
                        .credential(new AzureKeyCredential(effectiveApiKey))
                        .buildClient();

                AzureOpenAiChatOptions options = AzureOpenAiChatOptions
                        .builder()
                        .withDeploymentName(effectiveDeploymentName)
                        .build();

                chatModels.put(effectiveDeploymentName,
                        new AzureOpenAiChatModel(openAIClient, options));
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
