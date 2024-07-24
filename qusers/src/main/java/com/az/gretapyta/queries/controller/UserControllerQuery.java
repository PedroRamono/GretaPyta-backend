package com.az.gretapyta.queries.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.mapper2.UserMapper;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = APIController.USERS_URL)
// @RequiredArgsConstructor
public class UserControllerQuery extends UserController {

  public UserControllerQuery(UsersService service, UserMapper mapper) {
    super(service, mapper);
  }

  @Autowired
//  private final UsersService service;
//  @Autowired
//  private final UserMapper mapper;

//  @GetMapping(value = "/")
//  @ResponseBody
//  public String index() {
//    log.info("(UserController) Greetings to be passed ...");
//    return "Hello World ! from User Controller";
//  }

//  public UserController(UsersService service, UserMapper mapper) {
//    this.service = service;
//    this.mapper = mapper;
//  }

  // http://localhost:8091/api/ver1/users/all
  @Transactional(readOnly = true)
  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserDTO>> getAlL() {
    log.debug("(UserController) getting all Users");
    return ResponseEntity.ok(this.fetchAll());
  }

  // http://localhost:8091/api/ver1/users/searchid/2?lang=pl
  @GetMapping(value = "/searchid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public ResponseEntity<UserDTO> getItemByIdsLangFiltered(
      @PathVariable(name = "id") final Integer id ) {

    log.debug("===> Getting User by ID: " + id);
    try {
      return  ResponseEntity.ok(fetchDTOFromId(id));
    } catch (NotFoundException e) {
      log.error("Questionnaire with ID: '{}' not found !", id);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_id_does_not_exist", Constants.DEFAULT_LOCALE);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(id));
    }
  }

  // http://localhost:8091/api/ver1/users/anonymous/pl

  @Transactional(readOnly = true)
  @GetMapping(value = "/anonymous/{langCode}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDTO> getItemByCode(
      @PathVariable(name = "langCode") final String langCode) {

    try {
//      User entity = service.getItemByAnonymousFlag(langCode);
//      return ResponseEntity.ok(mapper.map(entity));
      return ResponseEntity.ok(fetchDTOByAnonymousFlag(langCode));
    } catch (NotFoundException e) {
      log.error("Anonymous User for language: '{}' not found !", langCode);
      String localeMess = CommonUtilities.getTranslatableMessage("error_item_of_code_does_not_exist", langCode);
      assert localeMess != null;
      throw new NotFoundException(localeMess.formatted(langCode));
    }
  }

  //---/ Servicing part /------------------------------------------------//
  public List<UserDTO> fetchAll() {
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
  public List<UserDTO> fetchDTOByFirstLastNameFirst(String firstName, String lastName) {
    List<User> list = service.getUserByFirstLastNameFirst(firstName, lastName);
    return  list.stream().map(mapper::map).toList();
  }
  //---/ Servicing part /------------------------------------------------//
}