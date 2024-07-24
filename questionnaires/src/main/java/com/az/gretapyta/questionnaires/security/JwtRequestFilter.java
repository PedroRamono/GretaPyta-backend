package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.service2.UsersService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Log4j2
@Component
public class JwtRequestFilter extends BaseUserIdentityFilter { // OncePerRequestFilter {

  @Autowired
  private UsersService usersService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    String method = request.getMethod(); // GET/POST/PUT/DELETE

    boolean excludeUrl = (path.endsWith(APIController.USER_IDENTITY_URL + APIController.LOGIN_API)
                       ||
                         (path.endsWith(APIController.USER_IDENTITY_URL + APIController.LOGOUT_API))) ;
    boolean excludeMethod = false; // method.equals("POST");

    return (excludeUrl || excludeMethod);
  }

  @Override
  protected void doFilterInternal( @Nonnull HttpServletRequest request,
                                   @Nonnull HttpServletResponse response,
                                   @Nonnull FilterChain chain ) throws ServletException, IOException {
    Optional<JWToken> optJwtToken = getUserTokenFromRequest(request);
    String userIdentifier = null;
    String jwtHashValue = null;
    if (optJwtToken.isPresent()) {
      try {
        userIdentifier = getUserIdentifierFromToken(optJwtToken.get().contentHash());
        jwtHashValue = optJwtToken.get().contentHash();
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }

    //Once we get the token validate it.
    if ((userIdentifier != null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
      User user = getUserFromIdentifier(userIdentifier); // usersService.getItemById(userId); //.orElseThrow(NotFoundException::new);

      // if token is valid configure Spring Security to manually set authentication
      if (jwtTokenUtil.validateTokenById(jwtHashValue, user.getId())) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken( user,
                null,
                user.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      } else {
        log.warn("Identifier provided by JWT Token and User Identifier don't match.");
      }
    }
    chain.doFilter(request, response);
  }

  private User getUserFromIdentifier(String userIdentifier ) {
    //By loginName:
    // User user = usersService.getUserByLoginName(username).orElseThrow(NotFoundException::new);
    //By User ID:
    Integer userId = Integer.valueOf(userIdentifier); //AZ808
    return usersService.getItemById(userId); //.orElseThrow(NotFoundException::new);
  }

  private String getUserIdentifierFromToken(String contentHash) throws Exception {
    try {
      return jwtTokenUtil.getSubjectIdentifierFromToken(contentHash);
    } catch (IllegalArgumentException e) {
      log.warn("Unable to get JWT Token");
      throw new IllegalArgumentException("Unable to get JWT Token", e);
    } catch (ExpiredJwtException e) {
      log.warn("JWT Token has expired");
      throw e; // new ExpiredJwtException(e);
    }
  }
}