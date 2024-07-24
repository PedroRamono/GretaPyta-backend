package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.mapper2.UserMapper;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.service2.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping(value = APIController.USERS_URL)
@RequiredArgsConstructor
@CrossOrigin // for Authentication
// @PreAuthorize("hasAuthority('ROLE_AU')")
public class UserController extends BaseController {
  public static final String USER_CONTROLLER_HOME_MESSAGE = "Hello World! from User Controller";

  protected final UsersService service;
  protected final UserMapper mapper;

  @GetMapping(value = "/")
  @ResponseBody
  public String index() {
    log.debug("(UserController) Greetings to be passed ...");
    return USER_CONTROLLER_HOME_MESSAGE;
  }

  // http://localhost:8091/api/ver1/users/all
  @Transactional(readOnly = true)
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserDTO>> getAlL(
      HttpServletResponse response ) {

    log.debug("(UserController) getting all Users");
    SetInHeaderReturnEntityInfo(response, UserDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(getAllItems());
  }

  // http://localhost:8091/api/ver1/users/searchid/2
  @GetMapping(value = APIController.SEARCH_ENTITY_BY_ID_API + "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<UserDTO> getItemByIdsLangFiltered(
      HttpServletResponse response,
      @PathVariable(name = "id") final Integer id ) {

    log.debug("===> Getting User by ID: {}", id);
    try {
      SetInHeaderReturnEntityInfo(response, UserDTO.class.getSimpleName(), false);
      return  ResponseEntity.ok(fetchDTOFromId(id));
    } catch (NotFoundException e) {
      log.error("User with ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/users/anonymous/pl
  @Transactional(readOnly = true)
  @GetMapping(value = "/anonymous/{langCode}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDTO> getItemByCode(
      HttpServletResponse response,
      @PathVariable(name = "langCode") final String langCode) {

    try {
      SetInHeaderReturnEntityInfo(response, UserDTO.class.getSimpleName(), false);
      return ResponseEntity.ok(fetchDTOByAnonymousFlag(langCode));
    } catch (NotFoundException e) {
      log.error("Anonymous User for language: '{}' not found !", langCode);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(langCode));
    }
  }

  // http://localhost:8091/api/ver1/users/searchbyloginname/?loginName=JohnyWalker333
  @GetMapping(value = "/searchbyloginname/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<UserDTO> getItemsByUserAndQuestionnaire(
      HttpServletResponse response,
      @RequestParam(name = "loginName", required = true) String loginName) {

    try {
      Optional<UserDTO> itemDto = fetchDTOByLoginName(loginName);
      if (itemDto.isPresent()) {
        SetInHeaderReturnEntityInfo(response, UserDTO.class.getSimpleName(), false);
        return ResponseEntity.ok(itemDto.get());
      }
      itemDto.orElseThrow(NotFoundException::new);
    } catch (NotFoundException | NullPointerException e) {
      log.error("User with login name = '{}' !", loginName);
      String langCode = Constants.DEFAULT_LOCALE; //TODO fetch (Browser) Request
      String localeMess = CommonUtilities.getTranslatableMessage("error_user_of_login_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(loginName));
    }
    return null;
  }

  // http://localhost:8091/api/ver1/users/searchbyfullname/?firstName=Johny&lastName=Walker
  @GetMapping(value = "/searchbyfullname/", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<List<UserDTO>> getItemsByUserAndQuestionnaire(
      HttpServletResponse response,
      @RequestParam(name = "firstName", required = true) String firstName,
      @RequestParam(name = "lastName", required = true) String lastName) {

    SetInHeaderReturnEntityInfo(response, UserDTO.class.getSimpleName(), true);
    return ResponseEntity.ok(fetchDTOByFirstLastNameFirst(firstName, lastName));
  }

  @PostMapping
  // @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserDTO createItem(@RequestBody UserDTO entityDto) throws Exception {
    return executeCreateItem(entityDto, entityDto.getPreferredLang());
  }

  //---/ Servicing part /------------------------------------------------//
   @Transactional(readOnly = true)
  public List<UserDTO> getAllItems() {
    return service.getAllItems().stream().map(mapper::map).toList();
  }

  public UserDTO fetchDTOFromId(Integer id) { //}, String langCode) {
    User entity = service.getItemById(id);
    return mapper.map(entity);
  }

  @Transactional(readOnly = true)
  public UserDTO fetchDTOByAnonymousFlag(String langCode) {
    User entity = service.getItemByAnonymousFlag(langCode);
    return mapper.map(entity);
  }

  @Transactional(readOnly = true)
  public Optional<UserDTO> fetchDTOByLoginName(String loginName) {
    Optional<User> ret = service.getUserByLoginName(loginName);
    return ret.map(entity -> Optional.ofNullable(mapper.map(entity))).orElse(null);
  }

  @Transactional(readOnly = true)
  public List<UserDTO> fetchDTOByFirstLastNameFirst(String firstName, String lastName) {
    List<User> list = service.getUserByFirstLastNameFirst(firstName, lastName);
    return  list.stream().map(mapper::map).toList();
  }

  public UserDTO getFirstUserFromList(String firstName, String lastName) {
    List<UserDTO> list = fetchDTOByFirstLastNameFirst(firstName, lastName);
    return (list.isEmpty() ? null : list.get(0));
  }

  public UserDTO executeCreateItem(UserDTO entityDto, String langCode) throws Exception {
    try {
      User entity = mapper.map(entityDto);
      User entityCreated = service.createEntity(entity, langCode);
      return mapper.map(entityCreated);
    } catch (ValidationException | BusinessException vbe) {
      throw vbe;
    } catch (Exception exception) {
      log.error("Create User failed", exception);
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted("User"),
          exception.fillInStackTrace() + ":" + exception.getMessage());
    }
  }
}