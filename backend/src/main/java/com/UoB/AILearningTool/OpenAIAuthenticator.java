package com.UoB.AILearningTool;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAIAuthenticator {
    private final Logger log = LoggerFactory.getLogger(OpenAIAuthenticator.class);

    private String apiKey;

    // Constructor that initializes the API key
    public OpenAIAuthenticator() {

          this.apiKey = "$OPENAI_API_KEY";
          log.info("OpenAI API key set.");

    }

    // Returns the API key as a "Bearer token" (used in authorization headers)
    public String getBearerToken() {
      if (this.apiKey == null || this.apiKey.equals("OPENAI_API_KEY") || this.apiKey.isEmpty()) {
        log.error("API Key not set or is empty.");
        throw new IllegalStateException("API Key for OpenAI is not set.");
      }
      return this.apiKey;
    }
}
