package com.UoB.AILearningTool.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.util.UUID;

@Entity
@Table(name = "chats")
public class ChatEntity {
    @Id
    private String chatID;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity owner;

    private String messageHistory;

    public ChatEntity() {
    }

    public ChatEntity(UserEntity owner, String initialMessage) {
        this.chatID = UUID.randomUUID().toString();
        this.owner = owner;
        this.messageHistory = "<|system|>\n" + initialMessage;
    }

    public String getChatID() {
        return chatID;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public String getMessageHistory() {
        return messageHistory;
    }

    public void addUserMessage(String message) {
        this.messageHistory += "\n<|user|>\n" + message;
    }

    public void addAIMessage(String message) {
        this.messageHistory += "\n<|assistant|>\n" + message;
    }
}

