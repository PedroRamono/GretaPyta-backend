package com.az.gretapyta.questionnaires.application;

import static com.az.gretapyta.questionnaires.application.DataLoaderDrawersApp.*;

import com.az.gretapyta.qcore.enums.QuestionnaireTypes;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.controller.QuestionnaireController;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.model.Drawer;
import com.az.gretapyta.questionnaires.repository.QuestionnairesRepository;
import com.az.gretapyta.questionnaires.service.DrawersService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Order(2)
@Log4j2
@SpringBootApplication
public class DataLoaderQuestionnairesApp implements ApplicationRunner {

  // 1 Questionnaire for 'Social Media' Drawer - DRAWER_CODE_SOCIAL_MEDIA = "SMD":
  public static final String QUESTNR_SMD_SURVEY = "QST_SMD_SRV";

  // 3 Questionnaires for 'People' Drawer - DRAWER_CODE_PEOPLE = "PPL":
  public static final String QUESTNR_PPL_IDENTIFY_YOURSELF = "GST_PPL_IDS";
  public static final String QUESTNR_PPL_FAVORITIES = "QST_PPL_FAV";
  public static final String QUESTNR_PPL_PLANS = "QST_PPL_PLN";

  public static final String QUESTNR_POL_US_2024_ELECTIONS = "QST_POL_USP";

  public static final String QUESTNR_POL_US_2024_ELECTIONS_URL_ID = "uselections2024";

  public static final String QUESTNR_PPO_QUIZ = "QST_PPO_QZ1";

  private final DrawersService drawersService;
  private final QuestionnaireController questionnaireController;

  private final QuestionnairesRepository questionnairesRepository;

  @Value("${spring.datasource.url}")
  private String dataSourceUrl;

  public static void main(String[] args) {
    SpringApplication.run(DataLoaderQuestionnairesApp.class, args);
  }

  @Autowired
  public DataLoaderQuestionnairesApp(DrawersService drawersService,
                                     QuestionnairesRepository questionnairesRepository,
                                     QuestionnaireController questionnaireController) {
    this.drawersService = drawersService;
    this.questionnairesRepository = questionnairesRepository;
    this.questionnaireController = questionnaireController;
  }

  @Override
  public void run(ApplicationArguments args) {
    boolean isTest = (dataSourceUrl.contains(TEST_DATABASE_SCHEMA));
    if (isTest || (questionnairesRepository.count() > 0)) {
      return;
    }
    /* //AZ909 */
    loadData();
    saveAllItems();

  }

  private void saveAllItems() {
    QuestionnaireDTO[] forSocialMediaArray =
      { SOCIAL_MEDIA1_DTO };

    QuestionnaireDTO[] forPeopleArray =
      { PEOPLE1_DTO,
        PEOPLE2_DTO,
        PEOPLE3_DTO };

    QuestionnaireDTO[] forPoliticsArray =
      { POLITICS1_DTO };

    QuestionnaireDTO[] forPotPouriArray =
        { POTPOURI_QUIZ_DTO };

    saveData(DRAWER_CODE_SOCIAL_MEDIA, forSocialMediaArray);
    saveData(DRAWER_CODE_PEOPLE, forPeopleArray);
    saveData(DRAWER_CODE_POLITICS, forPoliticsArray);
    saveData(DRAWER_CODE_POTPOURRI, forPotPouriArray);
  }

  private void saveData(String drawerCode, QuestionnaireDTO[] quest) {
    for (QuestionnaireDTO dto : quest) {
      Optional<Drawer> opt = drawersService.getItemByCode(drawerCode);
      if ((opt != null) && opt.isPresent()) {
        Drawer drawer = opt.get();
        dto.setDrawer(drawer);
        try {
          QuestionnaireDTO newDto = questionnaireController.executeCreateItem(dto, Constants.DEFAULT_LOCALE);
          log.info("New QuestionnaireDTO item was created: ID={}", newDto.getId());
          // return newDto;
        } catch (Exception e) {
          log.error(e);
          log.error("Cannot save QuestionnaireDTO for Drawer ID={} !", drawer.getId());
          // return null;
        }
      } else {
        log.error("Cannot load Questionnaires: Drawer '{}' cannot be found !", drawerCode);
      }
    }
  }

  public static QuestionnaireDTO SOCIAL_MEDIA1_DTO = initCommonDTO();
  public static QuestionnaireDTO PEOPLE1_DTO = initCommonDTO();
  public static QuestionnaireDTO PEOPLE2_DTO = initCommonDTO();
  public static QuestionnaireDTO PEOPLE3_DTO = initCommonDTO();
  public static QuestionnaireDTO POLITICS1_DTO = initCommonDTO();
  public static QuestionnaireDTO POTPOURI_QUIZ_DTO = initCommonDTO();

