package com.example.agentplayground.controller;

import com.example.agentplayground.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @GetMapping("/models")
    public Set<String> getSupportedModels() {
        return aiService.getSupportedModels();
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String model = payload.get("model");
        String message = payload.get("message");
        if (model == null || message == null) {
            throw new IllegalArgumentException("Model and message are required");
        }
        String response = aiService.chat(model, message);
        return Map.of("response", response);
    }
}
