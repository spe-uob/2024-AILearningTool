package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import com.UoB.AILearningTool.repository.ChatRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.UoB.AILearningTool.StringTools;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
public class SpringController {
    private static final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final DatabaseController DBC;
    private final OpenAIAPIController WXC;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    public SpringController(DatabaseController DBC, OpenAIAPIController WXC) {
        this.DBC = DBC;
        this.WXC = WXC;
    }

    // User registration
      @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (userRepository.existsById(username)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Username already exists"));
        }

        DBC.addUser(username, password, true);
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    // User Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<UserEntity> userOptional = userRepository.findById(username);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            UserEntity user = userOptional.get();
            String sessionID = user.getSessionID();

            List<ChatEntity> chats = chatRepository.findByOwner(user);
            List<String> chatIDs = new ArrayList<>();

            for (ChatEntity chat : chats) {
                chatIDs.add(chat.getChatID());
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username",username,
                    "sessionID", sessionID, 
                    "chatIDs", chatIDs
            ));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Invalid username or password"));
        }
    }


    // Check if the user exists
    @GetMapping("/checkSession")
    public ResponseEntity<Map<String, Boolean>> checkSession(@RequestParam String username) {
        boolean isLoggedIn = userRepository.existsById(username);
        return ResponseEntity.ok(Collections.singletonMap("loggedIn", isLoggedIn));
    }

    // Delete user
    @GetMapping("/revokeConsent")
    public ResponseEntity<Map<String, Object>> revokeConsent(@RequestParam String username) {
        if (DBC.removeUser(username)) {
            return ResponseEntity.ok(Collections.singletonMap("message", "User data deleted successfully."));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User not found"));
        }
    }

    // Get user's ID
    @GetMapping("/getUserID")
    public ResponseEntity<Map<String, Object>> getUserID(@RequestParam String username) {
        if (userRepository.existsById(username)) {
            return ResponseEntity.ok(Collections.singletonMap("userID", username));
        } else {
            return ResponseEntity.status(404).body(Collections.singletonMap("message", "User not found"));
        }
    }


    // Create a new chat session with Watsonx
    @PostMapping("/createChat")
    public ResponseEntity<Map<String, Object>> createChat(@RequestBody Map<String, String> request) {
        String sessionID = request.get("sessionID");
        String initialMessage = request.get("initialMessage");
    
        Optional<UserEntity> userOptional = userRepository.findBySessionID(sessionID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap("message", "Invalid session"));
        }
    
        UserEntity user = userOptional.get();
        
        ChatEntity chat = new ChatEntity(user, initialMessage, sessionID);
        chatRepository.save(chat);
        String chatID = chat.getChatID();


        // Add initial message to a new thread
        Integer sendMessageResultCode = WXC.sendUserMessage(chat, initialMessage);
        if (sendMessageResultCode != 200) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Unable to add initial message to the thread."));
        }

        // Run the new thread
        int runThreadResultCode = WXC.runThread(chat.getThreadID());
        if (runThreadResultCode != 200) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Unable to run a new thread."));
        }

        // Wait until any runs for a thread are completed
        while (WXC.isLocked(chat)) {
            log.info("Thread is busy");
        }

        // Get the response from the AI and save it to the database
        WatsonxResponse AIResponse = WXC.getLastThreadMessage(chat.getThreadID());
        if (AIResponse.statusCode != 200) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Unable to get last thread message."));
        }

        chat.addAIMessage(user, AIResponse.responseText);
        chatRepository.save(chat);
    
        return ResponseEntity.ok(Map.of(
            "chatID", chatID));
    }
    
    
    @PostMapping("/sendMessage")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String chatID = request.get("chatID");
        String newMessage = request.get("newMessage");

        UserEntity user = userRepository.findById(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap("message", "User not found"));
        }
    
        ChatEntity chat = chatRepository.findById(chatID).orElse(null);
        if (chat == null || !chat.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(404)
                .body(Collections.singletonMap("message", "Chat not found"));
        }

        // Add a message to the thread
        int sendMessageResultCode = WXC.sendUserMessage(chat, newMessage);
        if (sendMessageResultCode != 200) {
            return ResponseEntity.status(sendMessageResultCode)
                    .body(Collections.singletonMap("message", "Unable to add message to the thread."));
        }

        // Run the thread
        int runThreadResultCode = WXC.runThread(chat.getThreadID());
        if (runThreadResultCode != 200) {
            return ResponseEntity.status(500)
                    .body(Collections.singletonMap("message", "Unable to run the thread."));
        }

        // Wait until any runs for a thread are completed
        while (WXC.isLocked(chat)) {
            log.info("Thread is busy");
        }

        // Add new message to the message history
        WatsonxResponse AIResponse = WXC.getLastThreadMessage(chat.getThreadID());
        if (AIResponse.statusCode != 200) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("message", "Unable to receive the new AI response."));
        }

        chat.addUserMessage(user, newMessage);
        chat.addAIMessage(user, AIResponse.responseText);
        chatRepository.save(chat);

        return ResponseEntity.ok(Map.of(
                "role", "assistant",
                "content", AIResponse.responseText
        ));
    }

    @PostMapping("/getChatHistory")
    public ResponseEntity<Map<String, Object>> getChatHistory(@RequestBody Map<String, String> request) {
        String userID = request.get("userID");
        String chatID = request.get("chatID");

        UserEntity user = userRepository.findById(userID).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("message", "User doesn't exist"));
        }

        ChatEntity chat = chatRepository.findById(chatID).orElse(null);

        if (chat == null || !chat.getOwner().getUsername().equals(userID)) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("message", "Chat doesn't exist or you are not authorized to access it."));
        }

        return ResponseEntity.status(200)
                .body(new JSONObject(chat.getMessageHistory(user)).toMap());
    }

    @GetMapping("/getUserChats")
    public ResponseEntity<Map<String, Object>> getUserChats(@RequestParam String username) {
        Optional<UserEntity> user = userRepository.findById(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("message", "User not found"));
        }
    
        List<ChatEntity> chats = chatRepository.findByOwner(user.get());
        List<Map<String, String>> chatList = new ArrayList<>();
    
        for (ChatEntity chat : chats) {
            chatList.add(Map.of("chatID", chat.getChatID(), "title", "todo"));
        }
    
        return ResponseEntity.ok(Collections.singletonMap("chatList", chatList));
    }

}