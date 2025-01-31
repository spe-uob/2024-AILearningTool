package com.UoB.AILearningTool;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Service;

@Service
public class OpenAIAPIController {
    private final Logger log = LoggerFactory.getLogger(OpenAIAPIController.class);
    private final OpenAIAuthenticator authenticator;

    public OpenAIAPIController() {
        this.authenticator = new OpenAIAuthenticator();
    }

    // Sends a message to OpenAI's ChatGPT API.
    public WatsonxResponse sendUserMessage(String messageHistory) {
        String dataPayload = StringTools.watsonxToOpenAI(messageHistory);

        WatsonxResponse AIResponse;
        // Create HTTP request for OpenAI with API key in header and message history in request body.
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .headers("Content-Type", "application/json",
                         "Authorization", "Bearer " + authenticator.getBearerToken())
                .POST(HttpRequest.BodyPublishers.ofString(dataPayload))
                .build();

        // Try sending an HTTPS request to OpenAI API.
        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            Integer statusCode = response.statusCode();
            // If OpenAI API returned status 200, return the status code and the message.
            if (statusCode == 200) {
                String message = new JSONObject(response.body())
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                log.info("200 OpenAI response received.");

                AIResponse = new WatsonxResponse(statusCode, message);
            } else {
                // If OpenAI API request returned a status code other than 200, return error 500 and error message.
                log.warn("Non-200 OpenAI response received.");
                String message = new JSONObject(response.body())
                        .getJSONArray("error")
                        .getJSONObject(0)
                        .getString("message");
                AIResponse = new WatsonxResponse(500, message);
            }
        } catch (Exception e) {
            AIResponse = new WatsonxResponse(500, null);
            log.error("Exception {}\nHTTP status code: {}", e, 500);
        }
        return AIResponse;
    }
}
