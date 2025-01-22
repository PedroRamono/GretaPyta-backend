package com.az.gretapyta.questionnaires.model;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.questionnaires.model.interfaces.Presentable;
import com.az.gretapyta.questionnaires.model2.User;
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
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "DRAWERS")
public class Drawer extends BaseEntity implements Presentable, Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "CODE", nullable = false, unique = true, length = 16)
  private String code;

  @Column(name = "NAME_MULTILANG")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> nameMultilang;

  @Column(name = "READY_2_SHOW", nullable = false)
  private Boolean ready2Show;

  // Drawer -> Questionnaire:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "drawer", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<Questionnaire> questionnaires;

  // Drawer <- User:
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  // private Integer user;

  @Override
  public int getCreatorId() {
    return (user != null ? user.getId() : 0);
  }

  //----/ Business Logic section: /-------------------------------//
  @Override
  public void filterChildrenOnReady2Show(boolean isAdmin, int creatorId) {
    Presentable.filterChildrenOnReady2Show(isAdmin, creatorId, questionnaires);
  }
  //----/ Business Logic section: /-------------------------------//

  public String toString() {
    return "code=" + this.getCode() +
        ", name=" + this.getNameMultilang() +
        ", hashCode=" + this.hashCode();
  }
}