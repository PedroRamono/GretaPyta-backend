package com.az.gretapyta.questionnaires.application;

import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.controller.DrawerController;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.dto.DrawerDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.repository.DrawersRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Log4j2
@Order(2)
@SpringBootApplication
public class DataLoaderDrawersApp implements ApplicationRunner {
  public static final String TEST_DATABASE_SCHEMA = "gretapyta-test";

  public static final String DRAWER_CODE_COMMUNITIES = "DRW_COM";
  public static final String DRAWER_CODE_SOCIAL_MEDIA = "DRW_SMD";
  public static final String DRAWER_CODE_POLITICS = "DRW_POL";

  public static final String DRAWER_CODE_POTPOURRI = "DRW_PPU";
  public static final String DRAWER_CODE_PEOPLE = "DRW_PPL";

  private final DrawersRepository drawersRepository;
  private final DrawerController drawerController;
  private final UserController userController;

  @Value("${greta.defaults.load-init-data}")
  private boolean loadInitData;

  public static void main(String[] args) {
    SpringApplication.run(DataLoaderDrawersApp.class, args);
  }

  @Autowired
  public DataLoaderDrawersApp(DrawersRepository drawersRepository,
                              DrawerController drawerController,
                              UserController userController) {
    this.drawersRepository = drawersRepository;
    this.drawerController = drawerController;
    this.userController = userController;
  }

  @Override
  public void run(ApplicationArguments args) {
    if ((! loadInitData) || (drawersRepository.count() > 0)) {
      return;
    }
    log.debug("Loading Drawers: {}", DEMO_DRAWERS_DTO.size());
    loadData();
    for(DrawerDTO dto : DEMO_DRAWERS_DTO) {
      try {
        DrawerDTO newDto = drawerController.executeCreateItem(dto, Constants.DEFAULT_LOCALE);
        log.info("New DrawerDTO item was created: ID={}", newDto.getId());
        // return newDto;
      } catch (Exception e) {
        log.error(e);
        log.error("Cannot save DrawerDTO !");
        // return null;
      }
    }

    // testToString(); //TEST
  }

  private void testToString() {
    loadData();
    DEMO_DRAWERS_DTO.forEach(n -> log.info("===> {}", n.toString()));
  }

  private void loadData() {
    UserDTO USER_ADMINISTRATOR = userController.getFirstUserFromList("Greta", "Pyta");

    // (1)
    Map<String, String> elements1 = new TreeMap<>();
    elements1.put("en", "Social Media");
    elements1.put("pl", "Media Społecznościowe");
    elements1.put("ru", "Социальные медиа");
    // jsonNameTranslations = Converters.convertMapToJson(elements1);
    SOCIAL_MEDIA_DTO.setCode(DRAWER_CODE_SOCIAL_MEDIA);
    SOCIAL_MEDIA_DTO.setNameMultilang(elements1);
    SOCIAL_MEDIA_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (2)
    Map<String, String> elements2 = new TreeMap<>();
    elements2.put("en", "Politics");
    elements2.put("pl", "Polityka");
    elements2.put("ru", "Πолитика");
    // jsonNameTranslations = Converters.convertMapToJson(elements2);
    POLITICS_DTO.setCode(DRAWER_CODE_POLITICS);
    POLITICS_DTO.setNameMultilang(elements2);
    POLITICS_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (3)
    Map<String, String> elements3 = new TreeMap<>();
    elements3.put("en", "Communities");
    elements3.put("pl", "Społeczności");
    elements3.put("ru", "Сообщества");
    // jsonNameTranslations = Converters.convertMapToJson(elements3);
    COMMUNITIES_DTO.setCode(DRAWER_CODE_COMMUNITIES);
    COMMUNITIES_DTO.setNameMultilang(elements3);
    COMMUNITIES_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (4)
    Map<String, String> elements4 = new TreeMap<>();
    elements4.put("en", "Potpourri");
    elements4.put("pl", "Takie-tam-głupotki");
    elements4.put("ru", "Сообщества");
    // jsonNameTranslations = Converters.convertMapToJson(elements4);
    POT_POURI_DTO.setCode(DRAWER_CODE_POTPOURRI);
    POT_POURI_DTO.setNameMultilang(elements4);
    POT_POURI_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (5)
    Map<String, String> elements5 = new TreeMap<>();
    elements5.put("en", "People");
    elements5.put("pl", "Ludzie");
    elements5.put("ru", "Люди");
    // jsonNameTranslations = Converters.convertMapToJson(elements5);
    PEOPLE_DTO.setCode(DRAWER_CODE_PEOPLE);
    PEOPLE_DTO.setNameMultilang(elements5);
    PEOPLE_DTO.setUserId(USER_ADMINISTRATOR.getId());
  }

  public static DrawerDTO SOCIAL_MEDIA_DTO = initCommonDTO();
  public static DrawerDTO POLITICS_DTO = initCommonDTO();
  public static DrawerDTO POT_POURI_DTO = initCommonDTO();
  public static DrawerDTO COMMUNITIES_DTO = initCommonDTO();
  public static DrawerDTO PEOPLE_DTO = initCommonDTO();

  public static final List<DrawerDTO> DEMO_DRAWERS_DTO =
      List.of( SOCIAL_MEDIA_DTO,
          POLITICS_DTO,
          COMMUNITIES_DTO,
          POT_POURI_DTO,
          PEOPLE_DTO
      );

  public static DrawerDTO initCommonDTO() {
    DrawerDTO ret = new DrawerDTO();
    ret.setReady2Show(true);
    ret.setCreated(LocalDateTime.now());
    ret.setUpdated(LocalDateTime.now());
    return ret;
  }
}