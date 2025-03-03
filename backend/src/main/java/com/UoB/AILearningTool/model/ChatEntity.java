package com.UoB.AILearningTool.model;

import jakarta.persistence.*;
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

    @Column(name = "message_history", columnDefinition = "TEXT")
    private String messageHistory;

    public ChatEntity() {} 

   
    public ChatEntity(UserEntity owner, String initialMessage) {
        this.chatID = UUID.randomUUID().toString();  
        this.owner = owner;
        this.messageHistory = "<|user|>\n" + initialMessage;
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

    public void setMessageHistory(String newHistory) {
        this.messageHistory = newHistory;
    }

    public void addUserMessage(String message) {
        this.messageHistory += "\n<|user|>\n" + message;
    }

    public void addAIMessage(String message) {
        this.messageHistory += "\n<|assistant|>\n" + message;
    }
}
