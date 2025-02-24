package com.UoB.AILearningTool.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "username", unique = true, nullable = false)
    private String username; 

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "optional_consent")
    private boolean optionalConsent;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatEntity> chats;  
    public UserEntity() {}

    public UserEntity(String username, String password, boolean optionalConsent) {
        this.username = username;
        this.password = password;
        this.optionalConsent = optionalConsent;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getOptionalConsent() {
        return optionalConsent;
    }
}
