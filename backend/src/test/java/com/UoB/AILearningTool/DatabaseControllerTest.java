package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import com.UoB.AILearningTool.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private DatabaseController databaseController;

    @BeforeEach
    void setUp() {
        assertNotNull(userRepository);
        assertNotNull(chatRepository);
    }

    @Test
    void createUsers() {
        UserEntity mockUser = new UserEntity("testUser", "testPass", true);

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);
        when(userRepository.findById("testUser")).thenReturn(java.util.Optional.of(mockUser));

        String userId = databaseController.addUser("testUser", "testPass", true);

        assertNotNull(userId);
        assertEquals("testUser", userId);
    }


    @Test
    void createChats() {
       
        UserEntity mockUser = new UserEntity("testUser", "testPass", true);

        ChatEntity mockChat = new ChatEntity(mockUser, "Hello chatbot!");

      
        when(userRepository.findById("testUser")).thenReturn(java.util.Optional.of(mockUser));

        
        when(chatRepository.save(any(ChatEntity.class))).thenReturn(mockChat);

       
        String chatId = databaseController.createChat("testUser", "Hello chatbot!");

       
        assertNotNull(chatId);
    }

}
