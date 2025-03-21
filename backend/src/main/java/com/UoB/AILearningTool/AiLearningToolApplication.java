package com.UoB.AILearningTool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.CommandLineRunner;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class })
public class AiLearningToolApplication {

	public static void main(String[] args) {
		//  disable SSL if launched without keystore.p12
		if (!Files.exists(Paths.get("keystore.p12"))) {
			// Force disable SSL
			System.setProperty("server.ssl.enabled", "false");
		}
		SpringApplication.run(AiLearningToolApplication.class, args);
	}

}
