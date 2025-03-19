package com.UoB.AILearningTool;

import org.json.*;

public class Chat {
    final private String owner;
    private final JSONArray messageHistory = new JSONArray();
    private final String threadID;

    public Chat(User user, String initialMessage, String threadID) {
        this.owner = user.getID();
        addUserMessage(user.getID(), initialMessage);
        this.threadID = threadID;
    }

    public Boolean checkOwner(User user) {
        return this.owner.equals(user.getID());
    }

    public String getThreadID() {
        return this.threadID;
    }

    public void addUserMessage(String userID, String message) {
        if (this.owner.equals(userID)) {
            JSONObject newMessage = new JSONObject();
            newMessage.put("role", "user");
            newMessage.put("content", message);
            this.messageHistory.put(newMessage);
            System.out.println("User message: " + message + "has been saved.");
        }
    }

    public void addAIMessage(String userID, String message) {
        if (this.owner.equals(userID)) {
            JSONObject newMessage = new JSONObject();
            newMessage.put("role", "assistant");
            newMessage.put("content", message);
            this.messageHistory.put(newMessage);
            System.out.println("AI message: " + message + "has been saved.");
        }
    }

    public JSONArray getMessageHistory(User user) {
        if (checkOwner(user)) {
            return this.messageHistory;
        } else {return null;}
    }
}
