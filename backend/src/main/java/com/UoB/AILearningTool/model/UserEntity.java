package com.UoB.AILearningTool.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private String id;
    private String username;
    private String password;
    private boolean optionalConsent;

    public UserEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public UserEntity(String username, String password, boolean optionalConsent) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.optionalConsent = optionalConsent;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getOptionalConsent() {
        return optionalConsent;
    }

    public void setOptionalConsent(boolean optionalConsent) {
        this.optionalConsent = optionalConsent;
    }
}
