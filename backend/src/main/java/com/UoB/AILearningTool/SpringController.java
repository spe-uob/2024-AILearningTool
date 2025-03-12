package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import com.UoB.AILearningTool.repository.ChatRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.UoB.AILearningTool.StringTools;

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
        
        String sessionID = StringTools.generateSessionID();
        
        UserEntity newUser = new UserEntity(username, password, true);
        newUser.setSessionID(sessionID); 
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "sessionID", sessionID 
        ));
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
    public ResponseEntity<Map<String, Object>> createChat(@RequestBody Map<String, String> payload) {
        String sessionID = payload.get("sessionID");
        String initialMessage = payload.get("initialMessage");

        if (sessionID == null || sessionID.isEmpty()) {
            return ResponseEntity.status(400).body(Collections.singletonMap("message", "SessionID cannot be null"));
        }
        if (initialMessage == null || initialMessage.isEmpty()) {
            return ResponseEntity.status(400).body(Collections.singletonMap("message", "Initial message cannot be null"));
        }

        Optional<UserEntity> userOptional = userRepository.findBySessionID(sessionID); 
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("message", "User not found for sessionID"));
        }

        UserEntity user = userOptional.get(); 

        Optional<ChatEntity> existingChat = chatRepository.findBySessionID(sessionID);
        ChatEntity chat;
    
        if (existingChat.isEmpty()) {
            chat = new ChatEntity(user, initialMessage, sessionID);
        } else {
            chat = existingChat.get();
            chat.setMessageHistory("<|system|>\n" + initialMessage); 
        }
        chatRepository.save(chat); 
    
        String chatID = chat.getChatID();
    
        
        WatsonxResponse wresponse = WXC.sendUserMessage(chat.getMessageHistory());
    
        if (wresponse.statusCode == 200) {
            chat.addAIMessage(wresponse.responseText);
            chatRepository.save(chat); 
        }

        return ResponseEntity.ok(Map.of(
                "chatID", chatID,
                "sessionID", chat.getSessionID(),
                "initialMessage", initialMessage,
                "aiResponse", wresponse.responseText,
                "messageHistory", chat.getMessageHistory()
        ));
    }



    // Send a message to the chat and receive a response
    @PostMapping("/sendMessage")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String chatID = payload.get("chatID");
        String newMessage = payload.get("newMessage");

        ChatEntity chat = chatRepository.findById(chatID).orElse(null);

        if (chat == null || !chat.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(400).body(Collections.singletonMap("message", "Chat not found or access denied"));
        }

        // store user's messages
        chat.addUserMessage(newMessage);
        chatRepository.save(chat);

        String inputString = chat.getMessageHistory();
        WatsonxResponse wresponse = WXC.sendUserMessage(inputString);

        if (wresponse.statusCode == 200) {
            chat.addAIMessage(wresponse.responseText);
            chatRepository.save(chat);
        }

        return ResponseEntity.ok(Collections.singletonMap("response", wresponse.responseText));
    }

    // Send a message history and receive a response from Watsonx.
    @PostMapping("/sendIncognitoMessage")
    public ResponseEntity<Map<String, Object>> sendIncognitoMessage(@RequestBody Map<String, String> payload) {
        String inputString = payload.get("inputString");

        WatsonxResponse wresponse = WXC.sendUserMessage(inputString);
        return ResponseEntity.ok(Collections.singletonMap("response", wresponse.responseText));
    }

    // Get message history from a chat
    @GetMapping("/getChatHistory")
    public ResponseEntity<Map<String, Object>> getChatHistory(@RequestParam String username, @RequestParam String chatID) {
        ChatEntity chat = chatRepository.findById(chatID).orElse(null);

        if (chat == null || !chat.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "Chat not found or access denied"));
        }

        String chatHistory = chat.getMessageHistory();
        if (chatHistory.startsWith("<|system|>")) {
            chatHistory = chatHistory.replace("<|system|>\n", ""); 
        }

        return ResponseEntity.ok(Collections.singletonMap("history", chatHistory));
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
