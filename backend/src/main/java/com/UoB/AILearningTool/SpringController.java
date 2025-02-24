package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import com.UoB.AILearningTool.repository.ChatRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

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


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<UserEntity> userOptional = userRepository.findById(username);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Invalid username or password"));
        }
    }


    @GetMapping("/checkSession")
    public ResponseEntity<Map<String, Boolean>> checkSession(@RequestParam String username) {
        boolean isLoggedIn = userRepository.existsById(username);
        return ResponseEntity.ok(Collections.singletonMap("loggedIn", isLoggedIn));
    }


    @GetMapping("/revokeConsent")
    public ResponseEntity<Map<String, Object>> revokeConsent(@RequestParam String username) {
        if (DBC.removeUser(username)) {
            return ResponseEntity.ok(Collections.singletonMap("message", "User data deleted successfully."));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User not found"));
        }
    }


    @GetMapping("/getUserID")
    public ResponseEntity<Map<String, Object>> getUserID(@RequestParam String username) {
        if (userRepository.existsById(username)) {
            return ResponseEntity.ok(Collections.singletonMap("userID", username));
        } else {
            return ResponseEntity.status(404).body(Collections.singletonMap("message", "User not found"));
        }
    }


    @PostMapping("/createChat")
    public ResponseEntity<Map<String, Object>> createChat(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String initialMessage = payload.get("initialMessage");

        if (!userRepository.existsById(username)) {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "User not found"));
        }

        String chatID = DBC.createChat(username, initialMessage);
        return ResponseEntity.ok(Collections.singletonMap("chatID", chatID));
    }


    @PostMapping("/sendMessage")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String chatID = payload.get("chatID");
        String newMessage = payload.get("newMessage");

        ChatEntity chat = chatRepository.findById(chatID).orElse(null);

        if (chat == null || !chat.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(400).body(Collections.singletonMap("message", "Chat not found or access denied"));
        }


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


    @PostMapping("/sendIncognitoMessage")
    public ResponseEntity<Map<String, Object>> sendIncognitoMessage(@RequestBody Map<String, String> payload) {
        String inputString = payload.get("inputString");

        WatsonxResponse wresponse = WXC.sendUserMessage(inputString);
        return ResponseEntity.ok(Collections.singletonMap("response", wresponse.responseText));
    }


    @GetMapping("/getChatHistory")
    public ResponseEntity<Map<String, Object>> getChatHistory(@RequestParam String username, @RequestParam String chatID) {
        ChatEntity chat = chatRepository.findById(chatID).orElse(null);

        if (chat == null || !chat.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "Chat not found or access denied"));
        }

        return ResponseEntity.ok(Collections.singletonMap("history", chat.getMessageHistory()));
    }
}
