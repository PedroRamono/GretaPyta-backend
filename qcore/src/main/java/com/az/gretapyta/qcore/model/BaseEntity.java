package com.az.gretapyta.qcore.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import static com.az.gretapyta.qcore.util.Constants.BASE_DATE_TIME_FORMAT;

@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false, updatable = false)
  private Integer id; // Long

  @Column(name = "created", nullable = false)
  private LocalDateTime created;

  @Column(name = "updated", nullable = false)
  @JsonFormat(pattern = BASE_DATE_TIME_FORMAT)
  private LocalDateTime updated;
}