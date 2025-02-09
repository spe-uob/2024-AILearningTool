package com.UoB.AILearningTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

// Communication with SQLite database.
@Service
public class DatabaseController {
    private Connection connection;
    private Map<String, User> users = new HashMap<>();
    private Map<String, Chat> chats = new HashMap<>();

    public DatabaseController() {
        try {
            // Connect to a SQLite database.
            String url = "jdbc:sqlite:UserInformation.db"; 
            connection = DriverManager.getConnection(url);
            initializeDatabase();
        } catch (SQLException e) {
            // Handle database connection failure
        }
    }

    private void initializeDatabase() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id TEXT PRIMARY KEY," +
                "consent BOOLEAN)"
            );
            stmt.execute();
            stmt.close();

            stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS chats (" +
                "id TEXT PRIMARY KEY," +
                "userId TEXT," +
                "initialMessage TEXT," +
                "FOREIGN KEY(userId) REFERENCES users(id))"
            );
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            // Handle SQL exception
        }
    }

    public User getUser(String userID) {
        return users.get(userID);
    }

    // Create a new user and return their ID for cookie assignment
    public String addUser(boolean optionalConsent) {
        User user = new User(optionalConsent);
        String id = user.getID();
        try {
            // Add a user profile record to the SQLite database.
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO users (id, consent) VALUES (?, ?)"
            );
            stmt.setString(1, id);
            stmt.setBoolean(2, optionalConsent);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            // Handle SQL exception for adding user
        }
        users.put(id, user);
        return id;
    }

    // Remove all data stored about the user (profile, chat, etc.)
    public boolean removeUser(String id) {
        try {
            // Remove a user profile record from the SQLite database.
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM users WHERE id = ?"
            );
            stmt.setString(1, id);
            int count = stmt.executeUpdate();
            stmt.close();
            if (count > 0) {
                users.remove(id);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            // Handle SQL exception for removing user
            return false;
        }
    }

    // Creates a new chat
    public String createChat(User user, String initialMessage) {
        String id = StringTools.RandomString(20);
        chats.put(id, new Chat(user, initialMessage));
        return id;
    }

    // Deletes an existing chat
    public Boolean deleteChat(User user, String chatID) {
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
            } else { return null; }
        } else {
            return null;
        }
    }
}
