package com.az.gretapyta.questionnaires.application;

import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.controller.QuestionController;
import com.az.gretapyta.questionnaires.controller.StepController;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.model.StepQuestionLink;
import com.az.gretapyta.questionnaires.repository.QuestionsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Log4j2
@Order(5)
@SpringBootApplication
public class DataLoaderQuestionsApp implements ApplicationRunner {

  // Query strings for finding Steps:
  public static final String STEP_SMD_SURVEY_QUERY = "Which Soc. Media usage step";
  public static final String STEP_PPL_BIO_QUERY = "Person's Bio data step";
  public static final String STEP_PPL_PROF_QUERY = "Person's professional activities step";
  public static final String STEP_POL_US_ELECTIONS_QUERY = "US Presidential Elections 2024 step";
  public static final String STEP_PPO_QUIZ1_QUERY = "Quiz 1 step";

  // 1 Question for 'Social Media' Drawer - DRAWER_CODE_SOCIAL_MEDIA = "SMD":
  public static final String QUESTION_SMD_USAGE = "QUE_SMD_SRV_USG";

  // 3 Questions for 'People' Drawer - DRAWER_CODE_PEOPLE = "PPL":
  public static final String QUESTION_PPL_YOURSELF_SEX = "QUE_PPL_IDS_SEX";
  public static final String QUESTION_PPL_YOURSELF_AGE = "QUE_PPL_IDS_AGE";
  public static final String QUESTION_PPL_YOURSELF_MARITAL = "QUE_PPL_IDS_MRT";

  // 1 Question for 'People' Drawer - DRAWER_CODE_PEOPLE = "PPL":
  public static final String QUESTION_PPL_PROFESSION_HISTORY = "QUE_PPL_PRO_HST";
  public static final String QUESTION_PPL_PROFESSION_STATUS = "QUE_PPL_PRO_STS";

  // 2 Question for 'Politics' Drawer - DRAWER_CODE_POLITICS = "POL":
  public static final String QUESTION_POL_US_PRESIDENT_2024_PREDICT = "QUE_POL_USP_PRD";
  public static final String QUESTION_POL_US_PRESIDENT_2024_WANT = "QUE_POL_USP_WNT";


  // 3 Question for 'Potpourri' Drawer - DRAWER_CODE_POLITICS = "POL":
  public static final String QUESTION_PPO_QUIZ1_RIVER = "QUE_PPO_GEO_RVR";
  public static final String QUESTION_PPO_QUIZ1_MOUNTAIN = "QUE_PPO_GEO_MNT";
  public static final String QUESTION_PPO_QUIZ1_CAPITOL = "QUE_PPO_GEO_CPT";

  public UserDTO USER_ADMINISTRATOR;

  private final StepController stepController;
  private final QuestionsRepository questionsRepository;
  private final QuestionController questionController;
  private final UserController userController;

  @Value("${greta.defaults.load-init-data}")
  private boolean loadInitData;

  public static void main(String[] args) {
    SpringApplication.run(DataLoaderDrawersApp.class, args);
  }

  @Autowired
  public DataLoaderQuestionsApp( StepController stepController,
                                 QuestionsRepository questionsRepository,
                                 QuestionController questionController,
                                 UserController userController) {
    this.stepController = stepController;
    this.questionsRepository = questionsRepository;
    this.questionController = questionController;
    this.userController = userController;
  }

  @Override
  public void run(ApplicationArguments args) {
///    boolean isTest = (dataSourceUrl.contains(TEST_DATABASE_SCHEMA));
    if ((! loadInitData) || (questionsRepository.count() > 0)) {
      return;
    }
    USER_ADMINISTRATOR = userController.getFirstUserFromList("Greta", "Pyta");
    loadData();
    SaveAllItemsOnly();
    saveLinks(); // For creating Links Question->Step.
  }

