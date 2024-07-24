package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.controller.QuestionController;
import com.az.gretapyta.questionnaires.controller.QuestionControllerIT;
import com.az.gretapyta.questionnaires.controller.QuestionnaireControllerIT;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto2.AnswerProvidedDTO;
import com.az.gretapyta.questionnaires.dto2.QuestionAnswerDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.dto2.UserQuestionnaireDTO;
import com.az.gretapyta.questionnaires.model2.GenericValue;
import com.az.gretapyta.questionnaires.repository2.AnswersProvidedRepository;
import com.az.gretapyta.questionnaires.util.Converters;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static com.az.gretapyta.questionnaires.HttpRootRequestIT.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest( // classes = QuestionnairesApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(value = 26)
public class AnswerProvidedControllerIT extends BaseClassIT {

  @Autowired
  AnswerProvidedController controller;

  @Autowired
  private AnswersProvidedRepository repository;

  @Autowired
  UserController userController;

  @Autowired
  QuestionController questionController;

  @Autowired
  UserQuestionnaireController userQuestionnaireController;

  @Autowired
  QuestionAnswerController questionAnswerController;

  GenericValue genericValueTypeInteger;

  private QuestionDTO question333EnDto; // AnswerTypes.TEXT
  private QuestionDTO question444EnDto; // AnswerTypes.NUMBER_INTEGER
  private QuestionDTO question333RuDto; // AnswerTypes.TEXT

  private QuestionAnswerDTO questionAnswer333EnDto; // AnswerTypes.TEXT
  private QuestionAnswerDTO questionAnswer333RuDto; // AnswerTypes.TEXT
  private QuestionAnswerDTO questionAnswer444EnDto; // AnswerTypes.NUMBER_INTEGER

  @BeforeAll
  public void setUp() {
    // resetDb();

    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    // REPETITION from QuestionAnswerControllerIT.java !!!!!!!!!!!!!!!
    //
    UserDTO userAnonymousEnDTO = userController.fetchDTOByAnonymousFlag(Constants.DEFAULT_LOCALE);
    UserDTO userRussianUserDTO = userController.getFirstUserFromList(UserControllerIT.TEST_USER_RU_FIRST_NAME, UserControllerIT.TEST_USER_RU_LAST_NAME);

    UserQuestionnaireDTO userQuestionnaireTEST111EN = getUserQuestionnaireForCode(userAnonymousEnDTO.getId(),
        QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1);
    UserQuestionnaireDTO userQuestionnaireTEST222EN = getUserQuestionnaireForCode(userAnonymousEnDTO.getId(),
        QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST2);
    UserQuestionnaireDTO userQuestionnaireTEST111RU = getUserQuestionnaireForCode(userRussianUserDTO.getId(),
        QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1);

    question333EnDto = questionController.fetchDTOFromCode( // AnswerTypes.TEXT
        QuestionControllerIT.QUESTION_CODE_TEST3,
        userAnonymousEnDTO.getPreferredLang()).get();

    question444EnDto = questionController.fetchDTOFromCode( // AnswerTypes.NUMBER_INTEGER
        QuestionControllerIT.QUESTION_CODE_TEST4,
        userRussianUserDTO.getPreferredLang()).get();

    question333RuDto = questionController.fetchDTOFromCode( // AnswerTypes.TEXT
        QuestionControllerIT.QUESTION_CODE_TEST3,
        userRussianUserDTO.getPreferredLang()).get();
    //
    // REPETITION from QuestionAnswerControllerIT.java !!!!!!!!!!!!!!!

    questionAnswer333EnDto = questionAnswerController.getItemsByUserQuestionnaireIdAndQuestionId(
        userQuestionnaireTEST111EN.getId(),
        question333EnDto.getId()).get();
    questionAnswer333RuDto = questionAnswerController.getItemsByUserQuestionnaireIdAndQuestionId(
        userQuestionnaireTEST111RU.getId(),
        question333RuDto.getId()).get();

    // Questionnaire 2
    questionAnswer444EnDto = questionAnswerController.getItemsByUserQuestionnaireIdAndQuestionId(
        userQuestionnaireTEST222EN.getId(),
        question444EnDto.getId()).get();

    String valueType = question444EnDto.getAnswerType().getCode(); // AnswerTypes.NUMBER_INTEGER.getCode();
    int valueAsIntegerNumber = 1984;
    genericValueTypeInteger = new GenericValue(valueType, Integer.toString(valueAsIntegerNumber));
  }

  @AfterAll
  public  void resetDb() {
    repository.deleteAll();
  }

  // REPETITION from QuestionAnswerControllerIT.java !!!!!!!!!!!!!!!
  public UserQuestionnaireDTO getUserQuestionnaireForCode(int userId, String questionnaireCode) {
    Optional<UserQuestionnaireDTO> optionalQuestionnaire =
        userQuestionnaireController.getItemsByUserIdAndQuestionnaireCode(userId, questionnaireCode);
    return optionalQuestionnaire.get();
  }

  private void postForEntityAndAssert(AnswerProvidedDTO answerProvidedDto) throws Exception {
    String testUrl = BASE_URI + port + APIController.ANSWERS_PROVIDED_URL;
    String jsonContent = Converters.convertObjectToJson(answerProvidedDto);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling AnswerProvided Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.ANSWERS_PROVIDED_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);
    assertTrue(retObject.contains(AnswerProvidedController.ANSWER_PROVIDED_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new AnswerProvidedDTO for Question of text-input by Anonymous EN User," +
      " then create 1 new AnswerProvided for third Question.")
  public void test02() throws Exception {
    String valueType = question333EnDto.getAnswerType().getCode(); // AnswerTypes.TEXT.getCode();
    String valueAsStr = "this is text value answer by Anonymous EN User for Question QUE_TEST3";
    GenericValue genericValue = new GenericValue(valueType, valueAsStr);
    AnswerProvidedDTO answerProvidedDto = new AnswerProvidedDTO(questionAnswer333EnDto.getId(), genericValue); //AZ401 add .getId()

    postForEntityAndAssert(answerProvidedDto);
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) When valid new AnswerProvidedDTO for Question of text-input by RU User," +
      " then create 1 new AnswerProvided for third Question.")
  public void test03() throws Exception {
    String valueType = question333RuDto.getAnswerType().getCode(); // AnswerTypes.TEXT.getCode();
    String valueAsStr = "answer by RU User for Question QUE_TEST3";
    GenericValue genericValue = new GenericValue(valueType, valueAsStr);
    AnswerProvidedDTO answerProvidedDto = new AnswerProvidedDTO(questionAnswer333RuDto.getId(), genericValue); //AZ401 add .getId()

    postForEntityAndAssert(answerProvidedDto);
  }

  @Test
  @Order(value = 4)
  @DisplayName("(4) When valid new AnswerProvidedDTO for Question of numeric input by Anonymous EN user in Questionnaire 2," +
      " then create 1 new AnswerProvided for that Question.")
  public void test04() throws Exception {
    AnswerProvidedDTO answerProvidedDto = new AnswerProvidedDTO(questionAnswer444EnDto.getId(), genericValueTypeInteger); //AZ401 add .getId()
    String testUrl = BASE_URI + port + APIController.ANSWERS_PROVIDED_URL;
    String jsonContent = Converters.convertObjectToJson(answerProvidedDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<AnswerProvidedDTO> retObject = restTemplate.postForEntity(testUrl, entity, AnswerProvidedDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(AnswerProvidedDTO.class);
    assertNotNull(retObject.getBody().getId());

    entityValidIdForTest = retObject.getBody().getId();
  }

  //(2) GETs
  //
  @Test
  @Order(value = 5)
  @DisplayName("(5) When get AnswerProvidedDTO by valid ID, then returns 1 record of that ID.")
  public void test05() throws Exception {
    testGetByEntityId(APIController.ANSWERS_PROVIDED_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) When get AnswerProvidedDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test06() throws Exception {
    testGetByInvalidEntityId(APIController.ANSWERS_PROVIDED_URL);
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) Given 2 Selected Answers for Question 2, " +
      "when get Selected Answers by QuestionAnswer ID hen returns 2 records.")
  public void test07() throws Exception {
    String testUrl = BASE_URI + port + APIController.ANSWERS_PROVIDED_URL
        + "/search/byquestionanswer/?questionAnswerId=" + questionAnswer444EnDto.getId();

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.questionAnswerDTO", is(questionAnswer444EnDto.getId())))
            .andExpect(jsonPath("$.answer.value", is(genericValueTypeInteger.value()))
        );
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//
}