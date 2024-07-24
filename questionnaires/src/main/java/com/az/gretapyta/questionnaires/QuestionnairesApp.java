package com.az.gretapyta.questionnaires;

import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GeneralProperties.class) // (QuestionnaireConfig.class)
public class QuestionnairesApp {
  public static void main(final String[] args) {
    SpringApplication.run(QuestionnairesApp.class, args);
  }
}