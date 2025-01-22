package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.enums.UserQuestionnaireStatuses;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.HttpRootRequestIT;
import com.az.gretapyta.questionnaires.controller.QuestionnaireController;
import com.az.gretapyta.questionnaires.controller.QuestionnaireControllerIT;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.dto2.UserQuestionnaireDTO;
import com.az.gretapyta.questionnaires.repository2.UserQuestionnairesRepository;
import com.az.gretapyta.questionnaires.util.Converters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Optional;

import static com.az.gretapyta.questionnaires.HttpRootRequestIT.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest( // classes = QuestionnairesApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(value = 23)
public class UserQuestionnaireControllerIT extends BaseClassIT {

  @Autowired
  UserQuestionnaireController controller;

  @Autowired
  UserController userController;

  @Autowired
  QuestionnaireController questionnaireController;

  @Autowired
  private UserQuestionnairesRepository repository;

  private UserDTO userAnonymousEnDTO;
  private UserDTO userRussianUserDTO;

  private QuestionnaireDTO questionnaire111DTO;
  private QuestionnaireDTO questionnaire222DTO;

  InetAddress ipAddressFromLocal;
  InetAddress ipAddressFrom222;

  private UserDTO userAdministratorDTO;

  // @BeforeEach
  @BeforeAll
  public void setUp() {
    resetDb(); // Clear at the beginning.

    Optional<UserDTO> optUserDto = userController.fetchDTOByLoginName(UserControllerIT.TEST_USER_ADMIN_LOGIN_NAME);
    if(optUserDto.isPresent()) {
      userAdministratorDTO = optUserDto.get();
    } else {
      throw new NotFoundException(String.format("Admin. USer '%s' not found.", UserControllerIT.TEST_USER_ADMIN_LOGIN_NAME));
    }

    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    userAnonymousEnDTO = userController.fetchDTOByAnonymousFlag(Constants.DEFAULT_LOCALE);
    userRussianUserDTO = userController.getFirstUserFromList(UserControllerIT.TEST_USER_RU_FIRST_NAME, UserControllerIT.TEST_USER_RU_LAST_NAME);

    questionnaire111DTO = getQuestionnaireEntity(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1);
    questionnaire222DTO = getQuestionnaireEntity(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST2);

    try {
      ipAddressFromLocal = InetAddress.getLocalHost();
      ipAddressFrom222 = InetAddress.getByName("172.168.29.33");
    } catch (UnknownHostException e) {
      // TODO ...
    }
  }

  // @AfterEach
  public  void resetDb() {
    repository.deleteAll();
  }

  private QuestionnaireDTO getQuestionnaireEntity(String parentCode) {
    // Should still exist from QuestionnaireControllerIT Tests:
    Optional<QuestionnaireDTO> retObject = questionnaireController.fetchDTOFromCode(
        parentCode,
        userAdministratorDTO.getId(),
        Constants.DEFAULT_LOCALE);
    return retObject.get();
  }

  @Transactional
  private UserQuestionnaireDTO createUserQuestionnaireDTO( UserDTO userDTO,
                                                           QuestionnaireDTO questionnaireDTO,
                                                           InetAddress ipAddressFrom ) {
    return UserQuestionnaireController.createUserQuestionnaireDTO( userDTO,
                                                                   questionnaireDTO,
                                                                   userDTO.getPreferredLang(),
                                                                   ipAddressFrom.getHostAddress(),
                                                                   UserQuestionnaireStatuses.UNKNOWN,
                                                                   Collections.emptyList() );
  }

  private void postForEntityAndAssert( UserDTO userDto,
                                       QuestionnaireDTO questionnaireDTO,
                                        InetAddress inetAddress ) throws Exception {
    UserQuestionnaireDTO dto = createUserQuestionnaireDTO(userDto, questionnaireDTO, inetAddress);

    ResponseEntity<UserQuestionnaireDTO> retObject = postForEntity(dto, userDto.getId());

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(UserQuestionnaireDTO.class);
    assertThat(retObject.getBody().getUserDTO()).isEqualTo(userDto.getId());
    assertThat(retObject.getBody().getQuestionnaireDTO()).isEqualTo(questionnaireDTO.getId());

    assertThat(retObject.getBody().getIpAddressFrom().equalsIgnoreCase(inetAddress.getHostAddress()));

    entityValidIdForTest = retObject.getBody().getId();
  }

  private ResponseEntity<UserQuestionnaireDTO> postForEntity(UserQuestionnaireDTO itemDto, int takerUserId) throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_QUESTIONNAIRES_URL;
    String jsonContent = Converters.convertObjectToJson(itemDto);
    HttpHeaders headers = armHeaderWithAttribs(takerUserId);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    return restTemplate.postForEntity(testUrl, entity, UserQuestionnaireDTO.class);
  }

  private String getUrlForIndividualSearch(String entityUrl, String apiUrl, int userId, int questionnaireId) {
    // http://localhost:8091/api/ver1/users-questionnaires/search/?userId=1&questionnaireId=1
    // /search/byuserandqsn/
    StringBuilder testUrlPattern = new StringBuilder(HttpRootRequestIT.BASE_URI);
    testUrlPattern.append(port)
        .append(entityUrl)
        .append(apiUrl)
        .append("?userId=%d");
    if (questionnaireId > 0) {
      testUrlPattern.append("&questionnaireId=%d");
    }
    return String.format(testUrlPattern.toString(), userId, questionnaireId);
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling UserQuestionnaire Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_QUESTIONNAIRES_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(UserQuestionnaireController.USER_QUESTIONNAIRE_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new UserQuestionnaireDTO, then Anonymous User taking the first Questionnaire.")
  public void test02() throws Exception {
    postForEntityAndAssert(userAnonymousEnDTO, questionnaire111DTO, ipAddressFromLocal);
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) Another way: when valid new UserQuestionnaireDTO, then RU User taking the first Questionnaire.")
  public void test03() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_QUESTIONNAIRES_URL;
    UserQuestionnaireDTO dto = createUserQuestionnaireDTO(userRussianUserDTO, questionnaire111DTO, ipAddressFrom222);

    String jsonContent = Converters.convertObjectToJson(dto);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @Order(value = 4)
  @DisplayName("(4) When valid new UserQuestionnaireDTO, then Anonymous User taking the second Questionnaire.")
  public void test04() throws Exception {
    postForEntityAndAssert(userAnonymousEnDTO, questionnaire222DTO, ipAddressFromLocal);
  }

  //(2) GETs
  //
  @Test
  @Order(value = 5)
  @DisplayName("(5) When get UserQuestionnaireDTO by valid ID, then returns 1 record of that ID.")
  public void test05() throws Exception {
    testGetByEntityId(APIController.USERS_QUESTIONNAIRES_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) When get UserQuestionnaireDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test06() throws Exception {
    testGetByInvalidEntityId(APIController.USERS_QUESTIONNAIRES_URL);
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) Given 3 Questionnaires taken, when get all, then returns 3 records.")
  public void test07() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_QUESTIONNAIRES_URL + "/all-no-paging";

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(3))
        );
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) Given 2 existing Questionnaires for Anonymous User, when get all for the Anonymous User, then returns 2 records.")
  public void test08() throws Exception {
    String testUrl = getUrlForIndividualSearch(APIController.USERS_QUESTIONNAIRES_URL, "/search/", userAnonymousEnDTO.getId(), 0);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].userDTO", is(userAnonymousEnDTO.getId())))
        .andExpect(jsonPath("$[1].userDTO", is(userAnonymousEnDTO.getId()))
        );
  }

  @Test
  @Order(value = 9)
  @DisplayName("(9) Given 1 existing Questionnaire for RU User, when get all for the RU User, then returns 1 record.")
  public void test09() throws Exception {
    String testUrl = getUrlForIndividualSearch(APIController.USERS_QUESTIONNAIRES_URL, "/search/", userRussianUserDTO.getId(), 0);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].userDTO", is(userRussianUserDTO.getId())))
        .andExpect(jsonPath("$[0].questionnaireDTO", is(questionnaire111DTO.getId())))
        .andExpect(jsonPath("$[0].ipAddressFrom", is(ipAddressFrom222.getHostAddress()))
        );
  }

  @Test
  @Order(value = 10)
  @DisplayName("(10) When given a User's ID not valid, then no Questionnaires taken should be fund.")
  public void test10() throws Exception {
    String testUrl = getUrlForIndividualSearch(APIController.USERS_QUESTIONNAIRES_URL, "/search/", INVALID_PARENT_ID, 0);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(0))
        );
  }

  @Test
  @Order(value = 11)
  @DisplayName("(11) When valid User ID and Questionnaire ID, then returns relevant Questionnaire taken by User.")
  public void test11() throws Exception {
    String testUrl = getUrlForIndividualSearch(APIController.USERS_QUESTIONNAIRES_URL, "/search/byuserandqsnid/", userAnonymousEnDTO.getId(), questionnaire222DTO.getId());

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.userDTO", is(userAnonymousEnDTO.getId())))
        .andExpect(jsonPath("$.questionnaireDTO", is(questionnaire222DTO.getId())))
        .andExpect(jsonPath("$.ipAddressFrom", is(ipAddressFromLocal.getHostAddress()))
        );
  }

  @Test
  @Order(value = 12)
  @DisplayName("(12) When given a Questionnaire ID valid but User's ID not valid, then no Questionnaire taken should be fund and NotFoundException should be thrown.")
  public void test12() {
    String testUrl = getUrlForIndividualSearch(APIController.USERS_QUESTIONNAIRES_URL, "/search/byuserandqsnid/", INVALID_PARENT_ID, questionnaire222DTO.getId());

    try {
      mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
          .andDo(print())
          .andExpect(status().isInternalServerError())

          .andExpect(__ -> MatcherAssert.assertThat(
              __.getResolvedException(),
              CoreMatchers.instanceOf(SecurityException.class)))
          .andReturn();
    } catch(Exception e) {
      assertThat(e).isInstanceOf(ServletException.class); //  NotFoundException.class);
      assertThat(e.getMessage()).contains("NotFoundException");
    }
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//
}