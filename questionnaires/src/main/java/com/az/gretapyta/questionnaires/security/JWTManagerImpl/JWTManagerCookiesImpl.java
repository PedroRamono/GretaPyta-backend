package com.az.gretapyta.questionnaires.security.JWTManagerImpl;

import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Optional;

@Log4j2
public class JWTManagerCookiesImpl extends BaseJWTManager { /// implements A1_JWTManager {
  private static final String JWT_TITLE_SUFFIX = "-"; // ""|"; // for Cookie-based JWT

  public JWTManagerCookiesImpl(GeneralProperties generalProperties) {
    super(generalProperties);
  }

  @Override
  public String getUserTokenWrapperFromCarrier(HttpServletRequest request) {
    return extractCookieFromRequest(request, getJwtCookieName())
        .map(Cookie::getValue)
        .orElse(null);
  }

  @Override
  public void addUserTokenToResponse( HttpServletRequest request,
                                      HttpServletResponse response,
                                      String userJwt,
                                      boolean isAnonymous ) {
    Cookie userCookie = deliverUserCookie(userJwt, getJwtCookieName(), request);
    // userCookie.setMaxAge(20); //AZ909 isAnonymous ? 60 * 60 * 24 * 365 : security.getJwtCookieMaxAgeForUser()); // 1 year for Anonymous.
    userCookie.setMaxAge( isAnonymous ?
                          60 * 60 * 24 * 365 :
                          generalProperties.getSecurity().getJwtCookieMaxAgeForUser()); // 1 year for Anonymous.

    response.addCookie(userCookie);
  }

  @Override
  public String getJwtTitleSuffix() {
    return JWT_TITLE_SUFFIX;
  }

  //---/ Custom functionality part: /-------------------------------------------//
  //
  private String getJwtCookieName() {
    return generalProperties.getSecurity().getJwtCookieName();
  }

  private static Optional<Cookie> extractCookieFromRequest(HttpServletRequest request, String cookieName) { //TODO throws UnsupportedEncodingException
    Cookie[] cookies = request.getCookies();
    if ((cookies == null) || (cookies.length == 0)) {
      log.warn("No cookie for JWT Token retrieval.");
      return Optional.empty();
    }
    Optional<Cookie> tokenCookie =
        Arrays.stream(request.getCookies())
            .filter(cookie -> cookieName.equals(cookie.getName().trim()))
            .findFirst();
    if (tokenCookie.isPresent()) {
      log.debug("'{}' present in Cookies. Token: {}", cookieName, tokenCookie.get());
      return tokenCookie;
    }
    return Optional.empty();
  }

  public static Cookie deliverUserCookie(String userJwt, String jwtCookieName, HttpServletRequest request) {
    Cookie userJwtCookie;
    Optional<Cookie> optionalCookie = extractCookieFromRequest(request, jwtCookieName);
    if (optionalCookie.isPresent()) { // Existing cookie
      userJwtCookie = optionalCookie.get();
      userJwtCookie.setValue(userJwt);
    } else { // Create cookie
      userJwtCookie = new Cookie(jwtCookieName, userJwt);
    }
    userJwtCookie.setHttpOnly(true);
    return userJwtCookie;
  }
}