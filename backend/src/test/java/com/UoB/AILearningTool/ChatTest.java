package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.ChatRepository;
import com.UoB.AILearningTool.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public class ChatTest {

    private final String mockThreadId = "test-thread-id";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;



    @Test
    @DisplayName("Check if checkOwner method compares owners correctly.")
    public void checkOwnerTest() {
        UserEntity user = new UserEntity("testUserLogin", "testUserPassword");
        ChatEntity chat = new ChatEntity(user, "This is a first message.", mockThreadId);

        assertEquals(user, chat.getOwner());
    }

    @Test
    @DisplayName("Check if initial message history is saved correctly.")
    public void initialMessageHistoryTest() {
        String initialMessage = "This is a first message.";
        UserEntity user = new UserEntity("testUserLogin2", "testUserPassword2");
        ChatEntity chat = new ChatEntity(user, initialMessage, mockThreadId);

        JSONArray expected = new JSONArray().put(new JSONObject()
                .put("role", "user")
                .put("content", initialMessage));

        JSONArray actual = chat.getMessageHistory(user);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    @DisplayName("Check if user messages are added correctly.")
    public void addUserMessageTest() {
        String initialMessage = "This is a first message.";
        String newMessage = "Tell me a joke.";
        UserEntity user = new UserEntity("testUserLogin3", "testUserPassword3");
        ChatEntity chat = new ChatEntity(user, initialMessage, mockThreadId);

        chat.addUserMessage(user, newMessage);
        JSONArray history = chat.getMessageHistory(user);

        assertEquals(2, history.length());
        assertEquals("user", history.getJSONObject(0).getString("role"));
        assertEquals(initialMessage, history.getJSONObject(0).getString("content"));
        assertEquals("user", history.getJSONObject(1).getString("role"));
        assertEquals(newMessage, history.getJSONObject(1).getString("content"));
    }

    @Test
    @DisplayName("Check if AI messages are added correctly.")
    public void addAIMessageTest() {
        String initialMessage = "Need help finding courses.";
        String aiResponse = "Here is a suggestion";
        UserEntity user = new UserEntity("testUserLogin4", "testUserPassword4");
        ChatEntity chat = new ChatEntity(user, initialMessage, mockThreadId);

        chat.addAIMessage(user, aiResponse);
        JSONArray history = chat.getMessageHistory(user);

        assertEquals(2, history.length());
        assertEquals("user", history.getJSONObject(0).getString("role"));
        assertEquals("assistant", history.getJSONObject(1).getString("role"));
        assertEquals(aiResponse, history.getJSONObject(1).getString("content"));
    }



    @Test
    @DisplayName("Only the chat owner can access its message history (via database)")
    public void chatMessageHistoryPermissionTest() {
        final String initialMessage = "This is a first message.";

        // Ensure that the database is cleared before testing to prevent interference from old data
        chatRepository.deleteAll();
        userRepository.deleteAll();

// Create and save users and their chats
        for (int i = 0; i < 10; i++) {
            UserEntity user = new UserEntity("dbUser" + i, "password" + i);
            userRepository.save(user);
            ChatEntity chat = new ChatEntity(user, initialMessage, "thread" + i);
            chatRepository.save(chat);
        }

        List<UserEntity> users = userRepository.findAll();
        List<ChatEntity> chats = chatRepository.findAll();

        for (ChatEntity chat : chats) {
            UserEntity owner = chat.getOwner();


            JSONArray ownerHistory = chat.getMessageHistory(owner);
            assertNotNull(ownerHistory, "Owner should be able to access their chat");


            assertTrue(ownerHistory.length() >= 1);
            assertEquals(initialMessage, ownerHistory.getJSONObject(0).getString("content"));


            for (UserEntity user : users) {
                if (!user.getUsername().equals(owner.getUsername())) {
                    JSONArray otherHistory = chat.getMessageHistory(user);
                    assertNull(otherHistory, user.getUsername() + " 不应能访问 " + owner.getUsername() + " 的聊天");
                }
            }
        }
    }
}
