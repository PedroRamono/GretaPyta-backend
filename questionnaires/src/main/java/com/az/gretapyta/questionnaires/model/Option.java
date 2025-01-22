package com.az.gretapyta.questionnaires.model;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model.interfaces.Presentable;
import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import com.az.gretapyta.questionnaires.model2.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Entity
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "OPTIONS")
public class Option extends BaseEntity implements Presentable, Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "CODE", nullable = false, unique = true, length = 16)
  private String code;

  @Column(name = "NAME_MULTILANG")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> nameMultilang;

  @Column(name = "HELP_MULTILANG")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> helpMultilang;

  @Column(name = "READY_2_SHOW", nullable = false)
  private Boolean ready2Show;

  @Column(name = "PREFERRED_ANSWER")
  private Boolean preferredAnswer;

  // Option <- User:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  // private Integer user;

  //(1) Many-to-Many Join UP (Questionnaire-Step)
  //
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "options")
  private Set<Question> questionsDown = new HashSet<>();
  //
  //(1) Many-to-Many Join UP (Questionnaire-Step)

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "option", cascade = CascadeType.ALL)
  /// @OneToOne(mappedBy = "stepUp")
  private Set<QuestionOptionLink> questionOptions;

  // Option -> SelectedAnswer:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "optionAnswer")
  private Set<AnswerSelected> selectedAnswers;

  @Override
  public int getCreatorId() {
    return (user != null ? user.getId() : 0);
  }

  //----/ Business Logic section: /-------------------------------//
  @Override
  public void filterChildrenOnReady2Show(boolean isAdmin, int creatorId) {
  }
  //----/ Business Logic section: /-------------------------------//
}