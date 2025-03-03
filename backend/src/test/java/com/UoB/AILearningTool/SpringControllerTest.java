package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SpringControllerTest {
    
    @MockBean
    private DatabaseController mockDBController;

    @MockBean
    private OpenAIAPIController mockWXC;

    @MockBean
    private UserRepository userRepository; 

    @InjectMocks
    private SpringController springController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); 
        assertNotNull(userRepository);
        assertNotNull(mockDBController);
        assertNotNull(springController);
    }

    @Test
    public void testRegisterUser_Success() {
        
        Map<String, String> credentials = Map.of("username", "testUser", "password", "password123");

        
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());

        when(mockDBController.addUser("testUser", "password123", true)).thenReturn("testUser");

        
        ResponseEntity<Map<String, Object>> response = springController.registerUser(credentials);

        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("success"));
        assertEquals(true, response.getBody().get("success"));

        
        verify(mockDBController, times(1)).addUser("testUser", "password123", true);
        verify(userRepository, times(1)).findById("testUser");
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        Map<String, String> credentials = Map.of("username", "existingUser", "password", "password123");


        when(userRepository.findById("existingUser"))
                .thenReturn(Optional.of(new UserEntity("existingUser", "password123", true)));


        ResponseEntity<Map<String, Object>> response = springController.registerUser(credentials);


        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Username already exists", response.getBody().get("message"));


        verify(mockDBController, never()).addUser(anyString(), anyString(), anyBoolean());
    }
}
