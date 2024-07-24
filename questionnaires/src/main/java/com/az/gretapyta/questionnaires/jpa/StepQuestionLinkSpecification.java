package com.az.gretapyta.questionnaires.jpa;

import com.az.gretapyta.questionnaires.model.StepQuestionLink;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class StepQuestionLinkSpecification {

  public static Specification<StepQuestionLink> withParentStepId(@NonNull Integer stepId) {
    return (root, query, cb) -> cb.equal(root.get("stepDown").get("id"), stepId);
  }

  public static Specification<StepQuestionLink> withParentQuestionId(@NonNull Integer questionId) {
    return (root, query, cb) -> cb.equal(root.get("questionUp").get("id"), questionId);
  }
}