package com.example.agentplayground.config;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureLangChain4jConfig {

    @Value("${langchain4j.azure-open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.azure-open-ai.chat-model.endpoint}")
    private String endpoint;

    @Value("${langchain4j.azure-open-ai.chat-model.deployment-name}")
    private String deploymentName;

    @Bean
    @org.springframework.context.annotation.Primary
    public AzureOpenAiChatModel azureLangChainChatModel() {
        return AzureOpenAiChatModel.builder()
                .apiKey(apiKey)
                .endpoint(endpoint)
                .deploymentName(deploymentName)
                .build();
    }
}
