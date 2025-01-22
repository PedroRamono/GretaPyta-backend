package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.enums.VisibilityLevels;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.model2.UserMessage;
import com.az.gretapyta.questionnaires.repository2.UserMessageRepository;
import com.az.gretapyta.questionnaires.service2.UserMessagesService;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserMessagesServiceImpl extends BaseServiceImpl implements UserMessagesService {
  private final UserMessageRepository repository;

  private final UsersService usersService;

  @Override
  public List<UserMessage> getAllItems() {
    return repository.findAll();
  }

  public List<UserMessage> getItemsByUserId(Integer userId) {
    Specification<UserMessage> specUserId = GenericSpecification.getParentIdSpecs(
        userId,
        "user");
    return repository.findAll(specUserId);
  }

  public UserMessage getItemById(Integer id) {
    return repository.findById(id).orElseThrow(NotFoundException::new);
  }

  @Override
  public Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode) {
    if (lexiconName.equalsIgnoreCase("type")) {
      Set<LexiconItem> ret = new LinkedHashSet<>();
      EnumSet.allOf(VisibilityLevels.class)
          .forEach(n -> ret.add(new LexiconItem(n.getCode(), n.getLabel(langCode)))
          );
      return ret;
    }
    return Collections.emptySet(); // default.
  }

  public UserMessage createEntity(UserMessage entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  /* Currently not allowed to update existing User Message - might change.
  @Override
  public Step updateEntity(UserMessage entity, String lang) throws BusinessException {
    validateBeforeUpdate(entity, lang);
    return repository.save(entity);
  }
  */

  @Override
  protected <UserMessage extends BaseEntity> boolean validateBeforeCreate(UserMessage entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      User user = ((com.az.gretapyta.questionnaires.model2.UserMessage) entity).getUser();
      if (validateForIssuerExists(user.getId(), lang)) { // (1) Issuing User exists ?
        if (validateForIssuerIsRegisteredUser(user.getId(), lang)) { // (2) User is not Anonymous ?
          return validateForDestinationUserExists( // (3) Destination User (if ID provided) exists ?
              // ((com.az.gretapyta.questionnaires.model2.UserMessage) entity).getForUserId(),
              ((com.az.gretapyta.questionnaires.model2.UserMessage) entity),
              lang );
        }
      }
      return false;
    }
    return false;
  }  
  
  /* Currently not allowed to update existing User Message - might change.
  @Override
  protected <UserMessage extends BaseEntity> boolean validateBeforeUpdate(UserMessage entity, String lang) throws BusinessException {
    if (super.validateBeforeUpdate(entity, lang)) {
      if (((com.az.gretapyta.questionnaires.model2.UserMessage) entity).getForUserId() != null) {
        // Check if other User exists:
        return validateForDestinationUserExists(
            ((com.az.gretapyta.questionnaires.model2.UserMessage) entity).getForUserId(),
            lang );
      }
      return true;
    }
    return false;
  }
  */

  private boolean validateForIssuerExists(int uerId, String lang) throws BusinessException {
    if(usersService.getItemById(uerId) == null) {
      log.error("Adding User Message: Issuing User with ID={} does not exists.", uerId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_invalid_user", lang);
      assert localeMess != null;
      throw new BusinessException(localeMess); // Do not broadcast User ID to (any) Client app. !
    }
    return true;
  }

  private boolean validateForIssuerIsRegisteredUser(int userId, String lang) throws BusinessException {
    User user = usersService.getItemById(userId);
    if(user != null) {
      if (user.getAnonymousUser()) {
        log.error("Adding User Message: issuing User with ID={} is NOT registered User.", userId);
        String localeMess = CommonUtilities.getTranslatableMessage("error_action_not_allowed_for_anonymous_user", lang);
        assert localeMess != null;
        throw new BusinessException(localeMess); // Do not broadcast User ID to (any) Client app. !
      }
    }
    return true;
  }

  private boolean validateForDestinationUserExists(UserMessage entity, String lang) throws BusinessException {
    if (entity.getForUserId() == null) {
      return true;
    }
    try {
      User user = usersService.getItemById(entity.getForUserId());
    } catch (Exception e) {
      log.error("Adding User Message for other User: destination User with ID={} does not exists.", entity.getForUserId());
      String localeMess = CommonUtilities.getTranslatableMessage("error_user_message_destination_user_invalid", lang);
      assert localeMess != null;
      throw new BusinessException(localeMess); // Do not broadcast destination User ID to (any) Client app. !
    }
    return true;
  }
}