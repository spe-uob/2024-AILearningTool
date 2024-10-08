# 2024-AILearningTool
## Table of Contents
- [Project Brief](#project-brief)
- [Stakeholders](#stakeholders)
- [User Stories](#user-stories)
- [Requirements](#requirements)

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
	
