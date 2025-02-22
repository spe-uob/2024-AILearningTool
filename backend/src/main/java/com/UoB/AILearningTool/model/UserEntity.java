package com.UoB.AILearningTool.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean optionalConsent;

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

    public void setOptionalConsent(boolean optionalConsent) {
        this.optionalConsent = optionalConsent;
    }
}
