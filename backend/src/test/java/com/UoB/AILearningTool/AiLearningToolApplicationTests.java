package com.UoB.AILearningTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;


@SpringBootTest
class AiLearningToolApplicationTests {

	@LocalServerPort
	private int port;

	/*@Autowired
	private AiLearningToolApplication controller;

	private final RestTemplate restTemplate = new RestTemplate();

	@Test
	@DisplayName("Verify that the application context loads successfully.")
	void contextLoads() {
		Assertions.assertNotNull(controller);
	}

	@Test
	@DisplayName("Verify that the '/signup' endpoint creates a user ID cookie.")
	void signupEndpointTest() {
		// Act
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/signup", String.class);

		// Assert
		Assertions.assertNotNull(response.getHeaders().get("Set-Cookie"), "Response should contain a 'Set-Cookie' header.");
		Assertions.assertTrue(response.getHeaders().get("Set-Cookie").toString().contains("userID"), "Cookie should contain 'userID'.");
	}
	
	 */
}


