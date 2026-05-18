package com.example.agentplayground.controller;

import com.example.agentplayground.service.AzureAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/azure")
@RequiredArgsConstructor
public class AzureAiController {

    private final AzureAiService azureAiService;

    @GetMapping("/models")
    public Set<String> getSupportedModels() {
        return azureAiService.getSupportedModels();
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String model = payload.get("model");
        String message = payload.get("message");
        String previousResponse = payload.get("previousResponse");
        String previousRequest = payload.get("previousRequest");
        if (model == null || message == null) {
            throw new IllegalArgumentException("Model and message are required");
        }

        String response = azureAiService.chatWithAzureOpenAiModel(model, message, previousRequest, previousResponse);
        return Map.of("response", response);
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChatEvent(@RequestBody Map<String, String> payload) {
        String model = payload.get("model");
        String message = payload.get("message");
        String previousResponse = payload.get("previousResponse");
        String previousRequest = payload.get("previousRequest");

        return azureAiService.streamChatEvent(model, message, previousRequest, previousResponse);
    }
}
