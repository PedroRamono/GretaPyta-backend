package com.az.gretapyta.questionnaires.jpa;

import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import org.springframework.data.jpa.domain.Specification;

public class QuestionnaireStepLinkSpecification {

  public static Specification<QuestionnaireStepLink> hasQuestionnaires(int questionnaireId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("questionnaire_id"), questionnaireId);
  }

  public static Specification<QuestionnaireStepLink> hasSteps(int stepId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id").get("stepId"), stepId);
  }
}