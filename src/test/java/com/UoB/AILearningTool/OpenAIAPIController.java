package com.UoB.AILearningTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testing OpenAI API Controller for response handling")
public class OpenAIAPIControllerTest {

    @Test
    @DisplayName("Single request test")
    public void singleRequestTest() {
        OpenAIAPIController openAIAPIController = new OpenAIAPIController();
        openAIAPIController.authenticator.requestNewToken();  // Ensure token is available
        
        String preparedMessageHistory = "{\"input\": \"Hello, how can you help me?\"}";
        OpenAIResponse response = openAIAPIController.sendUserMessage(preparedMessageHistory);
        
        Assertions.assertEquals(200, response.getStatusCode(), "The status code should be 200 for a successful response.");
        Assertions.assertNotNull(response.getMessage(), "The message content should not be null.");
    }

    @Test
    @DisplayName("Multiple requests test")
    public void multiRequestTest() {
        OpenAIAPIController openAIAPIController = new OpenAIAPIController();
        openAIAPIController.authenticator.requestNewToken();  // Ensure token is available

        String preparedMessageHistory1 = "{\"input\": \"Tell me a joke.\"}";
        OpenAIResponse response1 = openAIAPIController.sendUserMessage(preparedMessageHistory1);
        
        String preparedMessageHistory2 = "{\"input\": \"What's the weather like today?\"}";
        OpenAIResponse response2 = openAIAPIController.sendUserMessage(preparedMessageHistory2);

        Assertions.assertEquals(200, response1.getStatusCode(), "First request should have a status code of 200.");
        Assertions.assertEquals(200, response2.getStatusCode(), "Second request should also have a status code of 200.");
        Assertions.assertNotNull(response1.getMessage(), "The first response message should not be null.");
        Assertions.assertNotNull(response2.getMessage(), "The second response message should not be null.");
        Assertions.assertNotEquals(response1.getMessage(), response2.getMessage(), "The responses for different inputs should differ.");
    }
}
