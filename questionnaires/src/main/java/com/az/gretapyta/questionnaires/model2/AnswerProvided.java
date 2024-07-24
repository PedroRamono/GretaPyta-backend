package com.az.gretapyta.questionnaires.model2;

import com.az.gretapyta.qcore.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;

@Entity
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "ANSWERS_PROVIDED")
public class AnswerProvided extends BaseEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "ANSWER")
  @JdbcTypeCode(SqlTypes.JSON)
  private GenericValue answer;

  @OneToOne(fetch = FetchType.LAZY)
  private QuestionAnswer questionAnswer;
}