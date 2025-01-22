package com.az.gretapyta.questionnaires.repository;

import com.az.gretapyta.questionnaires.model.Drawer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrawersRepository extends JpaRepository<Drawer, Integer>,
                                  JpaSpecificationExecutor<Drawer> {

  Optional<Drawer> findByCode(String code);

 @Query(value = "SELECT * FROM \"QuestionnairesPopularityCount\"(:byPopularity);", nativeQuery = true)
 Object[] getQuestionnairesPopularityCounts( @Param("byPopularity") Boolean byPopularity);
}