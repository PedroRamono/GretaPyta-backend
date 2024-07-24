package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsersService usersService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOptional = usersService.getUserByLoginName(username);
    if (userOptional.isEmpty()) {
      String langCode = Constants.DEFAULT_LOCALE;
      String localeMess = CommonUtilities.getTranslatableMessage("error_user_of_login_does_not_exist", langCode);
      assert localeMess != null;
      throw new UsernameNotFoundException(localeMess.formatted(username));
    }
    User user = userOptional.get();
    // For future multiple-roles scenario:
    List<String> rolesList = new ArrayList<>();
    rolesList.add(user.getRole());
    String[] strArray = rolesList.toArray(String[] ::new);
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getLoginName())
        .password(user.getPasswordHash())
        // .roles(user.getRole(), "NN") // if multiple roles
        .roles(strArray)
        .build();
  }
}