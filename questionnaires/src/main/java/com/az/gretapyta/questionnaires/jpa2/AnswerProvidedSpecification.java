package com.az.gretapyta.questionnaires.jpa2;

import com.az.gretapyta.questionnaires.model2.AnswerProvided;

import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class AnswerProvidedSpecification {
  public static Specification<AnswerProvided> withQuestionAnswerId(@NonNull Integer questionAnswerId) {
    return (root, query, cb) -> cb.equal(root.get("questionAnswer").get("id"), questionAnswerId);
  }
}