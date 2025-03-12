package com.UoB.AILearningTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;


// Communication with SQL database.
@Service
public class DatabaseController {
    private Map<String, User> users = new HashMap<>();
    private Map<String, Chat> chats = new HashMap<>();

    public User getUser(String userID) {
        return users.get(userID);
    }

    public DatabaseController() {
        // TODO: Connect to a MariaDB database.
    }

    // Create a new user and return their ID for cookie assignment
    public String addUser(boolean optionalConsent) {
        User user = new User(optionalConsent);
        String id = user.getID();
        // TODO: Add a user profile record to the MariaDB database.
        users.put(id, user);
        return id;
    }

    // Remove all data stored about the user (profile, chat, etc.)
    public boolean removeUser(String id) {
        // TODO: Remove a user profile record from the MariaDB database.
        if (users.containsKey(id)) {
            users.remove(id);
            return true;
        } else {return false;}
    }

    // Creates a new chat
    public String createChat(User user, String initialMessage, String threadID) {
        String id = StringTools.RandomString(20);
        chats.put(id, new Chat(user, initialMessage, threadID));
        return id;
    }

    // Deletes an existing chat
    public Boolean deleteChat(User user, String chatID) {
        Boolean success;
        Chat chat = chats.get(chatID);
        if (chat != null) {
            if (chat.checkOwner(user)) {
                chats.remove(chatID);
                return true;
            }
        }
        return false;
    }

    public Chat getChat(User user, String chatID) {
        Chat chat = chats.get(chatID);
        if (chat != null) {
            if (chat.checkOwner(user)) {
                return chat;
            } else {return null;}
        } else {
            return null;
        }
    }

}
