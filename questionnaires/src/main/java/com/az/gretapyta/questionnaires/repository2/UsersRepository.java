package com.az.gretapyta.questionnaires.repository2;

import com.az.gretapyta.questionnaires.model2.User;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer>,
                                         JpaSpecificationExecutor<User> {

//  @Query("select u from gretapyta.users u where u.anonymousUser = :anonymous")
//  List<User> findAnonymousUser(boolean anonymousUser, String preferredLang);

  @Override
  @Nonnull
  Page<User> findAll(@Nullable Specification<User> spec, @NonNull Pageable pageable);
}