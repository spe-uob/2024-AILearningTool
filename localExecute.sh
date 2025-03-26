#!/bin/bash
# Initialising the server outside of Docker container, eg. for development
# and debugging purposes. No SSL is used.
# First argument - openAI API key
# Second argument - preferred port number (optional)
# Preferred port number can be provided as an integer number
# in range 0-65535.
# Usage examples:
#       ./localExecute.sh somerandomOpenAIAPIKey1 9000
#       ./localExecute.sh somestrangeunreadableopenaiapikey 9000
# If no port argument is provided or the range is incorrect, a standard port 8080 is used.

# Process the OpenAI API key argument (if provided)
if [ "$1" != "" ]
then
echo "package com.UoB.AILearningTool;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAIAuthenticator {
    private final Logger log = LoggerFactory.getLogger(OpenAIAuthenticator.class);

    private String apiKey;

    // Constructor that initializes the API key
    public OpenAIAuthenticator() {
          this.apiKey = \"$1\";
          log.info(\"OpenAI API key set.\");
    }

    // Returns the API key as a \"Bearer token\" (used in authorization headers)
    public String getBearerToken() {
      if (this.apiKey == null || this.apiKey.equals(\"OPENAI_API_KEY\") || this.apiKey.isEmpty()) {
        log.error(\"API Key not set or is empty.\");
        throw new IllegalStateException(\"API Key for OpenAI is not set.\");
      }
      return this.apiKey;
    }
}" > backend/src/main/java/com/UoB/AILearningTool/OpenAIAuthenticator.java
else
echo "OpenAI API key not provided!"
exit 1
fi

# Processes the port argument (if provided)
if [ "$2" != "" ] && [ "$2" -ge 0 ] && [ "$2" -le 65535 ]
then
PORT=$2
else
PORT=8080
fi

# Overwrites the port used in backend
echo "spring.application.name=AILearningTool
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
spring.web.resources.static-locations=classpath:/static/
server.port=$PORT
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true" > backend/src/main/resources/application.properties

# Overwrites the port used in the frontend code
echo "const BACKEND_URL = 'http://localhost:$PORT';
export { BACKEND_URL };" > frontend/src/assets/globalConstants.js

# Builds the frontend code for localhost deployment without SSL, moves result to resources of the backend
cd frontend && npm run build && cp -a dist/. ../backend/src/main/resources/static/

# Executes the backend
cd ../backend && mvn clean && mvn spring-boot:run