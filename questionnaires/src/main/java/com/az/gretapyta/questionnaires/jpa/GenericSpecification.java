package com.az.gretapyta.questionnaires.jpa;

import com.az.gretapyta.qcore.enums.SearchOperation;
import com.az.gretapyta.qcore.jpa.SearchCriteriaBasic;
import com.az.gretapyta.qcore.jpa.SearchCriteriaOnChild;
import com.az.gretapyta.questionnaires.security.UserRoles;
import jakarta.persistence.criteria.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

@Log4j2
public class GenericSpecification<T> implements Specification<T> {
  protected final SearchCriteriaBasic searchCriteria;

  /* Maybe for future:
  protected List<SearchCriteria> criteriaList = new ArrayList<>();

  public BaseSpecification(List<SearchCriteria> criteriaList) {
    this.criteriaList = criteriaList;
  }
  */

  public <T extends SearchCriteriaBasic> GenericSpecification(SearchCriteriaBasic searchCriteria) {
    this.searchCriteria = searchCriteria;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Predicate predicate = null;
    boolean isChildSearch = SearchCriteriaOnChild.class.isInstance(searchCriteria);

    String key = searchCriteria.getKey();
    String childKey = isChildSearch ?
        ((SearchCriteriaOnChild)searchCriteria).getChildEntityName() :
        null;
    Object value = searchCriteria.getValue();

    Expression<String> keyExpression;
    if (isChildSearch) {
      keyExpression = root.get(childKey).get(key);
    } else {
      keyExpression = root.get(key);
    }

      switch (searchCriteria.getOperation()) {
      case IS_TRUE:
        // predicate = criteriaBuilder.equal(root.get(key), (Boolean)value);
        predicate = criteriaBuilder.isTrue(
            isChildSearch ?
            root.get(childKey).get(key) :
            root.get(key)
            );
        break;
      case EQUAL:
        predicate = criteriaBuilder.equal(keyExpression, value);
        break;
      case NOT_EQUAL:
        predicate = criteriaBuilder.notEqual(keyExpression, value);
        break;

//      case LESS_THAN:
//        convertedValue  = convertToComparable(root, criteria);
//        predicates.add(
//            criteriaBuilder.lessThan(getRoot(root, criteria.getKey()), convertedValue));
//        break;
//      case GREATER_THAN_EQUAL:
//        break;
      case EQUAL_IGNORE_CASE:
        predicate = criteriaBuilder.equal(
            criteriaBuilder.lower(keyExpression),
            value.toString().toLowerCase());
        break;
      case LIKE:
        predicate = criteriaBuilder.like(keyExpression, "%" + value + "%");
        break;
      case LIKE_IGNORE_CASE:
        predicate = criteriaBuilder.like(
            criteriaBuilder.lower(keyExpression),
            "%" + value.toString().toLowerCase() + "%"  );
        break;
        case MATCH_IN_LANG_MAP:
          log.warn("Search Operation {} is not Supported.", searchCriteria.getOperation());
          //TODO ...
          /*
          if (SearchCriteriaOnMap.class.isInstance(searchCriteria)) {
            SearchCriteriaOnMap mapCriteria = (SearchCriteriaOnMap)searchCriteria;
            predicate = criteriaBuilder.values().like(keyExpression, "%" + value + "%");
            // OR
            predicate = criteriaBuilder.createQuery().where();
          }
          */
          break;
        //TODO ...
        // criteriaBuilder.greaterThanOrEqualTo(
        // criteriaBuilder.lessThanOrEqualTo(
        // criteriaBuilder.lessThan(
        // criteriaBuilder.greaterThan(
        // criteriaBuilder.in(

      default:
        log.warn("Search Operation {} is not Supported.", searchCriteria.getOperation());
    }
    return predicate;
  }

  // (1)
  public static <T> Specification<T> getPrimaryIdSpecs(int id) {
    final SearchCriteriaBasic searchCriteria = new SearchCriteriaBasic("id", id, SearchOperation.EQUAL);
    Specification<T> specId = new GenericSpecification<T>(searchCriteria);
    return specId;
  }

  // (1-b)
  public static <T> Specification<T> getChildIdKeyValueSpecs(String parentIdName, int childId, String childName) {
    final SearchCriteriaOnChild searchCriteria = new SearchCriteriaOnChild(parentIdName, childId, SearchOperation.EQUAL, childName);
    Specification<T> specId = new GenericSpecification<T>(searchCriteria);
    return specId;
  }

  // (2)
  public static <T> Specification<T> getEntityCodeSpecs(String code) {
    final SearchCriteriaBasic searchCriteria = new SearchCriteriaBasic("code", code, SearchOperation.EQUAL);
    Specification<T> specCode = new GenericSpecification<T>(searchCriteria);
    return specCode;
  }

  // (3)
  public static <T> Specification<T> getParentIdSpecs(int parentId, String parentName) {
    return getChildIdKeyValueSpecs("id", parentId, parentName);
  }

  // (4)
  public static <T> Specification<T> getReady2ShowOrOwnerUserSpecs(int userId) {
    final SearchCriteriaOnChild searchCriteriaUserId = new SearchCriteriaOnChild("id", userId, SearchOperation.EQUAL, "user");
    final SearchCriteriaBasic searchCriteria1Ready2Show = new SearchCriteriaBasic("ready2Show", true, SearchOperation.IS_TRUE);

    GenericSpecification<T> specReady2Show = new GenericSpecification<T>(searchCriteria1Ready2Show);
    GenericSpecification<T> specUserId = new GenericSpecification<T>(searchCriteriaUserId);
    return specUserId.or(specReady2Show);
  }

  // (5)
  // excludeAdministrators
  public static <T> Specification<T> excludeAdministrators() {
    final SearchCriteriaBasic searchCriteria = new SearchCriteriaBasic(
        "role",
        UserRoles.ADMIN.getCode(),
        SearchOperation.NOT_EQUAL );
    Specification<T> specRole = new GenericSpecification<T>(searchCriteria);
    return specRole;
  }


  // Composites:
  //
  // (101) = (1) .and. (4)
  public static <T> Specification<T> getIdAndReady2ShowOrOwnerUserSpecs(int id, int userId) {
    Specification<T> specId = GenericSpecification.getPrimaryIdSpecs(id);
    Specification<T> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
    return specId.and(specOr);
  }

  // (102) = (2) .and. (4)
  public static <T> Specification<T> geCodeAndReady2ShowOrOwnerUserSpecs(String code, int userId) {
    Specification<T> specCode = GenericSpecification.getEntityCodeSpecs(code);
    Specification<T> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
    return specCode.and(specOr);
  }
}