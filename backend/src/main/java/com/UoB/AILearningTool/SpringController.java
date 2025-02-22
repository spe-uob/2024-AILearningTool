package com.UoB.AILearningTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.UserRepository;
import com.UoB.AILearningTool.repository.ChatRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController  // Use RestController for handling HTTP requests
public class SpringController {
    private final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final DatabaseController DBC;
    //    TODO: Replace with OpenAIAPIController with WatsonxAPIController when API quota issue will be resolved.
    //    private final WatsonxAPIController WXC = new WatsonxAPIController();
    private final OpenAIAPIController WXC;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public SpringController(DatabaseController DBC, OpenAIAPIController WXC) {
        this.DBC = DBC;
        this.WXC = WXC;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Username or password cannot be empty"));
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Username already exists"));
        }
        String userId = DBC.addUser(username, password, true);
    
        if (userId != null) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Internal server error: Could not create user"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            Cookie userIDCookie = new Cookie("userID", userOptional.get().getId());
            userIDCookie.setMaxAge(30 * 24 * 60 * 60);
            userIDCookie.setPath("/");
            response.addCookie(userIDCookie);

            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Invalid username or password"));
        }
    }

    // Assign a unique user ID for the user.
    @GetMapping("/signup")
    public void signup(@CookieValue(value = "optionalConsent", defaultValue = "false") boolean optionalConsent,
                       HttpServletResponse response) {
        String username = "defaultUser";
        String password = "defaultPass";
        String userID = DBC.addUser(username, password, optionalConsent);
        Cookie userIDCookie = new Cookie("userID", userID);
        
        userIDCookie.setMaxAge(30 * 24 * 60 * 60); // Cookie will expire in 30 days
        userIDCookie.setSecure(false);
        userIDCookie.setPath("/");
        response.addCookie(userIDCookie);
        
        log.info("Assigned a new userID: {}", userID);
    }


    // If user revokes their consent for data storage / optional cookies,
    // remove all data stored about them.
    @GetMapping("/revokeConsent")
    public void revokeConsent(@CookieValue(value = "userID", defaultValue = "") String userID,
                                 HttpServletResponse response) {
        if (DBC.removeUser(userID)) {
            response.setStatus(200);
            Cookie cookie = new Cookie("userID", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            log.info("Revoked consent for user, ID: {}", userID);
        } else {
            response.setStatus(400);
        }

    }

    // Create a new chat session with Watsonx
    @GetMapping("/createChat")
    public ResponseEntity<Map<String, Object>> createChat(@RequestParam String userID, @RequestParam String initialMessage) {
        UserEntity user = DBC.getUser(userID);
        if (user != null) {
            String chatID = DBC.createChat(user, initialMessage);
            return ResponseEntity.ok(Collections.singletonMap("chatID", chatID));
        } else {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "User not found"));
        }
    }

    // Send a message to the chat and receive a response
    @GetMapping("/sendMessage")
    public void sendMessage(@CookieValue(value = "userID", defaultValue = "") String userID,
                            @RequestParam(name = "newMessage") String newMessage,
                            @RequestParam(name = "chatID") String chatID,
                            HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setStatus(401);

        UserEntity user = DBC.getUser(userID);
        ChatEntity chat = DBC.getChat(user, chatID);
        if (chat == null) {
            return;
        }

        String inputString = chat.getMessageHistory();
        if (inputString == null) {
            return;
        }

        chat.addUserMessage(newMessage); 
        inputString = chat.getMessageHistory();
        WatsonxResponse wresponse = WXC.sendUserMessage(inputString);

        response.setStatus(wresponse.statusCode);
        try {
            if (wresponse.statusCode == 200) {
                chat.addAIMessage(wresponse.responseText);
            }
            response.getWriter().write(wresponse.responseText);
        } catch (IOException e) {
            log.warn(String.valueOf(e));
            response.setStatus(500);
        }
    }


    // Send a message history and receive a response from Watsonx.
    // Used for users who declined optional cookies.
    @GetMapping("/sendIncognitoMessage")
    public void sendIncognitoMessage(@CookieValue(value = "userID") String userID,
                                     @RequestParam(name = "inputString") String inputString,
                                     HttpServletResponse response) {
        log.warn("ID {}, newMessage: {}", userID, inputString);
        UserEntity user = DBC.getUser(userID);

        WatsonxResponse wresponse;
        response.setContentType("text/plain");

        if (user != null) {
            wresponse = WXC.sendUserMessage(inputString);
            response.setStatus(wresponse.statusCode);
            try {
                response.getWriter().write(wresponse.responseText);
                log.warn(wresponse.responseText);
            } catch (IOException e) {
                log.warn(String.valueOf(e));
                response.setStatus(500);
            }
        } else {
            log.warn("else {}", DBC.getUser(userID) == null);
            response.setStatus(401);
        }
    }

    // Get message history from a chat
    @GetMapping("/getChatHistory")
    public ResponseEntity<Map<String, Object>> getChatHistory(@RequestParam(name = "userID") String userID,
                                                              @RequestParam(name = "chatID") String chatID) {
        UserEntity user = DBC.getUser(userID);
        ChatEntity chat = DBC.getChat(user, chatID);
        if (chat == null) {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "Chat not found"));
        }

        return ResponseEntity.ok(Collections.singletonMap("history", chat.getMessageHistory()));
    }
}
