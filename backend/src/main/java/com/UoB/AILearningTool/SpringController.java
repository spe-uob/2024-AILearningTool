package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import com.UoB.AILearningTool.repository.ChatRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
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
    
       
        WatsonxResponse wresponse = WXC.sendUserMessage(chat, initialMessage);
        if (wresponse.statusCode != 200) {
            return ResponseEntity.status(500)
                .body(Collections.singletonMap("message", "Failed to get AI response"));
        }
    
        
        chat.addAIMessage(wresponse.responseText);
        chatRepository.save(chat);
    
        return ResponseEntity.ok(Map.of(
            "chatID", chatID,
            "aiResponse", wresponse.responseText
        ));
    }
    
    
    @PostMapping("/sendMessage")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String chatID = request.get("chatID");
        String newMessage = request.get("newMessage");
    
       
        Optional<UserEntity> userOptional = userRepository.findById(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap("message", "User not found"));
        }
    
        ChatEntity chat = chatRepository.findById(chatID).orElse(null);
        if (chat == null || !chat.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(404)
                .body(Collections.singletonMap("message", "Chat not found"));
        }
    
        chat.addUserMessage(newMessage);
        chatRepository.save(chat);
    
        WatsonxResponse wresponse = WXC.sendUserMessage(chat, newMessage);
        if (wresponse.statusCode != 200) {
            return ResponseEntity.status(500)
                .body(Collections.singletonMap("message", "Failed to get AI response"));
        }
    
        chat.addAIMessage(wresponse.responseText);
        chatRepository.save(chat);
    
        return ResponseEntity.ok(Map.of(
            "response", wresponse.responseText
        ));
    }

    // Get message history from a chat
    @GetMapping("/getChatHistory")
    public void getChatHistory(@CookieValue(value = "userID", defaultValue = "") String userID,
                               @RequestParam(name = "chatID") String chatID,
                               HttpServletResponse response) {
        response.setContentType("text/plain; charset=utf-8");
        
        Optional<UserEntity> userOptional = userRepository.findById(userID);
        if (userOptional.isEmpty()) {
            response.setStatus(401);
            try {
                response.getWriter().write("User doesn't exist!");
                return;
            } catch (IOException e) {
                return;
            }
        }
        
        UserEntity user = userOptional.get();
        ChatEntity chat = chatRepository.findById(chatID).orElse(null);
        
        if (chat == null || !chat.getOwner().getUsername().equals(userID)) {
            response.setStatus(404);
            try {
                response.getWriter().write("Chat doesn't exist or you are not authorized to access it.");
                return;
            } catch (IOException e) {
                return;
            }
        }
    
        String chatHistory = chat.getMessageHistory();
        if (chatHistory.startsWith("<|system|>")) {
            chatHistory = chatHistory.replace("<|system|>\n", ""); 
        }
    
       
        JSONArray messageHistory = new JSONArray();
        String[] messages = chatHistory.split("\n");
        for (int i = 0; i < messages.length; i++) {
            String message = messages[i];
            if (message.startsWith("<|user|>")) {
                JSONObject userMessage = new JSONObject();
                userMessage.put("role", "user");
                userMessage.put("content", message.replace("<|user|>", "").trim());
                messageHistory.put(userMessage);
            } else if (message.startsWith("<|assistant|>")) {
                JSONObject assistantMessage = new JSONObject();
                assistantMessage.put("role", "assistant");
                assistantMessage.put("content", message.replace("<|assistant|>", "").trim());
                messageHistory.put(assistantMessage);
            } else if (!message.isEmpty()) {
                JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", "system");
                systemMessage.put("content", message.trim());
                messageHistory.put(systemMessage);
            }
        }
    
        // If we reach here, chat != null and messageHistory != null
        response.setStatus(200);
        try {
            response.getWriter().write(messageHistory.toString());
        } catch (IOException e) {
            response.setStatus(500);
        }
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
            String[] lines = chat.getMessageHistory().split("\n");
            String title = "New Chat";
            
            for (String line : lines) {
                if (!line.startsWith("<|user|>") && !line.startsWith("<|assistant|>")) {
                    title = line.trim();
                    if (!title.isEmpty()) {
                        break;  
                    }
                }
            }
    
            chatList.add(Map.of("chatID", chat.getChatID(), "title", title));
        }
    
        return ResponseEntity.ok(Collections.singletonMap("chatList", chatList));
    }

}
