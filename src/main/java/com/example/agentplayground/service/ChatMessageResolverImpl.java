package com.example.agentplayground.service;

import dev.langchain4j.data.message.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMessageResolverImpl implements ChatMessageResolver {

    private final ChatMessageGenerator<List<ChatMessage>> defaultChatMessageGenerator;
    private final ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorV2;
    private final ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorBeta;
    private final ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorBeta2;
    private final ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorBeta3;


    public ChatMessageResolverImpl(ChatMessageGenerator<List<ChatMessage>> defaultChatMessageGenerator,
                                   ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorV2,
                                   ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorBeta,
                                   ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorBeta2,
                                   ChatMessageGenerator<List<ChatMessage>> waveChatMessageGeneratorBeta3) {
        this.defaultChatMessageGenerator = defaultChatMessageGenerator;
        this.waveChatMessageGeneratorV2 = waveChatMessageGeneratorV2;
        this.waveChatMessageGeneratorBeta = waveChatMessageGeneratorBeta;
        this.waveChatMessageGeneratorBeta2 = waveChatMessageGeneratorBeta2;
        this.waveChatMessageGeneratorBeta3 = waveChatMessageGeneratorBeta3;
    }

    @Override
    public List<ChatMessage> resolve(String modelName, String message, String previousRequest, String previousResponse) {
        if(modelName!=null && modelName.contains("wave")){
            return waveChatMessageGeneratorBeta3.generate(message, previousRequest, previousResponse);
        }
        return defaultChatMessageGenerator.generate(message,  previousRequest,  previousResponse);
    }
}
