package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.service2.UsersService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class UserIdentityAssignFilter extends BaseUserIdentityFilter {

  @Autowired
  private UsersService service;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    String method = request.getMethod(); // GET/POST/PUT/DELETE

    boolean excludeUrl = (path.endsWith(APIController.USER_IDENTITY_URL + APIController.LOGIN_API));
//        ||
//        (path.endsWith(APIController.USER_IDENTITY_URL + APIController.LOGOUT_API))) ;
    boolean excludeMethod = false; // method.equals("POST");

    return (excludeUrl || excludeMethod);
  }

  @Override
  protected void doFilterInternal( @Nonnull HttpServletRequest request,
                                   @Nonnull HttpServletResponse response,
                                   @Nonnull FilterChain chain) throws ServletException, IOException {

    // Cookie[] cookies = request.getCookies();

    if (needsToCreateAnonymousUserToken(request)) {
      String userJwt = createAnonymousUserJwtTokenAsString(request);
      if ( ! StringUtils.isBlank(userJwt)) {
        jwtManager().addUserTokenToResponse( request, response, userJwt, true );
      }
    }
    chain.doFilter(request, response);
  }

  private boolean needsToCreateAnonymousUserToken(HttpServletRequest request) {
    if (isLogoutRequest(request)) {
      return true;
    }
    Optional<JWToken> optJwtToken = getUserTokenFromRequest(request);
    return optJwtToken.isEmpty();
  }

  private boolean isLogoutRequest(HttpServletRequest request) {
    String path = request.getRequestURI();
    String method = request.getMethod(); // GET/POST/PUT/DELETE
    return (path.endsWith(APIController.USER_IDENTITY_URL + APIController.LOGOUT_API));
  }

  private String createAnonymousUserJwtTokenAsString(HttpServletRequest request) {
    User user = service.getItemByAnonymousFlag(getRequestLocale(request).getLanguage());
    if (user==null) {
      return null;
    }
    String tokenContent = jwtTokenUtil.generateTokenWithId(user.getId());
    return jwtManager().composeUserJwt(tokenContent);
  }

  private Locale getRequestLocale(HttpServletRequest request) {
    Enumeration<Locale> localeList = request.getLocales();
    Iterator<Locale> itr = localeList.asIterator(); // .h
    Locale requestLocale = null;
    Map<String, Locale> locales = getRegisteredLocales();
    while(itr.hasNext()) {
      Locale element = itr.next();
      if (locales.containsKey(element.getLanguage())) {
        requestLocale = element;
      }
    }
    return (requestLocale==null ? new Locale(Constants.DEFAULT_LOCALE) : requestLocale);
  }

  public static Map<String, Locale> getRegisteredLocales() {
    Locale locale1 = new Locale("en");
    Locale locale2 = new Locale("pl");
    Locale locale3 = new Locale("ru");

    Map<String, Locale> ret = new HashMap<>();
    // Set: ret.addAll( Arrays.stream((new Locale[] {locale1, locale2, locale3})).toList());
    ret.put(locale1.getLanguage(), locale1);
    ret.put(locale2.getLanguage(), locale2);
    ret.put(locale3.getLanguage(), locale3);
    return ret;
  }
}