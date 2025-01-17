package com.UoB.AILearningTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@RestController
public class SpringController {
    private final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final DatabaseController DBC;
    //    TODO: Replace with OpenAIAPIController with WatsonxAPIController when API quota issue will be resolved.
    //    private final WatsonxAPIController WXC = new WatsonxAPIController();
    private final OpenAIAPIController WXC;

    @Autowired
    public SpringController(DatabaseController DBC, OpenAIAPIController WXC) {
        this.DBC = DBC;
        this.WXC = WXC;
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
                           HttpServletResponse response) {
        response.setContentType("text/plain"); // Set the content type to text

        // Create a chat
        User user = DBC.getUser(userID);
        if (user != null) {
            WatsonxResponse wresponse;
            String chatID = DBC.createChat(user, initialMessage);

            // Send the message history (system prompt and initial message) to Watsonx API,
            // add the AI response to the message history of the chat.
            try {
                wresponse = WXC.sendUserMessage(DBC.getChat(DBC.getUser(userID), chatID).getMessageHistory(user));
                response.getWriter().write(chatID);
                response.setStatus(wresponse.statusCode);
                if (wresponse.statusCode == 200) {
                    DBC.getChat(DBC.getUser(userID), chatID).addAIMessage(userID, wresponse.responseText);
                }
            } catch (IOException e) {
                log.warn(String.valueOf(e));
            }
        } else {
            try {
                response.getWriter().write("null");
                response.setStatus(401);
            } catch (IOException e) {
                log.warn(String.valueOf(e));
            }
        }
    }

    // Send a message to the chat and receive a response
    @GetMapping("/sendMessage")
    public void sendMessage(@CookieValue(value = "userID", defaultValue = "") String userID,
                            @RequestParam(name = "newMessage") String newMessage,
                           @RequestParam(name = "chatID") String chatID,
                           HttpServletResponse response) {
        Chat chat = DBC.getChat(DBC.getUser(userID), chatID);
        String inputString = chat.getMessageHistory(DBC.getUser(userID));

        // Default response - Unauthorised
        WatsonxResponse wresponse = new WatsonxResponse(401, "");
        response.setContentType("text/plain");
        response.setStatus(401);

        if (chat != null && inputString != null) {
            // If a message can be added to the message history of a chat, then send the message history
            // to Watsonx API.
            boolean success = chat.addUserMessage(userID, newMessage);
            if (success) {
                inputString = chat.getMessageHistory(DBC.getUser(userID));
//                TODO: Revert wresponse when issue with Watsonx API quota will be resolved.
//                wresponse = WXC.sendUserMessage(StringTools.messageHistoryPrepare(inputString));
                wresponse = WXC.sendUserMessage(inputString);
                response.setStatus(wresponse.statusCode);
            }
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
        Chat chat = DBC.getChat(DBC.getUser(userID), chatID);
        String messageHistory = chat.getMessageHistory(DBC.getUser(userID));
        if (chat != null && messageHistory != null) {
            response.setContentType("text/plain");
            response.setStatus(200);
            try {
                response.getWriter().write(messageHistory);
            } catch (IOException e) {
                log.warn(String.valueOf(e));
            }
        } else {
            response.setContentType("text/plain");
            response.setStatus(401);
            try {
                response.getWriter().write("");
            } catch (IOException e) {
                log.warn(String.valueOf(e));
            }
        }
    }
}
