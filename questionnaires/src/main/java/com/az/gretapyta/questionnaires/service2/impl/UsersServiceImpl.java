package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import com.az.gretapyta.questionnaires.jpa2.UserSpecification;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.repository2.UsersRepository;
import com.az.gretapyta.questionnaires.security.PasswordEncoderImpl;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
  public User createEntity(User entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
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

  protected void validate4PortOut(User entity) {
    String password = entity.getPasswordHash();
    if ( ! ((password==null) || password.trim().isEmpty())) {
      String encryptedPassword = passwordEncoder().encode(password);
      entity.setPasswordHash(encryptedPassword);
    }
  }
  //----/ Business Logic section: /-------------------------------//
}