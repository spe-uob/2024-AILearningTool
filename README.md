# 2024-AILearningTool

<div align="center">

![logo](frontend/src/assets/logo.png)

</div>

## Table of Contents
- [Project Brief](#project-brief)
- [Stakeholders](#stakeholders)
- [User Stories](#user-stories)
- [Requirements](#list-of-requirements)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [User Instructions](#user-instructions)
- [Chatbot Interaction Flow](#chatbot-interaction-flow)
- [Developer Instructions](#developer-instructions)
- [Team Members](#team-members)

## Project Brief
"Create a web app Watson chatbot that the students (users) can access via a QR code from their
mobile device. The QR code could be printed out and left at strategic areas around the university for
new students to see.
The chatbot would provide information that a university student wished they knew when they first
arrived at your university. The content of the chatbot could be targeted to serve the students of a
particular course - or it could be usable by every university student at your university.
Content can include:
- what is IBM SkillsBuild
- what courses are available on SkillsBuild
- what free technologies are available on SkillsBuild
- allow the student to enter details of their course details - and provide links to relevant
SkillsBuild resources."

## Stakeholders
### Students:
- Receive personalized course recommendations
- Ask questions about navigating the platform and courses
- Access resources and assistance to resolve issues quickly
- View recommended learning paths and certifications

### IBM SkillsBuild Team:
- Monitor user interactions with the chatbot
- Analyze which chatbot responses are most helpful or effective based on user ratings.
- Manage chatbot content, updates, and integrations with the platform
- Escalate complex queries from the chatbot to human support

### Data Protection Authorities:
- Ensure compliance with data protection laws (e.g., GDPR)
- Monitor how user data is collected, processed, and stored
- Verify that the chatbot handles sensitive user information securely
- Enforce privacy guidelines for user consent and data management

## User stories

### Student A:
As a University of Bristol student, I want the chatbot to be able to give me advice if I am struggling with university life so that I am able to focus on my studies.

### Student B:
As a Computer Science student, I want to find good online courses with extracurricular knowledge, so that I could expand my skills and get some reputable certifications that will benefit my career.

### Student C:
As a student, I want to receive personalized course recommendations and ask the chatbot questions about the SkillsBuild platform, so that I can easily find relevant courses and quickly resolve any issues or confusion I encounter. 

### Student D:
As a college student, I would like someone to help me with my questions. I need a chatbot to help me understand IBM Skillsbuild better and find a course that suits me better!

### Student E:
As a university student, I want the AI chatbot to explain course concepts and foundational knowledge so that I can better understand and adapt to the new academic environment.

## List of requirements
### Must-have:
	1. Web-based UI - webpage capable of receiving inputs and returning outputs from Watsonx AI.
	2. Chatbot that can hold conversation and guide the user correctly. 
	3. Chatbot needs to provide a list of recommended courses for Computer Science students based on their interests.
	4. Chatbot needs to be able to answer questions about the IBM Skillsbuild platform.
	5. Chatbot needs to be able to provide advice for new university students.
	6. Web-based UI needs to ask users for their consent to storing "cookies" with anonymous identifiers on their device, as well as provide the functionality to withdraw the consent.
### Should-have
	1. Web-based UI needs to have a responsive design and use some CSS design framework.
	2. Chatbot needs to provide a list of recommended courses for students based on their course interests (not just computer science).
	3. Conversation should be initialized by clicking one of the buttons with predefined inputs - this will help to keep a relevant conversation topic.
	4. Web application should be able to save the conversation history unless the user has opted-out of cookies.
	5. Link to the chatbot should to be advertised by putting QR codes around the campus.
### Could-have:
	1. Users will be able to rate chatbot responses, allowing analysis of which responses are most helpful or effective.
 	2. Users will be able to contact SkillsBuild support directly through the chatbot for assistance with more complex issues.
### Won't-have:
	1. Chatbot will not assist with academic questions (eg. solving homework)
	
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

## Tech Stack:
### Frontend
The front end is a JavaScript Vue 3-based web application. It makes requests to the backend using HTTP requests.
### Backend
The backend is based on Spring Boot (an open-source Java framework).
Data will be stored in a SQLite database.
User prompts for the chatbot will be sent using API requests from the Spring Boot backend to the OpenAI language model.

![architecture_diagram](/docs/architecture_diagram.png)

## User Instructions:

1. **Open the Web App**:  

   Using any device with internet access (smartphone, tablet, laptop, etc.), open a web browser and navigate to [ailearningtool.ddns.net](ailearningtool.ddns.net).

2. **Cookie Consent**:  

   Upon opening the website, you will see a prompt notifying you that the chatbot uses local storage. By clicking "I Understand", you consent to the use of local storage for saving your preferences and conversation history. This helps the chatbot provide a more personalized experience across sessions.

3. **Login Page**: 

   To access the chatbot, you must first register an account. Once registered, you can log in using your credentials.

4. **Start Chatting with the Chatbot**:  

   After logging in, you can choose from three chat initialization buttons:
   - **I need help with choosing a course**
   - **I need help with the IBM SkillsBuild platform**
   - **I have questions about university life**

5. **Providing Additional Information**:  

   For certain queries (like "I need help with choosing a course"), the chatbot may ask for a little more information. This could include:
   - Your current experience level
   - Your preferred duration for the course
   - Specific topics or skills you're interested in
     
   The chatbot only asks what’s necessary to give you the best answer.

6. **Follow-Up Questions**:  

   Feel free to ask any follow-up questions after the chatbot provides its initial response. The chatbot can also share useful resources, such as links to courses.

7. **Returning to the Chat**:  

   If you click "I Understand", your conversation history and preferences will be saved in local storage. You can return to the web app at any time to continue where you left off or ask follow-up questions based on previous conversations.

8. **Add to Home Screen or Desktop**:

   If you enjoy using our app, feel free to download it to your home screen or desktop. Since we’ve implemented Progressive Web App (PWA) support, you can install the app on your device for quick access without needing to visit the website each time.

## Chatbot Interaction Flow:

This flowchart outlines the interaction pathways within the chatbot, guiding users through key topics such as SkillsBuild courses, university life questions, and IBM SkillsBuild platform information. Each pathway details the chatbot's prompts, and user responses, providing an overview of the chatbot’s functionality.

![watson_flow](/docs/watson_flow.png)

## Developer Instructions:
To get started with developing or contributing to this project, follow the steps below:

1. **Clone the Repository**:
   First, ensure you have [Git installed](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) on your machine. Then, open your terminal or command prompt and run the following command to clone the repository:
   ```git clone https://github.com/spe-uob/2024-AILearningTool.git```.

2. **Navigate to the Project Directory**:
   After cloning, navigate to the project directory:
   ```cd 2024-AILearningTool```.

3. **Backend Requirements**:
   - The backend of this project is built using Java 21, so make sure you have Java 21 installed on your machine. You can download the latest Java Development Kit (JDK) 21 from [here](https://www.oracle.com/uk/java/technologies/downloads/#java21).
   - The backend uses Maven as the build automation tool. If you don't have Maven installed, download the latest stable release [here](https://maven.apache.org/download.cgi).

5. **Frontend Requirements**:
   The frontend of this project is built using Vue 3, and it also requires npm and Yarn. Also, the frontend unit tests use Jest. Make sure you have them installed: 
   - npm installation guide [here](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm).
   - Yarn installation guide [here](https://classic.yarnpkg.com/lang/en/docs/install/#windows-stable).
   - Vue 3 installation guide [here](https://vuejs.org/guide/quick-start.html).
   - Jest installation guide [here](https://jestjs.io/docs/getting-started).
  
5. **Continuous Deployment with Docker**:
   This project uses Docker for Continuous Deployment (CD), so you will need to ensure that Docker is installed on the server (and your local machine if you wish). If it isn't installed, you can do so [here](https://docs.docker.com/engine/install/).

6. **Open the Project in Your IDE**:
   Open the cloned repository in your preferred Integrated Development Environment (IDE) (we recommend [IntelliJ](https://www.jetbrains.com/idea/download/?section=windows)) for further development.

7. **Add keystore.p12 file to backend/src/main/resources in the cloned repository**:
    Keystore has to contain both private key and full certificate chain files.
    Not adding the keystore file will result in unencrypted (non-HTTPS) connection.

8. **Test and Run the Application**:
   - To run the backend unit tests, use the commands: ```cd backend``` and then ```mvn test```.
   - To run the frontend unit tests, use the commands: ```cd frontend``` and then ```npm run test:unit```.
   - To run the application, run the localExecute shell script: ```./localExecute.sh API Key``` where ```API Key``` is the OpenAI API Key being used (replace ```API Key``` with the actual OpenAI API Key).
  
## Team Members:

| Name              | GitHub                                                      						| Email                 |
|-------------------|-----------------------------------------------------------------------------------------------------------|-----------------------|                           
| Vlad Kirilovics   | [vladislav-k1](https://github.com/vladislav-k1) and [kirilovich-vlad](https://github.com/kirilovich-vlad) | fi23561@bristol.ac.uk |
| Gerard Chaba      | [GerardChabaBristol](https://github.com/GerardChabaBristol) 						| tl23383@bristol.ac.uk |  
| Mohammed Elzobair | [yi23484](https://github.com/yi23484)	                  						| yi23484@bristol.ac.uk | 
| Weifan Liu 	    | [Liuwf4319](https://github.com/Liuwf4319)		          						| au22116@bristol.ac.uk |
| Zixuan Zhu 	    | [RainBOY-ZZX](https://github.com/RainBOY-ZZX)	          						| kh23199@bristol.ac.uk | 
| Siyuan Zhang 	    | [Siyuan106](https://github.com/Siyuan106)		          						| gr23994@bristol.ac.uk |
