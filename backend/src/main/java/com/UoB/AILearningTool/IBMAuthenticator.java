package com.UoB.AILearningTool;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IBMAuthenticator extends Thread {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger log = LoggerFactory.getLogger(SpringController.class);

    private String bearerToken;
    private Integer statusCode;

    // Returns a bearer token
    public String getBearerToken() {
        if (this.statusCode == null) {
            System.out.println("Token is not available yet. Requesting a new token.");
            requestNewToken();
        }
        return this.bearerToken;
    }

    // Return status code of the latest request
    public int getStatusCode() {
        if (this.statusCode == null) {
            requestNewToken();
        }
        return this.statusCode;
    }

    // Uses IBM IAM API key to receive a temporary Bearer token.
    public void requestNewToken() {
        final String API_KEY = "wXU_-wyEW1tG1S8n4T3-6eiZ70Pfc2WxqXwqExsorjDH";

        // Prepare a request with an API key to receive a Bearer token.
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://iam.cloud.ibm.com/identity/token"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                "grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=" + API_KEY
                        )
                ).build();

        try {
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            this.statusCode = response.statusCode();
            if (this.statusCode == 200) {
                this.bearerToken = new JSONObject(response.body()).getString("access_token");
                log.info("New Bearer token obtained.");
            }
        } catch (Exception e) {
            this.bearerToken = null;
            this.statusCode = 400;
            log.error("Exception {}\nHTTP status code: {}", e, this.statusCode.toString());
        }
    }

    // Finishes the current request and stops the scheduler.
    public void stopTimer() {
        this.scheduler.shutdown();
        log.info("Scheduler stopped.");
    }

    // Runs a scheduler that executes requestNewToken() in specified frequency.
    public void run() {
        this.scheduler.scheduleAtFixedRate(this::requestNewToken, 0, 58, TimeUnit.MINUTES);
    }
}
