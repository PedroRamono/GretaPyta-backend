package com.az.gretapyta.questionnaires.model2;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model.Questionnaire;
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
@Table(name = "USER_QUESTIONNAIRES")
public class UserQuestionnaire extends BaseEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "ANSWER_LANG", length = 4)
  private String answerLang;

  @Column(name = "IP_ADDRESS", length = 16)
  private String ipAddressFrom;

  @Column(name = "PROGRESS_STATUS", nullable = false, length = 4)
  private String completionStatus;

  // UserQuestionnaire <- User:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  // private Integer user;

  // UserQuestionnaire <- Questionnaire:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "questionnaire_id", nullable = false)
  private Questionnaire questionnaireUser;
  // private Integer questionnaireUser; //AZ404

  // UserQuestionnaire -> QuestionAnswer:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "userQuestionnaire", cascade = CascadeType.ALL)
  private Set<QuestionAnswer> questionAnswers;

  @Override
  public int hashCode() {
    final int prime = 22;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    return result;
  }
}