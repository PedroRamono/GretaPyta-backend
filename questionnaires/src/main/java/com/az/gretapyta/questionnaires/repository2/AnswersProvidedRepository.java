package com.az.gretapyta.questionnaires.repository2;

import com.az.gretapyta.questionnaires.model2.AnswerProvided;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswersProvidedRepository extends JpaRepository<AnswerProvided, Integer>,
                                                  JpaSpecificationExecutor<AnswerProvided> {
}