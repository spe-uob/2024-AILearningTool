#!/bin/bash
# Initialising the server outside of Docker container, eg. for development
# and debugging purposes. No SSL is used.
# Preferred port number can be provided as an integer number
# in range 0-65535, for example
#       ./localExecute.sh 9000
#       ./localExecute.sh 65535
# If no argument is provided or the range is incorrect, a standard port 8080 is used.

# Processes the argument (if provided)
if [ "$1" != "" ] && [ "$1" -ge 0 ] && [ "$1" -le 65535 ]
then
PORT=$1
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