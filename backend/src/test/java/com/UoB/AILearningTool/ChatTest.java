package com.UoB.AILearningTool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatTest {
    private Chat chat;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUser", true);
        chat = new Chat(user, "Hello!");
    }

    @Test
    void testCheckOwner() {
        assertTrue(chat.checkOwner(user));
    }

    @Test
    void testAddUserMessage() {
        assertTrue(chat.addUserMessage(user.getID(), "New message"));
    }

    @Test
    void testAddAIMessage() {
        assertTrue(chat.addAIMessage(user.getID(), "AI response"));
    }

    @Test
    void testGetMessageHistory() {
        assertNotNull(chat.getMessageHistory(user));
    }
}
