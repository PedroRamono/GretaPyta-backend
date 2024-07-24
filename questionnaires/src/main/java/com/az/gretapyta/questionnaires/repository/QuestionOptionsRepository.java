package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionOptionsRepository extends JpaRepository<QuestionOptionLink, Integer>,
    JpaSpecificationExecutor<QuestionOptionLink> {

  //OK: SELECT stp FROM gretapyta.steps stp WHERE stp.name_multilang->>'en' like '%Which Soc. Med%'
  //TODO List<QuestionOption> getAllByQuestionId(Integer questionId);
}