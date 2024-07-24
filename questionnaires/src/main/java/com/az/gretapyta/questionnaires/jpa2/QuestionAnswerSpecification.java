package com.az.gretapyta.questionnaires.jpa2;

import com.az.gretapyta.questionnaires.model2.QuestionAnswer;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class QuestionAnswerSpecification {

  public static Specification<QuestionAnswer> withUserQuestionnaireId(@NonNull Integer userQuestionnaireId) {
    return (root, query, cb) -> cb.equal(root.get("userQuestionnaire").get("id"), userQuestionnaireId);
  }

  public static Specification<QuestionAnswer> withQuestionId(@NonNull Integer questionId) {
    return (root, query, cb) -> cb.equal(root.get("question").get("id"), questionId);
  }

  public static Specification<QuestionAnswer> withAnswerProvidedId(@NonNull Integer answerProvidedId) {
    return (root, query, cb) -> cb.equal(root.get("answerProvided").get("id"), answerProvidedId);
  }
}