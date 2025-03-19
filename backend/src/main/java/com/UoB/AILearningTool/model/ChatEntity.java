package com.UoB.AILearningTool.model;

import com.UoB.AILearningTool.StringTools;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.UUID;

@Entity
@Table(name = "chats")
public class ChatEntity {
    @Id
    @Column(name = "chatid", unique = true, nullable = false)
    private String chatID;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private UserEntity owner;

    @Column(name = "sessionid", unique = true, nullable = false)
    private String sessionID;

    @Column(name = "message_history", columnDefinition = "TEXT")
    private String messageHistory;


    public ChatEntity() {}

    public ChatEntity(UserEntity owner, String initialMessage, String sessionID) {
        this.chatID = StringTools.RandomString(32);
        this.owner = owner;
        this.sessionID = sessionID;
        this.messageHistory = new JSONArray().put(
                new JSONObject().put(
                        "role", "user"
                ).put("content", initialMessage)
        ).toString();
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) { 
        this.sessionID = sessionID;
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
