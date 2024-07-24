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
@Table(name = Constants.LINK_TABLE_QUESTIONNAIRE_STEP)
public class QuestionnaireStepLink {

  @EmbeddedId
  private QuestionnaireStepLinkKey id;

  @ManyToOne
  @MapsId("questionnaireId")
  @JoinColumn(name = "questionnaire_id")
  private Questionnaire questionnaire;

  @ManyToOne
  @MapsId("stepId")
  @JoinColumn(name = "step_id")
  private Step stepUp;

  @Column(name = "DISPLAY_ORDER", nullable = false, unique = false)
  private int displayOrder;

  @Column(name = "TENANT_ID") // Multi-tenancy design
  private int tenantId;

  public QuestionnaireStepLink() {
  }

  @Override
  public int hashCode() {
    final int prime = 21;
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
    QuestionnaireStepLink other = (QuestionnaireStepLink) obj;
    if (id == null) {
      return other.id == null;
    } else return id.equals(other.id);
  }
}