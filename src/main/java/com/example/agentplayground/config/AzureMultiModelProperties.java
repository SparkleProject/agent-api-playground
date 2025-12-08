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

    @Data
    public static class AzureModelConfig {
        private String apiKey;
        private String endpoint;
        private String deploymentName;
    }
}
