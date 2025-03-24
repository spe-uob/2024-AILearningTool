package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
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
        UserEntity user = new UserEntity("testUserLogin1", "testUserPassword1", true);
        ChatEntity chat = new ChatEntity(user, "Hello there!", OAIC.createThread());

        Integer response = OAIC.sendUserMessage(chat, newMessage);
        Assertions.assertEquals(200, response);
        response = OAIC.runThread(chat.getThreadID());
        Assertions.assertEquals(200, response);
        // Wait until any runs for a thread are completed
        while (OAIC.isLocked(chat)) {
            continue;
        }
        WatsonxResponse AIResponse = OAIC.getLastThreadMessage(chat.getThreadID());


        Assertions.assertNotNull(AIResponse.responseText, "The message content should not be null.");
        Assertions.assertNotEquals(AIResponse.responseText, newMessage);
        Assertions.assertEquals(200, AIResponse.statusCode, "The status code should be 200 for a successful response.");
    }
}
