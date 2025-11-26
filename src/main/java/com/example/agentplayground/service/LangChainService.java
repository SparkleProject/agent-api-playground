package com.example.agentplayground.service;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LangChainService {

    private final ChatLanguageModel openAiChatModel;
    private final ChatLanguageModel vertexAiGeminiChatModel;
    private final ChatLanguageModel anthropicChatModel;

    @Autowired
    public LangChainService(OpenAiChatModel openAiChatModel,
            VertexAiGeminiChatModel vertexAiGeminiChatModel,
            AnthropicChatModel anthropicChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.vertexAiGeminiChatModel = vertexAiGeminiChatModel;
        this.anthropicChatModel = anthropicChatModel;
    }

    public String chatWithOpenAi(String message) {
        return openAiChatModel.generate(message);
    }

    public String chatWithGemini(String message) {
        return vertexAiGeminiChatModel.generate(message);
    }

    public String chatWithClaude(String message) {
        return anthropicChatModel.generate(message);
    }
}
