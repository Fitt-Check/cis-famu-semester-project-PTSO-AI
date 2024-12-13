package com.example.ptsoai.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final String apiKey = "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";
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";

    public String getSuggestions(String imageDescription) {
        try {
            // Create request body
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "user", "content", "Please classify this outfit based of the image description, and based on that classification give suggest to make it more casual, business casual, and business professional: " + imageDescription)
                    ),
                    "max_tokens", 150,
                    "temperature", 0.7
            );

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            // Send request
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            // Parse response
            Map responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                    return message.get("content").trim();
                }
            }
            return "No suggestions generated.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating suggestions: " + e.getMessage();
        }
    }
}

