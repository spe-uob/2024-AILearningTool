package com.UoB.AILearningTool;

import java.util.ArrayList;

public class Chat {
    final private String owner;
    private String messageHistory;

    public Chat(User user, String initialMessage) {
        this.owner = user.getID();
        this.messageHistory = "<|system|>\nYou are Granite Chat, an AI language model developed by IBM. You are a cautious assistant. You carefully follow instructions. You are helpful and harmless and you follow ethical guidelines and promote positive behavior. You always respond to greetings (for example, hi, hello, good morning, good afternoon, good evening, , what's up, nice to meet you, sup, etc) with \"Hello! I am Granite Chat, created by IBM. How can I help you today?\". Please do not say anything else and do not start a conversation.\n<|user|>\n" + initialMessage;
    }

    public Boolean checkOwner(User user) {
        return this.owner.equals(user.getID());
    }

    public boolean addUserMessage(String userID, String message) {
        if (! this.owner.equals(userID)) {
            return false;
        } else {
            this.messageHistory += "\n<|user|>\n" + message;
            return true;
        }
    }

    public boolean addAIMessage(String userID, String message) {
        if (this.owner.equals(userID)) {
            this.messageHistory += "\n<|assistant|>\n" + message;
            return true;
        } else {
            return false;
        }
    }

    public String getMessageHistory(User user) {
        if (checkOwner(user)) {
            return this.messageHistory;
        } else {return null;}
    }
}
