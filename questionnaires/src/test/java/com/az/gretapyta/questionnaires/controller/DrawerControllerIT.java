package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.QuestionnairesApp;
import com.az.gretapyta.questionnaires.dto.DrawerDTO;
import com.az.gretapyta.questionnaires.repository.DrawersRepository;
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

@SpringBootTest( classes = QuestionnairesApp.class,
                 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Order(value = 1) // CLASS_ORDER_ISSUE_SOLUTION
public class DrawerControllerIT extends BaseClassIT {

  @Autowired
  DrawerController controller;

  @Autowired
  private DrawersRepository repository;

  public final static String DRAWER_CODE_TEST1 = "DRW_TEST1";
  public final static String DRAWER_CODE_TEST2 = "DRW_TEST2";

  private DrawerDTO testDrawer1DTO;
  private DrawerDTO testDrawer2DTO;

  // @BeforeEach
  @BeforeAll
  public void setUp() {
    resetDb(); // Clear at the beginning.

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
                // .setControllerAdvice(new NotFoundException())
                .setControllerAdvice(new ServletException())
                .build();

    testDrawer1DTO = getTestDrawer1DTO();
    testDrawer2DTO = getTestDrawer2DTO();
  }

  // @AfterEach
  public  void resetDb() {
    repository.deleteAll();
  }

  public static DrawerDTO getTestDrawer1DTO() {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test 1 - Politics");
    elements.put("pl", "Test 1 - Polityka");
    elements.put("ru", "Тест 1 - Πолитика");
    return createTestDrawerDTO(DRAWER_CODE_TEST1, elements);
  }

  public static DrawerDTO getTestDrawer2DTO() {
    Map<String, String> elements = new TreeMap<>();
    elements.put("en", "Test 2 - Politics");
    elements.put("pl", "Test 2 - Polityka");
    elements.put("ru", "Тест 2 - Πолитика");
    return createTestDrawerDTO(DRAWER_CODE_TEST2, elements);
  }

  private static DrawerDTO createTestDrawerDTO(String code, Map<String, String> nameElements) {
    DrawerDTO drawerDTO = new DrawerDTO();
    drawerDTO.setReady2Show(false);

    drawerDTO.setCode(code);
    drawerDTO.setNameMultilang(nameElements);
    return drawerDTO;
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When calling Drawer Controller root URL, then the root message should be returned.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.DRAWERS_URL +"/";
    String retObject = restTemplate.getForObject(testUrl, String.class);

    assertTrue(retObject.contains(DrawerController.DRAWER_CONTROLLER_HOME_MESSAGE));
  }

  //(1) POSTs
  //
  @Test
  @Order(value = 2)
  @DisplayName("(2) When valid new DrawerDTO, then create new Drawer.")
  public void test02() throws Exception {
    String testUrl = BASE_URI + port + APIController.DRAWERS_URL;
    String jsonContent = Converters.convertObjectToJson(testDrawer1DTO);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonContent, headers);
    ResponseEntity<DrawerDTO> retObject = restTemplate.postForEntity(testUrl, entity, DrawerDTO.class);

    assertThat(retObject.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
    assertThat(retObject.getBody()).isInstanceOf(DrawerDTO.class);
    assertThat(retObject.getBody().getCode().equalsIgnoreCase(testDrawer1DTO.getCode()));

    entityValidIdForTest = retObject.getBody().getId();
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) Another way: when valid new DrawerDTO, then create new Drawer.")
  public void test03() throws Exception {
    String testUrl = BASE_URI + port + APIController.DRAWERS_URL;
    String jsonContent = Converters.convertObjectToJson(testDrawer2DTO);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isCreated())
        .andDo(print());

    // ResponseEntity<List<DrawerDTO>> foundDTO = controller.getAllItems(Constants.DEFAULT_LOCALE);
    Optional<DrawerDTO> optFoundDTO = controller.fetchDTOFromCode(DRAWER_CODE_TEST2, Constants.DEFAULT_LOCALE);

    assertTrue(optFoundDTO.isPresent());
    assertThat(optFoundDTO.get().getCode()).isEqualTo(DRAWER_CODE_TEST2);
  }

  //(2) GETs
  //
  @Test
  @Order(value = 4)
  @DisplayName("(4) When get DrawerDTO by valid ID, then returns 1 record of that ID.")
  public void test04() throws Exception {
    testGetByEntityId(APIController.DRAWERS_URL, entityValidIdForTest);
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) When get DrawerDTO by invalid ID, then no return record and Exception should be thrown.")
  public void test05() throws Exception {
    testGetByInvalidEntityId(APIController.DRAWERS_URL);
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) Given 2 Drawers, when get all, then returns 2 records with valid codes.")
  public void test06() throws Exception {
    String testUrl = BASE_URI + port + APIController.DRAWERS_URL + "/all";

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpectAll(jsonPath("$").isArray() ,jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].code", is(testDrawer1DTO.getCode())))
        .andExpect(jsonPath("$[1].code", is(testDrawer2DTO.getCode()))
        );
  }

  @Test
  @Order(value = 7)
  @DisplayName("(7) When given valid Drawer's code, then Drawer should be fund.")
  public void test07() throws Exception {
    String validCode = testDrawer2DTO.getCode();
    String testUrl = BASE_URI + port + APIController.DRAWERS_URL + "/searchcode/" + validCode;

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.code", is(validCode))
        );
  }

  @Test
  @Order(value = 8)
  @DisplayName("(8) When given Drawer's code not valid, then Drawer should not be fund and NotFoundException to be thrown.")
  public void test08() {
    String invalidCode =  "INVALID_DRW_CODE";
    String testUrl = BASE_URI + port + APIController.DRAWERS_URL + "/searchcode/" + invalidCode;
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