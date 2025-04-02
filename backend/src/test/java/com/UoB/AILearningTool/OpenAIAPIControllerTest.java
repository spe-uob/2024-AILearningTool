package com.UoB.AILearningTool;

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
        controller = new OpenAIAPIController(); 
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
