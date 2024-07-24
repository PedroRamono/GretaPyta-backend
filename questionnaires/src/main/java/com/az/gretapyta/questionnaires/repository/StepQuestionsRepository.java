package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.StepQuestionLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StepQuestionsRepository extends JpaRepository<StepQuestionLink, Integer>,
    JpaSpecificationExecutor<StepQuestionLink> {
}