package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.ChatRepository;
import com.UoB.AILearningTool.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private DatabaseController databaseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser() {
        UserEntity user = new UserEntity("testUser", "password123", true);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        String result = databaseController.addUser("testUser", "password123", true);
        assertEquals("testUser", result);
    }

    @Test
    void testGetUser() {
        UserEntity user = new UserEntity("testUser", "password123", true);
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));

        UserEntity result = databaseController.getUser("testUser");
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testRemoveUser() {
        when(userRepository.existsById("testUser")).thenReturn(true);
        doNothing().when(userRepository).deleteById("testUser");

        boolean result = databaseController.removeUser("testUser");
        assertTrue(result);
    }

    @Test
    void testCreateChat() {
        UserEntity user = new UserEntity("testUser", "password123", true);
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));

        ChatEntity chat = new ChatEntity(user, "Hello AI!", "mockSessionID"); 
        when(chatRepository.save(any(ChatEntity.class))).thenReturn(chat);

        ChatEntity createdChat = databaseController.createChat("mockSessionID", "Hello AI!");
        assertNotNull(createdChat);
        assertEquals("mockSessionID", createdChat.getSessionID());
    }
}
