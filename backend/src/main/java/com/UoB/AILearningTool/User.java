package com.UoB.AILearningTool;

import java.time.LocalDateTime;

// Class representing a user profile (someone who consented to optional cookies).
public class User {
    private final String id;
    private LocalDateTime lastActivityTime;

    public String getID() {
        return this.id;
    }

    public void updateLastActivityTime() {
        this.lastActivityTime = LocalDateTime.now();
    }

    // Create a user
    public User() {
        updateLastActivityTime();
        this.id = RandomString.make(25);
    }
}
