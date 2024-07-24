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
public class QuestionOptionLinkKey implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "question_id")
  private Integer questionId;

  @Column(name = "option_id")
  private Integer optionId;

  public QuestionOptionLinkKey() {
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((optionId == null) ? 0 : optionId.hashCode());
    result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
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
    QuestionOptionLinkKey other = (QuestionOptionLinkKey) obj;
    if (optionId == null) {
      if (other.optionId != null)
        return false;
    } else if (!optionId.equals(other.optionId))
      return false;
    if (questionId == null) {
      return other.questionId == null;
    } else return questionId.equals(other.questionId);
  }
}