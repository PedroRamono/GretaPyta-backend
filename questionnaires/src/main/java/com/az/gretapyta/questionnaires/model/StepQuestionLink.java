package com.az.gretapyta.questionnaires.model;

import com.az.gretapyta.questionnaires.util.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = Constants.LINK_TABLE_STEP_QUESTION)
public class StepQuestionLink {

  @EmbeddedId
  private StepQuestionLinkKey id;

  @ManyToOne
  @MapsId("stepId")
  @JoinColumn(name = "step_id")
  private Step stepDown; //AZ909 stepDown;

  @ManyToOne
  @MapsId("questionId")
  @JoinColumn(name = "question_id")
  private Question questionUp;

  @Column(name = "DISPLAY_ORDER", nullable = false, unique = false)
  private int displayOrder;

  @Column(name = "TENANT_ID") // Multi-tenancy design
  private int tenantId;

  public StepQuestionLink() {
  }

  @Override
  public int hashCode() {
    final int prime = 41;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    StepQuestionLink other = (StepQuestionLink) obj;
    if (id == null) {
      return other.id == null;
    } else return id.equals(other.id);
  }
}