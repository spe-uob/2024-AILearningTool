package com.UoB.AILearningTool.model;

import com.UoB.AILearningTool.StringTools;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

@Entity
@Table(name = "chats")
public class ChatEntity {
    @Id
    @Column(name = "chatid", unique = true, nullable = false)
    private String chatID;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private UserEntity owner;

    @Column(name = "threadid", unique = true, nullable = false)
    private String threadID;

    @Column(name = "message_history", columnDefinition = "TEXT")
    private String messageHistory;

    public void setThreadID(String threadID) {
        this.threadID = threadID;
    }

    public ChatEntity() {}

    public ChatEntity(UserEntity owner, String initialMessage, String threadID) {
        this.chatID = StringTools.RandomString(32);
        this.owner = owner;
        this.threadID = threadID;
        this.messageHistory = new JSONArray().put(
                new JSONObject().put(
                        "role", "user"
                ).put("content", initialMessage)
        ).toString();
    }

    public String getChatID() {
        return chatID;
    }

    public String getThreadID() {
        return threadID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public UserEntity getOwner() {
        return owner;
    }


    public JSONArray getMessageHistory(UserEntity user) {
        if (this.getOwner().equals(user)) {
            return new JSONArray(this.messageHistory);
        } else {return null;}

    }

    public void setMessageHistory(JSONArray newHistory) {
        this.messageHistory = new JSONArray(newHistory).toString();
    }

    public void addUserMessage(UserEntity user, String message) {
        if (this.getOwner().equals(user)) {
            this.messageHistory = this.getMessageHistory(user).put(
                    new JSONObject().put(
                            "role", "user"
                    ).put(
                            "content", message
                    )
            ).toString();
        }
    }

    public void addAIMessage(UserEntity user, String message) {
        if (this.getOwner().equals(user)) {
            this.messageHistory = this.getMessageHistory(user).put(
                    new JSONObject().put(
                            "role", "assistant"
                    ).put(
                            "content", message
                    )
            ).toString();
        }
    }
}
