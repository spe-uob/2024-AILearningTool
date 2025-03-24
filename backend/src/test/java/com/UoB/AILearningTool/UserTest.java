package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    @DisplayName("Checking whether 'User' constructor creates an object with optional consent correctly")
    public void correctFullConsentUserCreationTest() {
        UserEntity user = new UserEntity("testUserLogin1", "testUserPassword1", true);
        Assertions.assertEquals("testUserLogin1", user.getUsername());
        Assertions.assertTrue(user.getOptionalConsent());
    }

    @Test
    @DisplayName("Checking whether 'User' constructor creates an object without optional consent correctly")
    public void correctRequiredConsentUserCreationTest() {
        UserEntity user = new UserEntity("testUserLogin2", "testUserPassword2", false);
        Assertions.assertEquals("testUser2", user.getUsername());
        Assertions.assertFalse(user.getOptionalConsent());
    }
}
