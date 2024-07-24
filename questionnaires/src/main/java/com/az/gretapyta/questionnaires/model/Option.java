package com.az.gretapyta.questionnaires.model;


import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "OPTIONS")
public class Option extends BaseEntity implements Serializable {
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

  /* //TODO ... */
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
}