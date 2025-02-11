package com.UoB.AILearningTool;

import java.sql.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DatabaseController {
    private static final Logger log = LoggerFactory.getLogger(DatabaseController.class);
    private Connection connection;
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Chat> chats = new HashMap<>();

    public DatabaseController() {
        try {
            String url = "jdbc:sqlite:UserInformation.db";
            connection = DriverManager.getConnection(url);
            initializeDatabase();
            log.info("Database connected successfully.");
        } catch (SQLException e) {
            log.error("Failed to connect to the database: {}", e.getMessage(), e);
        }
    }

    private void initializeDatabase() {
        try {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (id TEXT PRIMARY KEY, consent BOOLEAN)";
            String createChatsTable = "CREATE TABLE IF NOT EXISTS chats (id TEXT PRIMARY KEY, userId TEXT, initialMessage TEXT, FOREIGN KEY(userId) REFERENCES users(id))";

            try (PreparedStatement stmt = connection.prepareStatement(createUsersTable)) {
                stmt.execute();
            }
            try (PreparedStatement stmt = connection.prepareStatement(createChatsTable)) {
                stmt.execute();
            }
            log.info("Database tables initialized.");
        } catch (SQLException e) {
            log.error("Failed to initialize database tables: {}", e.getMessage(), e);
        }
    }
    public User getUser(String userID) {
        return getUserById(userID);
    }
    public User getUserById(String userID) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("id"), rs.getBoolean("consent"));
            }
        } catch (SQLException e) {
            log.error("Failed to get user by ID: {}", e.getMessage(), e);
        }
        return null;
    }

    public String addUser(boolean optionalConsent) {
        User user = new User(optionalConsent);
        String id = user.getID();
        String query = "INSERT INTO users (id, consent) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setBoolean(2, optionalConsent);
            stmt.executeUpdate();
            users.put(id, user);
            log.info("User added with ID: {}", id);
        } catch (SQLException e) {
            log.error("Failed to add user: {}", e.getMessage(), e);
        }
        return id;
    }

    public boolean removeUser(String id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            int count = stmt.executeUpdate();
            if (count > 0) {
                users.remove(id);
                log.info("User removed with ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to remove user: {}", e.getMessage(), e);
        }
        return false;
    }

    public String createChat(User user, String initialMessage) {
        String chatID = StringTools.RandomString(20);
        String query = "INSERT INTO chats (id, userId, initialMessage) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, chatID);
            stmt.setString(2, user.getID());
            stmt.setString(3, initialMessage);
            stmt.executeUpdate();
            chats.put(chatID, new Chat(user, initialMessage));
            log.info("Chat created with ID: {}", chatID);
        } catch (SQLException e) {
            log.error("Failed to create chat: {}", e.getMessage(), e);
        }
        return chatID;
    }

    public boolean deleteChat(User user, String chatID) {
        Chat chat = chats.get(chatID);
        if (chat != null && chat.checkOwner(user)) {
            String query = "DELETE FROM chats WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, chatID);
                int count = stmt.executeUpdate();
                if (count > 0) {
                    chats.remove(chatID);
                    log.info("Chat deleted with ID: {}", chatID);
                    return true;
                }
            } catch (SQLException e) {
                log.error("Failed to delete chat: {}", e.getMessage(), e);
            }
        }
        return false;
    }

    public Chat getChat(User user, String chatID) {
        Chat chat = chats.get(chatID);
        if (chat != null && chat.checkOwner(user)) {
            return chat;
        }
        log.warn("Chat not found or user is not the owner: {}", chatID);
        return null;
    }
}
