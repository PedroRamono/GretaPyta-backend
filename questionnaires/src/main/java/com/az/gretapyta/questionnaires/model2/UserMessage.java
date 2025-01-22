package com.az.gretapyta.questionnaires.model2;

import com.az.gretapyta.qcore.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "USER_MESSAGES")
public class UserMessage extends BaseEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "MESSAGE", nullable = false, length = 1024)
  private String message;

  @Column(name = "VISIBILITY_LEVEL", nullable = false, length = 4)
  private String visibilityLevel;

  // UserComment <- User:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  // private Integer user;

  // Optional reference -> User:
  @Column(name = "FOR_USER_ID", nullable = true)
  private Integer forUserId;
}