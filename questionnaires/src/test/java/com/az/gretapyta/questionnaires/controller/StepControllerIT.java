package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.controller2.UserControllerIT;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.repository.StepsRepository;
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
/*
@TestPropertySource(locations = {"classpath:application.yml"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // to have @BeforeAll non-static
@Category(IntegrationTest.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
*/
@Order(value = 4)
public class StepControllerIT extends BaseClassIT {

  @Autowired
  StepController controller;

  @Autowired
  QuestionnaireController parentController;

  @Autowired
  UserController userController;

  @Autowired
  private StepsRepository repository;

  public final static int DISPLAY_ORDER_1 = 1;
  public final static int DISPLAY_ORDER_2 = 2;
  public final static int DISPLAY_ORDER_3 = 3;

  public final static String STEP_ONE_EN_NAME = "Test-Step 1";
  public final static String STEP_TWO_EN_NAME = "Test-Step 2";
  public final static String STEP_THREE_EN_NAME = "Test-Step 3";

  private StepDTO testStep111DTO;
  private StepDTO testStep222DTO;
  private StepDTO testStep333DTO;

  private QuestionnaireDTO questionnaire111DTO;
  private QuestionnaireDTO questionnaire222DTO;

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

    questionnaire111DTO = getParentEntity(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST1);
    questionnaire222DTO = getParentEntity(QuestionnaireControllerIT.QUESTIONNAIRE_CODE_TEST2);

    testStep111DTO = getTestStep111DTO(STEP_ONE_EN_NAME);
    testStep111DTO.setUserId(userAdministratorDTO.getId());

    testStep222DTO = getTestStep222DTO(STEP_TWO_EN_NAME);
    testStep222DTO.setUserId(userAdministratorDTO.getId());

