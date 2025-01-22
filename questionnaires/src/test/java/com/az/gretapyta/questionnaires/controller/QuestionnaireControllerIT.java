package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.enums.QuestionnaireTypes;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.controller2.UserControllerIT;
import com.az.gretapyta.questionnaires.dto.DrawerDTO;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.mapper.DrawerMapper;
import com.az.gretapyta.questionnaires.model.Drawer;
import com.az.gretapyta.questionnaires.repository.QuestionnairesRepository;
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

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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
@Order(value = 3)
public class QuestionnaireControllerIT extends BaseClassIT {

  @Autowired
  private QuestionnaireController controller;

  @Autowired
  DrawerController parentController;

  @Autowired
  QuestionnaireController grandParentController;

  @Autowired
  UserController userController;

  @Autowired
  private QuestionnairesRepository repository;

  @Autowired
  private DrawerMapper drawerMapper;

  public final static String QUESTIONNAIRE_CODE_TEST1 = "QST_TEST1";
  public final static String QUESTIONNAIRE_CODE_TEST2 = "QST_TEST2";

  private QuestionnaireDTO testQuestionnaire111DTO;
  private QuestionnaireDTO testQuestionnaire222DTO;

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

    Drawer drawer = getParentEntity();

    testQuestionnaire111DTO = getTestQuestionnaire1DTO();
    testQuestionnaire111DTO.setDrawerId(drawer.getId());

    testQuestionnaire222DTO = getTestQuestionnaire2DTO();
    testQuestionnaire222DTO.setDrawerId(drawer.getId());
  }

  // @AfterEach
  public  void resetDb() {
    repository.deleteAll();
  }

  private Drawer getParentEntity() {
    // Should still exist from DrawerControllerIT Tests:
    Optional<DrawerDTO> retObject = parentController.fetchDTOFromCode(
        DrawerControllerIT.DRAWER_CODE_TEST1,
        userAdministratorDTO.getId(),
        "en");
    DrawerDTO ret = retObject.get();
    Drawer entity = drawerMapper.map(ret);
    entity.setCreated(null);
    return entity;
  }

  public static QuestionnaireDTO getTestQuestionnaire1DTO() {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Questionnaire Test 1 - Politics");
    elements.put("pl", "Ankieta Test 1 - Polityka");
    elements.put("ru", "Анкета Тест 1 - Πолитика");
    return createTestQuestionnaireDTO(QUESTIONNAIRE_CODE_TEST1, QuestionnaireTypes.PREDICTION, elements);
  }

  public static QuestionnaireDTO getTestQuestionnaire2DTO() {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Questionnaire Test 2 - Politics(2)");
    elements.put("pl", "Ankieta Test 2 - Polityka(2)");
    elements.put("ru", "Анкета Тест 2 - Πолитика(2)");
    return createTestQuestionnaireDTO(QUESTIONNAIRE_CODE_TEST2, QuestionnaireTypes.PREDICTION, elements);
  }

  private static QuestionnaireDTO createTestQuestionnaireDTO(String code, QuestionnaireTypes questionnaireType, Map<String, String> nameElements) {
    QuestionnaireDTO questionnaireDTO = new QuestionnaireDTO();
    questionnaireDTO.setReady2Show(true);

    questionnaireDTO.setCode(code);
    questionnaireDTO.setQuestionnaireType(questionnaireType.getCode());
    questionnaireDTO.setNameMultilang(nameElements);
    return questionnaireDTO;
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling Questionnaire Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONNAIRES_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(QuestionnaireController.QUESTIONNAIRE_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new QuestionnaireDTO, then create new Questionnaire.")
  public void test02() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONNAIRES_URL;
    String jsonContent = Converters.convertObjectToJson(testQuestionnaire111DTO);

    HttpHeaders headers = armHeaderWithAttribs(userAdministratorDTO.getId());
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<QuestionnaireDTO> retObject = restTemplate.postForEntity(testUrl, entity, QuestionnaireDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(QuestionnaireDTO.class);
    assertThat(retObject.getBody().getCode().equalsIgnoreCase(QUESTIONNAIRE_CODE_TEST1));

    entityValidIdForTest = retObject.getBody().getId();
  }

  @Test
  @Order(value = 3)
   /*
  // Not going through ID Filter - no User authentication/authorization
  // TODO ... fix it
  @DisplayName("(3) Another way: when valid new QuestionnaireDTO, then create new Questionnaire.")
  public void test03() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONNAIRES_URL;
    String jsonContent = Converters.convertObjectToJson(testQuestionnaire222DTO);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());

    Optional<QuestionnaireDTO> optFoundDTO = controller.fetchDTOFromCode(
        QUESTIONNAIRE_CODE_TEST2,
        userAdministratorDTO.getId(),
        Constants.DEFAULT_LOCALE);

    assertTrue(optFoundDTO.isPresent());
    assertThat(optFoundDTO.get().getCode()).isEqualTo(QUESTIONNAIRE_CODE_TEST2);
  }
  */
  @DisplayName("(3) When valid new QuestionnaireDTO, then create another new Questionnaire.")
  public void test03() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONNAIRES_URL;
    String jsonContent = Converters.convertObjectToJson(testQuestionnaire222DTO);

    HttpHeaders headers = armHeaderWithAttribs(userAdministratorDTO.getId());
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<QuestionnaireDTO> retObject = restTemplate.postForEntity(testUrl, entity, QuestionnaireDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(QuestionnaireDTO.class);
    assertThat(retObject.getBody().getCode().equalsIgnoreCase(QUESTIONNAIRE_CODE_TEST1));

    entityValidIdForTest = retObject.getBody().getId();
  }

  //(2) GETs
  //
  @Test
  @Order(value = 4)
  @DisplayName("(4) When get QuestionnaireDTO by valid ID, then returns 1 record of that ID.")
  public void test04() throws Exception {
    testGetByEntityId(APIController.QUESTIONNAIRES_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) When get QuestionnaireDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test05() throws Exception {
    testGetByInvalidEntityId(APIController.QUESTIONNAIRES_URL);
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) Given 2 Questionnaires, when get all, then returns 2 records with valid codes.")
  public void test06() throws Exception {
    // http://localhost:8091/api/ver1/steps/search/?parentId=5&lang=pl
    String testUrl = BASE_URI + port + APIController.QUESTIONNAIRES_URL + "/all";

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].code", is(testQuestionnaire111DTO.getCode())))
        .andExpect(jsonPath("$[1].code", is(testQuestionnaire222DTO.getCode()))
        );
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) When given valid Questionnaire's code, then Questionnaire should be fund.")
  public void test07() throws Exception {
    String validCode = testQuestionnaire222DTO.getCode();
    String testUrl = BASE_URI + port + APIController.QUESTIONNAIRES_URL + "/searchcode/" + validCode;

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.code", is(validCode))
        );
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) When given Questionnaire's code not valid, then Questionnaire should not be fund and NotFoundException to be thrown.")
  public void test08() {
    String invalidCode =  "INVALID_QST_CODE";
    String testUrl = BASE_URI + port + APIController.QUESTIONNAIRES_URL + "/searchcode/" + invalidCode;
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