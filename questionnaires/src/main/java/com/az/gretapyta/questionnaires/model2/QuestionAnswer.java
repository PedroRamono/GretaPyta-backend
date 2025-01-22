package com.az.gretapyta.questionnaires.model2;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "QUESTION_ANSWERS")
public class QuestionAnswer extends BaseEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  // QuestionAnswer <- UserQuestionnaire:
  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = UserQuestionnaire.class)
  @JoinColumn(name = "user_questionnaire_id", referencedColumnName = "id", nullable = false)

  private UserQuestionnaire userQuestionnaire;

  // QuestionAnswer <- Question:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "question_id", nullable = false)
  private Question question;

  // QuestionAnswer -> AnswerSelection:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionAnswer", cascade = CascadeType.ALL)
  private Set<AnswerSelected> answerSelections;

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
  @JoinColumn(name = "answer_provided_id")
  private AnswerProvided answerProvided;
}