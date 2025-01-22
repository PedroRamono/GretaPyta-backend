package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto2.UserMessageDTO;
import com.az.gretapyta.questionnaires.mapper2.UserMessageMapper;
import com.az.gretapyta.questionnaires.model2.UserMessage;
import com.az.gretapyta.questionnaires.service2.UserMessagesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@Log4j2
@RestController
@RequestMapping(value = APIController.USER_MESSGES_URL)
@RequiredArgsConstructor
public class UserMessageController extends BaseController {
  public static final String USER_MESSAGE_CONTROLLER_HOME_MESSAGE = "Hello World ! from User-Message Controller";

  protected final UserMessagesService service;
  protected final UserMessageMapper mapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(UserMessageController) Greetings to be passed ...");
    return USER_MESSAGE_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/user-messages/all
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<UserMessageDTO>> getAl(
      HttpServletResponse response) {

    log.debug("(UserMessageController) getting all User Messages");
    SetInHeaderReturnEntityInfo(response, UserMessageDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems());
  }

  // http://localhost:8091/api/ver1/user-messages/searchid/1
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<UserMessageDTO> getItemById(
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id) {

    try {
      SetInHeaderReturnEntityInfo(response, UserMessageDTO.class.getSimpleName(), false);
      return ResponseEntity.ok(fetchDTOFromId(id));
    } catch (NotFoundException | NullPointerException e) {
      log.error("UserMessage for ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/user-messages/search/?userId=1
  @GetMapping(value = "/search/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<UserMessageDTO>> getItemsByUserId(
      HttpServletResponse response,
      @RequestParam(name = "userId", required = true) int userId) {

    try {
      SetInHeaderReturnEntityInfo(response, UserMessageDTO.class.getSimpleName(), true);
      return ResponseEntity.ok(getItemsForUser(userId));
    } catch (NotFoundException | NullPointerException e) {
      log.error("User-Message(s) for User with ID = {}'not found !", userId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(userId));
    }
  }

  // http://localhost:8091/api/ver1/user-messages/lexicon/type?lang=pl
  @GetMapping(value = "/lexicon/{lexiconName}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<Set<LexiconItem>> getLexicon(
      HttpServletResponse response,
      @PathVariable String lexiconName,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, LexiconItem.class.getSimpleName(), true);
      return ResponseEntity.ok(getLexiconForCode(lexiconName, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Lexicon '{}' not found !", lexiconName);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(lexiconName));
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserMessageDTO createItem(
      HttpServletRequest request,
      @RequestBody UserMessageDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeCreateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<UserMessageDTO> getAllItems() {
    return service.getAllItems().stream().map(mapper::map).toList();
  }

  public UserMessageDTO fetchDTOFromId(Integer id) {
    UserMessage entity = service.getItemById(id);
    return mapper.map(entity);
  }

  @Transactional(readOnly = true)
  public List<UserMessageDTO> getItemsForUser(int parentId) {
    return service.getItemsByUserId(parentId).stream().map(mapper::map).toList();
  }

  public UserMessageDTO executeCreateItem(UserMessageDTO entityDto, String langCode ) throws Exception {
    try {
      UserMessage entity = mapper.map(entityDto);
      UserMessage entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception ex) {
      log.error("Create UserMessage failed", ex);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("UserMessage"),
          ex.fillInStackTrace() + ":" + ex.getMessage());
    }
  }

  private Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode) {
    return service.getLexiconForCode(lexiconName, langCode);
  }
  //---/ Servicing part /------------------------------------------------//
}