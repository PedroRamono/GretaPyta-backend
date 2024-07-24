package com.az.gretapyta.questionnaires.model;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;
import com.az.gretapyta.questionnaires.util.Constants;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
// @Data // not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "QUESTIONNAIRES")
public class Questionnaire extends BaseEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = 0L;

  @Column(name = "CODE", nullable = false, unique = true, length = 16)
  private String code;

  @Column(name = "QUE_TYPE", nullable = false, length = 4)
  private String questionnaireType;

  @Column(name = "URL_ID_NAME", nullable = true, unique = true, length = 64)
  private String urlIdName;

  @Column(name = "PREFERRED_LANG", nullable = true, length = 4)
  private String preferredLang;

  @Column(name = "NAME_MULTILANG")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> nameMultilang;

  @Column(name = "DESCRIPTION_MULTILANG")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> descriptionMultilang;

  @Column(name = "READY_2_SHOW", nullable = false)
  private Boolean ready2Show;

  @Column(name = "COMMERCIAL_USAGE", nullable = false)
  private Boolean commercialUsage;

  // Questionnaire <- Drawer:
  @ManyToOne(fetch = FetchType.LAZY, optional = false) // FetchType.EAGER FetchType.LAZY
  @JoinColumn(name = "drawer_id", nullable = false)
  private Drawer drawer;


  //(1) Many-to-Many Join DOWN (Questionnaire-Step)
  //
  @ManyToMany
  @JoinTable(name = Constants.LINK_TABLE_QUESTIONNAIRE_STEP,
      joinColumns = @JoinColumn(name = "questionnaire_id"),
      inverseJoinColumns = @JoinColumn(name = "step_id"))
  private Set<Step> stepsUp;

  // Questionnaire -> Step:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionnaire")
  private Set<QuestionnaireStepLink> questionnaireSteps;
  //
  //(1) Many-to-Many Join DOWN (Questionnaire-Step)


  // Questionnaire -> UserQuestionnaire:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionnaireUser", cascade = CascadeType.ALL)
  private Set<UserQuestionnaire> qestionnaireUsers;

  //----/ Business Logic section: /-------------------------------//
  public Set<Question> getAllQuestions() {
    if ((stepsUp ==null) || stepsUp.isEmpty()) return Collections.EMPTY_SET; // In IT Tests scenario this will happen.

    Set<Question> ret = new HashSet<>();
    stepsUp.forEach(p -> ret.addAll(p.getQuestionsUp()));
    return ret;
  }
  //----/ Business Logic section: /-------------------------------//

//  @Override
//  public int hashCode() {
//    final int prime = 21;
//    int result = 1;
//    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
//    return result;
//  }
}