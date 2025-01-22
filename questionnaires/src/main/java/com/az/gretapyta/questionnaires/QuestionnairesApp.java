package com.az.gretapyta.questionnaires;

import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableConfigurationProperties(GeneralProperties.class) // (QuestionnaireConfig.class)
public class QuestionnairesApp {

  @Autowired
  GeneralProperties generalProperties;

  public static void main(final String[] args) {
    SpringApplication.run(QuestionnairesApp.class, args);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(generalProperties.getClientDomain())
                .allowedMethods("*");
      }
    };
  }
}