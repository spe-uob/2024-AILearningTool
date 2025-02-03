package com.UoB.AILearningTool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class AiLearningToolApplicationTest {

    // JUnit 5 will create a temporary directory for the test.
    @TempDir
    Path tempDir;

    @Test
    void testCopyFile() throws IOException {
        // Create a temporary source file in the temp directory
        Path sourceFile = tempDir.resolve("source.txt");
        String expectedContent = "This is a test file";
        Files.write(sourceFile, expectedContent.getBytes(StandardCharsets.UTF_8));

        // Define the target file path in the temp directory
        Path targetFile = tempDir.resolve("target.txt");

        // Create an instance of your application class.
        // (Make sure the copyFile method is package-private instead of private.)
        AiLearningToolApplication app = new AiLearningToolApplication();
        app.copyFile(sourceFile, targetFile);

        // Verify the target file exists
        assertTrue(Files.exists(targetFile), "Target file should exist after copy");

        // Verify that the content of the target file matches the expected content.
        String actualContent = Files.readString(targetFile, StandardCharsets.UTF_8);
        assertEquals(expectedContent, actualContent, "File content should match after copy");
    }
}
