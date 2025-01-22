package com.az.gretapyta.questionnaires.repository2;

import com.az.gretapyta.questionnaires.model2.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Integer>,
      JpaSpecificationExecutor<UserMessage> {
}