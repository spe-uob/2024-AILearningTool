package com.UoB.AILearningTool;

import java.time.LocalDateTime;

public class User {
    private final String id;
    private boolean optionalConsent = false;
    private LocalDateTime lastActivityTime;

    public String getID() {
        updateLastActivityTime();
        return this.id;
    }

    public boolean getOptionalConsent() {
        updateLastActivityTime();
        return this.optionalConsent;
    }

    public void updateLastActivityTime() {
        this.lastActivityTime = LocalDateTime.now();
    }

    public User(String username, boolean optionalConsent) {
        updateLastActivityTime();
        this.id = username;
        this.optionalConsent = optionalConsent;
    }
}
