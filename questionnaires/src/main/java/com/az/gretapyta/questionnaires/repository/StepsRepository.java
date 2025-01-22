package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StepsRepository extends JpaRepository<Step, Integer>,
                    JpaSpecificationExecutor<Step> {

//  @Query(value = "SELECT * FROM gretapyta.steps")
//  // List<Step> findByNameMultilangLike(@Param("pattern") String pattern);
//  List<Step> findByNameMultilangLike();
}