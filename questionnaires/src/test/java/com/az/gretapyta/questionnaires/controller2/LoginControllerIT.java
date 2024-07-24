package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.questionnaires.BaseClassIT;
import com.az.gretapyta.questionnaires.QuestionnairesApp;
import com.az.gretapyta.questionnaires.security.LoginInfo;
import com.az.gretapyta.questionnaires.util.Converters;
import jakarta.servlet.ServletException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.az.gretapyta.questionnaires.HttpRootRequestIT.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest( classes = {QuestionnairesApp.class},  // {QuestionnairesApp.class}
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Order(value = 22)
public class LoginControllerIT extends BaseClassIT {

  @Autowired
  LoginController controller;

  @BeforeAll
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @WithAnonymousUser
  @DisplayName("(1) When calling User login with invalid credentials, " +
      "then the status should be WRONG.")
  public void test01() throws Exception {
    String testUrl = BASE_URI + port + APIController.USER_IDENTITY_URL + APIController.LOGIN_API;
    String wrongPassword = "password";
    LoginInfo authenticationRequest = new LoginInfo(UserControllerIT.TEST_USER_EN_LOGIN_NAME, wrongPassword);
    String jsonContent = Converters.convertObjectToJson(authenticationRequest);

    try {
      mockMvc.perform(
              post(testUrl)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonContent))
          .andExpect(status().isOk())
          .andDo(print())

          .andExpect(__ -> MatcherAssert.assertThat(
              __.getResolvedException(),
              CoreMatchers.instanceOf(BadCredentialsException.class)))
          .andReturn();
    } catch(Exception e) {
      assertThat(e).isInstanceOf(ServletException.class); //  NotFoundException.class);
      assertThat(e.getMessage()).contains("INVALID_CREDENTIALS");
    }
  }

  @Test
  @Order(value = 2)
  @WithAnonymousUser
  @DisplayName("(2) When calling User login with correct credentials, " +
      "then the status should be OK.")
  public void test02() throws Exception {
    String testUrl = BASE_URI + port + APIController.USER_IDENTITY_URL + APIController.LOGIN_API;
    String password = UserControllerIT.TEST_PASSWORD_FOR_USER;
    LoginInfo authenticationRequest = new LoginInfo(UserControllerIT.TEST_USER_EN_LOGIN_NAME, password);
    String jsonContent = Converters.convertObjectToJson(authenticationRequest);

    mockMvc.perform(
            post(testUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @Order(value = 3)
  @WithAnonymousUser
  @DisplayName("(3) When calling User logout, then the status should be OK.")
  public void test03() throws Exception {
    String testUrl = BASE_URI + port + APIController.USER_IDENTITY_URL + APIController.LOGOUT_API;
    mockMvc.perform(
            post(testUrl))
        .andExpect(status().isOk())
        .andDo(print());
  }
}