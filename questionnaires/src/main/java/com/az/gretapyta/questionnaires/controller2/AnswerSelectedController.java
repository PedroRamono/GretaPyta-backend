package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto2.AnswerSelectedDTO;
import com.az.gretapyta.questionnaires.mapper2.AnswerSelectedMapper;
import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import com.az.gretapyta.questionnaires.service2.AnswersSelectedService;
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

@Log4j2
@RestController
@RequestMapping(value = APIController.ANSWERS_SELECTED_URL)
@RequiredArgsConstructor
public class AnswerSelectedController extends BaseController {
  public static final String ANSWER_SELECTED_CONTROLLER_HOME_MESSAGE = "Hello World! from AnswerSelected Controller";

  private final AnswersSelectedService service;
  private final AnswerSelectedMapper mapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(AnswerSelectedController) Greetings to be passed ...");
    return ANSWER_SELECTED_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/answers-selected/all
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AnswerSelectedDTO>> getAlL(
      HttpServletResponse response ) {

    log.debug("(AnswerSelectedController) getting all answer selections");
    SetInHeaderReturnEntityInfo(response, AnswerSelectedDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems());
  }

  // http://localhost:8091/api/ver1/questions-answers/searchid/1
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<AnswerSelectedDTO> getItemById(
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id) {

    try {
      SetInHeaderReturnEntityInfo(response, AnswerSelectedDTO.class.getSimpleName(), false);
      return ResponseEntity.ok(fetchDTOFromId(id));
    } catch (NotFoundException | NullPointerException e) {
      log.error("AnswerSelectedDTO for ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/questions-answers/search/byquestionanswer/?questionAnswerId=1
  @GetMapping(value = "/search/byquestionanswer/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<AnswerSelectedDTO>> getItemsByQuestionAnswer(
      HttpServletResponse response,
      @RequestParam(name = "questionAnswerId", required = true) int questionAnswerId) {

    SetInHeaderReturnEntityInfo(response, AnswerSelectedDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getItemsByQuestionAnswerId(questionAnswerId));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public AnswerSelectedDTO createItem(@RequestBody AnswerSelectedDTO entityDto) throws Exception {
    String langCode = Constants.DEFAULT_LOCALE; //TODO ...
    return executeCreateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<AnswerSelectedDTO> getAllItems() {
    return service.getAllItems().stream().map(mapper::map).toList();
  }

  public AnswerSelectedDTO fetchDTOFromId(Integer id) {
    AnswerSelected entity = service.getItemById(id);
    return mapper.map(entity);
  }

  @Transactional(readOnly = true)
  public List<AnswerSelectedDTO> getItemsByQuestionAnswerId(Integer questionAnswerId) {
    return service.getItemsByQuestionAnswerId(questionAnswerId).stream().map(mapper::map).toList();
  }

  public AnswerSelectedDTO executeCreateItem(AnswerSelectedDTO entityDto, String langCode) throws Exception {
    try {
      AnswerSelected entity = mapper.map(entityDto);
      AnswerSelected entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Create AnswerSelected failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("AnswerSelected"),
          exception.fillInStackTrace() + ":" + exception.getMessage());
    }
  }
  //---/ Servicing part /------------------------------------------------//
}