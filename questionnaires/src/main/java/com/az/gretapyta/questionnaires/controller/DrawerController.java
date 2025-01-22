package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.dto.DrawerDTO;
import com.az.gretapyta.questionnaires.mapper.DrawerMapper;
import com.az.gretapyta.questionnaires.model.Drawer;
import com.az.gretapyta.questionnaires.service.DrawersService;
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

import java.util.List;
import java.util.Optional;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@Log4j2
@RestController
@RequestMapping(value = APIController.DRAWERS_URL)
@RequiredArgsConstructor
public class DrawerController extends BaseController {
  public static final String DRAWER_CONTROLLER_HOME_MESSAGE = "Hello World! from Drawer Controller";

  private final DrawersService service;
  private final DrawerMapper mapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(DrawerController) Greetings to be passed ...");
    return DRAWER_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/drawers/all?lang=ru
  @Transactional(readOnly = true)
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DrawerDTO>> getAllDrawersLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    log.debug("===> Getting all Drawers with language selected: {}", langCode);

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());
    SetInHeaderReturnEntityInfo(response, DrawerDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems(userId, langCode));
  }

  // http://localhost:8091/api/ver1/drawers/searchid/2?lang=ru
  @Transactional(readOnly = true)
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DrawerDTO> getItemByIdsLangFiltered(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, DrawerDTO.class.getSimpleName(), false);

      // Get User ID from Request, set in DTO:
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());

      return ResponseEntity.ok(fetchDTOFromId(id, userId, langCode));
    } catch (NotFoundException e) {
      log.error("Drawer for ID = {} not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", langCode);
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/drawers/searchcode/DRW_POL?lang=pl
  @Transactional(readOnly = true)
  @GetMapping(value = "/searchcode/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DrawerDTO> getItemByCode(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "code") final String code,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, DrawerDTO.class.getSimpleName(), false);

      // Get User ID from Request, set in DTO:
      int userId = this.getUserIdFromRequestOrZero(request, langCode, this.getClass());

      return ResponseEntity.ok((fetchDTOFromCode(code, userId, langCode)).get());
    } catch (NotFoundException | NullPointerException e) {
      log.error("Drawer for code = '{}' not found !", code);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(code));
    }
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public DrawerDTO createItem(
      HttpServletRequest request,
      @RequestBody DrawerDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeCreateItem(entityDto, langCode);
  }

  /// @PostMapping(value = "/patch", produces = "application/json")
  @PutMapping(value = "/patch", produces = "application/json")
  @ResponseBody
  public DrawerDTO updateItem(
      HttpServletRequest request,
      @RequestBody DrawerDTO entityDto,
      @RequestParam(name = "lang", required = false, defaultValue = DEFAULT_LOCALE) String langCode)
      throws Exception {

    // Get User ID from Request, set in DTO:
    int userId = this.getUserIdFromRequest(request, langCode, this.getClass());
    entityDto.setUserId(userId);
    return executeUpdateItem(entityDto, langCode);
  }

  //---/ Servicing part /------------------------------------------------//
  //
  @Transactional(readOnly = true)
  public List<DrawerDTO> getAllItems(int userId, String langCode) {
    return service.getAllItems(userId).stream().map(p -> mapper.mapWithLang(p, langCode)).toList();
  }

  @Transactional(readOnly = true)
  public DrawerDTO fetchDTOFromId(Integer id, int userId, String langCode) {
    Drawer entity = service.getItemById(id, userId);
    return mapper.mapWithLang(entity, langCode);
  }

  @Transactional(readOnly = true)
  public Optional<DrawerDTO> fetchDTOFromCode(String code, int userId, String langCode) {
    Optional<Drawer> ret = service.getItemByCode(code, userId);
    return ret.map(entity -> Optional.ofNullable(mapper.mapWithLang(entity, langCode))).orElse(null);
  }

  public DrawerDTO executeCreateItem(DrawerDTO entityDto, String langCode) throws Exception {
    try {
      Drawer entity = mapper.map(entityDto);
      Drawer entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Create Drawer failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Drawer"),
          exception.fillInStackTrace() + ":" + exception.getMessage());
    }
  }

  public DrawerDTO executeUpdateItem(DrawerDTO entityDto, String langCode) throws Exception {
    try {
      Drawer entity = mapper.map(entityDto);
      Drawer entityReturned = service.updateEntity(entity, langCode);
      return mapper.map(entityReturned);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Update Drawer failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_update_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("Drawer", entityDto.getId()),
          exception.fillInStackTrace() + ": " + exception.getMessage());
    }
  }
  //---/ Servicing part /------------------------------------------------//

  //TODO... other methods (DELETE)
  /*
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrawer(@PathVariable(name = "id") final Long id) {
        drawersService.delete(id);
        return ResponseEntity.noContent().build();
    }
  */
}