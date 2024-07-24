package com.az.gretapyta.questionnaires;

import static org.assertj.core.api.Assertions.assertThat;

import com.az.gretapyta.questionnaires.controller.QuestionnaireController;
import com.az.gretapyta.questionnaires.repository.QuestionnairesRepository;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {QuestionnairesApp.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT // ,
) // properties ={"spring.profiles.active=test"}
@Category(IntegrationTest.class)
public class QuestionnairesAppIT {
  @Autowired
  private QuestionnairesRepository questionnairesRepository;
  @Autowired
  private QuestionnairesService questionnairesService;
  @Autowired
  private QuestionnaireController questionnaireController;

  @Test
  @Order(value = 1)
  @DisplayName("(1) Application context components are loaded.")
  void test1() {
    assertThat(questionnairesRepository).isNotNull();
    assertThat(questionnairesService).isNotNull();
    assertThat(questionnaireController).isNotNull();
  }
}