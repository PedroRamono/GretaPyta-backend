package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.controller2.UserControllerIT;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import com.az.gretapyta.questionnaires.repository.OptionsRepository;
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
@Order(value = 7)
public class OptionControllerIT extends BaseClassIT {

  @Autowired
  OptionController controller;

  @Autowired
  QuestionController parentController;

  @Autowired
  UserController userController;

  @Autowired
  private OptionsRepository repository;

  public final static String OPTION_CODE_TEST1 = "OPT_TEST1";
  public final static String OPTION_CODE_TEST2 = "OPT_TEST2";
  public final static String OPTION_CODE_TEST3 = "OPT_TEST3";

  public final static String OPTION_CODE_TEST4 = "OPT_TEST4";
  public final static String OPTION_CODE_TEST5 = "OPT_TEST5";


  private OptionDTO testOption111DTO;
  private OptionDTO testOption222DTO;
  private OptionDTO testOption333DTO;

  private OptionDTO testOption444DTO;
  private OptionDTO testOption555DTO;

  private QuestionDTO question111DTO;
  private QuestionDTO question222DTO;

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

    question111DTO = getParentEntity(QuestionControllerIT.QUESTION_CODE_TEST1); // the answer is radio buttons selection.
    question222DTO = getParentEntity(QuestionControllerIT.QUESTION_CODE_TEST2); // the answer is user multi-choice.

    testOption111DTO = getTestOption111DTO(OPTION_CODE_TEST1);
    testOption222DTO = getTestOption222DTO(OPTION_CODE_TEST2);
    testOption333DTO = getTestOption333DTO(OPTION_CODE_TEST3);

