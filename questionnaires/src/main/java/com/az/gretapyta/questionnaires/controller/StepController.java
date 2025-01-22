package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.mapper.QuestionnaireMapper;
import com.az.gretapyta.questionnaires.mapper.StepMapper;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.service.StepsService;
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

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@Log4j2
@RestController
@RequestMapping(value = APIController.STEPS_URL)
@RequiredArgsConstructor
public class StepController extends BaseController {
  public static final String STEP_CONTROLLER_HOME_MESSAGE = "Hello World ! from Step Controller";

  private final StepsService service;
  private final StepMapper mapper;

  private final QuestionnaireMapper questionnaireMapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(StepController) Greetings to be passed ...");
    return STEP_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/steps/allL?lang=en
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<StepDTO>> getAllStepsLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode ) {

    log.debug("(StepController) getting all Steps with language selected: {}", langCode);
    ///  return ResponseEntity.ok(service.getAllItems());
    SetInHeaderReturnEntityInfo(response, StepDTO.class.getSimpleName(), true);

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
    return ResponseEntity.ok(getAllItems(userId, langCode));
  }

  // http://localhost:8091/api/ver1/steps/search/byparent/?parentId=5&lang=pl
  @GetMapping(value = "/search/byparent/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<StepDTO>> getItemsByParentIdLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      // @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "parentId", required = true) int parentId, // Questionnaire.Id
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, StepDTO.class.getSimpleName(), true);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(getItemsForParent(parentId, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Step(s) for Questionnaire with ID = {}'not found !", parentId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(parentId));
    }
  }

  // http://localhost:8091/api/ver1/steps/searchid/6?lang=ru
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<StepDTO> getItemByIdsLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, StepDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(fetchDTOFromId(id, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Step for ID = {} not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }



////////////////////////////////////////////////////////////////////////////
  // SEARCH_ENTITY_ON_LANG_TEXT_API
// http://localhost:8091/api/ver1/steps/searchtxt/?fieldName=nameMultilang&txtFilter=Person's&lang=en
@GetMapping(value = APIController.SEARCH_ENTITY_ON_LANG_TEXT_API, produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional(readOnly = true)
public ResponseEntity<List<StepDTO>> getItemsByTextFilter(
    HttpServletRequest request,
    HttpServletResponse response,
    @RequestParam(name = "fieldName", required = false, defaultValue = DEFAULT_LOCALE) String fieldName,
    @RequestParam(name = "txtFilter", required = true, defaultValue = DEFAULT_LOCALE) String txtFilter,
    @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    SetInHeaderReturnEntityInfo(response, StepDTO.class.getSimpleName(), false);
    int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
    return ResponseEntity.ok(getAllItemsFiltered(fieldName, txtFilter, userId, langCode));
}
/////////////////////////////////////////////////////////////////////////////


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public StepDTO createItem(
      HttpServletRequest request,
      @RequestBody StepDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode
      ) throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeCreateItem(entityDto, Constants.DEFAULT_LOCALE);
  }

  // @PostMapping(value = "/patch", produces = "application/json")
  @PatchMapping(value = "/patch", produces = "application/json")
  @ResponseBody
  public StepDTO updateItem(
      HttpServletRequest request,
      @RequestBody StepDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeUpdateItem(entityDto, Constants.DEFAULT_LOCALE);
  }

  //---/ Servicing part /-----------------------------------------------//
  @Transactional(readOnly = true)
  public List<StepDTO> getAllItems(int userId, String langCode) {
    return service.getAllItems(userId).stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }

  @Transactional(readOnly = true)
  public List<StepDTO> getAllItemsFiltered(String fieldName, String txtFilter, int userId, String langCode) {
    return service.getAllItemsFiltered(fieldName, txtFilter, userId, langCode).stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }


  @Transactional(readOnly = true)
  public List<StepDTO> getItemsForParent(int parentId, int userId, String langCode) {
    List<StepDTO> list = service.getItemsByParentId(parentId).stream().map(p -> mapper.mapForParentWithLang(p, parentId, langCode)).toList();
    return list.stream().sorted(Comparator.comparing(StepDTO::getDisplayOrder)).toList(); // .reversed()
  }

  @Transactional(readOnly = true)
  public StepDTO fetchDTOFromId(Integer id, int userId, String langCode) {
    Step entity = service.getItemById(id, userId);
    return mapper.mapWithLang(entity, langCode);
  }

  public Optional<StepDTO> findByNameMultilangFirstLike(String pattern, int userId) {
    Optional<Step> ret = service.findByNameMultilangFirstLike(pattern, userId);
    return ret.map(step -> Optional.ofNullable(mapper.map(step))).orElse(null);
  }

  public StepDTO executeCreateItem(StepDTO entityDto, String langCode) throws Exception {
    try {
      Step entity = mapper.map(entityDto);
      Step entityCreated = service.createEntity(entity, langCode);
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

  public StepDTO executeUpdateItem(StepDTO entityDto, String langCode) throws Exception {
    try {
      Step entity = mapper.map(entityDto);
      Step entityReturned = service.updateEntity(entity, langCode);
      return mapper.map(entityReturned);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Update Step failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_update_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Step", entityDto.getId()),
          exception.fillInStackTrace() + ": " + exception.getMessage());
    }
  }

  // Step-> Questionnaire Link part
  public QuestionnaireStepLink executeCreateParentChildLink( QuestionnaireDTO questionnaireDTO,
                                                             StepDTO stepDTO,
                                                             int displayOrder,
                                                             int tenantId ) {
    Questionnaire questionnaire = questionnaireMapper.map(questionnaireDTO);
    Step step = mapper.map(stepDTO);
    return this.service.saveQuestionnaireStep(questionnaire, step, displayOrder, tenantId);
  }
  //---/ Servicing part /-----------------------------------------------//
}