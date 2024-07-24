package com.az.gretapyta.questionnaires.application;

import com.az.gretapyta.qcore.enums.GenderTypes;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.mapper2.UserMapper;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.repository2.UsersRepository;
import com.az.gretapyta.questionnaires.security.UserRoles;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.List;

/*
 * To load Anonymous Users (no login, credential check required) and some sample Users.
 *
 */

@Log4j2
@Order(6)
@SpringBootApplication
public class DataLoader2SampleUsers implements ApplicationRunner {
  private final UsersRepository usersRepository;
  private final UserController userController;
  private final UserMapper userMapper;

  @Value("${greta.defaults.load-init-data}")
  private boolean loadInitData;

  private UserDTO USER_SAMPLE1_DTO_EN;

  @Autowired
  public DataLoader2SampleUsers( UsersRepository usersRepository,
                                 UserController userController,
                                 UserMapper userMapper) {

    this.usersRepository = usersRepository;
    this.userController = userController;
    this.userMapper = userMapper;

  }

  public static void main(String[] args) {
    SpringApplication.run(DataLoader2SampleUsers.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
///    boolean isTest = (dataSourceUrl.contains(TEST_DATABASE_SCHEMA));
    if ((! loadInitData) || (usersRepository.count() > 0)) {
      return;
    }
    /* //AZ909 */
    log.info("Loading anonymous Users and sample Users: {}", ITEMS.size());
    // ITEMS.forEach(user -> this.saveUser(userMapper.map(user)));
    for (User user : ITEMS) {
      UserDTO dto = userMapper.map(user);
      dto.setPasswordHash(user.getPasswordHash());
      saveUser(dto);
    }
  }

  private UserDTO saveUser(UserDTO dto) {
    try {
      UserDTO newObj = userController.executeCreateItem(dto, dto.getPreferredLang());
      log.info("New UserDTO item was created: ID={}", newObj.getId());
      return newObj;
    } catch (Exception e) {
      log.error(e);
      log.error("Cannot save UserDTO for '{}' !", dto.getLoginName(), e);
      return null;
    }
  }

  public static User USER_ANONYMOUS_EN = User.createAnonymousUserForLanguage("en");
  public static User USER_ANONYMOUS_PL = User.createAnonymousUserForLanguage("pl");
  public static User USER_ANONYMOUS_RU = User.createAnonymousUserForLanguage("ru");

  public static User ADMINISTRATOR_EN =
      User.createUser("Greta",
          "Pyta",
          GenderTypes.NOT_DECLARED.getCode(),
          null,
          "gretapyta1984@sympatico.ca",
          "adminGreta",
          "!Bzyk4Ever!",
          "en",
          false,
          UserRoles.ADMIN.getCode());

  public static User USER_SAMPLE_CASUAL_EN =
      User.createUser("Johny",
                 "Walker",
                  GenderTypes.MALE.getCode(),
                  LocalDate.of(1987, 12, 31),
                 "johny_walker841@yahoo.com",
                 "JohnyWalker333",
                 "password123",
                 "en",
                 false,
                  UserRoles.USER_CASUAL.getCode());

  public static User USER_SAMPLE_CLIENT_PL =
      User.createUser("Wanda",
                 "Trzeboszewska",
                  GenderTypes.FEMALE.getCode(),
                  LocalDate.of(1999, 6, 1),
                 "wandatrzebsz6@gmail.com",
                 "wandamiranda",
                 "password999",
                 "pl",
                 false,
                  UserRoles.USER_CLIENT.getCode());

  public static final List<User> ITEMS =
      List.of( USER_ANONYMOUS_EN,
               USER_ANONYMOUS_PL,
               USER_ANONYMOUS_RU,

               ADMINISTRATOR_EN,
               USER_SAMPLE_CASUAL_EN,
               USER_SAMPLE_CLIENT_PL);
}