package com.UoB.AILearningTool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class })
public class AiLearningToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiLearningToolApplication.class, args);
	}

}
