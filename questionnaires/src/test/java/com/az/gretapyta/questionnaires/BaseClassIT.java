package com.az.gretapyta.questionnaires;

import com.az.gretapyta.qcore.controller.APIController;
import jakarta.persistence.MappedSuperclass;
import jakarta.servlet.ServletException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.experimental.categories.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.az.gretapyta.questionnaires.HttpRootRequestIT.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TestPropertySource(locations = {"classpath:application.yml"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // to have @BeforeAll non-static
@Category(IntegrationTest.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@MappedSuperclass
public abstract class BaseClassIT {
  public final static int INVALID_PARENT_ID = 0;

  @LocalServerPort
  protected int port;

  @Autowired
  protected TestRestTemplate restTemplate;

  protected MockMvc mockMvc;

  protected int entityValidIdForTest = 0;

  protected String getUrlForSearchFromParentId( String entityUrl,
                                                int parentId,
                                                String langCode ) {
    StringBuilder testUrlPattern = new StringBuilder(HttpRootRequestIT.BASE_URI);
    testUrlPattern.append(port)
        .append(entityUrl)
        .append("/search/byparent/?parentId=%d&lang=%s");
   return String.format(testUrlPattern.toString(), parentId, langCode);
  }

  protected void testGetByEntityId(String entityUrlApi, int entityId) throws Exception {
    String testUrl = BASE_URI + port + entityUrlApi
        + APIController.SEARCH_ENTITY_BY_ID_API + entityId;

    mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id", is(entityId))
        );
  }

  protected void testGetByInvalidEntityId(String entityUrlApi) throws Exception {
    String testUrl = BASE_URI + port + entityUrlApi
        + APIController.SEARCH_ENTITY_BY_ID_API + INVALID_PARENT_ID;
      try {
        mockMvc.perform(get(testUrl).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(__ -> MatcherAssert.assertThat(
                __.getResolvedException(),
                CoreMatchers.instanceOf(SecurityException.class)))
            .andReturn();

    } catch(Exception e) {
      assertThat(e).isInstanceOf(ServletException.class); // NotFoundException.class);
      assertThat(e.getMessage()).contains("NotFoundException");
    }
  }
}