    testStep333DTO = getTestStep333DTO(STEP_THREE_EN_NAME);
    testStep333DTO.setUserId(userAdministratorDTO.getId());
  }

  // @AfterEach
  public  void resetDb() {
    repository.deleteAll();
  }

  private QuestionnaireDTO getParentEntity(String parentCode) {
    // Should still exist from QuestionnaireControllerIT Tests:
    Optional<QuestionnaireDTO> retObject = parentController.fetchDTOFromCode(parentCode,
        userAdministratorDTO.getId(),
        Constants.DEFAULT_LOCALE);
    return retObject.get();
  }

  public static StepDTO getTestStep111DTO(String name) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", name);
    elements.put("pl", "Test-pierwszy krok");
    elements.put("ru", "Тест-Первый шаг");
    return createTestStepDTO(elements); /// displayOrder
  }

  public static StepDTO getTestStep222DTO(String name) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", name);
    elements.put("pl", "Test-drugi krok");
    elements.put("ru", "Тест-Шаг второй");
    return createTestStepDTO(elements); /// displayOrder
  }

  public static StepDTO getTestStep333DTO(String name) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", name);
    elements.put("pl", "Test-krok 2");
    elements.put("ru", "Тест-шаг 3");
    return createTestStepDTO(elements); /// displayOrder
  }

  private static StepDTO createTestStepDTO(Map<String, String> nameElements) { /// int displayOrder,
    StepDTO stepDTO = new StepDTO();
    stepDTO.setReady2Show(true);
    stepDTO.setNameMultilang(nameElements);
    return stepDTO;
  }

  private QuestionnaireStepLink saveLinkEntry(QuestionnaireDTO itemDto, StepDTO parentDto, int displayOrder, int tenantId) {
    return controller.executeCreateParentChildLink(
        itemDto,
        parentDto,
        displayOrder,
        tenantId );
  }

  private void testOneStepAndLinkToParent( StepDTO itemDto,
                                           QuestionnaireDTO parentDto,
                                           int displayOrder ) throws Exception {
    ResponseEntity<StepDTO> retObject = postForEntity(itemDto);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(StepDTO.class);
    assertNotNull(retObject.getBody().getId());

    entityValidIdForTest = retObject.getBody().getId();

    itemDto.setId(retObject.getBody().getId()); // Save ID of Step
    createAndAssertParentChildLink(retObject.getBody(), parentDto, displayOrder);
  }

  private void createAndAssertParentChildLink( StepDTO itemDto,
                                               QuestionnaireDTO parentDto,
                                               int displayOrder ) {

    QuestionnaireStepLink link = saveLinkEntry(parentDto,
        itemDto,
        displayOrder,
        0);

    assertNotNull(link);
    assertThat(link.getQuestionnaire().getId()).isEqualTo(parentDto.getId());
    assertThat(itemDto.getId()).isEqualTo(link.getStepUp().getId());
  }

  private ResponseEntity<StepDTO> postForEntity(StepDTO itemDto) throws Exception {
    String testUrl = BASE_URI + port + APIController.STEPS_URL;
    String jsonContent = Converters.convertObjectToJson(itemDto);
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpHeaders headers = armHeaderWithAttribs(userAdministratorDTO.getId());
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    return restTemplate.postForEntity(testUrl, entity, StepDTO.class);
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling Step Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.STEPS_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(StepController.STEP_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new 3 StepDTOs, then create 3 new Steps and link them to first Questionnaires.")
  public void test02() throws Exception {
    // Order: TEST2, TEST3, TEST1
    testOneStepAndLinkToParent(testStep111DTO, questionnaire111DTO, StepControllerIT.DISPLAY_ORDER_3); // Option 1
    testOneStepAndLinkToParent(testStep222DTO, questionnaire111DTO, StepControllerIT.DISPLAY_ORDER_1); // Option 2
    testOneStepAndLinkToParent(testStep333DTO, questionnaire111DTO, StepControllerIT.DISPLAY_ORDER_2); // Option 3
  }

  //(2) GETs
  //
  @Test
  @Order(value = 3)
  @DisplayName("(3) When get StepDTO by valid ID, then returns 1 record of that ID.")
  public void test03() throws Exception {
    testGetByEntityId(APIController.STEPS_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 4)
  @DisplayName("(4) When get StepDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test04() throws Exception {
    testGetByInvalidEntityId(APIController.STEPS_URL);
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) Given 3 Steps, when get all, then returns 3 records.")
  public void test05() throws Exception {
    String testUrl = BASE_URI + port + APIController.STEPS_URL + "/all";

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(3))
        );
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) Given 3 Steps for the first Questionnaire, when get all for the Questionnaire, then returns 3 records and in proper display order.")
  public void test06() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.STEPS_URL, questionnaire111DTO.getId(), Constants.DEFAULT_LOCALE);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(3)))
        // the order should be: TEST2, TEST3, TEST1.
        .andExpect(jsonPath("$[0].displayOrder", is(StepControllerIT.DISPLAY_ORDER_1)))
        .andExpect(jsonPath("$[1].displayOrder", is(StepControllerIT.DISPLAY_ORDER_2)))
        .andExpect(jsonPath("$[2].displayOrder", is(StepControllerIT.DISPLAY_ORDER_3))
        );
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) When existing 2 Steps then link them to the second Questionnaire.")
  public void test07() {
    // Order: TEST3, TEST2
    createAndAssertParentChildLink(testStep222DTO, questionnaire222DTO, StepControllerIT.DISPLAY_ORDER_2);
    createAndAssertParentChildLink(testStep333DTO, questionnaire222DTO, StepControllerIT.DISPLAY_ORDER_1);
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) Given 2 existing Steps for the second Questionnaire but with different display order, when get all for the Questionnaire,\n then returns 2 records and in proper display order.")
  public void test08() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.STEPS_URL, questionnaire222DTO.getId(), Constants.DEFAULT_LOCALE);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].displayOrder", is(StepControllerIT.DISPLAY_ORDER_1)))
        .andExpect(jsonPath("$[1].displayOrder", is(StepControllerIT.DISPLAY_ORDER_2))
        );
  }

  @Test
  @Order(value = 9)
  @DisplayName("(9) When given a parent's Questionnaire ID not valid, then no Step should be fund.")
  public void test09() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.STEPS_URL, INVALID_PARENT_ID, Constants.DEFAULT_LOCALE);

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