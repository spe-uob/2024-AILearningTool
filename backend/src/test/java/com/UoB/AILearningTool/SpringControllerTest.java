package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringControllerTest {
    @Mock
    private DatabaseController mockDBController;

    @Mock
    private OpenAIAPIController mockWXC;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private UserRepository userRepository; // Mock UserRepository

    @InjectMocks
    private SpringController springController;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @BeforeEach
    public void setUp() {
        assertNotNull(userRepository);
    }

    @Test
    public void testRegisterUser() {
        Map<String, String> credentials = Map.of("username", "testUser", "password", "password123");

        when(mockDBController.addUser("testUser", "password123", true)).thenReturn("testUser");
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = springController.registerUser(credentials, mockResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("success"));
        assertEquals(true, response.getBody().get("success"));

        verify(mockDBController, times(1)).addUser("testUser", "password123", true);
        verify(mockResponse, times(1)).addCookie(cookieCaptor.capture());

        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("userID", capturedCookie.getName());
        assertEquals("testUser", capturedCookie.getValue());
        assertEquals(30 * 24 * 60 * 60, capturedCookie.getMaxAge());
    }
}
