package com.UoB.AILearningTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class ChatTest {

    @Test
    @DisplayName("Check if checkOwner method compares owners correctly.")
    public void checkOwnerTest() {
        User user = new User("testUser", true);
        Chat chat = new Chat(user, "This is a first message.");
        
        Assertions.assertTrue(chat.checkOwner(user));
    }

    @Test
    @DisplayName("Check if initial message history is saved correctly.")
    public void initialMessageHistoryTest() {
        String initialMessage = "This is a first message.";
        String expectedMessageHistory = "<|system|>\nYour name is AI Learning Tool, and you are an assistant for IBM SkillsBuild..."
                + "<|user|>\nThis is a first message.";
        
        User user = new User("testUser", true);
        Chat chat = new Chat(user, initialMessage);
        
        String actualMessageHistory = chat.getMessageHistory(user);
        Assertions.assertEquals(expectedMessageHistory, actualMessageHistory);
    }

    @Test
    @DisplayName("Check if user messages are added to message history correctly.")
    public void addUserMessageTest() {
        String initialMessage = "This is a first message.";
        String expectedMessageHistory = "<|system|>\nYour name is AI Learning Tool, and you are an assistant for IBM SkillsBuild..."
                + "<|user|>\nThis is a first message.\n<|user|>\nTell me a joke.";

        String extraUserMessage = "Tell me a joke.";
        
        User user = new User("testUser", true);
        Chat chat = new Chat(user, initialMessage);
        
        chat.addUserMessage(user.getID(), extraUserMessage);
        
        String actualMessageHistory = chat.getMessageHistory(user);
        Assertions.assertEquals(expectedMessageHistory, actualMessageHistory);
    }

    @Test
    @DisplayName("Check if AI messages are added to message history correctly.")
    public void addAIMessageTest() {
        String initialMessage = "This is a first message.";
        String expectedMessageHistory = "<|system|>\nYour name is AI Learning Tool, and you are an assistant for IBM SkillsBuild..."
                + "<|user|>\nThis is a first message.\n<|assistant|>\nTell me a joke.";
        
        String extraAIMessage = "Tell me a joke.";
        
        User user = new User("testUser", true);
        Chat chat = new Chat(user, initialMessage);
        
        chat.addAIMessage(user.getID(), extraAIMessage);
        
        String actualMessageHistory = chat.getMessageHistory(user);
        Assertions.assertEquals(expectedMessageHistory, actualMessageHistory);
    }

    @Test
    @DisplayName("Check if message history in a chat can only be accessed by its owner.")
    public void chatMessageHistoryPermissionTest() {
        final String initialMessage = "This is a first message.";
        String actualMessageHistory;
        User currentUser;
        Chat currentChat;
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Chat> chats = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            currentUser = new User("user" + i, true);
            users.add(currentUser);
            currentChat = new Chat(currentUser, initialMessage);
            chats.add(currentChat);
            Assertions.assertNotNull(currentChat.getMessageHistory(currentUser));
        }

        for (int i = 0; i < 20; i++) {
            currentChat = chats.get(i);
            for (int j = 0; j < 20; j++) {
                if (i == j) { continue; }
                currentUser = users.get(j);
                actualMessageHistory = currentChat.getMessageHistory(currentUser);
                Assertions.assertNull(actualMessageHistory);
            }
        }
    }
}
