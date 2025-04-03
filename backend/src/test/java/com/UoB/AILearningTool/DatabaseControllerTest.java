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

public class DatabaseControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private DatabaseController databaseController;

    private UserEntity testUser;
    private ChatEntity testChat;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new UserEntity("testUser", "password123");
        testChat = new ChatEntity(testUser, "Hello, this is a test message.", "thread123");
    }

    @Test
    void testGetUser_UserExists() {
        // Given
        when(userRepository.findById("testUser")).thenReturn(Optional.of(testUser));

        // When
        UserEntity result = databaseController.getUser("testUser");

        // Then
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userRepository, times(1)).findById("testUser");
    }

    @Test
    void testGetUser_UserDoesNotExist() {
        // Given
        when(userRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        // When
        UserEntity result = databaseController.getUser("nonExistentUser");

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById("nonExistentUser");
    }

    @Test
    void testAddUser_UserAlreadyExists() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        // When
        boolean result = databaseController.addUser("testUser", "newPassword");

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("testUser");
    }


    @Test
    void testAddUser_UserDoesNotExist() {
        // Given
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());

        // When
        boolean result = databaseController.addUser("newUser", "newPassword");

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).findByUsername("newUser");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


    @Test
    void testCreateChat_Success() {
        
        when(userRepository.findBySessionID("validSessionID")).thenReturn(Optional.of(testUser));

        ChatEntity result = databaseController.createChat("validSessionID", "Initial message");

        assertNotNull(result);
        assertEquals("Initial message", new org.json.JSONArray(result.getMessageHistory(testUser).toString())
        .getJSONObject(0)
        .getString("content"));

        verify(chatRepository, times(1)).save(any(ChatEntity.class));
    }

    @Test
    void testCreateChat_Failure_UserNotFound() {
        
        when(userRepository.findBySessionID("invalidSessionID")).thenReturn(Optional.empty());
       
        ChatEntity result = databaseController.createChat("invalidSessionID", "Initial message");

        assertNull(result);
        verify(chatRepository, times(0)).save(any(ChatEntity.class));
    }

    @Test
    void testDeleteChat_Success() {
        
        when(chatRepository.findById("chat123")).thenReturn(Optional.of(testChat));
        
        boolean result = databaseController.deleteChat(testUser, "chat123");
        
        assertTrue(result);
        verify(chatRepository, times(1)).deleteById("chat123");
    }

    @Test
    void testDeleteChat_Failure_ChatNotFound() {
        
        when(chatRepository.findById("nonExistentChat")).thenReturn(Optional.empty());

        
        boolean result = databaseController.deleteChat(testUser, "nonExistentChat");

               assertFalse(result);
        verify(chatRepository, times(0)).deleteById("nonExistentChat");
    }

    @Test
    void testDeleteChat_Failure_NotOwner() {

        UserEntity anotherUser = new UserEntity("anotherUser", "password123");
        ChatEntity anotherChat = new ChatEntity(anotherUser, "Another user's message", "thread123");
        when(chatRepository.findById("chat123")).thenReturn(Optional.of(anotherChat));

        boolean result = databaseController.deleteChat(testUser, "chat123");

        assertFalse(result);
        verify(chatRepository, times(0)).deleteById("chat123");
    }
}
