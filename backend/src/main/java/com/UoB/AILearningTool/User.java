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

    // Existing constructor
    public User(boolean optionalConsent) {
        updateLastActivityTime();
        this.id = StringTools.RandomString(25);
        this.optionalConsent = optionalConsent;
    }

    // New constructor for creating User with specific ID and consent status
    public User(String id, boolean optionalConsent) {
        updateLastActivityTime();
        this.id = id;
        this.optionalConsent = optionalConsent;
    }
}
