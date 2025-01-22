package com.az.gretapyta.questionnaires.model;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model.interfaces.Presentable;
import com.az.gretapyta.questionnaires.model2.QuestionAnswer;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.util.Constants;
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
@Getter
@Setter
@Table(name = "QUESTIONS")
public class Question extends BaseEntity implements Presentable, Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "CODE", nullable = false, unique = true, length = 16)
  private String code;

  @Column(name = "TITLE_MULTILANG")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> titleMultilang;

  @Column(name = "QUESTION_MULTILANG", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> questionAskedMultilang;

  @Column(name = "ANSWER_TYPE", nullable = false, length = 4)
  private String answerType;

  @Column(name = "READY_2_SHOW", nullable = false)
  private Boolean ready2Show;

  // Drawer <- User:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  // private Integer user;

  //(1) Many-to-Many Join UP (Step-Question)
  //
  @ManyToMany(mappedBy = "QuestionsUp")
  private Set<Step> steps = new HashSet<>();

  @OneToMany(mappedBy = "questionUp", cascade = CascadeType.ALL)
  private Set<StepQuestionLink> stepQuestion = new HashSet<>();
  //
  //(1) Many-to-Many Join UP (Step-Question)

  //(2) Many-to-Many Join DOWN (Question-Option)
  //
  @ManyToMany
  @JoinTable(name = Constants.LINK_TABLE_QUESTION_OPTION,
      joinColumns = @JoinColumn(name = "question_id"),
      inverseJoinColumns = @JoinColumn(name = "option_id"))
  private Set<Option> options;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionDown")
  private Set<QuestionOptionLink> questionOptions;
  //
  //(2) Many-to-Many Join DOWN (Question-Option)

  // Question -> QuestionAnswer:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL)
  private Set<QuestionAnswer> questionAnswers;

  @Override
  public int getCreatorId() {
    return (user != null ? user.getId() : 0);
  }

  //----/ Business Logic section: /-------------------------------//
  @Override
  public void filterChildrenOnReady2Show(boolean isAdmin, int creatorId) {
    Presentable.filterChildrenOnReady2Show(isAdmin, creatorId, options);
  }
  //----/ Business Logic section: /-------------------------------//
}