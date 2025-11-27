package com.example.agentplayground.controller;

import com.example.agentplayground.service.LangChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/langchain")
@RequiredArgsConstructor
public class LangChainController {

    private final LangChainService langChainService;

    @GetMapping("/models")
    public Set<String> getSupportedModels() {
        return langChainService.getSupportedModels();
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String model = payload.get("model");
        String message = payload.get("message");
        if (model == null || message == null) {
            throw new IllegalArgumentException("Model and message are required");
        }
        String response = langChainService.chat(model, message);
        return Map.of("response", response);
    }
}
