package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.controller.OptionController;
import com.az.gretapyta.questionnaires.controller.QuestionController;
import com.az.gretapyta.questionnaires.controller.QuestionControllerIT;
import com.az.gretapyta.questionnaires.controller.QuestionnaireControllerIT;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto2.AnswerSelectedDTO;
import com.az.gretapyta.questionnaires.dto2.QuestionAnswerDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.dto2.UserQuestionnaireDTO;
import com.az.gretapyta.questionnaires.repository2.AnswersSelectedRepository;
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

import java.util.List;
import java.util.Optional;

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
@Order(value = 25)
public class AnswerSelectedControllerIT extends BaseClassIT {

  @Autowired
  AnswerSelectedController controller;

  @Autowired
  private AnswersSelectedRepository repository;

  @Autowired
  UserController userController;

  @Autowired
  QuestionController questionController;

  @Autowired
  UserQuestionnaireController userQuestionnaireController;

  @Autowired
  QuestionAnswerController questionAnswerController;

  @Autowired
  OptionController optionController;

  private UserDTO userAnonymousEnDTO;
  private UserDTO userRussianUserDTO;

  private QuestionAnswerDTO questionAnswer111EnDto; // AnswerTypes.RADIO_BUTTONS
  private QuestionAnswerDTO questionAnswer222EnDto; // AnswerTypes.MULTI_CHOICE

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

    // REPETITION from QuestionAnswerControllerIT.java !!!!!!!!!!!!!!!
    //
    userAnonymousEnDTO = userController.fetchDTOByAnonymousFlag(Constants.DEFAULT_LOCALE);
    userRussianUserDTO = userController.getFirstUserFromList(UserControllerIT.TEST_USER_RU_FIRST_NAME, UserControllerIT.TEST_USER_RU_LAST_NAME);

    UserQuestionnaireDTO userQuestionnaireTEST111 = getUserQuestionnaireForCode(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1);

    // AnswerTypes.RADIO_BUTTONS
    QuestionDTO question111EnDto = questionController.fetchDTOFromCode( // AnswerTypes.RADIO_BUTTONS
        QuestionControllerIT.QUESTION_CODE_TEST1,
        userAdministratorDTO.getId(),
        userAnonymousEnDTO.getPreferredLang()).get();

    // AnswerTypes.MULTI_CHOICE
    QuestionDTO question222EnDto = questionController.fetchDTOFromCode( // AnswerTypes.MULTI_CHOICE
        QuestionControllerIT.QUESTION_CODE_TEST2,
        userAdministratorDTO.getId(),
        userAnonymousEnDTO.getPreferredLang()).get();
    //
    // REPETITION from QuestionAnswerControllerIT.java !!!!!!!!!!!!!!!

    // Questionnaire 1
    questionAnswer111EnDto = questionAnswerController.getItemsByUserQuestionnaireIdAndQuestionId(
        userQuestionnaireTEST111.getId(),
        question111EnDto.getId()).get();

