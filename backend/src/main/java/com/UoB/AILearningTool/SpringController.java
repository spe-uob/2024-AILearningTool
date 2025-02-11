package com.UoB.AILearningTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SpringController {

    private final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final DatabaseController DBC;
    private final OpenAIAPIController WXC;

    @Autowired
    public SpringController(DatabaseController DBC, OpenAIAPIController WXC) {
        this.DBC = DBC;
        this.WXC = WXC;
    }

    @GetMapping("/getUserById")
    public ResponseEntity<User> getUserById(@RequestParam String userID) {
        User user = DBC.getUserById(userID);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            log.warn("User not found with ID: {}", userID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private final Map<String, String> userStore = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (userStore.containsKey(username) && userStore.get(username).equals(password)) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (userStore.containsKey(username)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Username already exists"));
        } else {
            userStore.put(username, password);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        }
    }

    @GetMapping("/signup")
    public void signup(@CookieValue(value = "optionalConsent", defaultValue = "false") boolean optionalConsent,
                       HttpServletResponse response) {
        String userID = DBC.addUser(optionalConsent);
        Cookie userIDCookie = new Cookie("userID", userID);
        userIDCookie.setMaxAge(30 * 24 * 60 * 60);
        userIDCookie.setSecure(false);
        userIDCookie.setPath("/");
        response.addCookie(userIDCookie);
        log.info("Assigned a new userID: {}", userID);
    }

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
            log.warn("Failed to revoke consent for user, ID: {}", userID);
        }
    }

    @GetMapping("/createChat")
    public void createChat(@CookieValue(value = "userID", defaultValue = "") String userID,
                           @RequestParam(name = "initialMessage") String initialMessage,
                           HttpServletResponse response) {
        response.setContentType("text/plain");

        User user = DBC.getUserById(userID);
        if (user != null) {
            String chatID = DBC.createChat(user, initialMessage);
            try {
                Chat chat = DBC.getChat(user, chatID);
                if (chat != null) {
                    String messageHistory = chat.getMessageHistory(user);
                    WatsonxResponse wresponse = WXC.sendUserMessage(messageHistory);
                    response.getWriter().write(chatID);
                    response.setStatus(wresponse.statusCode);

                    if (wresponse.statusCode == 200) {
                        chat.addAIMessage(userID, wresponse.responseText);
                        log.info("AI response added to chat, ID: {}", chatID);
                    }
                } else {
                    response.getWriter().write("null");
                    response.setStatus(400);
                    log.warn("Chat not found for user ID: {}", userID);
                }
            } catch (IOException e) {
                log.error("IOException occurred while creating chat: {}", e.getMessage(), e);
            }
        } else {
            try {
                response.getWriter().write("null");
                response.setStatus(401);
                log.warn("User not found while creating chat, ID: {}", userID);
            } catch (IOException e) {
                log.error("IOException while sending response: {}", e.getMessage(), e);
            }
        }
    }

    // Send a message to the chat and receive a response
    @GetMapping("/sendMessage")
    public void sendMessage(@CookieValue(value = "userID", defaultValue = "") String userID,
                            @RequestParam(name = "newMessage") String newMessage,
                           @RequestParam(name = "chatID") String chatID,
                           HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setStatus(401); // Default

        // 1) Get the user
        User user = DBC.getUser(userID);
        // 2) get the chat
        Chat chat = DBC.getChat(user, chatID);
        if (chat == null) {
            // null chat
            return;
        }

        // 3) Get existing history
        String inputString = chat.getMessageHistory(user);
        if (inputString == null) {
            return;
        }

        // 4) Try to add the new user message
        boolean success = chat.addUserMessage(userID, newMessage);
        if (success) {
            // 5) Re-fetch history & call AI
            inputString = chat.getMessageHistory(user);
            WatsonxResponse wresponse = WXC.sendUserMessage(inputString);

            // Update status from AI
            response.setStatus(wresponse.statusCode);

            try {
                if (wresponse.statusCode == 200) {
                    chat.addAIMessage(userID, wresponse.responseText);
                }
                response.getWriter().write(wresponse.responseText);
            } catch (IOException e) {
                log.warn(String.valueOf(e));
                response.setStatus(500);
            }
        }
    }

    // Send a message history and receive a response from Watsonx.
    // Used for users who declined optional cookies.
    @GetMapping("/sendIncognitoMessage")
    public void sendIncognitoMessage(@CookieValue(value = "userID") String userID,
                                     @RequestParam(name = "inputString") String inputString,
                                     HttpServletResponse response) {
        log.warn("ID {}, newMessage: {}", userID, inputString);
        User user = DBC.getUser(userID);
        WatsonxResponse wresponse;
        response.setContentType("text/plain");

        if (user != null) {
//            TODO: Revert wresponse when issue with Watsonx API quota will be resolved.
            wresponse = WXC.sendUserMessage(inputString);
//            wresponse = WXC.sendUserMessage(StringTools.messageHistoryPrepare(inputString));
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
    public void getChatHistory(@CookieValue(value = "userID", defaultValue = "") String userID,
                               @RequestParam(name = "chatID") String chatID,
                               HttpServletResponse response) {
        User user = DBC.getUser(userID);
        Chat chat = DBC.getChat(DBC.getUser(userID), chatID);
        if (chat == null) {
            response.setStatus(401);
        try {
            response.getWriter().write("");
        } catch (IOException e) {
            log.warn(String.valueOf(e));
            }
            return;
        }

        String messageHistory = chat.getMessageHistory(user);
        if (messageHistory == null) {
            response.setStatus(401);
            try {
                response.getWriter().write("");
            } catch (IOException e) {
                log.warn(String.valueOf(e));
            }
            return;

        }
        // If we reach here, chat != null and messageHistory != null
        response.setStatus(200);
        try {
            response.getWriter().write(messageHistory);
        } catch (IOException e) {
            log.warn(String.valueOf(e));
            response.setStatus(500);
        }
    }
}
