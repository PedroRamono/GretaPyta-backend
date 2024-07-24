package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto2.AnswerProvidedDTO;
import com.az.gretapyta.questionnaires.mapper2.AnswerProvidedMapper;
import com.az.gretapyta.questionnaires.model2.AnswerProvided;
import com.az.gretapyta.questionnaires.service2.AnswersProvidedService;
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
@RequestMapping(value = APIController.ANSWERS_PROVIDED_URL)
@RequiredArgsConstructor
public class AnswerProvidedController extends BaseController {
  public static final String ANSWER_PROVIDED_CONTROLLER_HOME_MESSAGE = "Hello World! from AnswerProvided Controller";

  private final AnswersProvidedService service;
  private final AnswerProvidedMapper mapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(AnswerProvidedController) Greetings to be passed ...");
    return ANSWER_PROVIDED_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/answers-provided/all
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AnswerProvidedDTO>> getAlL(
      HttpServletResponse response ) {
    log.debug("(AnswerProvidedController) getting all AnswerProvided");
    SetInHeaderReturnEntityInfo(response, AnswerProvidedDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems());
  }


  // http://localhost:8091/api/ver1/answers-provided/searchid/1
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<AnswerProvidedDTO> getItemById(
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id) {

    try {
      SetInHeaderReturnEntityInfo(response, AnswerProvidedDTO.class.getSimpleName(), false);
      return ResponseEntity.ok(fetchDTOFromId(id));
    } catch (NotFoundException | NullPointerException e) {
      log.error("AnswerProvidedDTO for ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/answers-provided/search/byquestionanswer/?questionAnswerId=1
  @GetMapping(value = "/search/byquestionanswer/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<AnswerProvidedDTO> getItemsByQuestionAnswer(
      HttpServletResponse response,
      @RequestParam(name = "questionAnswerId", required = true) int questionAnswerId) {

    try {
      SetInHeaderReturnEntityInfo(response, AnswerProvidedDTO.class.getSimpleName(), false);
      Optional<AnswerProvidedDTO> itemDto = getItemByQuestionAnswerId(questionAnswerId);
      if (itemDto.isPresent()) {
        return ResponseEntity.ok(itemDto.get());
      }
      itemDto.orElseThrow(NotFoundException::new);
    } catch (NotFoundException | NullPointerException e) {
      log.error("AnswerProvidedDTO for QuestionAnswer with ID = {} !", questionAnswerId);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(questionAnswerId));
    }
    return null;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public AnswerProvidedDTO createItem(@RequestBody AnswerProvidedDTO entityDto) throws Exception {
    String langCode = Constants.DEFAULT_LOCALE; //TODO ...
    return executeCreateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<AnswerProvidedDTO> getAllItems() {
    return service.getAllItems().stream().map(mapper::map).toList();
  }

  public AnswerProvidedDTO fetchDTOFromId(Integer id) {
    AnswerProvided entity = service.getItemById(id);
    return mapper.map(entity);
  }

  @Transactional(readOnly = true)
  public Optional<AnswerProvidedDTO> getItemByQuestionAnswerId(Integer questionAnswerId) {
    return service.getItemByQuestionAnswerId(questionAnswerId)
        .map(n -> Optional.ofNullable(mapper.map(n)))
        .orElse(null);
  }

  public AnswerProvidedDTO executeCreateItem(AnswerProvidedDTO entityDto, String langCode) throws Exception {
    try {
      AnswerProvided entity = mapper.map(entityDto);
      AnswerProvided entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Create AnswerProvided failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("AnswerProvided"),
          exception.fillInStackTrace() + ":" + exception.getMessage());
    }
  }
  //---/ Servicing part /------------------------------------------------//
}