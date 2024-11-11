package com.UoB.AILearningTool;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAIAPIController {
    private final Logger log = LoggerFactory.getLogger(OpenAIAPIController.class);
    private final OpenAIAuthenticator authenticator;

    final static String technicalDataPayload = """
    {
        "model": "gpt-4",
        "messages": [
            {"role": "system", "content": "You are an assistant."}
        ],
        "temperature": 0.7,
        "max_tokens": 900
    }
    """;

    public OpenAIAPIController() {
        this.authenticator = new OpenAIAuthenticator();
        this.authenticator.requestNewToken();
    }

    // Sends a message to OpenAI's ChatGPT API.
    public OpenAIResponse sendUserMessage(String preparedMessageHistory) {
        String dataPayload = preparedMessageHistory + technicalDataPayload;

        OpenAIResponse AIResponse;
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .headers("Content-Type", "application/json",
                         "Authorization", "Bearer " + authenticator.getBearerToken())
                .POST(HttpRequest.BodyPublishers.ofString(dataPayload))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            Integer statusCode = response.statusCode();
            if (statusCode == 200) {
                String message = new JSONObject(response.body())
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                log.info("200 OpenAI response received.");

                AIResponse = new OpenAIResponse(statusCode, message);
            } else {
                log.warn("Non-200 OpenAI response received.");
                String message = new JSONObject(response.body())
                        .getJSONArray("error")
                        .getJSONObject(0)
                        .getString("message");
                AIResponse = new OpenAIResponse(500, message);
            }
        } catch (Exception e) {
            AIResponse = new OpenAIResponse(500, null);
            log.error("Exception {}\nHTTP status code: {}", e, 500);
        }
        return AIResponse;
    }
}
