package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    // Creates a new empty thread
    public String createThread() {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://api.openai.com/v1/threads"))
                .headers("Content-Type", "application/json",
                         "OpenAI-Beta", "assistants=v2",
                        "Authorization", "Bearer " + authenticator.getBearerToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                log.info("New OpenAI thread created. {}", new JSONObject(response.body()).getString("id"));
                return new JSONObject(response.body()).getString("id");
            } else {
                // If OpenAI API request returned a status code other than 200, return error 500 and error message.
                log.warn("Unable to create OpenAI thread., {}", statusCode);
                return null;
            }
        } catch (Exception e) {
            log.error("Exception {}\n", e.toString());
            return null;
        }
    }

    // Run OpenAI thread.
    public int runThread(String threadID) {
        JSONObject runRequestBody = new JSONObject();
        runRequestBody.put("assistant_id", "asst_bVvnON3FADuXiLKbnihGY1iH");
        HttpRequest runRequest = HttpRequest
                .newBuilder(URI.create("https://api.openai.com/v1/threads/" + threadID + "/runs"))
                .headers("Content-Type", "application/json",
                        "Authorization", "Bearer " + authenticator.getBearerToken(),
                        "OpenAI-Beta", "assistants=v2")
                .POST(HttpRequest.BodyPublishers.ofString(runRequestBody.toString()))
                .build();
        try {
            HttpResponse<String> runResponse = HttpClient
                    .newBuilder()
                    .build()
                    .send(runRequest, HttpResponse.BodyHandlers.ofString());

            if (runResponse.statusCode() == 200) {
                log.info("Thread run initialised.");
                return 200;
            } else {
                log.warn("Unable to run a thread.");
                return runResponse.statusCode();
            }

        } catch (Exception e) {
            log.error("Exception {}\n", e.toString());
            return 500;
        }
    }

    // Check if a run is in progress for a thread
    public boolean isLocked(ChatEntity chat) {
        HttpRequest listMessagesRequest = HttpRequest
                .newBuilder(URI.create("https://api.openai.com/v1/threads/" + chat.getThreadID() + "/runs"))
                .headers("Content-Type", "application/json",
                        "Authorization", "Bearer " + authenticator.getBearerToken(),
                        "OpenAI-Beta", "assistants=v2")
                .GET()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(listMessagesRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String status = new JSONObject(response.body()).getJSONArray("data").getJSONObject(0).getString("status");
                return !status.equals("completed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    // Receive the latest message from the thread
    public WatsonxResponse getLastThreadMessage(String threadID) {
        String requestURL = "https://api.openai.com/v1/threads/" + threadID + "/messages?order=desc&limit=1";
        HttpRequest listMessagesRequest = HttpRequest
                .newBuilder(URI.create(requestURL))
                .headers("Content-Type", "application/json",
                        "Authorization", "Bearer " + authenticator.getBearerToken(),
                        "OpenAI-Beta", "assistants=v2")
                .GET()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(listMessagesRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new WatsonxResponse(
                        200,
                        new JSONObject(response.body())
                                .getJSONArray("data")
                                .getJSONObject(0)
                                .getJSONArray("content")
                                .getJSONObject(0)
                                .getJSONObject("text")
                                .getString("value")
                        );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new WatsonxResponse(404, "Unable to get last thread message.");
    }

    // Add a message to an OpenAI thread.
    public Integer sendUserMessage(ChatEntity chat, String message) {
        // Creating a request body
        JSONObject newMessage = new JSONObject();
        newMessage.put("role", "user");
        newMessage.put("content", message);

        // Create HTTP request for OpenAI with API key in header and message history in request body.
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://api.openai.com/v1/threads/" + chat.getThreadID() + "/messages"))
                .headers("Content-Type", "application/json",
                         "Authorization", "Bearer " + authenticator.getBearerToken(),
                         "OpenAI-Beta", "assistants=v2")
                .POST(HttpRequest.BodyPublishers.ofString(newMessage.toString()))
                .build();

        // Try sending an HTTPS request to OpenAI API.
        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                log.info("200 OpenAI message has been created.");
                return 200;
            } else {
                return 412;
            }
        } catch (Exception e) {
            log.error("Exception {}\nHTTP status code: {}", e, 500);
            return 500;
        }
    }
}
