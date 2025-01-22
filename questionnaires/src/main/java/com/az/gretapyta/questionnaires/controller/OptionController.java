package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.mapper.OptionMapper;
import com.az.gretapyta.questionnaires.mapper.QuestionMapper;
import com.az.gretapyta.questionnaires.model.Option;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import com.az.gretapyta.questionnaires.service.OptionsService;
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

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@Log4j2
@RestController
@RequestMapping(value = APIController.OPTIONS_URL)
@RequiredArgsConstructor
//OK: @CrossOrigin(origins = "http://localhost:4206", maxAge = 3600)
public class OptionController extends BaseController {
  public static final String OPTION_CONTROLLER_HOME_MESSAGE = "Hello World ! from Option Controller";

  private final OptionsService service;
  private final OptionMapper mapper;
  private final QuestionMapper questionMapper;
  private final QuestionsService questionsService;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(OptionController) Greetings to be passed ...");
    return OPTION_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/options/all?lang=en
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<OptionDTO>> getAlLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    log.debug("(OptionController) getting all Options with language selected: {}", langCode);
    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
    SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems(userId, langCode));
  }

  // http://localhost:8091/api/ver1/options/search/byparent/?parentId=5&lang=pl
  @GetMapping(value = "/search/byparent/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<OptionDTO>> getItemsByParentIdLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "parentId", required = true) int parentId,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), true);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(getItemsForParent(parentId, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Option(s) for Question with ID = {} not found !", parentId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(parentId));
    }
  }

  // http://localhost:8091/api/ver1/options/search/byparentcode/?parentCode=QUE_PPL_IDS_SEX&lang=pl
  @GetMapping(value = "/search/byparentcode/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<OptionDTO>> getItemsByParentCodeLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "parentCode", required = true) final String code,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), true);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(getItemsForParentCode(code, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Option(s) for Question with code = '{}' not found !", code);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(code));
    }
  }

  // http://localhost:8091/api/ver1/options/searchid/2?lang=ru
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<OptionDTO> getItemByIdsLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok(fetchDTOFromId(id, userId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Option for ID = {} not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  //---/ Passing Object Type in response issue ------------------------//
  // http://localhost:8091/api/ver1/options/searchcode/OPT_USEL24_OTDM?lang=pl
  @GetMapping(value = "/searchcode/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<OptionDTO> getItemByCode(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "code") final String code,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), false);
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
      return ResponseEntity.ok((fetchDTOFromCode(code, userId, langCode)).get());
    } catch (NotFoundException e) {
      log.error("Option for code = '{}' not found !", code);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(code));
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public OptionDTO createItem(
      HttpServletRequest request,
      @RequestBody OptionDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeCreateItem(entityDto, langCode);
  }

  @PatchMapping(value = "/patch", produces = "application/json")
  @ResponseBody
  public OptionDTO updateItem(
      HttpServletRequest request,
      @RequestBody OptionDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeUpdateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<OptionDTO> getAllItems(int userId, String langCode) {
    return service.getAllItems(userId).stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }

  @Transactional(readOnly = true)
  public List<OptionDTO> getItemsForParent(int parentId, int userId, String langCode) {
    List<OptionDTO> list = service.getItemsByParentId(parentId, userId).stream().map(p -> mapper.mapForParentWithLang(p, parentId, langCode)).toList();
    return list.stream().sorted(Comparator.comparing(OptionDTO::getDisplayOrder)).toList(); // .reversed()
  }

  @Transactional(readOnly = true)
  public List<OptionDTO> getItemsForParentCode(String parentCode, int userId, String langCode) {
    Optional<Question> parent = questionsService.getItemByCode(parentCode, userId);
    if (parent.isEmpty()) {
      throw new NotFoundException();
    }
    return getItemsForParent(parent.get().getId(), userId, langCode);
  }

  @Transactional(readOnly = true)
  public OptionDTO fetchDTOFromId(Integer id, int userId, String langCode) {
    Option entity = service.getItemById(id, userId);
    return mapper.mapWithLang(entity, langCode);
  }

  @Transactional(readOnly = true)
  public Optional<OptionDTO> fetchDTOFromCode(String code, int userId, String langCode) {
    Optional<Option> ret = service.getItemByCode(code, userId);
    return ret.map(entity -> Optional.ofNullable(mapper.mapWithLang(entity, langCode))).orElse(null);
  }

  public OptionDTO executeCreateItem(OptionDTO entityDto, String langCode) throws Exception {
    try {
      Option entity = mapper.map(entityDto);
      Option entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Create Option failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Option"),
          exception.fillInStackTrace() + ":" + exception.getMessage());
    }
  }

  public OptionDTO executeUpdateItem(OptionDTO entityDto, String langCode) throws Exception {
    try {
      Option entity = mapper.map(entityDto);
      Option entityReturned = service.updateEntity(entity, langCode);
      return mapper.map(entityReturned);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Update Option failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_update_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Option", entityDto.getId()),
          exception.fillInStackTrace() + ": " + exception.getMessage());
    }
  }

  // Option -> Question Link part /-----//
  public QuestionOptionLink executeCreateParentChildLink( QuestionDTO questionDTO,
                                                          OptionDTO optionDTO,
                                                          int displayOrder,
                                                          int tenantId ) {

    Question question = questionMapper.map(questionDTO);
    Option option = mapper.map(optionDTO);
    return this.service.saveQuestionOption(question, option, displayOrder, tenantId);
  }
  //---/ Servicing part /------------------------------------------------//
}