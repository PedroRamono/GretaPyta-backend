package com.az.gretapyta.questionnaires.security.JWTManagerImpl;

import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import com.az.gretapyta.questionnaires.security.JWTManager;
import com.az.gretapyta.questionnaires.security.JWToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public abstract class BaseJWTManager implements JWTManager {
  public static final String USER_JWT_TITLE = "Bearer";

  protected final GeneralProperties generalProperties;

  @Override
  public String composeUserJwt(String tokenContent) {
    return getJwtTitle() + tokenContent;
  }

  @Override
  public Optional<JWToken> getValidatedToken(String jwtToken) {
    JWToken ret = null;
    boolean isOk = true;
    if (! jwtToken.startsWith(USER_JWT_TITLE)) {
      log.warn("JWT Token does not begin with '" + USER_JWT_TITLE + "' String");
      isOk = false;
    }
    if (isOk) {
      String[] elements = jwtToken.split(getJwtTitleSuffix(), 2);
      if (elements.length != 2) {
        log.warn(String.format("Invalid structure of JWT Token for '%s' key.", USER_JWT_TITLE));
      } else {
        ret = new JWToken(elements[0], elements[1]);
      }
    }
    return Optional.ofNullable(ret);
  }

  protected String getJwtTitle() {
    return USER_JWT_TITLE + getJwtTitleSuffix();
  }
}