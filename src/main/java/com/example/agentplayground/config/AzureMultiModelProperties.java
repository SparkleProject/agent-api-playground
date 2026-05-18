package com.example.agentplayground.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import dev.langchain4j.model.azure.AzureOpenAiStreamingChatModel;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ToolChoice;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;

import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "spring.ai.azure.openai")
@Data
public class AzureMultiModelProperties {


    private Map<String, AiModelConfig> models = new LinkedHashMap<>();

    public Map<String, AiModelConfig> getModels() {
        return models;
    }

    public void setModels(LinkedHashMap<String, AiModelConfig> models) {
        this.models = models;
    }

    @Bean
    public Map<String, AzureOpenAiChatModel> springAiChatModels() {
        Map<String, AzureOpenAiChatModel> chatModels = new LinkedHashMap<>();
        models.forEach((key, config) -> {
            String effectiveApiKey = config.getApiKey();
            String effectiveEndpoint = config.getEndpoint();
            String effectiveDeploymentName = config.getDeploymentName();

            if (effectiveApiKey != null && !effectiveApiKey.isBlank() &&
                    effectiveEndpoint != null && !effectiveEndpoint.isBlank() &&
                    effectiveDeploymentName != null && !effectiveDeploymentName.isBlank()) {

                String displayModelName = config.getDisplayName();
                ResponseFormat responseFormat = displayModelName.equals("gpt-wave")
                    ? ResponseFormat.JSON
                    : ResponseFormat.TEXT;

                AzureOpenAiChatModel azureOpenAiChatModel = AzureOpenAiChatModel
                    .builder()
                    .timeout(Duration.of(3, ChronoUnit.MINUTES))
                    .maxRetries(2)
                    .responseFormat(responseFormat)
                    .apiKey(effectiveApiKey)
                    .deploymentName(effectiveDeploymentName)
                    .endpoint(effectiveEndpoint)
                    .build();

                OpenAiChatModel chatModel = OpenAiChatModel
                    .builder()
                    .apiKey(effectiveApiKey)
                    .baseUrl(effectiveEndpoint)
                    .modelName(effectiveDeploymentName)
                    .responseFormat(responseFormat)
                    .timeout(Duration.of(3, ChronoUnit.MINUTES))
                    .build();


                chatModels.put(displayModelName, azureOpenAiChatModel);
            }
        });

        return chatModels;
    }

    @Bean
    public AzureOpenAiStreamingChatModel azureOpenAiStreamingChatModel() {
        return AzureOpenAiStreamingChatModel
            .builder()
            .timeout(Duration.of(3, ChronoUnit.MINUTES))
            .maxRetries(2)
            .responseFormat(ResponseFormat.JSON)
            .apiKey("${AZURE_OPENAI_API_KEY}")
            .endpoint("${AZURE_OPENAI_ENDPOINT}")
            .deploymentName("gpt-5.1")
            .build();
    }

    @Bean
    public Map<String, OpenAiChatModel> openAiChatModels() {
        Map<String, OpenAiChatModel> chatModels = new LinkedHashMap<>();
        models.forEach((key, config) -> {
            String effectiveApiKey = config.getApiKey();
            String effectiveEndpoint = config.getEndpoint();
            String effectiveDeploymentName = config.getDeploymentName();

            if (effectiveApiKey != null && !effectiveApiKey.isBlank() &&
                effectiveEndpoint != null && !effectiveEndpoint.isBlank() &&
                effectiveDeploymentName != null && !effectiveDeploymentName.isBlank()) {

                String displayModelName = config.getDisplayName();
                ResponseFormat responseFormat = displayModelName.equals("gpt-wave")
                    ? ResponseFormat.JSON
                    : ResponseFormat.TEXT;

                OpenAiChatModel chatModel = OpenAiChatModel
                    .builder()
                    .apiKey(effectiveApiKey)
                    .baseUrl(effectiveEndpoint)
                    .modelName(effectiveDeploymentName)
                    .responseFormat(responseFormat)
                    .timeout(Duration.of(3, ChronoUnit.MINUTES))
                    .build();


                chatModels.put(displayModelName, chatModel);
            }
        });

        return chatModels;
    }

