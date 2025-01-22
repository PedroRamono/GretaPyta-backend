package com.az.gretapyta.questionnaires.jpa2;

import com.az.gretapyta.questionnaires.model2.User;
import jakarta.persistence.criteria.Expression;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

//TODO... move to GenericSpecification
public class UserSpecification {

  public static Specification<User> ageLessThan(int age) {
    return (root, query, cb) -> {
      LocalDate date = LocalDate.now().minusYears(age);
      Expression<LocalDate> birthdayDt = root.get("birthday").as(LocalDate.class);
      return cb.lessThan(birthdayDt, date);
    };
  }

  public static Specification<User> isLongTermUser(int longYears) {
    return (root, query, cb) -> {
      LocalDate date = LocalDate.now().minusYears(longYears);
      return cb.lessThan(root.get("created"), date);
    };
  }

  public static Specification<User> withPreferredLang(@NonNull String preferredLang) {
    return (root, query, cb) -> cb.equal(root.get("preferredLang"), preferredLang);
  }

  public static Specification<User> witAnonymousFlag() {
    return (root, query, cb) -> cb.isTrue(root.get("anonymousUser"));
  }

  public static Specification<User> withFirstName(@NonNull String firstName) {
    return (root, query, cb) -> cb.equal(root.get("firstName"), firstName);
  }

  public static Specification<User> withLastName(@NonNull String lastName) {
    return (root, query, cb) -> cb.equal(root.get("lastName"), lastName);
  }

  public static Specification<User> withLoginName(@NonNull String loginName) {
    return (root, query, cb) -> cb.equal(root.get("loginName"), loginName);
  }
}