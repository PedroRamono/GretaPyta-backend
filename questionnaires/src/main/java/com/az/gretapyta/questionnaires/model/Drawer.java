package com.az.gretapyta.questionnaires.model;

import com.az.gretapyta.qcore.model.BaseEntity;
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
public class Drawer extends BaseEntity implements Serializable {
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

  public String toString() {
    return "code=" + this.getCode() +
        ", name=" + this.getNameMultilang() +
        // ", questionnaires.size[Questionnaires]=" + (questionnaires==null ? "<null>" : questionnaires.size()) + //AZ909
        ", hashCode=" + this.hashCode();
  }
}