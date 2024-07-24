package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.enums.UserQuestionnaireStatuses;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto2.QuestionAnswerDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.dto2.UserQuestionnaireDTO;
import com.az.gretapyta.questionnaires.mapper2.UserQuestionnaireMapper;
import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;
import com.az.gretapyta.questionnaires.service2.UserQuestionnairesService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping(value = APIController.USERS_QUESTIONNAIRES_URL)
@RequiredArgsConstructor
public class UserQuestionnaireController extends BaseController {
  public static final String USER_QUESTIONNAIRE_CONTROLLER_HOME_MESSAGE = "Hello World! from UserQuestionnaire Controller";

  private final UserQuestionnairesService service;
  private final UserQuestionnaireMapper mapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(UserQuestionnaireController) Greetings to be passed ...");
    return USER_QUESTIONNAIRE_CONTROLLER_HOME_MESSAGE;
  }

  @GetMapping(value = "/all-no-paging", produces = MediaType.APPLICATION_JSON_VALUE)
  // @ResponseBody
  public ResponseEntity<List<UserQuestionnaireDTO>> getPosts(
      HttpServletResponse response ) {

    SetInHeaderReturnEntityInfo(response, UserQuestionnaireDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems());
  }

  // http://localhost:8091/api/ver1/users-questionnaires/searchid/1
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<UserQuestionnaireDTO> getItemById(
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id) {

    try {
      SetInHeaderReturnEntityInfo(response, UserQuestionnaireDTO.class.getSimpleName(), false);
      return ResponseEntity.ok(fetchDTOFromId(id));
    } catch (NotFoundException | NullPointerException e) {
      log.error("UserQuestionnaire for ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/users-questionnaires/search/?userId=1
  @GetMapping(value = "/search/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<UserQuestionnaireDTO>> getItemsByUserId(
      HttpServletResponse response,
      // @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "userId", required = true) int userId) {

    try {
      SetInHeaderReturnEntityInfo(response, UserQuestionnaireDTO.class.getSimpleName(), true);
      return ResponseEntity.ok(getItemsForUser(userId));
    } catch (NotFoundException | NullPointerException e) {
      log.error("User-Questionnaire(s) for User with ID = {}'not found !", userId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(userId));
    }
  }

  // http://localhost:8091/api/ver1/users-questionnaires/search/byuserandqsnid/?userId=1&questionnaireId=1
  @GetMapping(value = "/search/byuserandqsnid/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<UserQuestionnaireDTO> getItemsForUser(
      HttpServletResponse response,
      @RequestParam(name = "userId", required = true) int userId, // Step.Id
      @RequestParam(name = "questionnaireId", required = true) int questionnaireId) {
      // @PathVariable(name = "id") final Integer id,

    try {
      SetInHeaderReturnEntityInfo(response, UserQuestionnaireDTO.class.getSimpleName(), false);
      Optional<UserQuestionnaireDTO> itemDto = getItemsByUserIdAndQuestionnaireId(userId, questionnaireId);
      if (itemDto.isPresent()) {
        return ResponseEntity.ok(itemDto.get());
      }
      itemDto.orElseThrow(NotFoundException::new);
    } catch (NotFoundException | NullPointerException e) {
      log.error("User-Questionnaire for User with ID = {} and Questionnaire ID = {} not found !", userId, questionnaireId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(userId));
    }
    return null;
  }

  //TODO-TEST: include it in IT testing
  // http://localhost:8091/api/ver1/users-questionnaires/search/byuserandqsncode/?userId=1&questionnaireCode=QST_POL_USP
  @GetMapping(value = "/search/byuserandqsncode/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<UserQuestionnaireDTO> getItemsForUser(
      HttpServletResponse response,
      @RequestParam(name = "userId", required = true) int userId, // Step.Id
      @RequestParam(name = "questionnaireCode", required = true) String questionnaireCode) {
    // @PathVariable(name = "id") final Integer id,

    try {
      SetInHeaderReturnEntityInfo(response, UserQuestionnaireDTO.class.getSimpleName(), false);
      Optional<UserQuestionnaireDTO> itemDto = getItemsByUserIdAndQuestionnaireCode(userId, questionnaireCode);
      if (itemDto.isPresent()) {
        return ResponseEntity.ok(itemDto.get());
      }
      itemDto.orElseThrow(NotFoundException::new);
    } catch (NotFoundException | NullPointerException e) {
      log.error("User-Questionnaire for User with ID = {} and Questionnaire code = '{}' not found !", userId, questionnaireCode);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(userId));
    }
    return null;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserQuestionnaireDTO createItem(@RequestBody UserQuestionnaireDTO entityDto) throws Exception {
     return executeCreateItem(entityDto, entityDto.getAnswerLang());
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<UserQuestionnaireDTO> getAllItems() {
    return service.getAllItems().stream().map(mapper::map).toList();
  }

  public UserQuestionnaireDTO fetchDTOFromId(Integer id) {
    UserQuestionnaire entity = service.getItemById(id);
    return mapper.map(entity);
  }

  @Transactional(readOnly = true)
  public List<UserQuestionnaireDTO> getItemsForUser(int parentId) {
    return service.getItemsByUserId(parentId).stream().map(mapper::map).toList();
  }

  @Transactional(readOnly = true)
  public Optional<UserQuestionnaireDTO> getItemsByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId) {
    return service.getItemsByUserIdAndQuestionnaireId(userId, questionnaireId)
        .map(step -> Optional.ofNullable(mapper.map(step)))
        .orElse(null);
  }

  @Transactional(readOnly = true)
  public Optional<UserQuestionnaireDTO> getItemsByUserIdAndQuestionnaireCode(Integer userId, String questionnaireCode) {
    return service.getItemsByUserIdAndQuestionnaireCode(userId, questionnaireCode).map(mapper::map);
  }

  public Optional<UserQuestionnaireDTO> findByNameMultilangFirstLike(int userId, int questionnaireId) {
    Optional<UserQuestionnaire> ret = service.getItemsByUserIdAndQuestionnaireId(userId, questionnaireId);
    return ret.map(item -> Optional.ofNullable(mapper.map(item))).orElse(null);
  }

  public UserQuestionnaireDTO executeCreateItem(UserQuestionnaireDTO entityDto, String langCode) throws Exception {
    try {
      UserQuestionnaire entity = mapper.map(entityDto);
      UserQuestionnaire entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception ex) {
      log.error("Create UserQuestionnaire failed", ex);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("UserQuestionnaire"),
          ex.fillInStackTrace() + ":" + ex.getMessage());
    }
  }

  public static UserQuestionnaireDTO createUserQuestionnaireDTO(
      UserDTO userDTO,
      QuestionnaireDTO questionnaireDTO,
      String langCode,
      InetAddress ipAddressFrom,
      UserQuestionnaireStatuses status,
      List<QuestionAnswerDTO> questionAnswerList ) {

    return new UserQuestionnaireDTO( langCode,
                                     ipAddressFrom,
                                     status,
                                     userDTO.getId(),
                                     questionnaireDTO.getId(),
                                     questionAnswerList );
  }
  //---/ Servicing part /------------------------------------------------//
}