package com.UoB.AILearningTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Receiving IBM Bearer tokens.")
public class IBMAuthenticatorTest {
    @Test
    // Checking whether a single request works.
    public void singleRequestTest() {
        IBMAuthenticator testAuthenticator = new IBMAuthenticator();
        testAuthenticator.start();

        String token1 = testAuthenticator.getBearerToken();

        Assertions.assertEquals(200, testAuthenticator.getStatusCode());
        Assertions.assertNotEquals(null, token1);

        testAuthenticator.stopTimer();
    }

    @Test
    // Checking whether multiple requests return unique tokens.
    public void multiRequestTest() {
        IBMAuthenticator testAuthenticator = new IBMAuthenticator();
        testAuthenticator.start();

        String token1 = testAuthenticator.getBearerToken();
        testAuthenticator.requestNewToken();
        String token2 = testAuthenticator.getBearerToken();

        Assertions.assertEquals(200, testAuthenticator.getStatusCode());
        Assertions.assertNotEquals(token1, token2);

        testAuthenticator.stopTimer();
    }
}
