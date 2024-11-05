package com.UoB.AILearningTool;

import java.time.LocalDateTime;
import java.util.ArrayList;

// Class representing a user profile (someone who consented to optional cookies).
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


    // Create a user
    public User(boolean optionalConsent) {
        updateLastActivityTime();
        this.id = StringTools.RandomString(25);
        this.optionalConsent = optionalConsent;
    }
}
