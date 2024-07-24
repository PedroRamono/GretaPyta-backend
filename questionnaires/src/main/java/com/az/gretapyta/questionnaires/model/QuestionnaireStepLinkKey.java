package com.az.gretapyta.questionnaires.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class QuestionnaireStepLinkKey implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "questionnaire_id")
  private Integer questionnaireId;

  @Column(name = "step_id")
  private Integer stepId;

  public QuestionnaireStepLinkKey() {
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((stepId == null) ? 0 : stepId.hashCode());
    result = prime * result + ((questionnaireId == null) ? 0 : questionnaireId.hashCode());
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
    QuestionnaireStepLinkKey other = (QuestionnaireStepLinkKey) obj;
    if (stepId == null) {
      if (other.stepId != null)
        return false;
    } else if (!stepId.equals(other.stepId))
      return false;
    if (questionnaireId == null) {
      return other.questionnaireId == null;
    } else return questionnaireId.equals(other.questionnaireId);
  }
}