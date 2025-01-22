package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.mapper.QuestionMapper;
import com.az.gretapyta.questionnaires.mapper.QuestionnaireMapper;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@Log4j2
@RestController
@RequestMapping(value = APIController.QUESTIONNAIRES_URL)
@RequiredArgsConstructor
public class QuestionnaireController extends BaseController {
  public static final String QUESTIONNAIRE_CONTROLLER_HOME_MESSAGE = "Hello World ! from Questionnaire Controller";

  private final QuestionnairesService service;
  private final QuestionnaireMapper mapper;
  private final QuestionMapper questionMapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(QuestionnaireController) Greetings to be passed ...");
    return QUESTIONNAIRE_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/questionnaires/all?lang=pl
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<QuestionnaireDTO>> getAllQuestionnairesLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    log.debug("===> Getting all Questionnaires with language selected: {}", langCode);
    SetInHeaderReturnEntityInfo(response, QuestionnaireDTO.class.getSimpleName(), true);

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
    return ResponseEntity.ok(getAllItems(userId, langCode));
  }

  // http://localhost:8091/api/ver1/questionnaires/search/byparent/?parentId=2&lang=pl
  @GetMapping(value = "/search/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<QuestionnaireDTO>> getItemsByParentIdLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "parentId", required = true) int parentId, // Questionnaire.Id
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, QuestionnaireDTO.class.getSimpleName(), true);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(getItemsForParent(parentId, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Questionnaire(s) for Drawer with ID = {}'not found !", parentId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(parentId));
    }
  }

  // http://localhost:8091/api/ver1/questionnaires/search4questions/?questionnaireId=2&lang=pl
  @GetMapping(value = "/search4questions/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<Set<QuestionDTO>> getQuestionsByQuestionnaire(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "questionnaireId", required = true) int questionnaireId,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, QuestionnaireDTO.class.getSimpleName(), true);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(getAllQuestionsForQuestionnaire(questionnaireId, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Question(s) for Questionnaire with ID = {}'not found !", questionnaireId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(questionnaireId));
    }
  }

  // http://localhost:8091/api/ver1/questionnaires/searchid/2?lang=pl
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<QuestionnaireDTO> getItemByIdsLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    log.debug("===> Getting Questionnaires by ID with language filtered to: {}", langCode);
    try {
      SetInHeaderReturnEntityInfo(response, QuestionnaireDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return  ResponseEntity.ok(fetchDTOFromId(id, userId, langCode));
    } catch (NotFoundException e) {
      log.error("Questionnaire with ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/questionnaires/searchcode/GST_PPL_IDS?lang=pl
  @GetMapping(value = "/searchcode/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<QuestionnaireDTO> getItemByCode(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "code") final String code,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, QuestionnaireDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok((fetchDTOFromCode(code, userId, langCode)).get());

    } catch (NotFoundException | NullPointerException e) {
      log.error("Questionnaire for code: '{}' not found !", code);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(code));
    }
  }

  // http://localhost:8091/api/ver1/questionnaires/uselections2024
  // @GetMapping(value = "/{urlIdName}", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetMapping(value = "/{urlIdName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<QuestionnaireDTO> getQuestionnaireByUrlIdLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "urlIdName") final String urlIdName,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    log.debug("===> Getting Questionnaires by URL name: {}", urlIdName);
    try {
      SetInHeaderReturnEntityInfo(response, QuestionnaireDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      /*
      Optional<Questionnaire> entity = service.getItemByUrlIdName(urlIdName, userId);
      String prefLanguage = ((entity.get().getPreferredLang() == null) || entity.get().getPreferredLang().isEmpty() ? DEFAULT_LOCALE : entity.get().getPreferredLang()) ;
      return ResponseEntity.ok(mapper.mapWithLang(entity.get(), prefLanguage));
      */
      return ResponseEntity.ok(getItemByUrlIdName(urlIdName, userId));
    } catch (NotFoundException e) {
      log.error("Questionnaire with URL ID: '{}' not found !", urlIdName);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(urlIdName));
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
  public QuestionnaireDTO createItem(
      HttpServletRequest request,
      @RequestBody QuestionnaireDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeCreateItem(entityDto, langCode);
  }

  @PatchMapping(value = "/patch", produces = "application/json")
  @ResponseBody
  public QuestionnaireDTO updateItem(
      HttpServletRequest request,
      @RequestBody QuestionnaireDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeUpdateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<QuestionnaireDTO> getAllItems(int userId, String langCode) {
    return service.getAllItems(userId).stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }

  @Transactional(readOnly = true)
  public List<QuestionnaireDTO> getItemsForParent(int parentId, int userId, String langCode) {
    return service.getItemsByParentId(parentId, userId).stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }

  @Transactional(readOnly = true)
  public QuestionnaireDTO fetchDTOFromId(Integer id, int userId, String langCode) {
    Questionnaire entity = service.getItemById(id, userId);
    return mapper.mapWithLang(entity, langCode);
  }

  @Transactional(readOnly = true)
  public Optional<QuestionnaireDTO> fetchDTOFromCode(String code, int userId, String langCode) {
    Optional<Questionnaire> ret = service.getItemByCode(code, userId);
    return ret.map(entity -> Optional.ofNullable(mapper.mapWithLang(entity, langCode))).orElse(null);
  }

  @Transactional(readOnly = true)
  public QuestionnaireDTO getItemByUrlIdName(String urlIdName, int userId) {
    Optional<Questionnaire> entity = service.getItemByUrlIdName(urlIdName, userId);
    String prefLanguage =
        ((entity.get().getPreferredLang() == null) || entity.get().getPreferredLang().isEmpty() ? DEFAULT_LOCALE : entity.get().getPreferredLang()) ;
    return mapper.mapWithLang(entity.get(), prefLanguage);
  }

  @Transactional(readOnly = true)
  public Set<QuestionDTO> getAllQuestionsForQuestionnaire(Integer questionnaireId, int userId, String langCode) {
    return new HashSet<>(service
        .getAllQuestionsForQuestionnaire(questionnaireId, userId)
        .stream().map(p -> questionMapper.mapWithLang(p, langCode))
        .toList()
        );
  }

  public QuestionnaireDTO executeCreateItem(QuestionnaireDTO entityDto, String langCode) throws Exception {
    try {
      Questionnaire entity = mapper.map(entityDto);
      Questionnaire entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Create Questionnaire failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Questionnaire"),
          exception.fillInStackTrace() + ":" + exception.getMessage(), Questionnaire.class);
    }
  }

  public QuestionnaireDTO executeUpdateItem(QuestionnaireDTO entityDto, String langCode) throws Exception {
    try {
      Questionnaire entity = mapper.map(entityDto);
      Questionnaire entityReturned = service.updateEntity(entity, langCode);
      return mapper.map(entityReturned);

    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Update Questionnaire failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_update_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Questionnaire", entityDto.getId()),
          exception.fillInStackTrace() + ": " + exception.getMessage());
    }
  }

  private Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode) {
    return service.getLexiconForCode(lexiconName, langCode);
  }
  //---/ Servicing part /-----------------------------------------------//
}