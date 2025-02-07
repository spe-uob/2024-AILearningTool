# 2024-AILearningTool
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

- [workflows](/.github/workflows): Contains Maven Continuous Integration.
- [docs](/docs): Contains all project-related documentation. Notable files include:
  - [ETHICS.md](/docs/ETHICS.md): Includes the date of the ethics pre-approval request.
  - All diagrams/flowcharts.
- [frontend](/frontend): Contains all of the front-end code (in Vue 3 and Yarn) and documents (not used in the MVP stage):
  - [api](/frontend/api): Includes the cookies API.
  - [public](/frontend/public): Includes some front-end documents.
  - [src](/frontend/src): Includes the front-end code.
- [backend](/backend): Contains all of the back-end code (in Java), Maven config and documents:
  - [src/main](/backend/src/main): Includes the back-end Java code.
  - [src/main/static](/backend/src/main/resources/static): Includes all static documents, including index.html of the front-end.
  - [src/test/java/com/UoB/AILearningTool](/backend/src/test/java/com/UoB/AILearningTool): Includes all backend Java the unit tests.
  - [mvnw]((/backend/mvnw)), [mvnw.cmd]((/backend/mvnw.cmd))  and [pom.xml](/backend/pom.xml): Documents for Maven.
- [LICENSE](/LICENSE): Includes the project's MIT license file.

## Tech Stack:
### Frontend
The front end is a JavaScript Vue 3-based web application. It makes requests to the backend using HTTP requests.
### Backend
The backend is based on Spring Boot (open-source Java framework).
Data will be stored in a MariaDB database.
User prompts for the chatbot will be sent using API requests from the Spring Boot backend to the OpenAI language model.

![architecture_diagram](/docs/architecture_diagram.png)

## User Instructions:

1. **Open the Web App**:  

   Using any device with internet access (smartphone, tablet, laptop, etc.), open a web browser and navigate to the provided link [insert URL here].

2. **Cookie Consent**:  

   Upon opening the website, you will be prompted to accept or decline optional cookies. These cookies help improve your experience by storing preferences and conversation history. You can choose:
   - **Accept Cookies**: The chatbot will remember your preferences and chat history for future sessions.
   - **Decline Cookies**: The chatbot will function without storing your preferences or chat history.

3. **Start Chatting with the Chatbot**:  

   Once you are in, you can start typing your questions into the chatbox. You can ask about the following topics:
   - **What is IBM SkillsBuild?**
   - **What courses are available on SkillsBuild?**
   - **What free technologies are available on SkillsBuild?**
   - **General advice or questions related to university life.**

4. **Providing Additional Information**:  

   For certain questions (like available courses or university-related advice), the chatbot may ask for a little more information. This could include:
   - Your area of interest
   - Courses you are currently enrolled in  
   The chatbot only asks what’s necessary to give you the best answer.

5. **Follow-Up Questions**:  

   Feel free to ask any follow-up questions after the chatbot provides its initial response. The chatbot can also share useful resources, such as links to courses or articles.

6. **Adjust Preferences (Optional)**:  
  
   If you agreed to optional cookies, you can later modify your cookie consent and other settings in the web app’s preferences section. This is completely optional.

7. **Returning to the Chat**:  

   If you accepted the optional cookies, your conversation history will be saved for 30 days. You can return to the web app at any time within that period to continue where you left off or ask follow-up questions based on previous conversations.

## Chatbot Interaction Flow:

This flowchart outlines the interaction pathways within the chatbot, guiding users through key topics such as SkillsBuild courses, university life questions, and IBM SkillsBuild platform information. Each pathway details the chatbot's prompts, and user responses, providing an overview of the chatbot’s functionality.

![watson_flow](/docs/watson_flow.png)

## Developer Instructions:
To get started with developing or contributing to this project, follow the steps below:

1. **Clone the Repository**:
   First, ensure you have Git installed on your machine. Then, open your terminal or command prompt and run the following command to clone the repository:
   ```git clone https://github.com/spe-uob/2024-AILearningTool.git```

2. **Navigate to the Project Directory**:
   After cloning, navigate to the project directory:
   ```cd 2024-AILearningTool```

3. **Install Java 21**:
   The project is built using Java 21, so make sure you have Java 21 installed on your machine. You can download the latest Java Development Kit (JDK) 21 from [here](https://www.oracle.com/uk/java/technologies/downloads/#java21).

4. **Install Maven**:
   This project uses Maven as the build automation tool. If you don't have Maven installed, download the latest stable release [here](https://maven.apache.org/download.cgi).

5. **Install Vue and Yarn**:
   The front end of this project is built using Vue 3 and Yarn, so make sure you have them installed: 
   - Vue 3 installation guide [here](https://vuejs.org/guide/quick-start.html)
   - Yarn installation guide [here](https://classic.yarnpkg.com/lang/en/docs/install/#windows-stable)

6. **Open the Project in Your IDE**:
   Open the cloned repository in your preferred Integrated Development Environment (IDE) (we recommend IntelliJ) for further development.

7. **Add keystore.p12 file to backend/src/main/resources in the cloned repository**:
    Keystore has to contain both private key and full certificate chain files.
    Not adding the keystore file will result in unencrypted (non-HTTPS) connection.

8. **Test and Run the Server**:
   - To run the unit tests, use the command ```mvn test```
   - To start the server, use the script ```startServer.sh```
  
9. **Continuous Deployment (CD) with Docker and GHCR**:
  Our project includes Continuous Deployment (CD) with Docker and GitHub Container Registry (GHCR). Every time changes are pushed to the dev branch, a new Docker image is automatically built and pushed to our [GHCR repository](https://ghcr.io/spe-uob/2024-ailearningtool:latest).
  To pull the latest Docker image from GHCR and update the application on the server, follow these steps:
    - Start the Docker service: ```sudo systemctl start docker```
    - Run the pull script: ```./pull-from-ghcr.sh```


## Team Members:

| Name              | GitHub                                                      						| Email                 |
|-------------------|-----------------------------------------------------------------------------------------------------------|-----------------------|                           
| Vlad Kirilovics   | [vladislav-k1](https://github.com/vladislav-k1) and [kirilovich-vlad](https://github.com/kirilovich-vlad) | fi23561@bristol.ac.uk |
| Gerard Chaba      | [GerardChabaBristol](https://github.com/GerardChabaBristol) 						| tl23383@bristol.ac.uk |  
| Mohammed Elzobair | [yi23484](https://github.com/yi23484)	                  						| yi23484@bristol.ac.uk | 
| Weifan Liu 	    | [Liuwf4319](https://github.com/Liuwf4319)		          						| au22116@bristol.ac.uk |
| Zixuan Zhu 	    | [RainBOY-ZZX](https://github.com/RainBOY-ZZX)	          						| kh23199@bristol.ac.uk | 
| Siyuan Zhang 	    | [Siyuan106](https://github.com/Siyuan106)		          						| gr23994@bristol.ac.uk |
