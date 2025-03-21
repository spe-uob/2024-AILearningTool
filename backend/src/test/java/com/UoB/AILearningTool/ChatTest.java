package com.UoB.AILearningTool;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class ChatTest {
    private OpenAIAPIController OAIC;
    private String mockThreadId = "test-thread-id";

    @BeforeEach
    void setUp() {
        OAIC = new OpenAIAPIController();
    }

    @Test
    @DisplayName("Check if checkOwner method compares owners correctly.")
    public void checkOwnerTest() {
        User user = new User("testUser", true);  
        Chat chat = new Chat(user, "This is a first message.", mockThreadId);

        Assertions.assertTrue(chat.checkOwner(user));
    }

    @Test
    @DisplayName("Check if initial message history is saved correctly.")
    public void initialMessageHistoryTest() {
        String initialMessage = "This is a first message.";
        User user = new User("testUser2", true);  
        Chat chat = new Chat(user, initialMessage, mockThreadId);

        JSONArray expectedMessageHistory = new JSONArray();
        JSONObject onlyMessage = new JSONObject();
        onlyMessage.put("role", "user");
        onlyMessage.put("content", initialMessage);
        expectedMessageHistory.put(onlyMessage);

        JSONArray actualMessageHistory = chat.getMessageHistory(user);
        Assertions.assertEquals(expectedMessageHistory.toString(), actualMessageHistory.toString());
    }

    @Test
    @DisplayName("Check if messages are added to message history correctly.")
    public void addUserMessageTest() {
        String initialMessage = "This is a first message.";
        String extraUserMessage = "Tell me a joke.";
        User user = new User("testUser3", true);  
        Chat chat = new Chat(user, initialMessage, mockThreadId);

        // Create expected message history.
        JSONArray expectedMessageHistory = new JSONArray();
        JSONObject onlyMessage = new JSONObject();
        onlyMessage.put("role", "user");
        onlyMessage.put("content", initialMessage);
        expectedMessageHistory.put(onlyMessage);
        onlyMessage = new JSONObject();
        onlyMessage.put("role", "user");
        onlyMessage.put("content", extraUserMessage);
        expectedMessageHistory.put(onlyMessage);

        chat.addUserMessage(user.getID(), extraUserMessage);

        JSONArray actualMessageHistory = chat.getMessageHistory(user);
        Assertions.assertEquals(2, expectedMessageHistory.length());
        Assertions.assertEquals("user", actualMessageHistory.getJSONObject(0).getString("role"));
        Assertions.assertEquals(initialMessage, actualMessageHistory.getJSONObject(0).getString("content"));
        Assertions.assertEquals("user", actualMessageHistory.getJSONObject(1).getString("role"));
        Assertions.assertEquals(extraUserMessage, actualMessageHistory.getJSONObject(1).getString("content"));
    }

    @Test
    @DisplayName("Check if AI messages are added to message history correctly.")
    public void addAIMessageTest() {
        String initialMessage = "I need some assistance with finding courses on IBM SkillsBuild platform.";
        String aiResponse = "Here's a helpful response";
        User user = new User("testUser4", true);  
        Chat chat = new Chat(user, initialMessage, mockThreadId);

        chat.addAIMessage(user.getID(), aiResponse);

        JSONArray actualMessageHistory = chat.getMessageHistory(user);
        Assertions.assertEquals(2, actualMessageHistory.length());
        Assertions.assertEquals("user", actualMessageHistory.getJSONObject(0).getString("role"));
        Assertions.assertEquals("assistant", actualMessageHistory.getJSONObject(1).getString("role"));
        Assertions.assertNotNull(actualMessageHistory.getJSONObject(0).getString("content"));
        Assertions.assertEquals(aiResponse, actualMessageHistory.getJSONObject(1).getString("content"));
    }

    @Test
    @DisplayName("Check if message history in a chat can only be accessed by its owner.")
    public void chatMessageHistoryPermissionTest() {
        final String initialMessage = "This is a first message.";
        User currentUser;
        Chat currentChat;
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Chat> chats = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            currentUser = new User("testUser" + i, true);  
            users.add(currentUser);
            currentChat = new Chat(currentUser, initialMessage, mockThreadId);
            chats.add(currentChat);
            Assertions.assertNotNull(currentChat.getMessageHistory(currentUser));
        }

        for (int i = 0; i < 20; i++) {
            currentChat = chats.get(i);
            for (int j = 0; j < 20; j++) {
                if (i == j) {continue;}
                currentUser = users.get(j);
                JSONArray actualMessageHistory = currentChat.getMessageHistory(currentUser);
                Assertions.assertNull(actualMessageHistory);
            }
        }
    }
}