  private void SaveAllItemsOnly() {
    QuestionDTO[] allItems =
      { FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION,

        FOR_PEOPLE_Q1_STEP1_QUESTION_1,
        FOR_PEOPLE_Q1_STEP1_QUESTION_2,
        FOR_PEOPLE_Q1_STEP1_QUESTION_3,

        FOR_PEOPLE_Q1_STEP2_QUESTION_1,
        FOR_PEOPLE_Q1_STEP2_QUESTION_2,

        FOR_POLITICS_Q1_STEP1_QUESTION_1,
        FOR_POLITICS_Q1_STEP1_QUESTION_2,

        FOR_POTPOURI_Q1_STEP1_QUESTION_1,
        FOR_POTPOURI_Q1_STEP1_QUESTION_2,
        FOR_POTPOURI_Q1_STEP1_QUESTION_3 };

    saveData(null, allItems);
  }

  private void saveLinks() {
    QuestionDTO[] forSocMedArray =
      { FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION };

    QuestionDTO[] forBioDataArray =
      { FOR_PEOPLE_Q1_STEP1_QUESTION_1,
        FOR_PEOPLE_Q1_STEP1_QUESTION_2,
        FOR_PEOPLE_Q1_STEP1_QUESTION_3 };

    QuestionDTO[] forProfActivityArray =
      { FOR_PEOPLE_Q1_STEP2_QUESTION_1,
        FOR_PEOPLE_Q1_STEP2_QUESTION_2 };

    QuestionDTO[] forPoliticsArray =
      { FOR_POLITICS_Q1_STEP1_QUESTION_1,
        FOR_POLITICS_Q1_STEP1_QUESTION_2 };

    QuestionDTO[] forPotpouriArray =
        { FOR_POTPOURI_Q1_STEP1_QUESTION_1,
          FOR_POTPOURI_Q1_STEP1_QUESTION_2,
          FOR_POTPOURI_Q1_STEP1_QUESTION_3 };

    saveData(STEP_SMD_SURVEY_QUERY, forSocMedArray);
    saveData(STEP_PPL_BIO_QUERY, forBioDataArray);
    saveData(STEP_PPL_PROF_QUERY, forProfActivityArray);
    saveData(STEP_POL_US_ELECTIONS_QUERY, forPoliticsArray);
    saveData(STEP_PPO_QUIZ1_QUERY, forPotpouriArray);
  }

  private void saveData(String query, QuestionDTO[] list) {
    int displayOrder = 1;
    for(QuestionDTO dto : list) {
      Optional<QuestionDTO> oQquestion = questionController.fetchDTOFromCode( dto.getCode(),
                                                                              USER_ADMINISTRATOR.getId(),
                                                                              Constants.DEFAULT_LOCALE );
      QuestionDTO questionForLink = (((oQquestion != null) && oQquestion.isPresent()) ? oQquestion.get() : saveDto(dto));

      if (query==null || query.isEmpty()) { // No Link to be created
        continue;
      }

      // Adding to the link Table Question-Step:
      saveLinkEntry(query, questionForLink, displayOrder, 0);
      displayOrder++;
    }
  }

  private QuestionDTO saveDto(QuestionDTO dto) {
    try {
      QuestionDTO newDto = questionController.executeCreateItem(dto, Constants.DEFAULT_LOCALE);
      log.info("New QuestionDTO item was created: ID={}", newDto.getId());
      return newDto;
    } catch (Exception e) {
      log.error("Cannot save QuestionDTO !", e);
      return null;
    }
  }

  private StepQuestionLink saveLinkEntry(String query, QuestionDTO dtoForLink, int displayOrder, int tenantId) {
    Optional<StepDTO> stepDTO = stepController.findByNameMultilangFirstLike(query, USER_ADMINISTRATOR.getId());
    if (stepDTO.isPresent()) {
      try {
        StepQuestionLink newLink = questionController.executeCreateParentChildLink(stepDTO.get(), dtoForLink, displayOrder, tenantId);
        log.info("New StepQuestionLink item was created for Question ID={} linking to Step ID={}.",
            dtoForLink.getId(), stepDTO.get().getId());
        return newLink;
      } catch (Exception e) {
        log.error("\"New StepQuestionLink cannot be created: ", e);
        return null;
      }
    } else {
      log.error("Cannot load Composite Key StepQuestion: Step '{}' cannot be found !", query);
      return null;
    }
  }


