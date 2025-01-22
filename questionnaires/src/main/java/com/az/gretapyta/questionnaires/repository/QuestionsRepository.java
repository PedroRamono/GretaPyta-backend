package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionsRepository extends JpaRepository<Question, Integer>,
      JpaSpecificationExecutor<Question> {

  Optional<Question> findByCode(String code);
}