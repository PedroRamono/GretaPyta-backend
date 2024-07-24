package com.az.gretapyta.questionnaires.repository2;

import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswersSelectedRepository extends JpaRepository<AnswerSelected, Integer>,
                                                  JpaSpecificationExecutor<AnswerSelected> {

  @Query(value = "SELECT * FROM \"OptionsPopularityCount\"(:question_id, :byPopularity);", nativeQuery = true)
  Object[] getOptionsPopularityCounts( @Param("question_id") Integer question_id,
                                   @Param("byPopularity") Boolean byPopularity );
}