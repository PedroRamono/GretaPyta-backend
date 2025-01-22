package com.az.gretapyta.questionnaires.security.JWTManagerImpl;

import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JWTManagerHeadersImpl extends BaseJWTManager { /// } implements A1_JWTManager {
  public static final String HEADER_KEY_ACCESS_CONTROL = "Access-Control-Expose-Headers";
  public static final String HEADER_KEY_AUTHORIZATION = "Authorization";
  public static final String HEADER_KEY_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

  public static final String HEADER_KEY_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

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
    // Needed for Client App. calling API being able to use it:

log.info("(addUserTokenToResponse)-2 Outgoing Response: Anonymous User=" + isAnonymous +"; JWT=" +userJwt);

    response.setHeader(HEADER_KEY_ACCESS_CONTROL, HEADER_KEY_AUTHORIZATION); // Access-Control-Expose-Headers: X-Total-Count, X-Paging-PageSize
    response.setHeader(HEADER_KEY_AUTHORIZATION, userJwt);
    response.addHeader(HEADER_KEY_EXPOSE_HEADERS, HEADER_KEY_AUTHORIZATION);
  }

  @Override
  public String getJwtTitleSuffix() {
    return JWT_TITLE_SUFFIX;
  }
}