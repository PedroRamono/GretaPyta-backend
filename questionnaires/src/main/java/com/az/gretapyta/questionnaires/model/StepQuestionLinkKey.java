package com.az.gretapyta.questionnaires.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
public class StepQuestionLinkKey implements Serializable {

  @Column(name = "step_id")
  private Integer stepId;

  @Column(name = "question_id")
  private Integer questionId;

  public StepQuestionLinkKey() {
  }

  @Override
  public int hashCode() {
    final int prime = 51;
    int result = 1;
    result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
    result = prime * result + ((stepId == null) ? 0 : stepId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StepQuestionLinkKey other = (StepQuestionLinkKey) obj;
    if (questionId == null) {
      if (other.questionId != null)
        return false;
    } else if (!stepId.equals(other.questionId))
      return false;
    if (stepId == null) {
      return other.stepId == null;
    } else return stepId.equals(other.stepId);
  }
}