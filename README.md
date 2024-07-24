# Questionnaires-main


## Development

### Build

```
mvnw clean package
```

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
     - UserQuestionaire
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
  