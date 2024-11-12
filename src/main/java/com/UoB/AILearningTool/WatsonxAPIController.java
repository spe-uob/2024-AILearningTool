package com.UoB.AILearningTool;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.header.Header;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class WatsonxAPIController {
    private final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final IBMAuthenticator authenticator;

    final static String technicalDataPayload = """
    "parameters": {
        "decoding_method": "greedy",
        "max_new_tokens": 900,
        "min_new_tokens": 0,
        "stop_sequences": [],
        "repetition_penalty": 1.05
    },
    "model_id": "ibm/granite-13b-chat-v2",
    "project_id": "30726e85-a528-491c-83a3-eee53797f0da"
    }
    """;

    public WatsonxAPIController() {
        this.authenticator = new IBMAuthenticator();
        this.authenticator.start();
        this.authenticator.requestNewToken();
    }

    public void stopAuthenticator(){
        this.authenticator.stopTimer();
    }

    // Sends a message to Watsonx API.
    public WatsonxResponse sendUserMessage(String preparedMessageHistory) {
        String dataPayload = preparedMessageHistory + technicalDataPayload;

        WatsonxResponse WXResponse;
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://eu-gb.ml.cloud.ibm.com/ml/v1/text/generation?version=2023-05-29"))
                .headers("Content-Type", "application/json",
                         "Accept", "application/json",
                         "Authorization", ("Bearer " + authenticator.getBearerToken()) )
                .POST(HttpRequest.BodyPublishers.ofString(dataPayload)).build();

        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            Integer statusCode = response.statusCode();
            if (statusCode == 200) {
                String message = new JSONObject(response.body())
                        .getJSONArray("results")
                        .getJSONObject(0)
                        .getString("generated_text");
                log.info("200 Watsonx response received.");

                WXResponse = new WatsonxResponse(statusCode, message);
            } else {
                log.warn("Non-200 Watsonx response received.");
                String message = new JSONObject(response.body())
                        .getJSONArray("errors")
                        .getJSONObject(0)
                        .getString("message");
                WXResponse = new WatsonxResponse(500, message);
            }
        } catch (Exception e) {
            WXResponse = new WatsonxResponse(500, null);
            log.error("Exception {}\nHTTP status code: {}", e, 500);
        }
        return WXResponse;
    }
}
