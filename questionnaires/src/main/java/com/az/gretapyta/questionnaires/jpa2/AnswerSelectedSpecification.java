package com.az.gretapyta.questionnaires.jpa2;

import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class AnswerSelectedSpecification {
  public static Specification<AnswerSelected> withQuestionAnswerId(@NonNull Integer questionAnswerId) {
    return (root, query, cb) -> cb.equal(root.get("questionAnswer").get("id"), questionAnswerId);
  }

  public static Specification<AnswerSelected> withOptionId(@NonNull Integer optionId) {
    return (root, query, cb) -> cb.equal(root.get("optionAnswer").get("id"), optionId);
  }
}