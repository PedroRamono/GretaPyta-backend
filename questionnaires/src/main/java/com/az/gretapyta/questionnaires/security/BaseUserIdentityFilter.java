package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.questionnaires.configurationproperties.GeneralProperties;
import com.az.gretapyta.questionnaires.security.JWTManagerImpl.JWTManagerCookiesImpl;
import com.az.gretapyta.questionnaires.security.JWTManagerImpl.JWTManagerHeadersImpl;
import com.az.gretapyta.questionnaires.util.JwtTokenUtil;
import jakarta.persistence.MappedSuperclass;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Optional;

@Log4j2
@Component
@MappedSuperclass
public abstract class BaseUserIdentityFilter extends OncePerRequestFilter {

  /* TODO Encryption/decryption of Cookie.
  @Autowired
  CipherUtility cipherUtility;
  */

  @Autowired
  protected JwtTokenUtil jwtTokenUtil;

  @Autowired
  protected GeneralProperties generalProperties;

  @Bean
  public JWTManager jwtManager() {
    return (generalProperties.getSecurity().getJwtIsCookieBased() ?
        new JWTManagerCookiesImpl(generalProperties) :
        new JWTManagerHeadersImpl(generalProperties));
  }

  protected Optional<JWToken> getUserTokenFromRequest(HttpServletRequest request) {
    String tokenCarrier = jwtManager().getUserTokenWrapperFromCarrier(request);
    if ((tokenCarrier == null) || tokenCarrier.isEmpty()) {
      return Optional.empty();
    }
    return jwtManager().getValidatedToken(tokenCarrier);
  }
}