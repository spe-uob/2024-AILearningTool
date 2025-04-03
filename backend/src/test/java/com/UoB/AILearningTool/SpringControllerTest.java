package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.ChatRepository;
import com.UoB.AILearningTool.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private DatabaseController databaseController;

    @MockBean
    private OpenAIAPIController openAIAPIController;

    // regeister
    @Test
    void testRegisterUser_Success() throws Exception {
        Mockito.when(databaseController.addUser("testuser", "testpass")).thenReturn(true);

        String json = "{\"username\": \"testuser\", \"password\": \"testpass\"}";

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterUser_Fail_UserExists() throws Exception {
        Mockito.when(databaseController.addUser("testuser", "testpass")).thenReturn(false);

        String json = "{\"username\": \"testuser\", \"password\": \"testpass\"}";

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    // login
    @Test
    void testLoginUser_Success() throws Exception {
        UserEntity user = new UserEntity("testuser", "testpass");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(chatRepository.findByOwner(user)).thenReturn(Collections.emptyList());

        String json = "{\"username\": \"testuser\", \"password\": \"testpass\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionID").exists())
                .andExpect(jsonPath("$.chatIDs").isArray());
    }

    @Test
    void testLoginUser_Fail_WrongPassword() throws Exception {
        UserEntity user = new UserEntity("testuser", "correctpass");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        String json = "{\"username\": \"testuser\", \"password\": \"wrongpass\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    //session id check
    @Test
    void testCheckSession_Valid() throws Exception {
        Mockito.when(userRepository.findBySessionID("validSession")).thenReturn(Optional.of(new UserEntity()));

        mockMvc.perform(get("/checkSession")
                        .param("sessionID", "validSession"))
                .andExpect(status().isOk());
    }

    @Test
    void testCheckSession_Invalid() throws Exception {
        Mockito.when(userRepository.findBySessionID("invalidSession")).thenReturn(Optional.empty());

        mockMvc.perform(get("/checkSession")
                        .param("sessionID", "invalidSession"))
                .andExpect(status().isUnauthorized());
    }

    // revoke consent 
    @Test
    void testRevokeConsent_Success() throws Exception {
        Mockito.when(databaseController.removeUser("validSession")).thenReturn(true);

        String json = "{\"sessionID\": \"validSession\"}";
        mockMvc.perform(delete("/revokeConsent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testRevokeConsent_Fail() throws Exception {
        Mockito.when(databaseController.removeUser("invalidSession")).thenReturn(false);

        String json = "{\"sessionID\": \"validSession\"}";
        mockMvc.perform(delete("/revokeConsent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // creat chat
    @Test
    void testCreateChat_Success() throws Exception {
        UserEntity user = new UserEntity("testuser", "testpass");
        user.setSessionID("session123");
        ChatEntity chat = new ChatEntity(user, "hi", "thread123");

        Mockito.when(userRepository.findBySessionID("session123")).thenReturn(Optional.of(user));
        Mockito.when(openAIAPIController.createThread()).thenReturn("thread123");
        Mockito.when(openAIAPIController.sendUserMessage(any(), eq("hi"))).thenReturn(200);
        Mockito.when(openAIAPIController.runThread("thread123")).thenReturn(200);
        Mockito.when(openAIAPIController.isLocked(chat)).thenReturn(false);
        Mockito.when(openAIAPIController.getLastThreadMessage("thread123"))
                .thenReturn(new WatsonxResponse(200, "Hello there!"));

        String json = "{\"sessionID\": \"session123\", \"initialMessage\": \"hi\"}";

        mockMvc.perform(post("/createChat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatID").exists());
    }

    // get chat history
    @Test
    void testGetChatHistory_Success() throws Exception {
        UserEntity user = new UserEntity("testuser", "testpass");
        user.setSessionID("session123");
        JSONArray history = new JSONArray().put(new JSONObject().put("role", "user").put("content", "hello"));
        ChatEntity chat = new ChatEntity(user, "hello", "thread123");

        Mockito.when(userRepository.findBySessionID("session123")).thenReturn(Optional.of(user));
        Mockito.when(chatRepository.findById("chat123")).thenReturn(Optional.of(chat));

        mockMvc.perform(post("/getChatHistory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionID\": \"session123\", \"chatID\": \"chat123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.history").exists());
    }
}
