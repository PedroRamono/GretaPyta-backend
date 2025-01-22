package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.enums.GenderTypes;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.jpa2.UserSpecification;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.repository2.UsersRepository;
import com.az.gretapyta.questionnaires.security.PasswordEncoderImpl;
import com.az.gretapyta.questionnaires.security.UserRoles;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends BaseServiceImpl implements UsersService {
  private final UsersRepository repository;

  @Autowired
  private GeneralProperties generalProperties;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PasswordEncoderImpl(generalProperties.getSecurity().getSalt());
  }

  @Override
  public List<User> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<User> getAllItems(int userId) {
    if (isAdministrator(userId)) {
      return getAllItems();
    } else {
      Specification<User> specOr = GenericSpecification.excludeAdministrators();
      return repository.findAll(specOr, Sort.by("loginName"));
    }
  }

  @Override
  public User getItemById(Integer id) {
    return repository.findById(id).orElseThrow(NotFoundException::new);
  }

  @Override
  public User getItemByAnonymousFlag(String langCode) {
    return repository.findAll(UserSpecification.witAnonymousFlag()
        .and(UserSpecification.withPreferredLang(langCode))).stream().findFirst().orElse(null);
  }

  @Override
  public Optional<User> getUserByLoginName(String loginName) {
    return repository.findAll(UserSpecification.withLoginName(loginName)).stream().findFirst();
  }

  @Override
  public List<User> getUserByFirstLastNameFirst(String fName, String lName) {
    return repository.findAll(UserSpecification.withFirstName(fName)
                          .and(UserSpecification.withLastName(lName)));
  }

  @Override
  public boolean isLoginNameExistsAlready(String loginName) {
    return (getUserByLoginName(loginName).isPresent());
  }

  @Override
  public boolean isAdministrator(int userId) {
    return isUserOfRole(userId, UserRoles.ADMIN.getCode());
  }

  @Override
  public boolean isAdministrator4Demo(int userId) {
    return isUserOfRole(userId, UserRoles.ADMIN_DEMO.getCode());
  }

  @Override
  public User createEntityNoAuthorityCheck(User entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  @Override
  public User createEntity(User entity, int userId, String lang) throws BusinessException {
    checkAuthorityOnModifyingAdminUser(userId, entity.getRole(), lang); // can throw BE.
    return createEntityNoAuthorityCheck(entity, lang);
  }

  @Override
  public User updateEntity(User entity, int userId, String lang) throws BusinessException {
    checkAuthorityOnModifyingAdminUser(userId, entity.getRole(), lang); // can throw BE.
    validateBeforeUpdate(entity, lang);
    return repository.save(entity);
  }

  @Override
  public void updatePassword(String loginName, String newPassword) {
    Optional<User> user = getUserByLoginName(loginName);
    if (user.isPresent()) {
      user.get().setPasswordHash(newPassword);
      validate4PortOut(user.get()); // Password will be encoded.
      repository.save(user.get());
    }
  }

  @Override
  public void deleteEntity(int entityId, int userId, String lang) throws BusinessException {
    checkAuthorityOnDeletingEntity(userId, entityId, lang); // can throw BE.
    repository.deleteById(entityId);
  }

  //----/ Business Logic section: /-------------------------------//
  @Override
  protected <User extends BaseEntity> boolean validateBeforeCreate(User entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      String code = ((com.az.gretapyta.questionnaires.model2.User) entity).getLoginName();
      if (isLoginNameExistsAlready(code)) {
        log.error("Create User: login name '{}' exists already.", code);
        String localeMess = CommonUtilities.getTranslatableMessage("error_create_user_login_exist_already", Constants.DEFAULT_LOCALE);
        assert localeMess != null;
        throw new BusinessException(localeMess.formatted(code));
      } else {
        validate4PortOut((com.az.gretapyta.questionnaires.model2.User) entity);
        return true;
      }
    }
    return false;
  }

  @Override
  protected <User extends BaseEntity> boolean validateBeforeUpdate(User entity, String lang) throws BusinessException {
    if (super.validateBeforeUpdate(entity, lang)) {
      //(1) get Entity of ID
      //(2) fill in some 'Immutable' fields
      com.az.gretapyta.questionnaires.model2.User existingEntity = getItemById(entity.getId());
      validate4PortOutOnUpdate(existingEntity, (com.az.gretapyta.questionnaires.model2.User) entity);
      return true;
    }
    return false;
  }

  protected void validate4PortOut(User entity) {
    String password = entity.getPasswordHash();
    if ( ! ((password==null) || password.trim().isEmpty())) {
      String encryptedPassword = passwordEncoder().encode(password);
      entity.setPasswordHash(encryptedPassword);
    }
  }

  protected void validate4PortOutOnUpdate(User originalEntity, User destinationEntity) {
    // 'Immutable' properties of User entity:
    destinationEntity.setLoginName(originalEntity.getLoginName());
    destinationEntity.setCreated(originalEntity.getCreated());
    destinationEntity.setPasswordHash(originalEntity.getPasswordHash());
  }

  @Override
  public Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode) {
    if (lexiconName.equalsIgnoreCase("gender")) {
      Set<LexiconItem> ret = new LinkedHashSet<>();
      EnumSet.allOf(GenderTypes.class)
          .forEach(n -> ret.add(new LexiconItem(n.getCode(), n.getLabel(langCode)))
          );
      return ret;
    }
    return Collections.emptySet(); // default.
  }

  private void checkAuthorityOnModifyingAdminUser(int creatorId, String roleCode, String lang) throws BusinessException {
    if (roleCode.equalsIgnoreCase(UserRoles.ADMIN.getCode())) { // Action on Admin. User.
      if ( ! isAdministrator(creatorId)) { // Non-Admin. User tries action on Admin. User - not allowed.
        log.error("Action not allowed: Non-Administrative User with ID={} tried to add Admin. UserUser.", creatorId);
        String localeMess = CommonUtilities.getTranslatableMessage("error_no_user_authority_for_action", lang);
        assert localeMess != null;
        BusinessException be = new BusinessException(localeMess);
        be.setSuggestedHttpStatus(HttpStatus.UNAUTHORIZED.value());
        throw be;
      }
    }
  }

  private void checkAuthorityOnDeletingEntity(int creatorId, int entityId, String lang) throws BusinessException {
    if ( ! isAdministrator(creatorId)) { // Non-Admin. User tries action on Admin. User - not allowed.
      log.error("Action not allowed: Non-Administrative User with ID={} tried to delete User with ID={}.", creatorId, entityId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_no_user_authority_for_action", lang);
      assert localeMess != null;
      BusinessException be = new BusinessException(localeMess);
      be.setSuggestedHttpStatus(HttpStatus.UNAUTHORIZED.value());
      throw be;
    }
  }
  //----/ Business Logic section: /-------------------------------//

  private boolean isUserOfRole(int userId, String roleCode) {
    boolean ret = false;
    Optional<User> userOpt = repository.findById(userId);
    if (userOpt.isPresent()) {
      ret = userOpt.get().getRole().equalsIgnoreCase(roleCode); //  UserRoles.ADMIN.getCode());
    }
    return ret;
  }
}