    testOption444DTO = getTestOption444DTO(OPTION_CODE_TEST4);
    testOption555DTO = getTestOption555DTO(OPTION_CODE_TEST5);
  }

  // @AfterEach
  public  void resetDb() {
    repository.deleteAll();
  }

  private QuestionDTO getParentEntity(String code) {
    // Should still exist from QuestionControllerIT Tests:
    Optional<QuestionDTO> retObject = parentController.fetchDTOFromCode(code, userAdministratorDTO.getId(),"en");
    return retObject.get();
  }

  public static OptionDTO getTestOption111DTO(String code) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Option 1");
    elements.put("pl", "Test-Opcja 1");
    elements.put("ru", "Тест-Вариант 1");
    return createTestOptionDTO(code, elements);
  }

  public static OptionDTO getTestOption222DTO(String code) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Option 2");
    elements.put("pl", "Test-Opcja 2");
    elements.put("ru", "Тест-Вариант 2");
    return createTestOptionDTO(code, elements);
  }

  public static OptionDTO getTestOption333DTO(String code) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Option 3");
    elements.put("pl", "Test-Opcja 3");
    elements.put("ru", "Тест-Вариант 3");
    return createTestOptionDTO(code, elements);
  }

  public static OptionDTO getTestOption444DTO(String code) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Option Four");
    elements.put("pl", "Test-Opcja czwarta");
    elements.put("ru", "Тест-Вариант четвертый");
    return createTestOptionDTO(code, elements);
  }

  public static OptionDTO getTestOption555DTO(String code) {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test-Option Five");
    elements.put("pl", "Test-Opcja piąta");
    elements.put("ru", "Тест-Вариант пятый");
    return createTestOptionDTO(code, elements);
  }

  private static OptionDTO createTestOptionDTO(String code, Map<String, String> nameElements) {
    OptionDTO ret = new OptionDTO();
    ret.setReady2Show(true);
    ret.setCode(code);
    ret.setNameMultilang(nameElements);
    return ret;
  }

  private QuestionOptionLink saveLinkEntry(QuestionDTO parentDto, OptionDTO itemDto, int displayOrder, int tenantId) {
    return controller.executeCreateParentChildLink(
        parentDto,
        itemDto,
        displayOrder,
        tenantId );
  }

  private void testOneOptionAndLinkToParent( OptionDTO itemDto,
                                             QuestionDTO parentDto,
                                             int displayOrder ) throws Exception {
    ResponseEntity<OptionDTO> retObject = postForEntity(itemDto);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(OptionDTO.class);
    assertNotNull(retObject.getBody().getId());

    itemDto.setId(retObject.getBody().getId()); // Save ID of Option

    entityValidIdForTest = retObject.getBody().getId();

    createAndAssertParentChildLink(retObject.getBody(), parentDto, displayOrder);
  }

  private void createAndAssertParentChildLink( OptionDTO itemDto,
                                               QuestionDTO parentDto,
                                               int displayOrder ) {

    QuestionOptionLink link = saveLinkEntry(parentDto,
        itemDto,
        displayOrder,
        0);

    assertNotNull(link);
    assertThat(link.getQuestionDown().getId()).isEqualTo(parentDto.getId());
    assertThat(itemDto.getId()).isEqualTo(link.getOption().getId());
  }

  private ResponseEntity<OptionDTO> postForEntity(OptionDTO itemDto) throws Exception {
    String testUrl = BASE_URI + port + APIController.OPTIONS_URL;
    String jsonContent = Converters.convertObjectToJson(itemDto);
    HttpHeaders headers = armHeaderWithAttribs(userAdministratorDTO.getId());
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    return restTemplate.postForEntity(testUrl, entity, OptionDTO.class);
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling Option Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.OPTIONS_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(OptionController.OPTION_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new 3 OptionDTOs, then create 3 new Options and link them to first Question.")
  public void test02() throws Exception {
    // Order: TEST2, TEST3, TEST1
    testOneOptionAndLinkToParent(testOption111DTO, question111DTO, StepControllerIT.DISPLAY_ORDER_3); // Option 1
    testOneOptionAndLinkToParent(testOption222DTO, question111DTO, StepControllerIT.DISPLAY_ORDER_1); // Option 2
    testOneOptionAndLinkToParent(testOption333DTO, question111DTO, StepControllerIT.DISPLAY_ORDER_2); // Option 3
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3)  When valid new 2 OptionDTOs, then create 2 new Options and link them to second Question.")
  public void test03() throws Exception {
    // Order: TEST5, TEST4
    testOneOptionAndLinkToParent(testOption444DTO, question222DTO, StepControllerIT.DISPLAY_ORDER_2);
    testOneOptionAndLinkToParent(testOption555DTO, question222DTO, StepControllerIT.DISPLAY_ORDER_1);
  }

  //(2) GETs
  //
  @Test
  @Order(value = 4)
  @DisplayName("(4) When get OptionDTO by valid ID, then returns 1 record of that ID.")
  public void test04() throws Exception {
    testGetByEntityId(APIController.OPTIONS_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) When get OptionDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test05() throws Exception {
    testGetByInvalidEntityId(APIController.OPTIONS_URL);
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) Given 5 Options, when get all, then returns 5 records.")
  public void test06() throws Exception {
    String testUrl = BASE_URI + port + APIController.OPTIONS_URL + "/all";

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(5))
        );
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) Given 3 Options for the first Question, when get all for the first Question, then returns 3 records and in proper display order.")
  public void test07() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.OPTIONS_URL, question111DTO.getId(), Constants.DEFAULT_LOCALE);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(3)))
         // the order should be: TEST2, TEST3, TEST1.
        .andExpect(jsonPath("$[0].displayOrder", is(StepControllerIT.DISPLAY_ORDER_1)))
        .andExpect(jsonPath("$[0].code", is(testOption222DTO.getCode()))) // in reverse order
        .andExpect(jsonPath("$[1].displayOrder", is(StepControllerIT.DISPLAY_ORDER_2)))
        .andExpect(jsonPath("$[1].code", is(testOption333DTO.getCode())))
        .andExpect(jsonPath("$[2].displayOrder", is(StepControllerIT.DISPLAY_ORDER_3)))
        .andExpect(jsonPath("$[2].code", is(testOption111DTO.getCode()))
        );
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) Given 2 Options for the second Question, when get all for the second Question,\n then returns 2 records and in proper display order.")
  public void test08() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.OPTIONS_URL, question222DTO.getId(), Constants.DEFAULT_LOCALE);

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(2)))
        // the order should be: TEST3, TEST2.
        .andExpect(jsonPath("$[0].displayOrder", is(StepControllerIT.DISPLAY_ORDER_1)))
        .andExpect(jsonPath("$[0].code", is(testOption555DTO.getCode()))) // in reverse order
        .andExpect(jsonPath("$[1].displayOrder", is(StepControllerIT.DISPLAY_ORDER_2)))
        .andExpect(jsonPath("$[1].code", is(testOption444DTO.getCode()))
        );
  }

  @Test
  @Order(value = 9)
  @DisplayName("(9) When given a parent's Question ID not valid, then no Option should be fund.")
  public void test09() throws Exception {
    String testUrl = getUrlForSearchFromParentId(APIController.OPTIONS_URL, INVALID_PARENT_ID, Constants.DEFAULT_LOCALE);

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