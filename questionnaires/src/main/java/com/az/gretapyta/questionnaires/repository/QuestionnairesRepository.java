package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.Questionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionnairesRepository extends JpaRepository<Questionnaire, Integer>,
                                          JpaSpecificationExecutor<Questionnaire> {

  Optional<Questionnaire> findByCode(String code);

  Questionnaire findByUrlIdName(String urlIdName);
}