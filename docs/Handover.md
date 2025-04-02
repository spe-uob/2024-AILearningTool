# AILearningTool - Project Handover Document

---
## Table of Contents:
- [Introduction](#introduction)
- [Architecture Diagram](#architecture-diagram)
- [Code Explanation](#code-explanation)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Setup & Installation](#setup--installation)
  - [Prerequisites](#prerequisites)
  - [Cloning the Repository](#cloning-the-repository)
  - [Building the Project](#building-the-project)
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
  - [Handover.md](/docs/Handover.md): Includes key information to facilitate handover to the client.
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

![Architecture Diagram](https://github.com/spe-uob/2024-AILearningTool/blob/feature/issue-210/Improve-documentation/docs/Diagrams/architecture_diagram.png)

## Code Explanation:

### Backend:

- **Framework:** Spring Boot 3.3.4  
- **Main Functionality:**  
  - User and chat management (signup, chat creation, sending messages, retrieving chat history).
  - Integration with external AI APIs.
- **Key Files:**  
  - `AiLearningToolApplication.java`: Main application file; handles configuration overwrites and SSL keystore setup.
  - `DatabaseController.java`: Manages user and chat data.
  - `SpringController.java`: Exposes REST endpoints for chat interactions.
  - `main/resources`: Includes static resources such as application properties.
  - `main/test` Incldes all backend unit tests (in Java).
- **Dependencies:**  
  - Spring Boot Starter Web, Security, JDBC, Logging.

### Frontend:

- **Framework:** Vue.js  
- **Key Features:**  
  - Chat UI with a welcome screen, message display (including markdown formatting and animation via the TypingText component), and an input area.
  - Text-to-speech (TTS) functionality using the Web Speech API, with a speaker icon provided for assistant messages.
  - Automated frontend build integrated with the backend using Maven (frontend-maven-plugin).
- **Key Files & Directories:**
  - `src/Display interface`: Includes Vue components for login, main view, and cookie management.
  - `src/components/MainContent.vue`: Main chat component.
  - `src/components/helpers/TypingText.vue`: Component to animate assistant messages.
  - `src/assets/`: Contains theme configuration and language translation files.
  - `src/main`: Initializes the Vue app, imports dependencies, and mounts the root component.
  - `tests/unit`: Includes all frontend unit tests (in Jest).
- **Build Process:**  
  - Managed by the frontend-maven-plugin in the Maven build.
  - The frontend build output (from `npm run build`) is copied into `backend/src/main/resources/static`.


---

## Setup & Installation:

### Prerequisites:
- Java JDK 21+  
- Maven 3.6+  
- Node.js (recommended v18+) & npm (managed by frontend-maven-plugin)  
- Git

### Cloning the Repository:
```bash
git clone https://github.com/spe-uob/2024-AILearningTool.git
```

### Building the Project:

**Navigate to project directory**
```bash
cd 2024-AILearningtool.git
```

**Clean and build entire project**
```bash
./localExecute.sh <OpenAI API Key>
```

**Access the Application:**

- Local:
```bash
http://localhost:8080
```
- On AWS/EC2:
```bash
Use the instance’s public IP or DNS with port 8080 (e.g., http://<public-ip>:8080).
```
---
## Configuration Details:
### Application Properties ###
- Location:
  - backend/src/main/resources/application.properties
- Key Settings:
  - server.port: Default is 8080.
  - SSL settings (keystore, key alias, etc.) if HTTPS is enabled.
  - Encoding settings:
    ``` bash
    Copy
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
---
## Testing & Troubleshooting:
- Running backend tests:
  ```bash
  mvn test
  ```
- Running frontend tests:
  ```bash
  npm run test:unit
  ```
- Debugging:
  - Use browser developer tools to inspect console errors, network requests, and verify resource loading.
  - For backend debugging, enable Spring Boot DevTools for live reload.
---
## License:
The project is licensed under the following [MIT License.](https://github.com/spe-uob/2024-AILearningTool/blob/feature/issue-210/Improve-documentation/LICENSE)

