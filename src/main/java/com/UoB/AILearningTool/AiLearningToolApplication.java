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
public class AiLearningToolApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AiLearningToolApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Path keystorePath = Paths.get("keystore.p12");

		// 2) Copy keystore.p12 to src/main/resources
		Path resourcesDir = Paths.get("src","main", "resources");
		Path targetKeystore = resourcesDir.resolve("keystore.p12");
		copyFile(keystorePath, targetKeystore);
		System.out.println("keystore.p12 copied to src/main/resources successfully");

		// 3) Overwrite application.properties
		Path applicationProps = Paths.get("application.properties");
		overwriteApplicationProperties(applicationProps);
		System.out.println("application.properties has been updated successfully");
	}

	private void copyFile(Path source, Path target) throws IOException {
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	private void overwriteApplicationProperties(Path propsFile) throws IOException {
		List<String> lines = Arrays.asList(
				"spring.application.name=AILearningTool",
				"server.port=8080",
				"spring.servlet.multipart.max-file-size=50MB",
				"spring.servlet.multipart.max-request-size=50MB",
				"spring.web.resources.static-locations=classpath:/static/",
				"",
				"server.ssl.key-store=classpath:keystore.p12",
				"server.ssl.key-store-password=ailearntool",
				"server.ssl.key-store-type=PKCS12",
				"server.ssl.key-alias=myalias"
		);
		Files.write(propsFile, lines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE );
	}

}
