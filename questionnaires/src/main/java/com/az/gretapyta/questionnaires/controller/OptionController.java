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

@Log4j2
@RestController
@RequestMapping(value = APIController.OPTIONS_URL)
@RequiredArgsConstructor
public class OptionController extends BaseController {
  public static final String OPTION_CONTROLLER_HOME_MESSAGE = "Hello World ! from Option Controller";

  private final OptionsService service;
  private final OptionMapper mapper;
  private final QuestionMapper questionMapper;

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
      HttpServletResponse response,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    log.debug("(OptionController) getting all Options with language selected: {}", langCode);
    SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems(langCode));
  }

  // http://localhost:8091/api/ver1/options/search/byparent/?parentId=5&lang=pl
  @GetMapping(value = "/search/byparent/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<OptionDTO>> getItemsByParentIdLangFiltered(
      HttpServletResponse response,
      @RequestParam(name = "parentId", required = true) int parentId,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), true);
      return ResponseEntity.ok(getItemsForParent(parentId, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Option(s) for Question with ID = {}'not found !", parentId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(parentId));
    }
  }

  // http://localhost:8091/api/ver1/options/searchid/2?lang=ru
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<OptionDTO> getItemByIdsLangFiltered(
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), false);
      return ResponseEntity.ok(fetchDTOFromId(id, langCode));
    } catch (NotFoundException | NullPointerException e) {
      log.error("Option for ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  //---/ Passing Object Type in response issue ------------------------//
  // http://localhost:8091/api/ver1/options/searchcode/OPT_USEL24_OTDM?lang=pl
  //(1) In header:
  @GetMapping(value = "/searchcode/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<OptionDTO> getItemByCode(
      HttpServletResponse response,
      @PathVariable(name = "code") final String code,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, OptionDTO.class.getSimpleName(), false);
      return ResponseEntity.ok((fetchDTOFromCode(code, langCode)).get());
    } catch (NotFoundException e) {
      log.error("Option for code: '{}' not found !", code);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(code));
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public OptionDTO createItem(@RequestBody OptionDTO entityDto) throws Exception {
    return executeCreateItem(entityDto, Constants.DEFAULT_LOCALE);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<OptionDTO> getAllItems(String langCode) {
    return service.getAllItems().stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }

  @Transactional(readOnly = true)
  public List<OptionDTO> getItemsForParent(int parentId, String langCode) {
    List<OptionDTO> list = service.getItemsByParentId(parentId).stream().map(p -> mapper.mapForParentWithLang(p, parentId, langCode)).toList();
    return list.stream().sorted(Comparator.comparing(OptionDTO::getDisplayOrder)).toList(); // .reversed()
  }

  @Transactional(readOnly = true)
  public OptionDTO fetchDTOFromId(Integer id, String langCode) {
    Option entity = service.getItemById(id);
    return mapper.mapWithLang(entity, langCode);
  }

  @Transactional(readOnly = true)
  public Optional<OptionDTO> fetchDTOFromCode(String code, String langCode) {
    Optional<Option> ret = service.getItemByCode(code);
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