# AILearningTool - Project Handover Document

---
## Table of Contents:
- [Introduction](#introduction)
- [Architecture Diagram](#architecture-diagram)
- [Code Explanation](#code-explanation)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Setup & Installation](#setup--installation)
- [Configuration Details](#configuration-details)
  - [Application Properties](#application-properties)
  - [Frontend Configuration](#frontend-configuration)
- [Testing & Troubleshooting](#testing--troubleshooting)
- [License](#license)


## Introduction:

This document serves as a guide for new developers to continue working on our project. It includes explanations of the code infrastructure and overall repository structure of our project.

---

## Project Structure:
Below is an overview of the key components of the system:

- [.github](/.github): Contains all GitHub templates.
  - [workflows](/.github/workflows): Contains all CI and CD GitHub Actions.
- [docs](/docs): Contains all project-related documentation such as diagrams, presentations, UI screenshots, etc... Notable files include:
  - [ETHICS.md](/docs/ETHICS.md): Includes the date of the ethics pre-approval request.
  - [User-Feedback](/docs/User-Feedback): Includes user feedback from the testing day.
  - [Screenshots](/docs/Screenshots): Includes screenshots showing the evolution of the UI.
- [frontend](/frontend): Contains all of the frontend code (in Vue 3) and dependencies:
  - [node](/frontend/node): Includes Node.js runtime and package managers.
  - [public](/frontend/public): Includes our logo in various sizes, a favicon, and documents for PWA support.
  - [src](/frontend/src): Includes the frontend code:
    - [Display interface](/frontend/src/Display%20interface): Includes Vue components for login, main view, and cookie management.
    - [assets](/frontend/src/assets): Includes JavaScript and CSS global constants such as colors, languages, and responsive UI constants.
    - [components](/frontend/src/components): Includes the main UI components such as HistorySidebar, MainContent, and SettingSidebar for structuring the interface.
      - [helpers](/frontend/src/components/helpers): Includes helper classes used in the main components such as TypingText (used for "typing-like" AI responses).
    - [App](/frontend/src/App.vue): The root component that structures the entire application layout.
    - [main](/frontend/src/main.js): Initializes the Vue app, imports dependencies, and mounts the root component.
    - [registerServiceWorker](/frontend/src/registerServiceWorker.js): Registers the service worker to enable PWA features.
  - [unit](/frontend/tests/unit): Includes all frontend unit tests (in Jest).
- [backend](/backend): Contains all of the backend code (in Java), Maven config, and documents:
  - [main](/backend/src/main): Includes the backend code:
    - [AILearningTool](/backend/src/main/java/com/UoB/AILearningTool): Contains all core application logic, including API controllers, database management, and AI integration.
    - [resources](/backend/src/main/resources): Includes static resources such as application properties.
  - [test](/backend/src/test/java/com/UoB/AILearningTool): Includes all backend unit tests (in Java).
  - [mvnw](/backend/mvnw), [mvnw.cmd](/backend/mvnw.cmd)  and [pom.xml](/backend/pom.xml): Documents for Maven.
- [LICENSE](/LICENSE): Includes the project's MIT license.
- [.gitignore](/.gitignore): Includes files that should be ignored by Git when making a commit.
- [Dockerfile](/Dockerfile): Includes steps to assemble a Docker image (for CD).
- [HELP.md](/HELP.md): Contains official documentation to help with learning Maven and Spring Boot.
- [localExecute.sh](/localExecute.sh): Shell script used to run the application.

---

## Architecture Diagram:

![Architecture Diagram](/docs/Diagrams/architecture_diagram.png)

## Code Explanation:

### Backend:

- **Framework:** Spring Boot 3.3.4  
- **Main Functionality:**  
  - User and chat management (signup, chat creation, sending messages, retrieving chat history).
  - Integration with external AI APIs.
- **Key Files:**
  - `backend/src/main/java/com/UoB/AILearningTool/`: 
    - `model/`: Stores definitions for user and chat SQL database tables.
    - `repository/`: Stores `repository` interfaces - they allow to search for records conveniently, in Java.
    - `AiLearningToolApplication.java`: Main application file; Start the backend server.
    - `DatabaseController.java`: Manages user and chat data.
    - `OpenAIAPIAuthenticator.java`: Stores the API key, fetches Bearer token.
    - `OpenAIAPIController.java`: Implements backend <-> OpenAI communication.
    - `SpringController.java`: Exposes REST endpoints for chat interactions.
    - `StringTools.java`: Includes legacy pre-JSON string parsing methods and random string generators.
    - `WatsonxResponse.java`: Abstracts the status code and response value from other API request information, mostly used in some unit tests.
  - `backend/src/test`: Includes all backend unit tests.
  - `backend/src/main/resources/`:
    - `application.properties` - Stores Spring Boot parameters, such as port, paths and SSL configuration.
    - `static/` - Stores assembled Vue.js frontend code.
- **Dependencies:**  
  - Spring Boot Starter Web, JDBC, Logging.

### Frontend:

- **Framework:** Vue.js  
- **Key Features:**  
  - Chatbot UI with a welcome screen, message display (including Markdown formatting and animation via the TypingText component), and an input area.
  - Text-to-speech (TTS) functionality using the Web Speech API, with a speaker icon provided for assistant messages.
  - SQLite database, used to store chat information in case of server failures.
- **Key Files & Directories:**
  - `src/`: 
    - `Display interface/`: Includes Vue components for login, main view, and cookie management.
    - `components/`:
      - `MainContent.vue`: Main chat component.
      - `helpers/TypingText.vue`: Component to animate assistant messages.
      - `HistorySidebar.vue`: component to display list of chat sessions.
      - `SettingsSidebar.vue`: component to display interface for configuring settings.
    - `assets/`:
      - `color.js` - contains standard and high contrast colour scheme constants.
      - `language.js` - contains translations of UI.
      - `globalConstants.js` - contains the backend URL.
      - `logo.png` - contains the project logo.
      - `responsive.css` - contains stylesheet used for responsive UI.
    - `main.js`: Initializes the Vue app, imports dependencies, and mounts the root component.
    - `tests/unit`: Includes all frontend unit tests (in Jest).
- **Build Process:**  
  - Managed by the frontend-maven-plugin in the Maven build.
  - The frontend build output (from `npm run build`) is copied into `backend/src/main/resources/static`.


---

## Setup & Installation:

1. **Clone the Repository**:
   First, ensure you have [Git installed](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) on your machine. Then, open your terminal or command prompt and run the following command to clone the repository:
   ```git clone https://github.com/spe-uob/2024-AILearningTool.git```.

2. **Navigate to the Project Directory**:
   After cloning, navigate to the project directory:
   ```cd 2024-AILearningTool```.

3. **Backend Requirements**:
   The backend of this project is built using Java 21 and uses Maven as the build automation tool. Shell scripts are also required to build the backend, so make sure you have the following installed on your machine:
   - Latest Java Development Kit (JDK) 21 installation guide [here](https://www.oracle.com/uk/java/technologies/downloads/#java21).
   - Latest Maven stable release installation guide [here](https://maven.apache.org/download.cgi).
   - Linux-based operating system with Bash support (to execute `localExecute.sh` shell script).

4. **Frontend Requirements**:
   The frontend of this project is built using Vue 3, and it also requires npm and Yarn. Also, the frontend unit tests use Jest. Make sure you have them installed: 
   - npm installation guide [here](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm).
   - Yarn installation guide [here](https://classic.yarnpkg.com/lang/en/docs/install/#windows-stable).
   - Vue 3 installation guide [here](https://vuejs.org/guide/quick-start.html).
   - Jest installation guide [here](https://jestjs.io/docs/getting-started).

5. **Continuous Deployment with Docker**:
     This project uses Docker for Continuous Deployment (CD), so you will need to ensure that Docker is installed on the server (and your local machine if you wish). If it 
     isn't installed, you can do so [here](https://docs.docker.com/engine/install/).
     CD is implemented using `deploy-ghcr.yml` GitHub workflow.
     The workflow initialises the latest version of Ubuntu OS, sets up Java 21 and Maven;
     substitutes the `$OPENAI_API_KEY` in `OpenAIAuthenticator.java` file to the OpenAI API key;
     replaces `application.properties` file with a one that enables HTTPS and uses `keystore.p12`;
     replaces `$BACKEND_URL` variable in frontend;
     builds the `.jar` file and a container with it, pushes the container to GHCR;
     connects to your server using `ssh`, pull the container from GHCR and runs it.

     It uses the following "secrets":
     - `OPENAI_API_KEY` - stores OpenAI API key, can be obtained [here](https://platform.openai.com/api-keys).
     - `CONTAINER_REGISTRY_PAT` - PAT for GHCR.
     - `SERVER_IP` - IP address of your server.
     - `SERVER_USER` - Username that the workflow can ssh to your server with.
     - `SSH_PRIVATE_KEY` - Private key that can be used to authenticate to your server.
     It also uses `BACKEND_URL` environment variable, which is defined on line 13.

    Please ensure that you have a `database.db` file in the SSH user directory on the server. If the file does not exist, create one using: ```touch database.db```.

6. **(For CD execution) Add keystore.p12 file to the root of your server**:
   You can obtain private key and full certificate chain files for free using [Certbot](https://certbot.eff.org/) - a software that helps with issuing Let's Encrypt certificates.
   You then have to create `keystore.p12` that has to contain both private key and full certificate chain files, protected by password noted in `application.properties` (default:`ailearntool`).
   If you don't want to use SSL - don't use the Docker image, but run the server using `localExecute.sh` instead.

7. **Open the Project in Your IDE**:
   Open the cloned repository in your preferred Integrated Development Environment (IDE) (we recommend [IntelliJ](https://www.jetbrains.com/idea/download/?section=windows)) for further development.

8. **Test and Run the Application**:
   - To run the backend unit tests, use the commands: ```cd backend``` and then ```mvn test```.
   - To run the frontend unit tests, use the commands: ```cd frontend``` and then ```npm run test:unit```.
   - To run the application, run the localExecute shell script: ```./localExecute.sh OpenAI_API_Key Port_number``` (replace OpenAI_API_Key and Port_number with their respective values).
---
## Configuration Details:
### Application Properties ###
- Location:
  - backend/src/main/resources/application.properties
- Key Settings:
  - `server.port`: Default is 8080 on local, non-Docker execution.
  - SSL settings (keystore, key alias, etc.) if HTTPS is enabled:
    ```
    server.port=443
    server.ssl.key-store=./file:keystore.p12
    server.ssl.key-store-password=ailearntool"
    ```
  - Encoding settings:
    ```
    spring.http.encoding.charset=UTF-8
    spring.http.encoding.enabled=true
    spring.http.encoding.force=true
    ```

- Overwriting at Runtime:
  - The AiLearningToolApplication.java file includes a method to overwrite application.properties at startup, ensuring the latest configuration (including encoding and SSL settings) is used.

### Frontend Configuration ###
- Dependencies:
  - Managed via package.json.
- Build Automation:
  - The Maven plugin frontend-maven-plugin runs npm install and npm run build from the frontend folder. The output is copied to backend/src/main/resources/static.

### SSL Configuration ###
  By default, when you execute AI Learning Tool locally, SSL is disabled and backend works on port 8080.
  SSL is enabled by GitHub runner `deploy-ghcr.yml` by replacing variable names with their values.
  Pay attention to the following GitHub secrets - you will need to replace them with your own:
  It uses the following "secrets":
  - `OPENAI_API_KEY` - stores OpenAI API key, can be obtained [here](https://platform.openai.com/api-keys).
  - `CONTAINER_REGISTRY_PAT` - PAT for GHCR.
  - `SERVER_IP` - IP address of your server.
  - `SERVER_USER` - Username that the workflow can ssh to your server with.
  - `SSH_PRIVATE_KEY` - Private key that can be used to authenticate to your server. It also uses BACKEND_URL environment variable, which is defined on line 13.

  Also keep in mind the `BACKEND_URL` environment variable - it is used for frontend -> backend communication.
  You can obtain private key and full certificate chain files for free using Certbot - a software that helps with issuing Let's Encrypt certificates. 
  You then have to create a `keystore.p12` file that contains both private key and full certificate chain files generated by Certbot, protected by password noted in application.properties (default:ailearntool). 
  If you don't want to use SSL (and communicate using HTTPS) - don't use the Docker image, but run the server using `localExecute.sh` instead.

### OpenAI configuration ###
  We use OpenAI services for the chatbot functionality. You will have to create your own API key and an assistant.
  `Assistant` can be created [here](https://platform.openai.com/assistants).
  Your assistant needs to include a JSON file with information about courses on IBM SkillsBuild. IBM has no free APIs for this, so you will have to do this manually or use a file from this repo. You can choose any base model you like, although keep in mind that this will affect performance and usage costs.
  Replace the assistant_id in `runThread` method of `OpenAIAPIController.java` with your assistant id value.

---
## Testing & Troubleshooting:
- Running backend tests:
  - `localExecute.sh` does the testing for you before execution, but if you already launched this at least once, you can also do:
  ```bash
  mvn test
  ```
- Running frontend tests (only works after `localExecute.sh` or if you have substituted variable names with values manually):
  ```bash
  npm run test:unit
  ```
- Debugging:
  - Use developer tools of your browser to inspect console errors, network requests, and verify resource loading.
  - Make sure to clear your browser's cache before you try to debug new version of your code.
  - For backend debugging, enable Spring Boot DevTools for live reload.
---
## License:
The project is licensed under the following [MIT License.](https://github.com/spe-uob/2024-AILearningTool/blob/feature/issue-210/Improve-documentation/LICENSE)

