package com.example.agentplayground.controller;

import com.example.agentplayground.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/openai/chat")
    public Map<String, String> chatOpenAi(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response = aiService.chatWithOpenAi(message);
        return Map.of("response", response);
    }

    @PostMapping("/gemini/chat")
    public Map<String, String> chatGemini(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response = aiService.chatWithGemini(message);
        return Map.of("response", response);
    }

    @PostMapping("/claude/chat")
    public Map<String, String> chatClaude(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String response = aiService.chatWithClaude(message);
        return Map.of("response", response);
    }
}
