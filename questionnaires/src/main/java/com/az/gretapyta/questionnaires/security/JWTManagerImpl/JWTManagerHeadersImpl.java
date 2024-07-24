package com.az.gretapyta.questionnaires.security.JWTManagerImpl;

import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JWTManagerHeadersImpl extends BaseJWTManager { /// } implements A1_JWTManager {
  public static final String HEADER_KEY_AUTHORIZATION = "Authorization";
  private static final String JWT_TITLE_SUFFIX = " "; // for Header-based JWT

  public JWTManagerHeadersImpl(GeneralProperties generalProperties) {
    super(generalProperties);
  }

  @Override
  public String getUserTokenWrapperFromCarrier(HttpServletRequest request) {
    return request.getHeader(HEADER_KEY_AUTHORIZATION);
  }

  @Override
  public void addUserTokenToResponse( HttpServletRequest request,
                                      HttpServletResponse response,
                                      String userJwt,
                                      boolean isAnonymous ) {
    response.setHeader(HEADER_KEY_AUTHORIZATION, userJwt);
  }

  @Override
  public String getJwtTitleSuffix() {
    return JWT_TITLE_SUFFIX;
  }
}