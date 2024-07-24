package com.az.gretapyta.questionnaires.model2;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model.Option;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "ANSWERS_SELECTED")
public class AnswerSelected extends BaseEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  // AnswerSelection <- QuestionAnswer:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "question_answer_id", nullable = false)
  private QuestionAnswer questionAnswer;

  // AnswerSelection <- Option:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "option_id", nullable = false)
  private Option optionAnswer;
}