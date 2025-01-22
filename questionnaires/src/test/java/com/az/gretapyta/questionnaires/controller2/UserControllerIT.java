package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.enums.GenderTypes;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.QuestionnairesApp;
import com.az.gretapyta.questionnaires.controller.DrawerController;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.mapper2.UserMapper;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.repository.*;
import com.az.gretapyta.questionnaires.repository2.AnswersSelectedRepository;
import com.az.gretapyta.questionnaires.repository2.UsersRepository;
import com.az.gretapyta.questionnaires.security.UserRoles;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static com.az.gretapyta.qcore.controller.APIController.RESTRICTED_ENTITY;
import static com.az.gretapyta.questionnaires.HttpRootRequestIT.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest( classes = {QuestionnairesApp.class},  // {QuestionnairesApp.class}
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@ContextConfiguration
@Order(value = 1)
public class UserControllerIT extends BaseClassIT {

  public final static String TEST_USER_EN_LOGIN_NAME = "johnymockito";
  public final static String TEST_USER_RU_LOGIN_NAME = "natalya_voroz";
  public final static String TEST_USER_ADMIN_LOGIN_NAME = "gardengnome";

  public final static String TEST_PASSWORD_FOR_USER ="password123";

  public final static String TEST_USER_RU_FIRST_NAME = "Natalya";
  public final static String TEST_USER_RU_LAST_NAME = "Vorozanskaya";

  @Autowired
  UserMapper mapper;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UsersRepository repository;

  // For clearing up all other, User-dependent Entities:
  @Autowired
  private DrawersRepository drawerRepository;
  @Autowired
  private QuestionnairesRepository questionnairesRepository;
  @Autowired
  private StepsRepository stepsRepository;
  @Autowired
  private QuestionsRepository questionsRepository;
  @Autowired
  private OptionsRepository optionsRepository;
  @Autowired
  private AnswersSelectedRepository answersSelectedRepository;

  private int seedMasterId;
  private UserDTO anonymousEnDTO;
  private UserDTO johnyDTO;
  private UserDTO natalyaDTO;
  private UserDTO gardenGnomeDTO;

  // @BeforeEach
  @BeforeAll
  public void setUp() {
    resetDb(); // Clear at the beginning.
    seedAdminUser();

    mockMvc = MockMvcBuilders
                  .webAppContextSetup(context)
                  .alwaysDo(print())
                  .apply(springSecurity())
                  .build();

    //(1)
    User anonymousEn = User.createAnonymousUserForLanguage(Constants.DEFAULT_LOCALE);
    anonymousEnDTO = mapper.map(anonymousEn);
    anonymousEnDTO.setCreated(null); // Get to nullify it

    //(2)
    User johny = User.createUser("Johny",
        "Mockito",
        GenderTypes.MALE.getCode(),
        null,
        "jomockito3@google.com",
        TEST_USER_EN_LOGIN_NAME,
        TEST_PASSWORD_FOR_USER,
        "en",
        false,
        UserRoles.USER_CASUAL.getCode());
    johnyDTO = mapper.map(johny);
    johnyDTO.setPasswordHash(johny.getPasswordHash()); // Mapper intentionally doesn't map it when USER -> USerDTO
    johnyDTO.setCreated(null); // Get to nullify it

    //(3)
    User natalya = User.createUser(TEST_USER_RU_FIRST_NAME,
        TEST_USER_RU_LAST_NAME,
        GenderTypes.FEMALE.getCode(),
        null,
        "natvor101@yandex.ru",
        TEST_USER_RU_LOGIN_NAME,
        "999-password!",
        "ru",
        false,
        UserRoles.USER_CLIENT.getCode());
    natalyaDTO = mapper.map(natalya);
    natalyaDTO.setPasswordHash(natalya.getPasswordHash()); // Mapper intentionally doesn't map it when USER -> USerDTO
    natalyaDTO.setCreated(null); // Get to nullify it

    //(4)
    User gardenGnome = User.createUser("garden",
        "gnome",
        GenderTypes.NOT_DECLARED.getCode(),
        null,
        "ggnome1984@yahoo.ca",
        TEST_USER_ADMIN_LOGIN_NAME,
        "!bzyk4Eve909",
        "pl",
        false,
        UserRoles.ADMIN.getCode());
    gardenGnomeDTO = mapper.map(gardenGnome);
    gardenGnomeDTO.setPasswordHash(gardenGnome.getPasswordHash()); // Mapper intentionally doesn't map it when USER -> USerDTO
    gardenGnomeDTO.setCreated(null); // Get to nullify it
  }

  // @AfterEach
  public void resetDb() {
    answersSelectedRepository.deleteAll();

    optionsRepository.deleteAll();
    questionsRepository.deleteAll();
    stepsRepository.deleteAll();
    questionnairesRepository.deleteAll();
    drawerRepository.deleteAll();

    repository.deleteAll();
  }

  // Initial Creator (Admin.) is needed to create consecutive Users
  private void seedAdminUser() {
    User masterAdmin = User.createUser("garden",
        "",
        GenderTypes.NOT_DECLARED.getCode(),
        null,
        "",
        "seedmasteradmin",
        "password123",
        "en",
        false,
        UserRoles.ADMIN.getCode());

    // No need to encrypt password for Admin. in Test.
    // String encryptedPassword = passwordEncoder().encode(masterAdmin.getPasswordHash());
    // masterAdmin.setPasswordHash(encryptedPassword);
    User resUser = repository.save(masterAdmin);
    seedMasterId = resUser.getId();
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  //(1) POSTs
  //
  @Test
  @Order(value = 1)
  @DisplayName("(01) When valid new Anonymous UserDTO, then create new Anonymous for EN language.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL;
    String jsonContent = Converters.convertObjectToJson(anonymousEnDTO);
    HttpHeaders headers = armHeaderWithAttribs(seedMasterId);

    mockMvc.perform(
        post(testUrl)
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent))
      .andExpect(status().isCreated()) // .isCreated() / .isOk()
      .andDo(print());
  }

  @Test
  @Order(value = 2)
  @DisplayName("(02) When valid new Casual-Role User's DTO of English pref., then create new English-speaking User.")
  public void test02() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL;
    String jsonContent = Converters.convertObjectToJson(johnyDTO);
    HttpHeaders headers = armHeaderWithAttribs(seedMasterId);

    mockMvc.perform(
            post(testUrl)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated()) // .isCreated())
        .andDo(print());
  }

  @Test
  @Order(value = 3)
  @DisplayName("(03) When valid new Client-Role User's DTO of Russian pref., then create new Russian-speaking User.")
  public void test03() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL;
    String jsonContent = Converters.convertObjectToJson(natalyaDTO);

    HttpHeaders headers = armHeaderWithAttribs(seedMasterId);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<UserDTO> retObject = restTemplate.postForEntity(testUrl, entity, UserDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(UserDTO.class);
    assertThat(Objects.requireNonNull(retObject.getBody()).getLoginName().equalsIgnoreCase(natalyaDTO.getLoginName()));

    entityValidIdForTest = retObject.getBody().getId();
  }

  @Test
  @Order(value = 4)
  @DisplayName("(04) When valid new Administrator-Role User's DTO, then create new Admin. User.")
  public void test04() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL;
    String jsonContent = Converters.convertObjectToJson(gardenGnomeDTO);
    HttpHeaders headers = armHeaderWithAttribs(seedMasterId);

    mockMvc.perform(
            post(testUrl)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  //(2) GETs
  //
  @Test
  @Order(value = 5)
  @DisplayName("(1) When calling User Controller root URL, then the root message should be returned.")
  public void test05() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(UserController.USER_CONTROLLER_HOME_MESSAGE));
  }

  @Test
  @Order(value = 6)
  @WithAnonymousUser
  @DisplayName("(06) When calling User Controller restricted URL by Anonymous User, " +
      "then the Unauthorized status should be returned.")
  public void test06() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY + "/all";
    mockMvc.perform(get(testUrl))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @Order(value = 7)
  @WithMockUser(roles = "LU")
  @DisplayName("(07) When calling User Controller restricted URL by User, " +
      "then the and Access Forbidden status should be returned.")
  public void test07() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY + "/all";
    mockMvc.perform(get(testUrl))
        .andExpect(status().isForbidden())
        .andDo(print());
  }

  @Test
  @Order(value = 8)
  @WithMockUser(roles = "AM")
  @DisplayName("(08) Given all 5 Users, when calling User Controller restricted URL by Administrator" +
      " to get all, then return 5 records.")
  public void test08() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL +  RESTRICTED_ENTITY + "/all";
    HttpHeaders headers = armHeaderWithAttribs(seedMasterId);

    mockMvc.perform(
        get(testUrl)
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(5))
        );
  }

  @Test
  @Order(value = 9)
  @WithMockUser(roles = "AM")
  @DisplayName("(09) When get UserDTO by valid ID by Administrator, then returns 1 record of that ID.")
  public void test09() throws Exception {
    /// testGetByEntityId(APIController.USERS_URL, entityValidIdForTest);
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY +
         APIController.SEARCH_ENTITY_BY_ID_API + entityValidIdForTest;
    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id", is(entityValidIdForTest))
        );
  }

  @Test
  @Order(value = 10)
  @WithMockUser(roles = "AM")
  @DisplayName("(10) When get UserDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test10() {
    // testGetByInvalidEntityId(APIController.USERS_URL);
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY
        + APIController.SEARCH_ENTITY_BY_ID_API + INVALID_PARENT_ID;

    try {
      mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
          .andDo(print())
          .andExpect(status().isNotFound()) // .isInternalServerError())

          .andExpect(__ -> MatcherAssert.assertThat(
              __.getResolvedException(),
              CoreMatchers.instanceOf(NotFoundException.class)))
          .andReturn();
    } catch(Exception e) {
      assertThat(e).isInstanceOf(ServletException.class); // NotFoundException.class);
      assertThat(e.getMessage()).contains("NotFoundException");
    }
  }

  @Test
  @Order(value = 11)
  @WithMockUser(roles = "AM")
  @DisplayName("(11) Given 1 existing User for Anonymous EN User, when get Anonymous for EN, then returns 1 record.")
  public void test11() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY +
        "/anonymous/" + anonymousEnDTO.getPreferredLang() ;

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        // .andExpect(jsonPath("$.id", is(anonymousEnDTO.getId())))
        .andExpect(jsonPath("$.anonymousUser", is(true)))
        .andExpect(jsonPath("$.preferredLang", is(anonymousEnDTO.getPreferredLang()))
        );
  }

  @Test
  @Order(value = 12)
  @WithMockUser(roles = "AM")
  @DisplayName("(12) Given 1 existing User for EN User, when get User by login name, then returns 1 record.")
  public void test12() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY +
        "/searchbyloginname/?loginName=" + TEST_USER_EN_LOGIN_NAME;

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        // .andExpect(jsonPath("$.id", is(johnyDTO.getId())))
        .andExpect(jsonPath("$.firstName", is(johnyDTO.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(johnyDTO.getLastName()))
        );
  }

  @Test
  @Order(value = 13)
  @WithMockUser(roles = "AM")
  @DisplayName("(13) Given 1 existing User for RU User, when get User by first and last name, then returns 1 record.")
  public void test13() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY +
        "/searchbyfullname/?firstName=" + TEST_USER_RU_FIRST_NAME +"&lastName=" +TEST_USER_RU_LAST_NAME;

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].firstName", is(natalyaDTO.getFirstName())))
        .andExpect(jsonPath("$[0].lastName", is(natalyaDTO.getLastName()))
        );
  }

  @Test
  @Order(value = 14)
  @WithMockUser(roles = "AM")
  @DisplayName("(14) When given a non-existent User's name, then no User should be fund.")
  public void test14() throws Exception {
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY +
        "/searchbyfullname/?firstName=" + TEST_USER_RU_FIRST_NAME +"&lastName=" +TEST_USER_RU_LAST_NAME + "invalid";

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(0))
        );
  }

  @Test
  @Order(value = 15)
  @WithMockUser(roles = "AM")
  @DisplayName("(15) When given a non-existent User's login name, then no User should be fund.")
  public void test15() {
    String NON_EXISTENT_LOGIN = TEST_USER_EN_LOGIN_NAME + "invalid";
    String testUrl = BASE_URI + port + APIController.USERS_URL + RESTRICTED_ENTITY +
        "/searchbyloginname/?loginName=" + NON_EXISTENT_LOGIN;

    try {
      mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
          .andDo(print())
          .andExpect(status().isNotFound())

          .andExpect(__ -> MatcherAssert.assertThat(
              __.getResolvedException(),
              CoreMatchers.instanceOf(NotFoundException.class)))
          .andReturn();
    } catch(Exception e) {
      assertThat(e).isInstanceOf(ServletException.class); //  NotFoundException.class);
      assertThat(e.getMessage()).contains(NON_EXISTENT_LOGIN);
    }
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//
}