    @Bean
    public Map<String, StreamingChatModel> streamingChatModels() {
        Map<String, StreamingChatModel> chatModels = new LinkedHashMap<>();
        models.forEach((key, config) -> {
            String effectiveApiKey = config.getApiKey();
            String effectiveEndpoint = config.getEndpoint();
            String effectiveDeploymentName = config.getDeploymentName();

            if (effectiveApiKey != null && !effectiveApiKey.isBlank() &&
                effectiveEndpoint != null && !effectiveEndpoint.isBlank() &&
                effectiveDeploymentName != null && !effectiveDeploymentName.isBlank()) {

                String displayModelName = config.getDisplayName();
                String reasoningEffort = config.getReasoningEffort() != null ? config.getReasoningEffort(): "low";

                StreamingChatModel streamingChatModel = createOpenAiStreamingChatModel(
                    effectiveEndpoint,
                    effectiveApiKey,
                    effectiveDeploymentName,
                    reasoningEffort,
                    displayModelName,
                    Optional.empty()
                );

                chatModels.put(displayModelName, streamingChatModel);
            }
        });
        return chatModels;
    }



    @Data
    public static class AiModelConfig {
        private String displayName;
        private String apiKey;
        private String endpoint;
        private String deploymentName;
        private String reasoningEffort;


        public void setReasoningEffort(String reasoningEffort) {
            this.reasoningEffort = reasoningEffort;
        }

        public String getReasoningEffort() {
            return reasoningEffort;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

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

    @SneakyThrows
    private static OpenAiStreamingChatModel createOpenAiStreamingChatModel(String baseUrl,
                                                                           String apiKey,
                                                                           String modelName,
                                                                           String reasoningEffort,
                                                                           String displayModelName,
                                                                           Optional<ToolChoice> toolChoice) {
        String effectiveApiKey = modelName.contains("gemini") ? generateAccessToken(): apiKey;

        final var openAiStreamingChatModelBuilder = OpenAiStreamingChatModel
            .builder()
            .timeout(Duration.of(3, ChronoUnit.MINUTES))
            .responseFormat(ResponseFormat.JSON)
            .strictJsonSchema(true)
            .reasoningEffort(reasoningEffort.toLowerCase()) // can be high, medium, low, or minimal
            .apiKey(effectiveApiKey)
            .baseUrl(baseUrl)
            .modelName(modelName);
    /* ONLY for information: The following Gemini specific parameters is the same as calling reasoningEffort(...)
        .customParameters(
            Map.of(
                "extra_body", Map.of(
                    "google", Map.of(
                        "thinking_config", Map.of(
                            // "thinking_level", "LOW" // same as reasoningEffort("low")
                            "thinking_level", reasoningEffort.toUpperCase()
                        )
                    )
                )
            )
        )
    */

        toolChoice.ifPresent(choice -> {
            // doesn't seem to make Gemini to write and execute python code. Need more tests to confirm.
            openAiStreamingChatModelBuilder.defaultRequestParameters(
                OpenAiChatRequestParameters.builder().toolChoice(choice).build()
            );
        });

        return openAiStreamingChatModelBuilder.build();
    }

    private static String generateAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        // Define the required scopes (e.g., full Cloud Platform access).
        // Scopes control the permissions of the access token.
        if (credentials.createScopedRequired()) {
            credentials = credentials.createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        }

        // Ensure the token is not expired, refreshing it if necessary.
        // This is especially useful in scenarios where the token might be null initially
        // or when it expires during a long-running process.
        credentials.refreshIfExpired();

        // Get the access token
        AccessToken token = credentials.getAccessToken();

        return token.getTokenValue();
    }


}
