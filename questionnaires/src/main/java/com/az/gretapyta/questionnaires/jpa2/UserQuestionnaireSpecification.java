package com.az.gretapyta.questionnaires.jpa2;

import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class UserQuestionnaireSpecification {

  public static Specification<UserQuestionnaire> withUserId(@NonNull Integer userId) {
    return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
  }

  public static Specification<UserQuestionnaire> withQuestionnaireId(@NonNull Integer questionnaireId) {
    return (root, query, cb) -> cb.equal(root.get("questionnaireUser").get("id"), questionnaireId);
  }
}