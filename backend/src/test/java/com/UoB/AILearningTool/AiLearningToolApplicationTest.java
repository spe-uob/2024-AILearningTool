package com.UoB.AILearningTool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.*;

class AiLearningToolApplicationTest {

    void copyFile(Path source, Path target) throws IOException {
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
                "spring.http.encoding.charset=UTF-8",
                "spring.http.encoding.enabled=true",
                "spring.http.encoding.force=true",
                "",
                "server.ssl.key-store=classpath:keystore.p12",
                "server.ssl.key-store-password=ailearntool",
                "server.ssl.key-store-type=PKCS12",
                "server.ssl.key-alias=myalias",
                
                "spring.datasource.url=jdbc:sqlite:database.db",
                "spring.datasource.driver-class-name=org.sqlite.JDBC",
                "spring.datasource.username=",
                "spring.datasource.password=",
                "spring.jpa.database-platform=org.hibernate.dialect.SQLiteDialect",
                "spring.jpa.hibernate.ddl-auto=update"
        );
        Files.write(propsFile, lines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE );
    }

    // JUnit 5 will create a temporary directory for the test.
    @TempDir
    Path tempDir;

    @Test
    void testCopyFile() throws IOException {
        // Create a temporary source file in the temp directory
        Path sourceFile = tempDir.resolve("source.txt");
        String expectedContent = "This is a test file";
        Files.writeString(sourceFile, expectedContent);

        // Define the target file path in the temp directory
        Path targetFile = tempDir.resolve("target.txt");

        // Create an instance of your application class.
        copyFile(sourceFile, targetFile);

        // Verify the target file exists
        assertTrue(Files.exists(targetFile), "Target file should exist after copy");

        // Verify that the content of the target file matches the expected content.
        String actualContent = Files.readString(targetFile, StandardCharsets.UTF_8);
        assertEquals(expectedContent, actualContent, "File content should match after copy");
    }

    /**
     * Test that the overwriteApplicationProperties method writes the expected lines to a file.
     */
    @Test
    void testOverwriteApplicationProperties() throws IOException {
        // Create a temporary file to act as application.properties.
        Path propsFile = tempDir.resolve("application.properties");

        overwriteApplicationProperties(propsFile);

        // Verify that the properties file exists.
        assertTrue(Files.exists(propsFile), "Properties file should exist after writing.");

        // Read all lines from the file.
        List<String> actualLines = Files.readAllLines(propsFile, StandardCharsets.UTF_8);

        // Define the expected content (exactly as written in your method).
        List<String> expectedLines = Arrays.asList(
                "spring.application.name=AILearningTool",
                "server.port=8080",
                "spring.servlet.multipart.max-file-size=50MB",
                "spring.servlet.multipart.max-request-size=50MB",
                "spring.web.resources.static-locations=classpath:/static/",
                "",
                "spring.http.encoding.charset=UTF-8",
                "spring.http.encoding.enabled=true",
                "spring.http.encoding.force=true",
                "",
                "server.ssl.key-store=classpath:keystore.p12",
                "server.ssl.key-store-password=ailearntool",
                "server.ssl.key-store-type=PKCS12",
                "server.ssl.key-alias=myalias",
                
                "spring.datasource.url=jdbc:sqlite:database.db",
                "spring.datasource.driver-class-name=org.sqlite.JDBC",
                "spring.datasource.username=",
                "spring.datasource.password=",
                "spring.jpa.database-platform=org.hibernate.dialect.SQLiteDialect",
                "spring.jpa.hibernate.ddl-auto=update"
        );

        // Verify that the file content is exactly as expected.
        assertEquals(expectedLines, actualLines, "The properties file content should match the expected lines.");
    }
}
