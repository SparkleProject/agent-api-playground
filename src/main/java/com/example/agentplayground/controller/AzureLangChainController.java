package com.example.agentplayground.controller;

import com.example.agentplayground.service.AzureLangChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/azure-langchain")
@RequiredArgsConstructor
public class AzureLangChainController {

    private final AzureLangChainService azureLangChainService;

    @GetMapping("/models")
    public Set<String> getSupportedModels() {
        return azureLangChainService.getSupportedModels();
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String model = payload.get("model");
        String message = payload.get("message");
        if (model == null || message == null) {
            throw new IllegalArgumentException("Model and message are required");
        }
        String response = azureLangChainService.chat(model, message);
        return Map.of("response", response);
    }
}
