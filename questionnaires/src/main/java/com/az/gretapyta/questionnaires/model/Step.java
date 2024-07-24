package com.az.gretapyta.questionnaires.model;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.util.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Entity
// @Data / Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "STEPS")
public class Step extends BaseEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "NAME_MULTILANG")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> nameMultilang;

  @Column(name = "READY_2_SHOW", nullable = false)
  private Boolean ready2Show;

  //(1) Many-to-Many Join UP (Questionnaire-Step)
  //
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "stepsUp")
  private Set<Questionnaire> questionnaires;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "stepUp", cascade = CascadeType.ALL)
  /// @OneToOne(mappedBy = "stepUp")
  private Set<QuestionnaireStepLink> questionnaireStep;
  //
  //(1) Many-to-Many Join UP (Questionnaire-Step)

  //(2) Many-to-Many Join DOWN (Step-Question)
  //
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = Constants.LINK_TABLE_STEP_QUESTION,
      joinColumns = @JoinColumn(name = "step_id"),
      inverseJoinColumns = @JoinColumn(name = "question_id"))
  private Set<Question> QuestionsUp; // = new HashSet<>();

  // Step -> Question:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "stepDown")
  private Set<StepQuestionLink> stepQuestions; // = new HashSet<>();
  //
  //(2) Many-to-Many Join DOWN (Step-Question)
}