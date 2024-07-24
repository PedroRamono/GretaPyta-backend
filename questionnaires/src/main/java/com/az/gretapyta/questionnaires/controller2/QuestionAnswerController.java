package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto2.AnswerProvidedDTO;
import com.az.gretapyta.questionnaires.dto2.AnswerSelectedDTO;
import com.az.gretapyta.questionnaires.dto2.QuestionAnswerDTO;
import com.az.gretapyta.questionnaires.dto2.UserQuestionnaireDTO;
import com.az.gretapyta.questionnaires.mapper2.QuestionAnswerMapper;
import com.az.gretapyta.questionnaires.model2.QuestionAnswer;
import com.az.gretapyta.questionnaires.service2.QuestionAnswersService;
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
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping(value = APIController.QUESTIONS_ANSWERS_URL)
@RequiredArgsConstructor
public class QuestionAnswerController extends BaseController {
  public static final String QUESTION_ANSWER_CONTROLLER_HOME_MESSAGE = "Hello World! from QuestionAnswerController Controller";

  private final QuestionAnswersService service;
  private final QuestionAnswerMapper mapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(QuestionAnswerController) Greetings to be passed ...");
    return QUESTION_ANSWER_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/questions-answers/all
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<QuestionAnswerDTO>> getAlL(
      HttpServletResponse response ) {

    log.debug("(QuestionAnswerController) getting all items");
    SetInHeaderReturnEntityInfo(response, QuestionAnswerDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems());
  }

  // http://localhost:8091/api/ver1/questions-answers/searchid/1
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<QuestionAnswerDTO> getItemById(
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id) {

    try {
      SetInHeaderReturnEntityInfo(response, QuestionAnswerDTO.class.getSimpleName(), false);
      return ResponseEntity.ok(fetchDTOFromId(id));
    } catch (NotFoundException | NullPointerException e) {
      log.error("QuestionAnswer for ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/questions-answers/search/byuserquestionnaire/?userQuestionnaireId=1
  @GetMapping(value = "/search/byuserquestionnaire/", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<QuestionAnswerDTO>> getItemsByUserQuestionnaire(
      HttpServletResponse response,
      @RequestParam(name = "userQuestionnaireId", required = true) int userQuestionnaireId ) {

    SetInHeaderReturnEntityInfo(response, QuestionAnswerDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getItemsByUserQuestionnaireId(userQuestionnaireId));
  }

  // http://localhost:8091/api/ver1/questions-answers/search/byuserquestionnaireandquestion/?userQuestionnaireId=1&questionId=1
  @GetMapping(value = "/search/byuserquestionnaireandquestion/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<QuestionAnswerDTO> getItemsByUserQuestionnaireAndQuestion(
      HttpServletResponse response,
      @RequestParam(name = "userQuestionnaireId", required = true) int userQuestionnaireId, // Step.Id
      @RequestParam(name = "questionId", required = true) int questionId) {
    // @PathVariable(name = "id") final Integer id,

    try {
      SetInHeaderReturnEntityInfo(response, QuestionAnswerDTO.class.getSimpleName(), false);
      Optional<QuestionAnswerDTO> itemDto = getItemsByUserQuestionnaireIdAndQuestionId(userQuestionnaireId, questionId);
      if (itemDto.isPresent()) {
        return ResponseEntity.ok(itemDto.get());
      }
      itemDto.orElseThrow(NotFoundException::new);
    } catch (NotFoundException | NullPointerException e) {
      log.error("QuestionAnswerDTO for UserQuestionnaire with ID = {} and Question ID = {} not found !", userQuestionnaireId, questionId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(userQuestionnaireId));
    }
    return null;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public QuestionAnswerDTO createItem(@RequestBody QuestionAnswerDTO entityDto) throws Exception {
    String langCode = Constants.DEFAULT_LOCALE; //TODO ...
    return executeCreateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<QuestionAnswerDTO> getAllItems() {
    return service.getAllItems().stream().map(mapper::map).toList();
  }

  public QuestionAnswerDTO fetchDTOFromId(Integer id) {
    QuestionAnswer entity = service.getItemById(id);
    return mapper.map(entity);
  }

  @Transactional(readOnly = true)
  public  List<QuestionAnswerDTO> getItemsByUserQuestionnaireId(Integer userQuestionnaireId) {
    return service.getAllItemsByUserQuestionnaireId(userQuestionnaireId).stream().map(mapper::map).toList();
  }

  @Transactional(readOnly = true)
  public Optional<QuestionAnswerDTO> getItemsByUserQuestionnaireIdAndQuestionId(
      Integer userQuestionnaireId,
      Integer questionId) {
    return service.getItemByUserQuestionnaireIdAndQuestionId(userQuestionnaireId, questionId)
        .map(n -> Optional.ofNullable(mapper.map(n)))
        .orElse(null);
  }

  public QuestionAnswerDTO executeCreateItem(QuestionAnswerDTO entityDto, String langCode) throws Exception {
    try {
      QuestionAnswer entity = mapper.map(entityDto);
      QuestionAnswer entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception ex) {
      log.error("Create QuestionAnswer failed", ex);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("AnswerProvided"),
          ex.fillInStackTrace() + ":" + ex.getMessage());
    }
  }

  @Transactional
  public static QuestionAnswerDTO createQuestionAnswerDTO( UserQuestionnaireDTO userQuestionnaireDTO,
                                                           int questionId,// QuestionDTO questionDTO
                                                           List<AnswerSelectedDTO> answerSelectionsDTO,
                                                           AnswerProvidedDTO answerProvidedDTO) {
    return new QuestionAnswerDTO(
        userQuestionnaireDTO.getId(),
        questionId,
        answerSelectionsDTO,
        answerProvidedDTO );
  }
  //---/ Servicing part /------------------------------------------------//
}