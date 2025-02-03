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
		//  disable SSL if launched without keystore.p12
		if (!Files.exists(Paths.get("src/main/resources/keystore.p12"))) {
			// Force disable SSL
			System.setProperty("server.ssl.enabled", "false");
		}
		SpringApplication.run(AiLearningToolApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Path keystorePath = Paths.get("src", "main", "resources", "keystore.p12");

		// If there's no keystore in the working directory, skip the SSL setup.
		if (!Files.exists(keystorePath)) {
			System.out.println("No keystore.p12 found in working directory; skipping SSL copy/overwrite");
			return;
		}

		// Otherwise, proceed with the copy and overwrite
		Path resourcesDir = Paths.get("src","main","resources");
		Path targetKeystore = resourcesDir.resolve("keystore.p12");
		copyFile(keystorePath, targetKeystore);
		System.out.println("keystore.p12 copied to src/main/resources successfully");

		Path applicationProps = Paths.get("application.properties");
		overwriteApplicationProperties(applicationProps);
		System.out.println("application.properties has been updated successfully");
	}


	private static void copyFile(Path source, Path target) throws IOException {
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
