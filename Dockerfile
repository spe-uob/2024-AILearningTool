# Use an official OpenJDK runtime as a base image.
FROM openjdk:21-jdk-slim

# Specify the JAR file's build location.
ARG JAR_FILE=backend/target/*.jar

# Copy the JAR file into the container.
COPY ${JAR_FILE} app.jar

# Define the command to run your application.
ENTRYPOINT ["java", "-jar", "/app.jar"]
