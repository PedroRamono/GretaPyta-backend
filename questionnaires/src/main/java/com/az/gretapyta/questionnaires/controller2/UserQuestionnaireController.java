package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.enums.EnumCommon;
import com.az.gretapyta.qcore.enums.UserQuestionnaireStatuses;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto2.*;
import com.az.gretapyta.questionnaires.dto2.wizards.TakeQuestionDTO;
import com.az.gretapyta.questionnaires.dto2.wizards.TakeQuestionnaireDTO;
import com.az.gretapyta.questionnaires.mapper2.AnswerProvidedMapper;
import com.az.gretapyta.questionnaires.mapper2.AnswerSelectedMapper;
import com.az.gretapyta.questionnaires.mapper2.QuestionAnswerMapper;
import com.az.gretapyta.questionnaires.mapper2.UserQuestionnaireMapper;
import com.az.gretapyta.questionnaires.model2.*;
import com.az.gretapyta.questionnaires.service2.AnswersProvidedService;
import com.az.gretapyta.questionnaires.service2.AnswersSelectedService;
import com.az.gretapyta.questionnaires.service2.QuestionAnswersService;
import com.az.gretapyta.questionnaires.service2.UserQuestionnairesService;
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

import java.util.Collections;
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

  private final QuestionAnswersService questionAnswersService;
  private final QuestionAnswerMapper questionAnswerMapper;

  private final AnswersSelectedService answersSelectedService;
  private final AnswerSelectedMapper answersSelectedMapper;

  private final AnswersProvidedService answersProvidedService;
  private final AnswerProvidedMapper answerProvidedMapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(UserQuestionnaireController) Greetings to be passed ...");
    return USER_QUESTIONNAIRE_CONTROLLER_HOME_MESSAGE;
  }

  @GetMapping(value = "/all-no-paging", produces = MediaType.APPLICATION_JSON_VALUE)
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

    try {
      SetInHeaderReturnEntityInfo(response, UserQuestionnaireDTO.class.getSimpleName(), false);
      Optional<UserQuestionnaireDTO> itemDto = getItemsByUserIdAndQuestionnaireId(userId, questionnaireId);
      if (itemDto.isPresent()) {
        return ResponseEntity.ok(itemDto.get());
      }
      // itemDto.orElseThrow(NotFoundException::new);
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
      // itemDto.orElseThrow(NotFoundException::new);
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

  @PostMapping(value = "/takeone") // , produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserQuestionnaireDTO answerQuestionnaire(@RequestBody TakeQuestionnaireDTO entityDto,
                                    HttpServletRequest request) throws Exception {
    return executeAnswerQuestionnaire(request, entityDto, entityDto.getLangCode());
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
      String ipAddressFrom,
      UserQuestionnaireStatuses status,
      List<QuestionAnswerDTO> questionAnswerList ) {

    return new UserQuestionnaireDTO( langCode,
                                     ipAddressFrom,
                                     status,
                                     userDTO.getId(),
                                     questionnaireDTO.getId(),
                                     questionAnswerList );
  }


  //===/ Posting User taking Questionnaire /===============================//
  //
  @Transactional
  public UserQuestionnaireDTO executeAnswerQuestionnaire( HttpServletRequest request,
                                            TakeQuestionnaireDTO entityDto,
                                            String langCode ) throws Exception {
    //(1) IP Address:
    String ipAddress = request.getRemoteHost();

    //(2) User ID:
    int userId = this.getUserIdFromRequest(request, langCode, UserQuestionnaireController.class);

    // There might be different Exception than User ID Exception as above.
    return postAnswerQuestionnaire(entityDto, userId, ipAddress, langCode);
  }

  private UserQuestionnaireDTO postAnswerQuestionnaire( TakeQuestionnaireDTO entityDto,
                                    int userId,
                                    String ipAddress,
                                    String langCode ) throws Exception {

    try {
      UserQuestionnaireStatuses statusEnum =
          (UserQuestionnaireStatuses) EnumCommon.getEnumFromCode(UserQuestionnaireStatuses.values(), entityDto.getCompletionStatus());

      if (statusEnum == null) {
        statusEnum = UserQuestionnaireStatuses.UNKNOWN;
      }

      //(1) User-Questionnaire:
      UserQuestionnaireDTO userQuestionnaireDTO = new UserQuestionnaireDTO(
          langCode,
          ipAddress,
          statusEnum,
          userId,
          entityDto.getQuestionnaireId(),
          Collections.emptyList());

      UserQuestionnaireDTO newObj = executeCreateItem(userQuestionnaireDTO, langCode);

      //(2) Questionnaire-Questions:
      for (TakeQuestionDTO n : entityDto.getQuestionAnswers()) {
        createAnswerQuestion(newObj.getId(), n, langCode);
      }

      return this.fetchDTOFromId(newObj.getId()); // Re-load the now complete DTO object.
    } catch (Exception ex) {
      log.error("Create UserQuestionnaire failed", ex);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("UserQuestionnaire") +ex.getMessage(),
          ex.fillInStackTrace() + ":" + ex.getMessage());
    }
  }

  private void createAnswerQuestion( int userQuestionnaireId,
                                     TakeQuestionDTO takeQuestionDTO,
                                     String langCode ) throws Exception {

    //(1) Question-Answer(s):
    QuestionAnswerDTO qaDTO = getQuestionAnswerDTO(userQuestionnaireId, takeQuestionDTO);
    QuestionAnswer entity = questionAnswerMapper.map(qaDTO);
    QuestionAnswer entityCreated = questionAnswersService.createEntity(entity, langCode);

    boolean isUserInput = AnswerTypes.isOfUserInputType(takeQuestionDTO.getAnswerType());
    if (isUserInput) { // Answer provided or User Selections(s) ?
      //(2-a) Answer-User Response:
      GenericValue value = new GenericValue(takeQuestionDTO.getAnswerType(),
          takeQuestionDTO.getAnswerProvided());
      createAnswerProvided(entityCreated.getId(), value, langCode);
    } else {
      //(2-b) Answer-Selection(s):
      for (int n : takeQuestionDTO.getAnswersSelectionIds()) {
        createAnswerSelected(entityCreated.getId(), n, langCode);
      }
    }
  }

  private AnswerProvided createAnswerProvided( int questionAnswerId,
                                     GenericValue value,
                                     String langCode ) throws Exception {
    AnswerProvidedDTO dto = new AnswerProvidedDTO(questionAnswerId, value);
    return answersProvidedService.createEntity(answerProvidedMapper.map(dto), langCode);
  }

  private AnswerSelected createAnswerSelected( int questionAnswerId,
                                     int optionId,
                                     String langCode ) throws Exception {
    AnswerSelectedDTO answerSelectedDTO = new AnswerSelectedDTO(questionAnswerId, optionId);
    return answersSelectedService.createEntity(answersSelectedMapper.map(answerSelectedDTO), langCode);
  }

  private QuestionAnswerDTO getQuestionAnswerDTO( int userQuestionnaireId,
                                                  TakeQuestionDTO takeQuestionDTO ) {

    return new QuestionAnswerDTO(
        userQuestionnaireId,
        takeQuestionDTO.getQuestionId(),
        Collections.emptyList(),
        null );
  }
  //
  //===/ Posting User taking Questionnaire /===============================//

  //---/ Servicing part /------------------------------------------------//
}