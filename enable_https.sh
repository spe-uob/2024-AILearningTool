#!/bin/bash

# The script will change Spring Boot application parameters to deploy with HTTPS encryption.

# Define the application properties file and keystore file
PROPERTIES_FILE="src/main/resources/application.properties"
KEYSTORE_FILE="keystore.p12"

# Verify if keystore.p12 exists in the current directory
if [[ ! -f "$KEYSTORE_FILE" ]]; then
  echo "Error: $KEYSTORE_FILE not found in the current directory."
  exit 1
else
  sudo cp "$KEYSTORE_FILE" "src/main/resources/$KEYSTORE_FILE"
  sudo chmod 777 "src/main/resources/$KEYSTORE_FILE"
  echo "keystore file has been successfully copied."
fi

# Overwrite the application.properties file with the new content
cat > "$PROPERTIES_FILE" <<EOL
spring.application.name=AILearningTool
server.port=8080
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
spring.web.resources.static-locations=classpath:/static/

server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=ailearntool
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=myalias
EOL

echo "application.properties has been successfully updated."