    questionAnswer222EnDto = questionAnswerController.getItemsByUserQuestionnaireIdAndQuestionId(
        userQuestionnaireTEST111.getId(),
        question222EnDto.getId()).get();
  }

  public  void resetDb() {
    repository.deleteAll();
  }

  // REPETITION from QuestionAnswerControllerIT.java !!!!!!!!!!!!!!!
  private UserQuestionnaireDTO getUserQuestionnaireForCode(String questionnaireCode) {
    Optional<UserQuestionnaireDTO> optionalQuestionnaire =
        userQuestionnaireController.getItemsByUserIdAndQuestionnaireCode(
            userAnonymousEnDTO.getId(),
            questionnaireCode);
    return optionalQuestionnaire.get();
  }

  private void postForEntityAndAssert(AnswerSelectedDTO answerSelectedDto) throws Exception {
    String testUrl = BASE_URI + port + APIController.ANSWERS_SELECTED_URL;
    String jsonContent = Converters.convertObjectToJson(answerSelectedDto);

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
  @DisplayName("(1) When calling AnswerSelected Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.ANSWERS_SELECTED_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);
    assertTrue(retObject.contains(AnswerSelectedController.ANSWER_SELECTED_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new AnswerSelectedDTO for Question of singleton selection choice," +
      " then create 1 new AnswerSelected for first Question.")
  public void test02() throws Exception {
    List<OptionDTO> options =
        optionController.getItemsForParent( questionAnswer111EnDto.getQuestionDTO(),
                                            userAdministratorDTO.getId(),
                                            userAnonymousEnDTO.getPreferredLang() );
    OptionDTO optionDto = options.get(0); // first one.

    postForEntityAndAssert(new AnswerSelectedDTO(questionAnswer111EnDto.getId(), optionDto.getId()));
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) When valid another AnswerSelectedDTO for Question of singleton selection choice, " +
      "then no return record should be created and Exception should be thrown.")
  public void test03() throws Exception {
    List<OptionDTO> options =
        optionController.getItemsForParent( questionAnswer111EnDto.getQuestionDTO(),
                                            userAdministratorDTO.getId(),
                                            userAnonymousEnDTO.getPreferredLang());

    OptionDTO optionDto = options.get(options.size() -1);
    AnswerSelectedDTO answerSelectedDto = new AnswerSelectedDTO(questionAnswer111EnDto.getId(), optionDto.getId());
    String testUrl = BASE_URI + port + APIController.ANSWERS_SELECTED_URL;
    String jsonContent = Converters.convertObjectToJson(answerSelectedDto);

    try {
      mockMvc.perform(post(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonContent))
          .andDo(print())
          .andExpect(status().isInternalServerError())

          .andExpect(__ -> MatcherAssert.assertThat(
              __.getResolvedException(),
              CoreMatchers.instanceOf(SecurityException.class)))
          .andReturn();
    } catch(Exception e) {
      assertThat(e).isInstanceOf(ServletException.class); // NotFoundException.class);
      assertThat(e.getMessage()).contains("BusinessException");
    }
  }

  @Test
  @Order(value = 4)
  @DisplayName("(4) When valid new AnswerSelectedDTO for Question of multiple selection choice," +
      " then create first AnswerSelected for second Question.")
  public void test04() throws Exception {
    List<OptionDTO> options =
        optionController.getItemsForParent( questionAnswer222EnDto.getQuestionDTO(),
                                            userAdministratorDTO.getId(),
                                            userAnonymousEnDTO.getPreferredLang() );
    OptionDTO option111Dto = options.get(0); // first one.

    postForEntityAndAssert(new AnswerSelectedDTO(questionAnswer222EnDto.getId(), option111Dto.getId()));
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) When valid new AnswerSelectedDTO for Question of multiple selection choice," +
      " then create second AnswerSelected for second Question.")
  public void test05() throws Exception {
    List<OptionDTO> options =
        optionController.getItemsForParent( questionAnswer222EnDto.getQuestionDTO(),
                                            userAdministratorDTO.getId(),
                                            userAnonymousEnDTO.getPreferredLang() );
    OptionDTO option222Dto = options.get(options.size() -1); // last one.
    AnswerSelectedDTO answerSelectedDto = new AnswerSelectedDTO(questionAnswer222EnDto.getId(), option222Dto.getId());
    String testUrl = BASE_URI + port + APIController.ANSWERS_SELECTED_URL;
    String jsonContent = Converters.convertObjectToJson(answerSelectedDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<AnswerSelectedDTO> retObject = restTemplate.postForEntity(testUrl, entity, AnswerSelectedDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(AnswerSelectedDTO.class);
    assertNotNull(retObject.getBody().getId());

    entityValidIdForTest = retObject.getBody().getId();
  }

  //(2) GETs
  //
  @Test
  @Order(value = 6)
  @DisplayName("(6) When get AnswerSelectedDTO by valid ID, then returns 1 record of that ID.")
  public void test06() throws Exception {
    testGetByEntityId(APIController.ANSWERS_SELECTED_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) When get AnswerSelectedDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test07() throws Exception {
    testGetByInvalidEntityId(APIController.ANSWERS_SELECTED_URL);
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) Given 2 Selected Answers for Question 2, " +
      "when get Selected Answers by QuestionAnswer ID hen returns 2 records.")
  public void test08() throws Exception {
    String testUrl = BASE_URI + port + APIController.ANSWERS_SELECTED_URL
        + "/search/byquestionanswer/?questionAnswerId=" + questionAnswer222EnDto.getId();

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(2))
        );
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//
}