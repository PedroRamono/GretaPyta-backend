package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.HttpRootRequestIT;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.controller2.UserControllerIT;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest( // classes = QuestionnairesApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(value = 6)
public class QuestionnairelControllerUtilityIT extends BaseClassIT {

  @Autowired
  protected QuestionnaireController controller;

  @Autowired
  UserController userController;

  private QuestionnaireDTO testQuestionnaire111DTO;
  private QuestionnaireDTO testQuestionnaire222DTO;

  @BeforeAll
  public void setUp() {

    Optional<UserDTO> optUserDto = userController.fetchDTOByLoginName(UserControllerIT.TEST_USER_ADMIN_LOGIN_NAME);
    if(optUserDto.isPresent()) {
      userAdministratorDTO = optUserDto.get();
    } else {
      throw new NotFoundException(String.format("Admin. USer '%s' not found.", UserControllerIT.TEST_USER_ADMIN_LOGIN_NAME));
    }

    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    testQuestionnaire111DTO =
        controller.fetchDTOFromCode(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1,
            userAdministratorDTO.getId(),
            Constants.DEFAULT_LOCALE).get();

    testQuestionnaire222DTO =
        controller.fetchDTOFromCode(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST2,
            userAdministratorDTO.getId(),
            Constants.DEFAULT_LOCALE).get();
  }

  private String getUrlForIndividualQuestionsSearch(String entityUrl, String apiUrl, int questionId, String langCode) {
    // http://localhost:8091/api/ver1/questionnaires/search4questions/?questionnaireId=2&lang=pl
    String testUrlPattern = HttpRootRequestIT.BASE_URI + port +
        entityUrl +
        apiUrl +
        "?questionnaireId=%d" +
        "&lang=%s";
    return String.format(testUrlPattern, questionId, langCode);
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) Given existing first Questionnaire, when get all Questions for it, then should return 4 records.")
  public void test1() throws Exception {
    // http://localhost:8091/api/ver1/questionnaires/search4questions/?questionnaireId=2&lang=pl
    String testUrl = getUrlForIndividualQuestionsSearch( APIController.QUESTIONNAIRES_URL,
        "/search4questions/",
        testQuestionnaire111DTO.getId(),
        Constants.DEFAULT_LOCALE);
    List<QuestionDTO> retObject = restTemplate.getForObject(testUrl, List.class);

    assertThat(retObject.size()).isEqualTo(4);
  }

  @Test
  @Order(value = 2)
  @DisplayName("(2) Given existing second Questionnaire, when get all Questions for it, then 1 records should be returned.")
  public void test2() throws Exception {
    // http://localhost:8091/api/ver1/questionnaires/search4questions/?questionnaireId=2&lang=pl
    String testUrl = getUrlForIndividualQuestionsSearch( APIController.QUESTIONNAIRES_URL,
        "/search4questions/",
        testQuestionnaire222DTO.getId(),
        Constants.DEFAULT_LOCALE);
    List<QuestionDTO> retObject = restTemplate.getForObject(testUrl, List.class);

    assertThat(retObject.size()).isEqualTo(1);
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) Given invalid Questionnaire ID, when get all Questions for it, then no records should be returned.")
  public void test3() throws Exception {
    // http://localhost:8091/api/ver1/questionnaires/search4questions/?questionnaireId=2&lang=pl
    String testUrl = getUrlForIndividualQuestionsSearch( APIController.QUESTIONNAIRES_URL,
        "/search4questions/",
        INVALID_PARENT_ID,
        Constants.DEFAULT_LOCALE);
    try {
      ArrayList<QuestionDTO> retObject = restTemplate.getForObject(testUrl, ArrayList.class);
      assertThat(retObject.size()).isEqualTo(0);
    } catch (Exception e) {
      // assertThat(e).isInstanceOf(NotFoundException.class);
      assertThat(e).isInstanceOf(RestClientException.class); //  NotFoundException.class);
      // assertThat(e.getMessage()).contains("NotFoundException");
    }
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//
}