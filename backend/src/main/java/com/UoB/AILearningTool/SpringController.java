package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.ChatRepository;
import com.UoB.AILearningTool.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SpringController {
    private static final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final DatabaseController DBC;
    private final OpenAIAPIController OAIC;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    public SpringController(DatabaseController DBC, OpenAIAPIController OAIC) {
        this.DBC = DBC;
        this.OAIC = OAIC;
    }

    // User registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.status(400).build();
        }

        boolean success = DBC.addUser(username, password);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // User Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            UserEntity user = userOptional.get();
            String sessionID = user.getSessionID();

            List<ChatEntity> chats = chatRepository.findByOwner(user);
            List<String> chatIDs = new ArrayList<>();

            for (ChatEntity chat : chats) {
                chatIDs.add(chat.getChatID());
                // TODO: Chat titles
                // chat.getMessageHistory(user).getJSONObject(0).getJSONObject(0).getString("content");
            }

            return ResponseEntity.ok(Map.of(
                    "sessionID", sessionID, 
                    "chatIDs", chatIDs
            ));
        } else {
            return ResponseEntity.status(401).build();
        }
    }


    // Check if the user exists
    @GetMapping("/checkSession")
    public ResponseEntity<Map<String, Boolean>> checkSession(@RequestParam String sessionID) {
        boolean isLoggedIn = userRepository.findBySessionID(sessionID).isPresent();
        if (isLoggedIn) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    // Delete user
    @GetMapping("/revokeConsent")
    public ResponseEntity<Map<String, Object>> revokeConsent(@RequestParam String sessionID) {
        boolean success = DBC.removeUser(sessionID);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }


    // Create a new chat session with Watsonx
    @PostMapping("/createChat")
    public ResponseEntity<Map<String, Object>> createChat(@RequestBody Map<String, String> request) {
        String sessionID = request.get("sessionID");
        String initialMessage = request.get("initialMessage");

        // Check if user is authorised
        Optional<UserEntity> userOptional = userRepository.findBySessionID(sessionID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap("message", "Invalid session"));
        }
    
        UserEntity user = userOptional.get();
        
        ChatEntity chat = new ChatEntity(user, initialMessage, this.OAIC.createThread());
        chatRepository.save(chat);
        String chatID = chat.getChatID();


        // Add initial message to a new thread
        Integer sendMessageResultCode = OAIC.sendUserMessage(chat, initialMessage);
        if (sendMessageResultCode != 200) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Unable to add initial message to the thread."));
        }

        // Run the new thread
        int runThreadResultCode = OAIC.runThread(chat.getThreadID());
        if (runThreadResultCode != 200) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Unable to run a new thread."));
        }

        // Wait until any runs for a thread are completed
        while (OAIC.isLocked(chat)) {
            log.info("Thread is busy");
        }

        // Get the response from the AI and save it to the database
        WatsonxResponse AIResponse = OAIC.getLastThreadMessage(chat.getThreadID());
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
        String sessionID = request.get("sessionID");
        String chatID = request.get("chatID");
        String newMessage = request.get("newMessage");

        UserEntity user = userRepository.findBySessionID(sessionID).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap("message", "User not found"));
        }
    
        ChatEntity chat = chatRepository.findById(chatID).orElse(null);
        if (chat == null || !chat.getOwner().getSessionID().equals(sessionID)) {
            return ResponseEntity.status(404)
                .body(Collections.singletonMap("message", "Chat not found"));
        }

        // Add a message to the thread
        int sendMessageResultCode = OAIC.sendUserMessage(chat, newMessage);
        if (sendMessageResultCode != 200) {
            return ResponseEntity.status(sendMessageResultCode)
                    .body(Collections.singletonMap("message", "Unable to add message to the thread."));
        }

        // Run the thread
        int runThreadResultCode = OAIC.runThread(chat.getThreadID());
        if (runThreadResultCode != 200) {
            return ResponseEntity.status(500)
                    .body(Collections.singletonMap("message", "Unable to run the thread."));
        }

        // Wait until any runs for a thread are completed
        while (OAIC.isLocked(chat)) {
            log.info("Thread is busy");
        }

        // Add new message to the message history
        WatsonxResponse AIResponse = OAIC.getLastThreadMessage(chat.getThreadID());
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

    @PostMapping( "/getChatHistory")
    public ResponseEntity<Map<String, Object>> getChatHistory(@RequestBody Map<String, String> request) {
        String sessionID = request.get("sessionID");
        String chatID = request.get("chatID");

        UserEntity user = userRepository.findBySessionID(sessionID).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("message", "User doesn't exist"));
        }

        ChatEntity chat = chatRepository.findById(chatID).orElse(null);

        if (chat == null || !chat.getOwner().getSessionID().equals(sessionID)) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("message", "Chat doesn't exist or you are not authorized to access it."));
        }

        return ResponseEntity.status(200).
                body(Map.of("history", chat.getMessageHistory(user).toString()));
    }

    @GetMapping("/getUserChats")
    public ResponseEntity<Map<String, Object>> getUserChats(@RequestParam String sessionID) {
        Optional<UserEntity> user = userRepository.findBySessionID(sessionID);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "User not found"));
        }
    
        List<ChatEntity> chats = chatRepository.findByOwner(user.get());
        List<Map<String, String>> chatList = new ArrayList<>();
    
        for (ChatEntity chat : chats) {
            chatList.add(Map.of("chatID", chat.getChatID(), "title", chat.getMessageHistory(user.get()).getJSONObject(0).getString("content")));
        }
    
        return ResponseEntity.ok(Collections.singletonMap("chatList", chatList));
    }

}