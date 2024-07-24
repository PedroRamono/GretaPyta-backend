package com.az.gretapyta.questionnaires.jpa;

import com.az.gretapyta.questionnaires.model.Questionnaire;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class QuestionnaireSpecification {

  public static Specification<Questionnaire> withParentId(@NonNull Integer drawerId) {
    return (root, query, cb) -> cb.equal(root.get("drawer").get("id"), drawerId);
  }
}