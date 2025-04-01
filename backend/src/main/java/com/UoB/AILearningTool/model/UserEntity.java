package com.UoB.AILearningTool.model;

import jakarta.persistence.*;
import java.util.List;
import com.UoB.AILearningTool.StringTools;
import java.security.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "userid", unique = true, nullable = false)
    private String userID;

    @Column(name = "username", unique = true, nullable = false)
    private String username; 
    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatEntity> chats;  

    public UserEntity() {}

    @Column(name = "sessionid", unique = true, nullable = false)
    private String sessionID;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.userID = StringTools.RandomString(32);
        // TODO: Adjust this later
        this.sessionID = StringTools.generateSessionID();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
