package com.UoB.AILearningTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@DisplayName("Testing OpenAI API Controller for response handling")
public class OpenAIAPIControllerTest {

    @Test
    @DisplayName("OpenAI API should be able to generate some response for initial chat.")
    public void createThreadTest() {
        OpenAIAPIController OAIC = new OpenAIAPIController();
        Assertions.assertNotNull(OAIC.createThread(), "Thread IDs must not be null");
    }

    @Test
    @DisplayName("OpenAI API should be able to generate some response for initial chat.")
    public void initialMessageHistoryRequestTest() {
        String newMessage = "I need help with finding online courses SkillsBuild.";
        OpenAIAPIController OAIC = new OpenAIAPIController();
        DatabaseController DBC = new DatabaseController();
        User user = DBC.getUser(DBC.addUser(true));
        Chat chat = DBC.getChat(user, DBC.createChat(user, newMessage, OAIC.createThread()));

        Integer response = OAIC.sendUserMessage(chat, newMessage);
        OAIC.runThread(chat.getThreadID());
        WatsonxResponse AIResponse = OAIC.getLastThreadMessage(chat.getThreadID());

        Assertions.assertNotNull(AIResponse.responseText, "The message content should not be null.");
        Assertions.assertEquals(200, AIResponse.statusCode, "The status code should be 200 for a successful response.");
    }
//    TODO: Refactor this test
//    @Test
//    @DisplayName("OpenAI API should be able to generate response for chat history with multiple messages.")
//    public void developedMessageHistoryRequestTest() {
//        OpenAIAPIController OAIC = new OpenAIAPIController();
//        DatabaseController DBC = new DatabaseController();
//        String userID = DBC.addUser(true);
//        String chatID = DBC.createChat(DBC.getUser(userID), "I need some help with finding an online course.", OAIC.createThread());
//
//        WatsonxResponse response = openAIAPIController.sendUserMessage(messageHistory);
//
//        Assertions.assertNotNull(response.responseText, "The message content should not be null.");
//        Assertions.assertEquals(200, response.statusCode, "The status code should be 200 for a successful response.");
//    }
}
