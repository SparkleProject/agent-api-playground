package com.example.agentplayground.config;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;

@Configuration
public class AzureOpenAiConfig {

    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deploymentName;

    @Bean
    public OpenAIClient azureOpenAIClient() {
        return new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildClient();
    }

    @Bean
    public AzureOpenAiChatModel azureOpenAiChatModel(OpenAIClient openAIClient) {
        var options = AzureOpenAiChatOptions.builder()
                .withDeploymentName(deploymentName)
                .build();
        return new AzureOpenAiChatModel(openAIClient, options);
    }
}
