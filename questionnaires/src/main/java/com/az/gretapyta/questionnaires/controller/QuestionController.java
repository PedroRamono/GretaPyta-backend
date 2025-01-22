package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.mapper.QuestionMapper;
import com.az.gretapyta.questionnaires.mapper.StepMapper;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.model.StepQuestionLink;
import com.az.gretapyta.questionnaires.service.QuestionsService;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@Log4j2
@RestController
@RequestMapping(value = APIController.QUESTIONS_URL)
@RequiredArgsConstructor
public class QuestionController extends BaseController {
  public static final String QUESTION_CONTROLLER_HOME_MESSAGE = "Hello World ! from Question Controller";

  private final QuestionsService service;
  private final QuestionMapper mapper;
  private final StepMapper stepMapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(QuestionController) Greetings to be passed ...");
    return QUESTION_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/questions/all?lang=en
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<QuestionDTO>> getAlLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    log.debug("(QuestionController) getting all Questions with language selected: {}", langCode);
    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
    SetInHeaderReturnEntityInfo(response, QuestionDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems(userId, langCode));
  }

  // http://localhost:8091/api/ver1/questions/search/byparent/?parentId=5&lang=pl
  @GetMapping(value = "/search/byparent/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<QuestionDTO>> getItemsByParentIdLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      // @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "parentId", required = true) int parentId, // Step.Id
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, QuestionDTO.class.getSimpleName(), true);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(getItemsForParent(parentId, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Question(s) for Step with ID = {}'not found !", parentId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(parentId));
    }
  }

  // http://localhost:8091/api/ver1/questions/searchid/2?lang=ru
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<QuestionDTO> getItemByIdsLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, QuestionDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(fetchDTOFromId(id, userId, langCode));
    } catch (NotFoundException e) {
      log.error("Question for ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/questions/searchcode/OPT_USEL24_OTDM?lang=pl
  @GetMapping(value = "/searchcode/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<QuestionDTO> getItemByCode(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "code") final String code,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, QuestionDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok((fetchDTOFromCode(code, userId, langCode)).get());
    } catch (NotFoundException | NullPointerException e) {
      log.error("Question for code: '{}' not found !", code);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(code));
    }
  }

  // http://localhost:8091/api/ver1/questionnaires/lexicon/type?lang=pl
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
  public QuestionDTO createItem(
      HttpServletRequest request,
      @RequestBody QuestionDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);

    return executeCreateItem(entityDto, langCode);
  }

  @PatchMapping(value = "/patch", produces = "application/json")
  @ResponseBody
  public QuestionDTO updateItem(
      HttpServletRequest request,
      @RequestBody QuestionDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);

    return executeUpdateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<QuestionDTO> getAllItems(int userId, String langCode) {
    return service.getAllItems(userId).stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }

  @Transactional(readOnly = true)
  public List<QuestionDTO> getItemsForParent(int parentId, int userId, String langCode) {
    List<QuestionDTO> list = service.getItemsByParentId(parentId, userId).stream().map(p -> mapper.mapForParentWithLang(p, parentId, langCode)).toList();
    return list.stream().sorted(Comparator.comparing(QuestionDTO::getDisplayOrder)).toList(); // .reversed()
  }

  @Transactional(readOnly = true)
  public QuestionDTO fetchDTOFromId(Integer id, int userId, String langCode) {
    Question entity = service.getItemById(id, userId);
    return mapper.mapWithLang(entity, langCode);
  }

  @Transactional(readOnly = true)
  public Optional<QuestionDTO> fetchDTOFromCode(String code, int userId, String langCode) {
    Optional<Question> ret = service.getItemByCode(code, userId);
    return ret.map(entity -> Optional.ofNullable(mapper.mapWithLang(entity, langCode))).orElse(null);
  }

  public QuestionDTO executeCreateItem(QuestionDTO entityDto, String langCode) throws Exception {
    try {
      Question entity = mapper.map(entityDto);
      Question entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Create Step failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Step"),
          exception.fillInStackTrace() + ":" + exception.getMessage());
    }
  }

  // Question -> Step Link part
  public StepQuestionLink executeCreateParentChildLink( StepDTO stepDTO,
                                                        QuestionDTO questionDTO,
                                                        int displayOrder,
                                                        int tenantId ) throws BusinessException {

    Step step = stepMapper.map(stepDTO);
    Question question = mapper.map(questionDTO);
    return this.service.saveStepQuestion(step, question, displayOrder, tenantId);
  }

  public QuestionDTO executeUpdateItem(QuestionDTO entityDto, String langCode) throws Exception {
    try {
      Question entity = mapper.map(entityDto);
      Question entityReturned = service.updateEntity(entity, langCode);
      return mapper.map(entityReturned);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Update Question failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_update_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Question", entityDto.getId()),
          exception.fillInStackTrace() + ": " + exception.getMessage());
    }
  }

  private Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode) {
    return service.getLexiconForCode(lexiconName, langCode);
  }
  //---/ Servicing part /------------------------------------------------//
}