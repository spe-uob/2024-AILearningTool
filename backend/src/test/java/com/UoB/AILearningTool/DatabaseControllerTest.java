package com.UoB.AILearningTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DatabaseControllerTest {
    @Test
    @DisplayName("Check whether users can be created.")
    public void createUsers() {
        DatabaseController DBC = new DatabaseController();
        ArrayList<String> usernames = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            usernames.add(DBC.addUser(true));
        }

        for (String username : usernames) {
            User user = DBC.getUser(username);
            Assertions.assertEquals(username, user.getID());
        }
    }

    @Test
    @DisplayName("Check whether users can be deleted.")
    public void deleteUsers() {
        DatabaseController DBC = new DatabaseController();
        ArrayList<String> usernames = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            usernames.add(DBC.addUser(true));
        }

        // Deleting all users
        for (String username : usernames) {
            DBC.removeUser(username);
        }

        // Search for non-existing user must return null
        for (String username : usernames) {
            Assertions.assertNull(DBC.getUser(username));
        }
    }

    @Test
    @DisplayName("Check whether chats can be created and accessed.")
    public void createChats() {
        DatabaseController DBC = new DatabaseController();
        ArrayList<User> users = new ArrayList<>();
        ArrayList<String> chatIDs = new ArrayList<>();

        // Create users.
        for (int i = 0; i < 20; i++) {
            users.add(DBC.getUser(DBC.addUser(true)));
        }

        // Create chats.
        for (User user : users) {
            chatIDs.add(DBC.createChat(user, "This is a first message.", "TEST"));
        }

        // DBC.getChat() must return a non-null element.
        for (int i = 0; i < 20; i++) {
            Chat actualChat = DBC.getChat(users.get(i), chatIDs.get(i));
            Assertions.assertNotNull(actualChat);
        }
    }

    @Test
    @DisplayName("Check whether chats can only be accessed by their owners.")
    public void accessChatPermissionTest() {
        Chat currentChat;
        User currentUser;
        DatabaseController DBC = new DatabaseController();
        ArrayList<User> users = new ArrayList<>();
        ArrayList<String> chatIDs = new ArrayList<>();

        // Create users.
        for (int i = 0; i < 20; i++) {
            users.add(DBC.getUser(DBC.addUser(true)));
        }

        // Create chats.
        for (User user : users) {
            chatIDs.add(DBC.createChat(user, "This is a first message.", "TEST"));
        }

        for (int i = 0; i < 20; i++) {
            // If currentUser is the chat owner, Chat object is returned.
            currentUser = users.get(i);
            currentChat = DBC.getChat(currentUser, chatIDs.get(i));
            Assertions.assertNotNull(currentChat);
            for (int j = 0; j < 20; j++) {
                if (i == j) {continue;}
                // If currentUser isn't the chat owner, null object is returned.
                currentUser = users.get(j);
                currentChat = DBC.getChat(currentUser, chatIDs.get(i));
                Assertions.assertNull(currentChat);
            }
        }
    }

}
