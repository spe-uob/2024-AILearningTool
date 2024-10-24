package com.UoB.AILearningTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Communication with SQL database.
public class DatabaseController {
    private Map<String, User> users = new HashMap<>();

    public DatabaseController() {
        // TODO: Connect to a MariaDB database.
    }

    // Create a new user and return their ID for cookie assignment
    public String addUser() {
        String id = RandomString.make(20);
        // TODO: Add a user profile record to the database.
        users.put(id, new User());
        return id;
    }

    // Remove all data stored about the user (profile, chat, etc.)
    public void removeUser(String id) {
        // TODO: Remove a user profile record from the database.
        users.remove(id);
    }

}