  /*
   * Just 1 Question for elements0.put("en", "Which Soc. Media usage step");
   */
  public static QuestionDTO FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION = initCommonDTO();

  /*
   * 3 Question for elements1.put("en", "Person's Bio data step");
   */
  public static QuestionDTO FOR_PEOPLE_Q1_STEP1_QUESTION_1 = initCommonDTO();
  public static QuestionDTO FOR_PEOPLE_Q1_STEP1_QUESTION_2 = initCommonDTO();
  public static QuestionDTO FOR_PEOPLE_Q1_STEP1_QUESTION_3 = initCommonDTO();

  /*
   * 2 Questions for elements2.put("en", "Person's professional activities step");
   */
  public static QuestionDTO FOR_PEOPLE_Q1_STEP2_QUESTION_1 = initCommonDTO();
  public static QuestionDTO FOR_PEOPLE_Q1_STEP2_QUESTION_2 = initCommonDTO();

  /*
   * 2 Questions for elements4.put("en", "US Presidential Elections 2024 step");
   */
  public static QuestionDTO FOR_POLITICS_Q1_STEP1_QUESTION_1 = initCommonDTO();
  public static QuestionDTO FOR_POLITICS_Q1_STEP1_QUESTION_2 = initCommonDTO();

  /*
   * 3 Questions for elements5.put("en", "Quiz 1 step");
   */
  public static QuestionDTO FOR_POTPOURI_Q1_STEP1_QUESTION_1 = initCommonDTO();
  public static QuestionDTO FOR_POTPOURI_Q1_STEP1_QUESTION_2 = initCommonDTO();
  public static QuestionDTO FOR_POTPOURI_Q1_STEP1_QUESTION_3 = initCommonDTO();

