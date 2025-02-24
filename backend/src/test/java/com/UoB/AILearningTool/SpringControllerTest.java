package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
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
    private UserRepository userRepository; // Mock UserRepository

    @InjectMocks
    private SpringController springController;

    @BeforeEach
    public void setUp() {
        assertNotNull(userRepository);
        assertNotNull(mockDBController);
    }

    @Test
    public void testRegisterUser_Success() {
        // 🔹 模拟用户注册信息
        Map<String, String> credentials = Map.of("username", "testUser", "password", "password123");

        // 🔹 模拟数据库行为：用户不存在，注册成功
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());
        when(mockDBController.addUser("testUser", "password123", true)).thenReturn("testUser");

        // 🔹 执行 `registerUser()`
        ResponseEntity<Map<String, Object>> response = springController.registerUser(credentials);

        // ✅ 验证返回值
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("success"));
        assertEquals(true, response.getBody().get("success"));

        // ✅ 验证数据库行为
        verify(mockDBController, times(1)).addUser("testUser", "password123", true);
        verify(userRepository, times(1)).findById("testUser");
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        // 🔹 模拟已存在的用户
        Map<String, String> credentials = Map.of("username", "existingUser", "password", "password123");

        when(userRepository.findById("existingUser")).thenReturn(Optional.of(new UserEntity("existingUser", "password123", true)));

        // 🔹 执行 `registerUser()`
        ResponseEntity<Map<String, Object>> response = springController.registerUser(credentials);

        // ✅ 断言返回状态
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Username already exists", response.getBody().get("message"));

        // ✅ 确保 `addUser()` **不会** 被调用
        verify(mockDBController, never()).addUser(anyString(), anyString(), anyBoolean());
    }
}