  private void loadData() {
    // (1)
    Map<String, String> elements0 = new TreeMap<>();
    elements0.put("en", "Social Media Survey");
    elements0.put("pl", "Ankieta nt. Mediów Społecznościowych");
    elements0.put("ru", "Опрос в социальных сетях");
    // jsonNameTranslations = Converters.convertMapToJson(elements1);
    SOCIAL_MEDIA1_DTO.setCode(QUESTNR_SMD_SURVEY);
    SOCIAL_MEDIA1_DTO.setQuestionnaireType(QuestionnaireTypes.SURVEY);
    SOCIAL_MEDIA1_DTO.setNameMultilang(elements0);

    //(2)
    Map<String, String> elements1 = new TreeMap<>();
    elements1.put("en", "Tell us about you");
    elements1.put("pl", "Powiedz coś o sobie");
    elements1.put("ru", "Расскажи нам о себе");
    PEOPLE1_DTO.setCode(QUESTNR_PPL_IDENTIFY_YOURSELF);
    PEOPLE1_DTO.setQuestionnaireType(QuestionnaireTypes.ONBOARDING);
    PEOPLE1_DTO.setNameMultilang(elements1);

    //(3)
    Map<String, String> elements2 = new TreeMap<>();
    elements2.put("en", "Tell us about your favorities");
    elements2.put("pl", "Powiedz co lubisz");
    elements2.put("ru", "Расскажите нам о своих предпочтениях");
    PEOPLE2_DTO.setCode(QUESTNR_PPL_FAVORITIES);
    PEOPLE2_DTO.setQuestionnaireType(QuestionnaireTypes.SURVEY);
    PEOPLE2_DTO.setNameMultilang(elements2);

    //(4)
    Map<String, String> elements3 = new TreeMap<>();
    elements3.put("en", "What are your plans ?");
    elements3.put("pl", "Jakie są Twoje plany ?");
    elements3.put("ru", "Каковы ваши планы ?");
    PEOPLE3_DTO.setCode(QUESTNR_PPL_PLANS);
    PEOPLE3_DTO.setQuestionnaireType(QuestionnaireTypes.SURVEY);
    PEOPLE3_DTO.setNameMultilang(elements3);

    //(5)
    Map<String, String> elements4 = new TreeMap<>();
    elements4.put("en", "US Presidential Elections 2024 - who ?");
    elements4.put("pl", "Wybory prezydenckie USA 2024 - kto ?");
    elements4.put("ru", "Президентские выборы в США 2024 года – кто ?");
    POLITICS1_DTO.setCode(QUESTNR_POL_US_2024_ELECTIONS);
    POLITICS1_DTO.setQuestionnaireType(QuestionnaireTypes.PREDICTION);
    POLITICS1_DTO.setNameMultilang(elements4);
    POLITICS1_DTO.setUrlIdName(QUESTNR_POL_US_2024_ELECTIONS_URL_ID);

    //(6) Quiz
    Map<String, String> elements5 = new TreeMap<>();
    elements5.put("en", "The quiz no. 1");
    elements5.put("pl", "Zgadywanka nr. 1");
    elements5.put("ru", "Викторина номер 1");
    POTPOURI_QUIZ_DTO.setCode(QUESTNR_PPO_QUIZ);
    POTPOURI_QUIZ_DTO.setQuestionnaireType(QuestionnaireTypes.QUIZ);
    POTPOURI_QUIZ_DTO.setNameMultilang(elements5);
  }

  private void testToString() {
    loadData();
    QuestionnaireDTO[] socMedArray = {SOCIAL_MEDIA1_DTO};
    QuestionnaireDTO[] peopleArray = {PEOPLE1_DTO, PEOPLE2_DTO, PEOPLE3_DTO};
    QuestionnaireDTO[] politicsArray = {POLITICS1_DTO};
    QuestionnaireDTO[] ptPouriArray = {POTPOURI_QUIZ_DTO};

    Arrays.stream(socMedArray).forEach(n -> logInfoLine(n.toString()));
    Arrays.stream(peopleArray).forEach(n -> logInfoLine(n.toString()));
    Arrays.stream(politicsArray).forEach(n -> logInfoLine(n.toString()));
    Arrays.stream(ptPouriArray).forEach(n -> logInfoLine(n.toString()));
  }

  public static void logInfoLine(String message) {
    log.info("===> {}", message);
  }

  private static QuestionnaireDTO initCommonDTO() {
    QuestionnaireDTO ret = new QuestionnaireDTO();
    ret.setReady2Show(false);
    ret.setCreated(LocalDateTime.now());
    ret.setUpdated(LocalDateTime.now());
    return ret;
  }
}