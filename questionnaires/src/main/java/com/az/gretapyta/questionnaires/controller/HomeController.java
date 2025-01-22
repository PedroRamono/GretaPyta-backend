package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.EntityDictionary;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.service2.LocaleDictService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@Log4j2
@RestController
@RequestMapping(value = APIController.API_ROOT_URL)
@RequiredArgsConstructor
public class HomeController extends BaseController {
  public static final String HOME_REQUEST_MESSAGE = "GretaPyta Home !";

  protected final LocaleDictService localeDictService;

  @GetMapping(value = "/")
  public @ResponseBody String greeting() {
    return HOME_REQUEST_MESSAGE;
  }

  // http://localhost:8091/api/ver1/dictionary?lang=pl
  @Transactional(readOnly = true)
  @GetMapping(value = "/dictionary", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<EntityDictionary>> getAllEntitiesDictionary(
      HttpServletResponse response,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

log.info("===> Getting Dictionary of Entities for language: {}", langCode);
    return ResponseEntity.ok(getAllEntitiesDictionary(langCode));
  }

  // http://localhost:8091/api/ver1/dictionary/search/drawer?lang=pl
  @GetMapping(value = "/dictionary/search/" + "{entity}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<EntityDictionary> getEntityByCodeLangFiltered(
      HttpServletResponse response,
      @PathVariable(name = "entity") final String entity,
      @RequestParam(name = "lang", required = false, defaultValue = Constants.DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, EntityDictionary.class.getSimpleName(), false);
log.info("===> Getting Dictionary for {} with language: {}", entity, langCode);
      return ResponseEntity.ok((getEntityDictionary(entity, langCode)).get());

    } catch (NotFoundException | NullPointerException e) {
      log.error("Questionnaire for code: '{}' not found !", entity);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(entity));
    }
  }

  //---/ Servicing part /------------------------------------------------//
  @Transactional(readOnly = true)
  public List<EntityDictionary> getAllEntitiesDictionary(String langCode) {
    return localeDictService.getAllEntitiesDictionary(langCode);
  }

  @Transactional(readOnly = true)
  public Optional<EntityDictionary> getEntityDictionary(String entityName, String langCode) {
    return localeDictService.getEntityDictionary(entityName, langCode);
  }
  //---/ Servicing part /------------------------------------------------//
}