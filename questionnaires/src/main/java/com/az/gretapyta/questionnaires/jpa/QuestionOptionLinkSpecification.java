package com.az.gretapyta.questionnaires.jpa;

import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class QuestionOptionLinkSpecification {

  public static Specification<QuestionOptionLink> withParentQuestionId(@NonNull Integer questionId) {
    return (root, query, cb) -> cb.equal(root.get("questionDown").get("id"), questionId);
  }

  public static Specification<QuestionOptionLink> withParentOptionId(@NonNull Integer optionId) {
    return (root, query, cb) -> cb.equal(root.get("option").get("id"), optionId);
  }
}