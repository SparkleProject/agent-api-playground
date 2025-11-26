package com.example.agentplayground.controller;

import com.example.agentplayground.service.LangChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/langchain")
@RequiredArgsConstructor
public class LangChainController {

    private final LangChainService langChainService;

    @PostMapping("/openai/chat")
    public Map<String, String> chatOpenAi(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response = langChainService.chatWithOpenAi(message);
        return Map.of("response", response);
    }

    @PostMapping("/gemini/chat")
    public Map<String, String> chatGemini(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response = langChainService.chatWithGemini(message);
        return Map.of("response", response);
    }

    @PostMapping("/claude/chat")
    public Map<String, String> chatClaude(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response = langChainService.chatWithClaude(message);
        return Map.of("response", response);
    }
}
