package com.UoB.AILearningTool;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@DisplayName("Checking the functionality of WatsonxAPIController.")
public class WatsonxAPIControllerTest {

    @Test
    @DisplayName("Response from Watsonx API with status code 200 and size > 0 can be received")
    public void watsonxSingleValidResponseTest() {
        final Logger log = LoggerFactory.getLogger(SpringController.class);

        final String unformattedTestInput = "<|system|>\nYou are Granite Chat, an AI language model developed by IBM. You are a cautious assistant. You carefully follow instructions. You are helpful and harmless and you follow ethical guidelines and promote positive behavior. You always respond to greetings (for example, hi, hello, g'\'day, morning, afternoon, evening, night, what\'s up, nice to meet you, sup, etc) with \"Hello! I am Granite Chat, created by IBM. How can I help you today?\". Please do not say anything else and do not start a conversation.\n<|user|>\nhello, please introduce yourself.";
        final String formattedTestInput = StringTools.messageHistoryPrepare(unformattedTestInput);

        WatsonxAPIController testWXController = new WatsonxAPIController();

        WatsonxResponse testMessage = testWXController.sendUserMessage(formattedTestInput);

        testWXController.stopAuthenticator();

        Assertions.assertEquals(200, testMessage.statusCode);
        Assertions.assertTrue(testMessage.responseText.length() > 1);
    }

    @Test
    @DisplayName("Multiple valid responses from Watsonx API with status code 200 and size > 0 can be received")
    public void watsonxMultiValidResponsesTest() {
        final Logger log = LoggerFactory.getLogger(SpringController.class);

        final String unformattedTestInput = "<|system|>\nYou are Granite Chat, an AI language model developed by IBM. You are a cautious assistant. You carefully follow instructions. You are helpful and harmless and you follow ethical guidelines and promote positive behavior. You always respond to greetings (for example, hi, hello, g'\'day, morning, afternoon, evening, night, what\'s up, nice to meet you, sup, etc) with \"Hello! I am Granite Chat, created by IBM. How can I help you today?\". Please do not say anything else and do not start a conversation.\n<|user|>\nhello, please introduce yourself.";
        final String formattedTestInput = StringTools.messageHistoryPrepare(unformattedTestInput);

        WatsonxAPIController testWXController = new WatsonxAPIController();

        for (int i = 1; i < 11; i++) {
            log.info("Valid response N{}/10 received", i);
            WatsonxResponse testMessage = testWXController.sendUserMessage(formattedTestInput);
            Assertions.assertEquals(200, testMessage.statusCode);
            Assertions.assertTrue(testMessage.responseText.length() > 1);
        }

        testWXController.stopAuthenticator();
    }

}
