package com.example.agentplayground.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AzureAiService {

    private final ChatMessageResolver chatMessageResolver;
    private final Map<String, AzureOpenAiChatModel> models;
    private final Map<String, StreamingChatModel> streamingChatModels;
    private final Map<String, OpenAiChatModel> openChatModels;

    private final AzureOpenAiStreamingChatModel azureOpenAiStreamingChatModel;

    @Autowired
    public AzureAiService(ChatMessageResolver chatMessageResolver,
                          @Value("#{springAiChatModels}") Map<String, AzureOpenAiChatModel> models,
                          Map<String, StreamingChatModel> streamingChatModels, Map<String, OpenAiChatModel> openChatModels,
                          AzureOpenAiStreamingChatModel azureOpenAiStreamingChatModel) {
        this.chatMessageResolver = chatMessageResolver;
        this.models = models;
        this.streamingChatModels = streamingChatModels;
        this.openChatModels = openChatModels;
        this.azureOpenAiStreamingChatModel = azureOpenAiStreamingChatModel;
    }

    public Set<String> getSupportedModels() {
        return models.keySet();
    }

    public String chatWithAzureOpenAiModel(String modelName, String userInput, String previousRequest, String previousResponse) {
        AzureOpenAiChatModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model or missing credentials: " + modelName);
        }
        List<ChatMessage> messages = createMessage(modelName, userInput, previousRequest, previousResponse);
        return model.chat(messages).aiMessage().text();
    }

    public String chatWithOpenAiModel(String modelName, String userInput, String previousRequest, String previousResponse) {
        OpenAiChatModel model = openChatModels.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported model or missing credentials: " + modelName);
        }
        List<ChatMessage> messages = createMessage(modelName, userInput, previousRequest, previousResponse);
        return model.chat(messages).aiMessage().text();
    }

    public SseEmitter streamChatEvent(String modelName, String userInput, String previousRequest, String previousResponse) {
        List<ChatMessage> messages = createMessage(modelName, userInput, previousRequest, previousResponse);
        SseEmitter emitter = new SseEmitter(180_000L);

        Thread.startVirtualThread(() -> {
            try {
                azureOpenAiStreamingChatModel.chat(messages, new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String s) {
                        try {
                            emitter.send(s);
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse chatResponse) {
                        emitter.complete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        emitter.completeWithError(throwable);
                    }
                });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private List<ChatMessage> createMessage(String modelName, String userInput, String previousRequest, String previousResponse) {
        return chatMessageResolver.resolve(modelName, userInput, previousRequest, previousResponse);
    }
}
