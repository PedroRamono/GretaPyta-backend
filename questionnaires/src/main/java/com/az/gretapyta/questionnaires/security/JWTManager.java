package com.az.gretapyta.questionnaires.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface JWTManager {

  String getUserTokenWrapperFromCarrier(HttpServletRequest request);
  void addUserTokenToResponse( HttpServletRequest request,
                                               HttpServletResponse response,
                                               String userJwt,
                                               boolean isAnonymous );
  Optional<JWToken> getValidatedToken(String jwtToken);
  String composeUserJwt(String tokenContent);
  String getJwtTitleSuffix();
}