package com.az.gretapyta.questionnaires;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.questionnaires.controller.HomeController;

import static org.assertj.core.api.Assertions.assertThat;

import static io.restassured.RestAssured.given;
import io.restassured.response.ValidatableResponse;

import org.junit.jupiter.api.Order;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Category(IntegrationTest.class)
public class HttpRootRequestIT {

  public final static String BASE_URI = "http://localhost:";

  @Value(value="${local.server.port}")
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private ValidatableResponse response;

  @Test
  @Order(value = 1)
  @DisplayName("(1) When Request to Home Controller, then should return default home message.")
  public void test1() {
    assertThat(this.restTemplate.getForObject(BASE_URI + port + APIController.API_ROOT_URL,
        String.class)).isEqualTo(HomeController.HOME_REQUEST_MESSAGE);
  }

  @Test
  @Order(value = 1)
  @DisplayName("(2) When Request to Home Controller, then Status should return 200 and response content should contain home message.")
  public void test2() {
    // http://localhost:8091/api/ver1/
    String url = BASE_URI + port + APIController.API_ROOT_URL;

    response = given().contentType("application/json")
        .header("Content-Type", "application/json")
        .when().get(url).then().statusCode(200);

    String actual = response.extract().asString();
    System.out.println("Result :" + actual);
    Assertions.assertEquals(HomeController.HOME_REQUEST_MESSAGE, actual);
  }
}