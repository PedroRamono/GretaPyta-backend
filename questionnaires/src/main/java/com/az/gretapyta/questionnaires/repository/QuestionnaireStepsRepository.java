package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireStepsRepository extends JpaRepository<QuestionnaireStepLink, Integer>,
                                                      JpaSpecificationExecutor<QuestionnaireStepLink> {
}