package com.az.gretapyta.questionnaires.repository2;

import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionnairesRepository extends JpaRepository<UserQuestionnaire, Integer>,
                            JpaSpecificationExecutor<UserQuestionnaire> {
}