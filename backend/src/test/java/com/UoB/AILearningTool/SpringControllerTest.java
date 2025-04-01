package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringControllerTest {
    @Mock
    private DatabaseController mockDBC;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SpringController springController;

    private UserEntity testUser;

//    @BeforeEach
//    public void setUp() throws Exception {
//        MockitoAnnotations.openMocks(this);
//        testUser = new UserEntity("testUser", "password123");
//        springController = new SpringController(mockDBC, null);
//
//        Field userRepoField = SpringController.class.getDeclaredField("userRepository");
//        userRepoField.setAccessible(true);
//        userRepoField.set(springController, userRepository);
//    }
//
//    @Test
//    void testRegisterUser_Success() {
//        Map<String, String> credentials = Map.of("username", "testUser", "password", "password123");
//
//        when(userRepository.existsById("testUser")).thenReturn(false);
//        when(mockDBC.addUser("testUser", "password123", true)).thenReturn("testUser");
//
//        ResponseEntity<Map<String, Object>> response = springController.registerUser(credentials);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue(response.getBody().containsKey("success"));
//
//        verify(mockDBC, times(1)).addUser("testUser", "password123", true);
//    }
//
//    @Test
//    void testRegisterUser_UsernameExists() {
//        Map<String, String> credentials = Map.of("username", "existingUser", "password", "password123");
//
//        when(userRepository.existsById("existingUser")).thenReturn(true);
//
//        ResponseEntity<Map<String, Object>> response = springController.registerUser(credentials);
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertTrue(response.getBody().containsKey("message"));
//        assertEquals("Username already exists", response.getBody().get("message"));
//
//        verify(mockDBC, never()).addUser(anyString(), anyString(), anyBoolean());
//    }
//
//    @Test
//    void testRevokeConsent_Success() {
//        when(mockDBC.removeUser("testUser")).thenReturn(true);
//
//        ResponseEntity<Map<String, Object>> response = springController.revokeConsent("testUser");
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("User data deleted successfully.", response.getBody().get("message"));
//
//        verify(mockDBC, times(1)).removeUser("testUser");
//    }
//
//    @Test
//    void testRevokeConsent_Failure() {
//        when(mockDBC.removeUser("testUser")).thenReturn(false);
//
//        ResponseEntity<Map<String, Object>> response = springController.revokeConsent("testUser");
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertEquals("User not found", response.getBody().get("message"));
//
//        verify(mockDBC, times(1)).removeUser("testUser");
//    }
}
