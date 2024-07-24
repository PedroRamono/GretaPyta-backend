package com.az.gretapyta.qcore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.az.gretapyta.qcore.util.Constants.BASE_DATE_TIME_FORMAT;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public abstract class BaseDTO {
  private Integer id;

  @NotNull
  @JsonFormat(pattern = BASE_DATE_TIME_FORMAT)
  private LocalDateTime created;

  @NotNull
  @JsonFormat(pattern = BASE_DATE_TIME_FORMAT)
  // @Getter(AccessLevel.NONE) // Don't broadcast it.
  private LocalDateTime updated;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if ((o == null) || (Hibernate.getClass(this) != Hibernate.getClass(o))) return false;
    BaseDTO otherDTO = (BaseDTO) o;
    return ((id != null) && Objects.equals(id, otherDTO.id));
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}