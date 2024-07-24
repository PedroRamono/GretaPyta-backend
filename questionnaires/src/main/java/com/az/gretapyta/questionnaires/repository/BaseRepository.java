package com.az.gretapyta.questionnaires.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

  @Override
  @Nonnull
  List<T> findAll();

  List<T> findAll(Specification<T> specification);
}