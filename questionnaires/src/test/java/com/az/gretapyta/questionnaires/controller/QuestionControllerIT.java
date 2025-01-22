package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.controller2.UserControllerIT;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.model.StepQuestionLink;
import com.az.gretapyta.questionnaires.repository.QuestionsRepository;
import com.az.gretapyta.questionnaires.util.Converters;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static com.az.gretapyta.questionnaires.HttpRootRequestIT.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest( // classes = QuestionnairesApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(value = 5)
public class QuestionControllerIT extends BaseClassIT {

  @Autowired
  QuestionController controller;

  @Autowired
  StepController parentController;

  @Autowired
  UserController userController;

  @Autowired
  QuestionnaireController grandParentController;

  @Autowired
  private QuestionsRepository repository;

  public final static String QUESTION_CODE_TEST1 = "QUE_TEST1";
  public final static String QUESTION_CODE_TEST2 = "QUE_TEST2";
  public final static String QUESTION_CODE_TEST3 = "QUE_TEST3";

  public final static String QUESTION_CODE_TEST4 = "QUE_TEST4";

  private QuestionDTO testQuestion111DTO;
  private QuestionDTO testQuestion222DTO;
  private QuestionDTO testQuestion333DTO;
  private QuestionDTO testQuestion444DTO;

  private StepDTO step1DTO;
  private StepDTO step2DTO;

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

    loadParentEntities(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1);

    testQuestion111DTO = getTestQuestion111DTO(QUESTION_CODE_TEST1, AnswerTypes.RADIO_BUTTONS);
    testQuestion111DTO.setUserId(userAdministratorDTO.getId());
    testQuestion222DTO = getTestQuestion222DTO(QUESTION_CODE_TEST2, AnswerTypes.MULTI_CHOICE);
    testQuestion222DTO.setUserId(userAdministratorDTO.getId());
    testQuestion333DTO = getTestQuestion333DTO(QUESTION_CODE_TEST3, AnswerTypes.TEXT);
    testQuestion333DTO.setUserId(userAdministratorDTO.getId());
    testQuestion444DTO = getTestQuestion444DTO(QUESTION_CODE_TEST4, AnswerTypes.NUMBER_INTEGER);
    testQuestion444DTO.setUserId(userAdministratorDTO.getId());
  }

  // @AfterEach
  public  void resetDb() {
    repository.deleteAll();
  }

  private void loadParentEntities(String grandParentCode) {
    // Should still exist from QuestionnaireControllerIT Tests:

    Optional<QuestionnaireDTO> grandParent = grandParentController.fetchDTOFromCode(
          grandParentCode,
          userAdministratorDTO.getId(),
          Constants.DEFAULT_LOCALE);

    List<StepDTO> retList = parentController.getItemsForParent(
        grandParent.get().getId(),
        userAdministratorDTO.getId(),
        Constants.DEFAULT_LOCALE);
    if ((retList == null) || (retList.size() < 2)) {
      throw new NotFoundException("Steps List for Questionnaire with ID = " + grandParent.get().getId() +  " could not be found !");
    }

    // Only Step 1 and Step 2 to have Questions.
    for (StepDTO n : retList) {
      if (n.getName().equalsIgnoreCase(StepControllerIT.STEP_ONE_EN_NAME)) {
        step1DTO = n;
      } else if (n.getName().equalsIgnoreCase(StepControllerIT.STEP_TWO_EN_NAME)) {
        step2DTO = n;
      }
    }
  }

  public static QuestionDTO getTestQuestion111DTO(String code, AnswerTypes answerType) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Question One");
    elements.put("pl", "Test-pierwsze pytanie");
    elements.put("ru", "Тест-Первый Вопрос");
    return createTestQuestionDTO( code, answerType, elements);
  }

  public static QuestionDTO getTestQuestion222DTO(String code, AnswerTypes answerType) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Question (2)");
    elements.put("pl", "Test-pytanie (2)");
    elements.put("ru", "Тест-Вопрос (2)");
    return createTestQuestionDTO( code, answerType, elements);
  }

  public static QuestionDTO getTestQuestion333DTO(String code, AnswerTypes answerType) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Question 3");
    elements.put("pl", "Test-pytanie 3");
    elements.put("ru", "Тест-Вопрос 3");
    return createTestQuestionDTO( code, answerType, elements);
  }

  public static QuestionDTO getTestQuestion444DTO(String code, AnswerTypes answerType) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Question 4");
    elements.put("pl", "Test-pytanie 4");
    elements.put("ru", "Тест-Вопрос 4");
    return createTestQuestionDTO( code, answerType, elements);
  }

  private static QuestionDTO createTestQuestionDTO(String code, AnswerTypes answerType, Map<String, String> nameElements) {
    QuestionDTO questionDTO = new QuestionDTO();
    questionDTO.setReady2Show(true);

    questionDTO.setCode(code);
    questionDTO.setAnswerType(answerType.getCode());
    questionDTO.setQuestionAskedMultilang(nameElements);
    return questionDTO;
  }

  private StepQuestionLink saveLinkEntry(StepDTO parentDto, QuestionDTO itemDto, int displayOrder, int tenantId) throws Exception {
    return controller.executeCreateParentChildLink(
        parentDto,
        itemDto,
        displayOrder,
        tenantId );
  }

  private void testOneStepAndLinkToParent( QuestionDTO itemDto,
                                           StepDTO parentDto,
                                           int displayOrder ) throws Exception {
    ResponseEntity<QuestionDTO> retObject = postForEntity(itemDto);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(QuestionDTO.class);
    assertNotNull(retObject.getBody().getId());

    entityValidIdForTest = retObject.getBody().getId();

    itemDto.setId(retObject.getBody().getId()); // Save ID of Question
    createAndAssertParentChildLink(retObject.getBody(), parentDto, displayOrder);
  }

  private void createAndAssertParentChildLink( QuestionDTO itemDto,
                                               StepDTO parentDto,
                                               int displayOrder ) throws Exception {

    StepQuestionLink link = saveLinkEntry(parentDto,
        itemDto,
        displayOrder,
        0);

    assertNotNull(link);
    assertThat(link.getStepDown().getId()).isEqualTo(parentDto.getId());
    assertThat(itemDto.getId()).isEqualTo(link.getQuestionUp().getId());
  }

  private ResponseEntity<QuestionDTO> postForEntity(QuestionDTO itemDto) throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_URL;
    String jsonContent = Converters.convertObjectToJson(itemDto);
    HttpHeaders headers = armHeaderWithAttribs(userAdministratorDTO.getId());
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    return restTemplate.postForEntity(testUrl, entity, QuestionDTO.class);
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling Question Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(QuestionController.QUESTION_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new 3 QuestionDTOs, then create 3 new Questions and link them to first Step.")
  public void test02() throws Exception {
    testOneStepAndLinkToParent(testQuestion111DTO, step1DTO, StepControllerIT.DISPLAY_ORDER_3);
    testOneStepAndLinkToParent(testQuestion222DTO, step1DTO, StepControllerIT.DISPLAY_ORDER_1);
    testOneStepAndLinkToParent(testQuestion333DTO, step1DTO, StepControllerIT.DISPLAY_ORDER_2);
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) Given 3 Questions for the first Step, when get all for the Step, then returns 3 records and in proper display order.")
  public void test03() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.QUESTIONS_URL, step1DTO.getId(), Constants.DEFAULT_LOCALE);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].code", is(testQuestion222DTO.getCode())))
        // the order should be: TEST2, TEST3, TEST1.
         .andExpect(jsonPath("$[0].displayOrder", is(StepControllerIT.DISPLAY_ORDER_1)))
         .andExpect(jsonPath("$[1].code", is(testQuestion333DTO.getCode())))
         .andExpect(jsonPath("$[1].displayOrder", is(StepControllerIT.DISPLAY_ORDER_2)))
         .andExpect(jsonPath("$[2].code", is(testQuestion111DTO.getCode())))
         .andExpect(jsonPath("$[2].displayOrder", is(StepControllerIT.DISPLAY_ORDER_3))
        );
  }

  @Test
  @Order(value = 4)
  @DisplayName("(4) When trying to add Question that already is within siblings Step, then it should be prevented and BusinessException should be thrown.")
  public void test04() {
    try {
      createAndAssertParentChildLink(testQuestion222DTO, step2DTO, StepControllerIT.DISPLAY_ORDER_2);
    } catch (Exception e) {
      assertThat(e).isInstanceOf(BusinessException.class); //  NotFoundException.class);
      assertThat(e.getMessage()).contains("already exists within Questionnaire");
    }
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) When valid new QuestionDTO, then create new Questions and link them to second Stepm with order 3.")
  public void test05() throws Exception {
    testOneStepAndLinkToParent(testQuestion444DTO, step2DTO, StepControllerIT.DISPLAY_ORDER_3);
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) When get QuestionDTO by valid ID, then returns 1 record of that ID.")
  public void test06() throws Exception {
    testGetByEntityId(APIController.QUESTIONS_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) When get QuestionDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test07() throws Exception {
    testGetByInvalidEntityId(APIController.QUESTIONS_URL);
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) Given 1 Question for the second Step, when get all for the Step, then returns 1 record and with display order = 3.")
  public void test08() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.QUESTIONS_URL, step2DTO.getId(), Constants.DEFAULT_LOCALE);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].code", is(testQuestion444DTO.getCode())))
        .andExpect(jsonPath("$[0].displayOrder", is(StepControllerIT.DISPLAY_ORDER_3))
        );
  }

  @Test
  @Order(value = 9)
  @DisplayName("(9) Given 4 Questions, when get all, then returns 4 records.")
  public void test09() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_URL + "/all";

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(4))
        );
  }

  @Test
  @Order(value = 10)
  @DisplayName("(10) When given a parent's Step ID not valid, then no Question should be fund.")
  public void test10() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.QUESTIONS_URL, INVALID_PARENT_ID, Constants.DEFAULT_LOCALE);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(0))
        );
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//
}