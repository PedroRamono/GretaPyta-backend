package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptionsRepository extends JpaRepository<Option, Integer>,
      JpaSpecificationExecutor<Option> {

  Optional<Option> findByCode(String code);
}