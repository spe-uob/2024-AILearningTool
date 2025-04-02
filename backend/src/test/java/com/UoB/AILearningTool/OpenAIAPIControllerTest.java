package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenAIAPIControllerTest {

    private OpenAIAPIController controller;

    @BeforeEach
    void setup() {
        controller = new OpenAIAPIController(); // 没有依赖注入，直接构造
    }

    @SuppressWarnings("unchecked")
    @Test
    void createThreadTest() throws Exception {
        // mock static HttpClient.newBuilder().build() and send()
        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"id\": \"thread_abc123\"}");

        try (MockedStatic<HttpClient> mockedHttpClientStatic = mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);
            when(builder.build()).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            mockedHttpClientStatic.when(HttpClient::newBuilder).thenReturn(builder);

            String threadId = controller.createThread();
            assertNotNull(threadId, "Thread ID should not be null");
            assertEquals("thread_abc123", threadId);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void initialMessageHistoryRequestTest() throws Exception {
        // 准备 ChatEntity 和模拟数据
        ChatEntity mockChat = new ChatEntity();
        mockChat.setThreadID("thread_test");

        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        String jsonResponse = """
            {
              "data": [
                {
                  "content": [
                    {
                      "text": {
                        "value": "Hello from OpenAI!"
                      }
                    }
                  ]
                }
              ]
            }
        """;

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);

        try (MockedStatic<HttpClient> mockedHttpClientStatic = mockStatic(HttpClient.class)) {
            HttpClient.Builder builder = mock(HttpClient.Builder.class);
            when(builder.build()).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            mockedHttpClientStatic.when(HttpClient::newBuilder).thenReturn(builder);

            WatsonxResponse response = controller.getLastThreadMessage(mockChat.getThreadID());
            assertEquals(200, response.status());
            assertEquals("Hello from OpenAI!", response.message());
        }
    }
}


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
        UserEntity user = new UserEntity("testUserLogin1", "testUserPassword1");
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
