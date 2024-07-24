package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.controller.*;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.dto2.*;
import com.az.gretapyta.questionnaires.repository2.QuestionAnswersRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.az.gretapyta.questionnaires.HttpRootRequestIT.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest( // classes = QuestionnairesApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(value = 24)
public class QuestionAnswerControllerIT extends BaseClassIT {

  @Autowired
  QuestionAnswerController controller;

  @Autowired
  UserController userController;

  @Autowired
  UserQuestionnaireController userQuestionnaireController;

  @Autowired
  QuestionnaireController questionnaireController;

  @Autowired
  StepController stepController;

  @Autowired
  QuestionController questionController;

  @Autowired
  OptionController optionController;

  @Autowired
  private QuestionAnswersRepository repository;

  private UserQuestionnaireDTO userQuestionnaireTEST111EN;
  private UserQuestionnaireDTO userQuestionnaireTEST222EN;
  private UserQuestionnaireDTO userQuestionnaireTEST111RU;

  private QuestionDTO question111EnDto; // AnswerTypes.RADIO_BUTTONS
  private QuestionDTO question222EnDto; // AnswerTypes.MULTI_CHOICE
  private QuestionDTO question333EnDto; // AnswerTypes.TEXT

  private QuestionDTO question444EnDto; // AnswerTypes.NUMBER_INTEGER
  private QuestionDTO question333RuDto; // AnswerTypes.TEXT

  // @BeforeEach
  @BeforeAll
  public void setUp() {
    resetDb(); // Clear at the beginning.

    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    UserDTO userAnonymousEnDTO = userController.fetchDTOByAnonymousFlag(Constants.DEFAULT_LOCALE);
    UserDTO userRussianUserDTO = userController.getFirstUserFromList(UserControllerIT.TEST_USER_RU_FIRST_NAME, UserControllerIT.TEST_USER_RU_LAST_NAME);

    List<UserQuestionnaireDTO> userQuestionnaire111DTOList = getUserQuestionnaireEntity(userAnonymousEnDTO);
    List<UserQuestionnaireDTO> userQuestionnaire222DTOlist = getUserQuestionnaireEntity(userRussianUserDTO);

    userQuestionnaireTEST111EN = getUserQuestionnaireForCode( userAnonymousEnDTO.getId(),
                                                            QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1 );
    userQuestionnaireTEST222EN = getUserQuestionnaireForCode( userAnonymousEnDTO.getId(),
                                                            QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST2 );
    userQuestionnaireTEST111RU = getUserQuestionnaireForCode( userRussianUserDTO.getId(),
                                                              QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1 );

    question111EnDto = questionController.fetchDTOFromCode( // AnswerTypes.RADIO_BUTTONS
        QuestionControllerIT.QUESTION_CODE_TEST1,
        userAnonymousEnDTO.getPreferredLang()).get();

    question222EnDto = questionController.fetchDTOFromCode( // AnswerTypes.MULTI_CHOICE
        QuestionControllerIT.QUESTION_CODE_TEST2,
        userAnonymousEnDTO.getPreferredLang()).get();

    question333EnDto = questionController.fetchDTOFromCode( // AnswerTypes.TEXT
        QuestionControllerIT.QUESTION_CODE_TEST3,
        userAnonymousEnDTO.getPreferredLang()).get();

    question444EnDto = questionController.fetchDTOFromCode( // AnswerTypes.NUMBER_INTEGER
        QuestionControllerIT.QUESTION_CODE_TEST4,
        userRussianUserDTO.getPreferredLang()).get();

    question333RuDto = questionController.fetchDTOFromCode( // AnswerTypes.TEXT
        QuestionControllerIT.QUESTION_CODE_TEST3,
        userRussianUserDTO.getPreferredLang()).get();


    //AZ909 //////////////////////////////////////
    //(1) get all taken questionnaires for User 1 (Anonymous EN)
    //(2) For each (1) get all Steps.
    //(3) for each (2) get all Questions.
    //(4) for each (3) mock answering (choices-based) it.

    //(5) repeat 1-4 for User 2 (RU)
    do1To4(userAnonymousEnDTO);
    do1To4(userRussianUserDTO);

    int questionnaire1Id = (userQuestionnaire111DTOList.isEmpty() ? 0 : userQuestionnaire111DTOList.get(0).getQuestionnaireDTO());
    int questionnaire2Id = (userQuestionnaire222DTOlist.isEmpty() ? 0 : userQuestionnaire222DTOlist.get(0).getQuestionnaireDTO());

    Set<QuestionDTO> setUser1 = questionnaireController.getAllQuestionsForQuestionnaire(
        questionnaire1Id,
        "en");

    Set<QuestionDTO> setUser2 = questionnaireController.getAllQuestionsForQuestionnaire(
        questionnaire2Id,
        "ru");

    Optional<QuestionnaireDTO> OptTestQuestionnaire222DTO =
        questionnaireController.fetchDTOFromCode(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST2,
            Constants.DEFAULT_LOCALE);

    QuestionnaireDTO testQuestionnaire222DTO = OptTestQuestionnaire222DTO.get();

    Set<QuestionDTO> set2 = questionnaireController.getAllQuestionsForQuestionnaire(
        questionnaire2Id,
        "ru");
  }

  // @AfterEach
  public  void resetDb() {
    repository.deleteAll();
  }

  private List<UserQuestionnaireDTO> getUserQuestionnaireEntity(UserDTO userDto) {
    return userQuestionnaireController.getItemsForUser(userDto.getId());
  }

  //(1) -(4) ////////////////////////////////////////////////////////////////////////
  //
  private void do1To4(UserDTO userDto) { // throws Exception {
    //(1)
    List<UserQuestionnaireDTO> userQuestionnaireDTOList = getUserQuestionnaireEntity(userDto);

    String mess1 = "===> (1) User %s Answers %d Questionnaire(s).";
    printDevInfo(String.format(mess1, userDto.getLoginName(), userQuestionnaireDTOList.size()));

    for (UserQuestionnaireDTO n1 : userQuestionnaireDTOList) {
      QuestionnaireDTO questionnaireDTO = questionnaireController.fetchDTOFromId( n1.getQuestionnaireDTO(),
                                                                                  userDto.getPreferredLang() );

      //(2)
      // Steps provided in proper display order:
      List<StepDTO> steps = stepController.getItemsForParent(questionnaireDTO.getId(), userDto.getPreferredLang());

      String mess2 = "  ===> (2) Questionnaire %s has %d Step(s).";
      printDevInfo(String.format(mess2, questionnaireDTO.getCode(), steps.size()));

      for (StepDTO n2 : steps) {
        // Questions provided in proper display order:
        List<QuestionDTO> questions = questionController.getItemsForParent(n2.getId(), userDto.getPreferredLang());
        String mess3 = "    ===> (3) Step (order=%s) has %d Question(s).";
        printDevInfo(String.format(mess3, n2.getDisplayOrder(), questions.size()));

        //(3)
        for (QuestionDTO n3 : questions) {
          // Options provided in proper display order:
          List<OptionDTO> options = optionController.getItemsForParent(n3.getId(), userDto.getPreferredLang());
          String mess4 = "      ===> (4) Question %s (order=%d) has %d Option(s). User to answer it !";
          printDevInfo(String.format(mess4, n3.getCode(), n3.getDisplayOrder(), options.size()));
          presentOptions(options);

          boolean doIt = false; // false/true;

          //(4)
          if (doIt) {
            List<AnswerSelectedDTO> answerSelectedList = new ArrayList<>(); //TODO ...
            try {
              postForEntityAndAssert(n1, n3, answerSelectedList, null);
            } catch (Exception e) {
              System.out.printf("Error: " + e);
            }
          }
        }
      }
    }
  }

  private void presentOptions(List<OptionDTO> options) {
    for (OptionDTO n : options) {
      String mess = "        ===> (5) Option %s (%s) - order=%d, preferred answer: %s.";
      printDevInfo(String.format(mess,
          n.getName(),
          n.getCode(),
          n.getDisplayOrder(),
          (n.isPreferredAnswer() ? "Yes" : "No")));
    }
  }

  private static void printDevInfo(String devInfo) {
    System.out.println(devInfo);
  }
  //
  //(1) -(4) ////////////////////////////////////////////////////////////////////////


  public UserQuestionnaireDTO getUserQuestionnaireForCode(int userId, String questionnaireCode) {
    Optional<UserQuestionnaireDTO> optionalQuestionnaire =
        userQuestionnaireController.getItemsByUserIdAndQuestionnaireCode(userId, questionnaireCode);
    return optionalQuestionnaire.get();
  }

  @Transactional
  public static QuestionAnswerDTO createQuestionAnswerDTO( UserQuestionnaireDTO userQuestionnaireDTO,
                                                           QuestionDTO questionDTO,
                                                           List<AnswerSelectedDTO> answerSelectionsDTO,
                                                           AnswerProvidedDTO answerProvidedDTO) {
    return QuestionAnswerController.createQuestionAnswerDTO( userQuestionnaireDTO,
                                                             questionDTO.getId(),
                                                             answerSelectionsDTO,
                                                             answerProvidedDTO );
  }

  private void postForEntityAndAssert( UserQuestionnaireDTO userQuestionnaireDTO,
                                       QuestionDTO questionDTO,
                                       List<AnswerSelectedDTO> answerSelectionsDTO,
                                       AnswerProvidedDTO answerProvidedDTO ) throws Exception {
    QuestionAnswerDTO dto = createQuestionAnswerDTO(userQuestionnaireDTO, questionDTO, answerSelectionsDTO, answerProvidedDTO);

    ResponseEntity<QuestionAnswerDTO> retObject = postForEntity(dto);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(QuestionAnswerDTO.class);
    assertThat(retObject.getBody().getUserQuestionnaireDTO()).isEqualTo(userQuestionnaireDTO.getId());
    assertThat(retObject.getBody().getQuestionDTO()).isEqualTo(questionDTO.getId());
  }

  private ResponseEntity<QuestionAnswerDTO> postForEntity(QuestionAnswerDTO itemDto) throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL;
    String jsonContent = Converters.convertObjectToJson(itemDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    return restTemplate.postForEntity(testUrl, entity, QuestionAnswerDTO.class);
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling QuestionAnswer Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(QuestionAnswerController.QUESTION_ANSWER_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When Anonymous User answers to first, single-selection-type question of first Questionnaire," +
      " then QuestionAnswer record is created.")
  public void test02() throws Exception {
    QuestionAnswerDTO questionAnswerDto = createQuestionAnswerDTO(
        userQuestionnaireTEST111EN,
        question111EnDto,
        Collections.emptyList(),
        null);
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL;
    String jsonContent = Converters.convertObjectToJson(questionAnswerDto);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) When Anonymous User answers to second, multi-selection-type Question of first Questionnaire," +
      " then QuestionAnswer record is created.")
  public void test03() throws Exception {
    QuestionAnswerDTO questionAnswerDto = createQuestionAnswerDTO(
        userQuestionnaireTEST111EN, // userQuestionnaireTEST222
        question222EnDto,
        Collections.emptyList(),
        null);
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL;
    String jsonContent = Converters.convertObjectToJson(questionAnswerDto);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<QuestionAnswerDTO> retObject = restTemplate.postForEntity(testUrl, entity, QuestionAnswerDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(QuestionAnswerDTO.class);
    assertNotNull(retObject.getBody().getId());
  }

  @Test
  @Order(value = 4)
  @DisplayName("(4) When Anonymous User answers to third, user-provided-answer-type question of first Questionnaire," +
      " then QuestionAnswer record is created.")
  public void test04() throws Exception {
    QuestionAnswerDTO questionAnswerDto = createQuestionAnswerDTO(
        userQuestionnaireTEST111EN,
        question333EnDto,
        Collections.emptyList(),
        null);
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL;
    String jsonContent = Converters.convertObjectToJson(questionAnswerDto);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) When RU User answers to third, user-provided-answer-type question of first Questionnaire," +
      " then QuestionAnswer record is created.")
  public void test05() throws Exception {
    QuestionAnswerDTO questionAnswerDto = createQuestionAnswerDTO(
        userQuestionnaireTEST111RU,
        question333RuDto,
        Collections.emptyList(),
        null);
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL;
    String jsonContent = Converters.convertObjectToJson(questionAnswerDto);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<QuestionAnswerDTO> retObject = restTemplate.postForEntity(testUrl, entity, QuestionAnswerDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(QuestionAnswerDTO.class);
    assertNotNull(retObject.getBody().getId());

    entityValidIdForTest = retObject.getBody().getId();
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) When Anonymous User answers to first, user-provided-answer-type question of second Questionnaire," +
      " then QuestionAnswer record is created.")
  public void test06() throws Exception {
    QuestionAnswerDTO questionAnswerDto = createQuestionAnswerDTO(
        userQuestionnaireTEST222EN,
        question444EnDto,
        Collections.emptyList(),
        null);
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL;
    String jsonContent = Converters.convertObjectToJson(questionAnswerDto);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  //(2) GETs
  //
  @Test
  @Order(value = 7)
  @DisplayName("(7) When get Answered Questions by valid ID, then returns 1 record of that ID.")
  public void test07() throws Exception {
    testGetByEntityId(APIController.QUESTIONS_ANSWERS_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) When get Answered Question by invalid ID, then no return record and Exception should be thrown.")
  public void test08() throws Exception {
    testGetByInvalidEntityId(APIController.QUESTIONS_ANSWERS_URL);
  }

  @Test
  @Order(value = 9)
  @DisplayName("(9) Given 4 answered Questions for Questionnaire 1, when get Answered Questions by UserQuestionnaire ID then returns 4 records.")
  public void test09() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL
            + "/search/byuserquestionnaire/?userQuestionnaireId=" + userQuestionnaireTEST111EN.getId();

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(3))
        );
  }

  @Test
  @Order(value = 10)
  @DisplayName("(10) Given 1 answered Question for Questionnaire 2, when get Answered Questions by UserQuestionnaire ID then returns 1 record.")
  public void test10() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL
        + "/search/byuserquestionnaire/?userQuestionnaireId=" + userQuestionnaireTEST222EN.getId();

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(1))
        );
  }

  @Test
  @Order(value = 11)
  @DisplayName("(11) When get Answered Questions by UserQuestionnaire with invalid ID, then no record(s) should be returned.")
  public void test11() throws Exception {
    String testUrl = BASE_URI + port + APIController.QUESTIONS_ANSWERS_URL
        + "/search/byuserquestionnaire/?userQuestionnaireId=" + INVALID_PARENT_ID;

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
         .andExpect(jsonPath("$").isEmpty()
        );
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//
}