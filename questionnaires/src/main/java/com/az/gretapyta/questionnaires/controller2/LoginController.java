package com.az.gretapyta.questionnaires.controller2;

import com.az.gretapyta.qcore.controller.APIController;
import com.az.gretapyta.qcore.controller.BaseController;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.security.JWTManager;
import com.az.gretapyta.questionnaires.security.LoginInfo;
import com.az.gretapyta.questionnaires.util.JwtTokenUtil;
import com.az.gretapyta.questionnaires.service2.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Log4j2
@RestController
@RequestMapping(value = APIController.USER_IDENTITY_URL)
@RequiredArgsConstructor
@CrossOrigin // for Authentication
public class LoginController extends BaseController {

  private final UsersService service;

  // @Autowired
  private final AuthenticationManager authenticationManager;

  // @Autowired
  private final JwtTokenUtil jwtTokenUtil;

  @Autowired
  protected JWTManager jwtManager;

  @RequestMapping(value = APIController.LOGIN_API, method = RequestMethod.POST)
  //AZ OK: public ResponseEntity<?> createAuthenticationToken(
  public ResponseEntity<HttpStatus> createAuthenticationToken(
      @RequestBody LoginInfo authenticationRequest,
      HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    authenticate(authenticationRequest.loginName(), authenticationRequest.pin());
    final User user = service.getUserByLoginName(authenticationRequest.loginName())
          .orElseThrow(NotFoundException::new);

    String tokenContent = jwtTokenUtil.generateTokenWithId(user);
    String userJwt = jwtManager.composeUserJwt(tokenContent);
    jwtManager.addUserTokenToResponse(request, response, userJwt,false);

    //OK: return ResponseEntity.ok(new JwtResponse(tokenContent));
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @RequestMapping(value = APIController.LOGOUT_API, method = RequestMethod.POST) // .DELETE)
  public ResponseEntity<HttpStatus> logOutUser(
      HttpServletRequest request,
      HttpServletResponse response) {

    // Do nothing - the Filter will create Anonymous user JWT and pass it to Response.

    return ResponseEntity.ok(HttpStatus.OK);
  }

    //---/ Servicing part /------------------------------------------------//
  private void authenticate(String loginName, String password) throws Exception {
    Objects.requireNonNull(loginName);
    Objects.requireNonNull(password);
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginName, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
  //---/ Servicing part /------------------------------------------------//
}