  private void loadData() {
    // (1)
    // FOR: elements0.put("en", "Which Soc. Med. usage step");
    Map<String, String> elements0 = new TreeMap<>();
    elements0.put("en", "Which Soc. Med. are you using at present ?");
    elements0.put("pl", "Jakich Mediów Społ. używasz obecnie ?");
    elements0.put("ru", "Какая соц. Мед. ты используешь сейчас ?");
    // jsonNameTranslations = Converters.convertMapToJson(elements1);
    FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setCode(QUESTION_SMD_USAGE);
    // FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setTitleMultilang("");
    FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setQuestionAskedMultilang(elements0);
    FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setAnswerType(AnswerTypes.MULTI_CHOICE.getCode());
    FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setUserId(USER_ADMINISTRATOR.getId());

    // (2)
    // FOR: elements1.put("en", "Person's Bio data step");
    Map<String, String> elements11 = new TreeMap<>();
    elements11.put("en", "What is your gender ?");
    elements11.put("pl", "Jakiej płci jesteś ?");
    elements11.put("ru", "Какого Вы пола ?");
    FOR_PEOPLE_Q1_STEP1_QUESTION_1.setCode(QUESTION_PPL_YOURSELF_SEX);
    // FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setTitleMultilang("");
    FOR_PEOPLE_Q1_STEP1_QUESTION_1.setQuestionAskedMultilang(elements11);
    FOR_PEOPLE_Q1_STEP1_QUESTION_1.setAnswerType(AnswerTypes.RADIO_BUTTONS.getCode());
    FOR_PEOPLE_Q1_STEP1_QUESTION_1.setUserId(USER_ADMINISTRATOR.getId());

    //(3)
    Map<String, String> elements12 = new TreeMap<>();
    elements12.put("en", "What age range are you in ?");
    elements12.put("pl", "W jakim jesteś przedziale wiekowym ?");
    elements12.put("ru", "В каком возрастном диапазоне вы находитесь ?");
    FOR_PEOPLE_Q1_STEP1_QUESTION_2.setCode(QUESTION_PPL_YOURSELF_AGE);
    // FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setTitleMultilang("");
    FOR_PEOPLE_Q1_STEP1_QUESTION_2.setQuestionAskedMultilang(elements12);
    FOR_PEOPLE_Q1_STEP1_QUESTION_2.setAnswerType(AnswerTypes.LIST_CHOICE.getCode());
    FOR_PEOPLE_Q1_STEP1_QUESTION_2.setUserId(USER_ADMINISTRATOR.getId());

    //(4)
    Map<String, String> elements13 = new TreeMap<>();
    elements13.put("en", "Are you married ?");
    elements13.put("pl", "Czy jesteś w związku małżeńskim ?");
    elements13.put("ru", "Ты женат ?");
    FOR_PEOPLE_Q1_STEP1_QUESTION_3.setCode(QUESTION_PPL_YOURSELF_MARITAL);
    // FOR_SOCIAL_MEDIA_Q1_STEP1_QUESTION.setTitleMultilang("");
    FOR_PEOPLE_Q1_STEP1_QUESTION_3.setQuestionAskedMultilang(elements13);
    FOR_PEOPLE_Q1_STEP1_QUESTION_3.setAnswerType(AnswerTypes.LIST_CHOICE.getCode());
    FOR_PEOPLE_Q1_STEP1_QUESTION_3.setUserId(USER_ADMINISTRATOR.getId());

    // (5)
    // FOR: elements2.put("en", "Person's professional activities step");
    Map<String, String> elements21 = new TreeMap<>();
    elements21.put("en", "How many years of employment do you have ?");
    elements21.put("pl", "Ile lat pracy zawodowej posiadasz ?");
    elements21.put("ru", "Сколько лет у вас стажа работы ?");
    FOR_PEOPLE_Q1_STEP2_QUESTION_1.setCode(QUESTION_PPL_PROFESSION_HISTORY);
    // FOR_PEOPLE_Q1_STEP2_QUESTION_1.setTitleMultilang("");
    FOR_PEOPLE_Q1_STEP2_QUESTION_1.setQuestionAskedMultilang(elements21);
    FOR_PEOPLE_Q1_STEP2_QUESTION_1.setAnswerType(AnswerTypes.NUMBER_INTEGER.getCode());
    FOR_PEOPLE_Q1_STEP2_QUESTION_1.setUserId(USER_ADMINISTRATOR.getId());

    //(6)
    Map<String, String> elements22 = new TreeMap<>();
    elements22.put("en", "Are you currently employed ?");
    elements22.put("pl", "Czy na obecną chwilę jesteś zatrudniony(a) ?");
    elements22.put("ru", "Ты трудоустроен в данный момент ?");
    FOR_PEOPLE_Q1_STEP2_QUESTION_2.setCode(QUESTION_PPL_PROFESSION_STATUS);
    // FOR_PEOPLE_Q1_STEP2_QUESTION_1.setTitleMultilang("");
    FOR_PEOPLE_Q1_STEP2_QUESTION_2.setQuestionAskedMultilang(elements22);
    FOR_PEOPLE_Q1_STEP2_QUESTION_2.setAnswerType(AnswerTypes.RADIO_BUTTONS.getCode());
    FOR_PEOPLE_Q1_STEP2_QUESTION_2.setUserId(USER_ADMINISTRATOR.getId());

    //(7) FOR: elements4.put("en", "US Presidential Elections 2024 step");
    Map<String, String> elements23 = new TreeMap<>();
    elements23.put("en", "Who will be the next President of USA ?");
    elements23.put("pl", "Kto zostanie następnym Prezydentem USA ?");
    elements23.put("ru", "Кто станет следующим президентом США ?");
    FOR_POLITICS_Q1_STEP1_QUESTION_1.setCode(QUESTION_POL_US_PRESIDENT_2024_PREDICT);
    // FOR_PEOPLE_Q1_STEP2_QUESTION_1.setTitleMultilang("");
    FOR_POLITICS_Q1_STEP1_QUESTION_1.setQuestionAskedMultilang(elements23);
    FOR_POLITICS_Q1_STEP1_QUESTION_1.setAnswerType(AnswerTypes.RADIO_BUTTONS.getCode());
    FOR_POLITICS_Q1_STEP1_QUESTION_1.setUserId(USER_ADMINISTRATOR.getId());

    //(8)
    Map<String, String> elements24 = new TreeMap<>();
    elements24.put("en", "Who would you prefer to be the next President of USA ?");
    elements24.put("pl", "Kogo chciałbyś/chciałabyś żeby został następnym Prezydentem USA ?");
    elements24.put("ru", "Кого бы вы предпочли стать следующим президентом США ?");
    FOR_POLITICS_Q1_STEP1_QUESTION_2.setCode(QUESTION_POL_US_PRESIDENT_2024_WANT);
    // FOR_PEOPLE_Q1_STEP1_QUESTION_2.setTitleMultilang("");
    FOR_POLITICS_Q1_STEP1_QUESTION_2.setQuestionAskedMultilang(elements24);
    FOR_POLITICS_Q1_STEP1_QUESTION_2.setAnswerType(AnswerTypes.RADIO_BUTTONS.getCode());
    FOR_POLITICS_Q1_STEP1_QUESTION_2.setUserId(USER_ADMINISTRATOR.getId());

    //(9)
    Map<String, String> elements25 = new TreeMap<>();
    elements25.put("en", "What is the longest river in Asia ?");
    elements25.put("pl", "Jaka jest najdłuższa rzeka w Azji ?");
    elements25.put("ru", "Какая самая длинная река в Азии ?");
    FOR_POTPOURI_Q1_STEP1_QUESTION_1.setCode(QUESTION_PPO_QUIZ1_RIVER);
    // FOR_POTPOURI_Q1_STEP1_QUESTION_1.setTitleMultilang("");
    FOR_POTPOURI_Q1_STEP1_QUESTION_1.setQuestionAskedMultilang(elements25);
    FOR_POTPOURI_Q1_STEP1_QUESTION_1.setAnswerType(AnswerTypes.RADIO_BUTTONS.getCode());
    FOR_POTPOURI_Q1_STEP1_QUESTION_1.setUserId(USER_ADMINISTRATOR.getId());

    //(10)
    Map<String, String> elements26 = new TreeMap<>();
    elements26.put("en", "What is the tallest mountain in South America ?");
    elements26.put("pl", "Jaki jest najwyższy szczyt górski w Ameryce Południowej ?");
    elements26.put("ru", "Какая самая высокая гора в Южной Америке ?");
    FOR_POTPOURI_Q1_STEP1_QUESTION_2.setCode(QUESTION_PPO_QUIZ1_MOUNTAIN);
    // FOR_POTPOURI_Q1_STEP1_QUESTION_2.setTitleMultilang("");
    FOR_POTPOURI_Q1_STEP1_QUESTION_2.setQuestionAskedMultilang(elements26);
    FOR_POTPOURI_Q1_STEP1_QUESTION_2.setAnswerType(AnswerTypes.RADIO_BUTTONS.getCode());
    FOR_POTPOURI_Q1_STEP1_QUESTION_2.setUserId(USER_ADMINISTRATOR.getId());

    //(11)
    Map<String, String> elements27 = new TreeMap<>();
    elements27.put("en", "What is the the capital of Senegal (Africa) ?");
    elements27.put("pl", "Jakie miasto jest stolicą Senegalu (Afryka) ?");
    elements27.put("ru", "Какая столица Сенегала (Африка) ?");
    FOR_POTPOURI_Q1_STEP1_QUESTION_3.setCode(QUESTION_PPO_QUIZ1_CAPITOL);
    // FOR_POTPOURI_Q1_STEP1_QUESTION_3.setTitleMultilang("");
    FOR_POTPOURI_Q1_STEP1_QUESTION_3.setQuestionAskedMultilang(elements27);
    FOR_POTPOURI_Q1_STEP1_QUESTION_3.setAnswerType(AnswerTypes.RADIO_BUTTONS.getCode());
    FOR_POTPOURI_Q1_STEP1_QUESTION_3.setUserId(USER_ADMINISTRATOR.getId());
  }

  private static QuestionDTO initCommonDTO() {
    QuestionDTO ret = new QuestionDTO();
    ret.setReady2Show(true);
    ret.setCreated(LocalDateTime.now());
    ret.setUpdated(LocalDateTime.now());
    return ret;
  }
}