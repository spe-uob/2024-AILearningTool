package com.UoB.AILearningTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController  // Use RestController for handling HTTP requests
@Service @Controller
public class SpringController {
    private final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final DatabaseController DBC;
    private final OpenAIAPIController WXC;

    @Autowired
    public SpringController(DatabaseController DBC, OpenAIAPIController WXC) {
        this.DBC = DBC;
        this.WXC = WXC;
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

    // Assign a unique user ID for the user.
    @GetMapping("/signup")
    public void signup(@CookieValue(value = "optionalConsent", defaultValue = "false") boolean optionalConsent,
                      HttpServletResponse response) {
        Cookie userIDCookie = new Cookie("userID", DBC.addUser(optionalConsent));
        userIDCookie.setMaxAge(30 * 24 * 60 * 60); // Cookie will expire in 30 days
        userIDCookie.setSecure(false);
//        userIDCookie.setAttribute("SameSite", "None");
        userIDCookie.setPath("/");
        response.addCookie(userIDCookie);
        log.info("Assigned a new userID.");
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
    public void createChat(@CookieValue(value = "userID", defaultValue = "") String userID,
                           @RequestParam(name = "initialMessage") String initialMessage,
                           HttpServletResponse response) throws IOException {
        response.setContentType("text/plain"); // Set the content type to text

        // Get the user
        User user = DBC.getUser(userID);
        if (user == null) {
            response.setStatus(401);
            response.getWriter().write("User doesn't exist!");
            log.warn("User {} doesn't exist!", userID);
            return;
        }

        // Create new chat in the database
        String chatID = DBC.createChat(user, initialMessage, WXC.createThread());
        Chat chat = DBC.getChat(user, chatID);

        // Add initial message to a new thread
        Integer sendMessageResultCode = WXC.sendUserMessage(chat, initialMessage);
        if (sendMessageResultCode != 200) {
            try {
                response.getWriter().write("Unable to add initial message to the thread.");
                response.setStatus(500);
                return;
            } catch (IOException e) {
                log.error(String.valueOf(e));
                return;
            }
        }

        // Run the new thread
        int runThreadResultCode = WXC.runThread(chat.getThreadID());
        if (runThreadResultCode != 200) {
            try {
                response.getWriter().write("Unable to run a new thread.");
                response.setStatus(500);
                return;
            } catch (IOException e) {
                log.error(String.valueOf(e));
                return;
            }
        }

        // Wait until any runs for a thread are completed
        while (WXC.isLocked(chat)) {
            log.info("Thread is busy");
        }

        // Get the response from the AI and save it to the database
        WatsonxResponse AIResponse = WXC.getLastThreadMessage(chat.getThreadID());
        if (AIResponse.statusCode != 200) {
            try {
                response.getWriter().write("Unable to get last thread message.");
                response.setStatus(500);
                return;
            } catch (IOException e) {
                log.error(String.valueOf(e));
                return;
            }
        }
        chat.addAIMessage(userID, AIResponse.responseText);
        response.getWriter().write(chatID);
        response.setStatus(200);
    }

    // Send a message to the chat and receive a response
    @GetMapping("/sendMessage")
    public void sendMessage(@CookieValue(value = "userID", defaultValue = "") String userID,
                            @RequestParam(name = "newMessage") String newMessage,
                           @RequestParam(name = "chatID") String chatID,
                           HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        // Get the user and chat
        User user = DBC.getUser(userID);
        Chat chat = DBC.getChat(user, chatID);

        // Check if the chat is available
        if (chat == null) {
            try {
                response.getWriter().write("Chat doesn't exist or you are not authorized to access it.");
                response.setStatus(404);
                return;
            } catch (IOException e) {
                log.warn(String.valueOf(e));
                return;
            }
        }

        // Add a message to the thread
        int sendMessageResultCode = WXC.sendUserMessage(chat, newMessage);
        if (sendMessageResultCode != 200) {
            try {
                response.getWriter().write("Unable to add message to the thread.");
                response.setStatus(sendMessageResultCode);
                return;
            } catch (IOException e) {
                log.warn(String.valueOf(e));
                return;
            }
        }

        // Run the thread
        int runThreadResultCode = WXC.runThread(chat.getThreadID());
        if (runThreadResultCode != 200) {
            try {
                response.getWriter().write("Unable to run the thread.");
                response.setStatus(500);
                return;
            } catch (IOException e) {
                log.warn(String.valueOf(e));
                return;
            }
        }

        // Wait until any runs for a thread are completed
        while (WXC.isLocked(chat)) {
            log.info("Thread is busy");
        }

        // Add new message to the message history
        WatsonxResponse AIResponse = WXC.getLastThreadMessage(chat.getThreadID());
        if (AIResponse.statusCode != 200) {
            try {
                response.getWriter().write("Unable to receive the new AI response.");
                response.setStatus(runThreadResultCode);
                return;
            } catch (IOException e) {
                log.warn(String.valueOf(e));
            }
        }

        chat.addUserMessage(userID, newMessage);
        chat.addAIMessage(userID, AIResponse.responseText);
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("role", "assistant");
        responseJSON.put("content", AIResponse.responseText);
        response.getWriter().write(responseJSON.toString());
        response.setStatus(200);

    }

    // Get message history from a chat
    @GetMapping("/getChatHistory")
    public void getChatHistory(@CookieValue(value = "userID", defaultValue = "") String userID,
                               @RequestParam(name = "chatID") String chatID,
                               HttpServletResponse response) {
        User user = DBC.getUser(userID);
        Chat chat = DBC.getChat(DBC.getUser(userID), chatID);
        if (chat == null) {
            response.setStatus(404);
            try {
                response.getWriter().write("Chat doesn't exist or you are not authorized to access it.");
                return;
            } catch (IOException e) {
                log.warn(String.valueOf(e));
            }
        }

        JSONArray messageHistory = chat.getMessageHistory(user);

        // If we reach here, chat != null and messageHistory != null
        response.setStatus(200);
        try {
            response.getWriter().write(messageHistory.toString());
        } catch (IOException e) {
            log.warn(String.valueOf(e));
            response.setStatus(500);
        }
    }
}
