package com.UoB.AILearningTool;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ChatTest {
    @Test
    @DisplayName("Check if checkOwner method compares owners correctly.")
    public void checkOwnerTest() {
        OpenAIAPIController OAIC = new OpenAIAPIController();
        User user = new User(true);
        Chat chat = new Chat(user, "This is a first message.", OAIC.createThread());

        Assertions.assertTrue(chat.checkOwner(user));
    }

    @Test
    @DisplayName("Check if initial message history is saved correctly.")
    public void initialMessageHistoryTest() {
        String initialMessage = "This is a first message.";
        OpenAIAPIController OAIC = new OpenAIAPIController();
        User user = new User(true);
        Chat chat = new Chat(user, initialMessage, OAIC.createThread());

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

        // Create a chat and add 2 user messages.
        User user = new User(true);
        OpenAIAPIController OAIC = new OpenAIAPIController();
        Chat chat = new Chat(user, initialMessage, OAIC.createThread());

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

        // Add a new user message to the chat
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
        OpenAIAPIController OAIC = new OpenAIAPIController();
        String initialMessage = "I need some assistance with finding courses on IBM SkillsBuild platform.";
        String extraUserMessage = "Tell me a joke.";

        // Create a chat
        User user = new User(true);
        Chat chat = new Chat(user, initialMessage, OAIC.createThread());

        // Add a new user message to the chat
        OAIC.runThread(chat.getThreadID());
        chat.addAIMessage(user.getID(), extraUserMessage);

        JSONArray actualMessageHistory = chat.getMessageHistory(user);
        Assertions.assertEquals(2, actualMessageHistory.length());
        Assertions.assertEquals("user", actualMessageHistory.getJSONObject(0).getString("role"));
        Assertions.assertEquals("assistant", actualMessageHistory.getJSONObject(1).getString("role"));
        Assertions.assertNotNull(actualMessageHistory.getJSONObject(0).getString("content"));
        Assertions.assertNotNull(actualMessageHistory.getJSONObject(1).getString("content"));
    }

    @Test
    @DisplayName("Check if message history in a chat can only be accessed by its owner.")
    public void chatMessageHistoryPermissionTest() {
        final String initialMessage = "This is a first message.";
        User currentUser;
        Chat currentChat;
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Chat> chats = new ArrayList<>();

        // Creating chats and checking whether their owners can access their message histories.
        for (int i = 0; i < 20; i++) {
            currentUser = new User(true);
            users.add(currentUser);
            currentChat = new Chat(currentUser, initialMessage, "TEST");
            chats.add(currentChat);
            // Message history has to be returned to its owner
            Assertions.assertNotNull(currentChat.getMessageHistory(currentUser));
        }

        for (int i = 0; i < 20; i++) {
            currentChat = chats.get(i);
            // Users that aren't owners of the chat will receive null instead of message history.
            for (int j = 0; j < 20; j++) {
                if (i == j) {continue;}
                currentUser = users.get(j);
                JSONArray actualMessageHistory  = currentChat.getMessageHistory(currentUser);
                Assertions.assertNull(actualMessageHistory);
            }
        }
    }
}
