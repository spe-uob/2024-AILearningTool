package com.UoB.AILearningTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    @DisplayName("Checking whether 'User' constructor creates an object with optional consent correctly")
    public void correctFullConsentUserCreationTest() {
        User user = new User(true);
        Assertions.assertEquals(25, user.getID().length());
        Assertions.assertTrue(user.getOptionalConsent());
    }
    @Test
    @DisplayName("Checking whether 'User' constructor creates an object without optional consent correctly")
    public void correctRequiredConsentUserCreationTest() {
        User user = new User(false);
        Assertions.assertEquals(25, user.getID().length());
        Assertions.assertFalse(user.getOptionalConsent());
    }
}
