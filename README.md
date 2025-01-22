# Questionnaires-main

## Purpose
Questionnaire/Quiz/Survey/Prediction (QQSP) Self-Service Application.
This Project is its back-end part.

## Description
A User can utilize the Application anonymously or can create his/her 
profile and use it in logged-in state.
- Anonymous User can only take (answer) QQSP.
- Logged User can take QQSP but also can create his/her custom QQSP
for others to answer.
- a commercial User can create QQSP and then can have an access to
aggregated and detailed results data.

## Project Structure
There are 4 main modules: 
- (a) qcore - common base library
- (b) questionnaire-admin
- (c) questionnaires
- (d) qusers
 
Where (b), (c) and (d) are of Application-level (potentially for Microservices architecture).

**Currently only 'questionnaires' Application is being developed.**
 
## Structure
- (1) a Drawer of specific type (Category) holds like-minded Questionnaires.
- (2) a Questionnaire (or Quiz/Survey/Predictions) should have one or multiple Steps (Sections).
- (3) a Step has Question(s).
- (4) a Question can provide Option(s) for User to select from - or expect User custom input for Answer.

## Development

Start your application with the following command - here with the profile `production`:

```
java -Dspring.profiles.active=production -jar ./target/questionnairesmain-0.0.1-SNAPSHOT.jar
```

If required, a Docker image can be created with the Spring Boot plugin. Add `SPRING_PROFILES_ACTIVE=production` as
environment variable when running the container.

```
mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=com.az.gretapyta/questionnairesmain
```
## Project lifecycle

(From Apache Maven Project - Introduction to the Build Lifecycle)
   - validate - validate the project is correct and all necessary information is available
   - compile - compile the source code of the project
   - test - test the compiled source code using a suitable unit testing framework. These tests should not require the code be packaged or deployed
   - package - take the compiled code and package it in its distributable format, such as a JAR.
   - verify - run any checks on results of integration tests to ensure quality criteria are met
   - install - install the package into the local repository, for use as a dependency in other projects locally
   - deploy - done in the build environment, copies the final package to the remote repository for sharing with other developers and projects.

### Testing:

1. Unit tests:
 
    mvn rest


2. Integration tests (with Unit tests as well):
 
   mvn verify


3. E2E test:
 
   (TODO...)
 
## Entities' Model hierarchy:

  1. Drawer
       - Questionnaire
         - Step
           - Question
             - Option


  2. User
     - UserMessage 
     - UserQuestionnaire
       - QuestionAnswer
         - SelectedAnswer
         - ProvidedAnswer
           - GenericValue
            

      
### One-to-One link:
  QuestionAnswer <--> ProvidedAnswer 

### Many-to-Many links:
1. Questionnaire <==> Step (QuestionnaireStepLink)
2. Step <==> Question (StepQuestionLink) 
3. Question <==> Option (QuestionOptionLink)
  
