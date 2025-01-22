package com.az.gretapyta.questionnaires.configurationproperties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Data
// @Component
@Validated
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "greta")
public class GeneralProperties {

  private String clientDomain; // for now assumed only single Client Domain.

  //(1)
  @Data
  @Validated
  public static class Security {
    // @Length(max = 16, min = 4)
    @NotNull
    private String jwtCookieName;
    private int jwtCookieMaxAgeForUser;

    private String publicKey;
    private String vector;
    @NotNull
    private String salt; // For hashing User password.
    private String jwtSecret; // For JWT hash.
    private Boolean jwtIsCookieBased;
  }

  @Data
  @Validated
  public static class Defaults {
    @NotNull
    @Length(max = 3, min = 2)
    private String localeCode;

    private Boolean loadInitData;
  }

  @Data
  @Validated
  public static class Formats {
    private String baseDateTime;
  }

  private Security security;
  private Defaults defaults;
  private Formats formats;
}