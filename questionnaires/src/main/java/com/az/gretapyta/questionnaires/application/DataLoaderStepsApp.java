package com.az.gretapyta.questionnaires.application;

import static com.az.gretapyta.questionnaires.application.DataLoaderQuestionnairesApp.*;

import com.az.gretapyta.qcore.util.Constants;

import com.az.gretapyta.questionnaires.controller.QuestionnaireController;
import com.az.gretapyta.questionnaires.controller.StepController;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.repository.StepsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.*;

@Order(4)
@Log4j2
@SpringBootApplication
public class DataLoaderStepsApp implements ApplicationRunner  {
  private final QuestionnaireController questionnaireController;
  private final StepsRepository stepsRepository;
  private final StepController stepController;
  private final UserController userController;

  public UserDTO USER_ADMINISTRATOR;

  @Value("${greta.defaults.load-init-data}")
  private boolean loadInitData;

  public static void main(String[] args) {
    SpringApplication.run(DataLoaderQuestionnairesApp.class, args);
  }

  @Autowired
  public DataLoaderStepsApp( QuestionnaireController questionnaireController,
                             StepsRepository stepsRepository,
                             StepController stepController,
                             UserController userController) {

    this.questionnaireController = questionnaireController;
    this.stepsRepository = stepsRepository;
    this.stepController = stepController;
    this.userController = userController;
  }

  @Override
  public void run(ApplicationArguments args) {
///    boolean isTest = (dataSourceUrl.contains(TEST_DATABASE_SCHEMA));
    if ((! loadInitData) || (stepsRepository.count() > 0)) {
      return;
    }
    USER_ADMINISTRATOR = userController.getFirstUserFromList("Greta", "Pyta");
    loadData();
    SaveAllItemsOnly();
    saveLinks(); // For creating Links Step->Questionnaire.
    // testToString(); //TEST
  }

  private void SaveAllItemsOnly() {
    StepDTO[] allItems =
      { FOR_SOCIAL_MEDIA_STEP1_DTO,

        FOR_PEOPLE_STEP1_DTO,
        FOR_PEOPLE_STEP2_DTO,
        FOR_PEOPLE_STEP3_DTO,

        FOR_POLITICS_STEP1_DTO,
        FOR_POTPOURI_STEP1_DTO };
    saveData(null, allItems);
  }

  private void saveLinks() {
    StepDTO[] forSocMediaArray =
      { FOR_SOCIAL_MEDIA_STEP1_DTO };

    StepDTO[] forPeopleArray =
      { FOR_PEOPLE_STEP1_DTO,
        FOR_PEOPLE_STEP2_DTO,
        FOR_PEOPLE_STEP3_DTO };

    StepDTO[] forPoliticsArray =
      { FOR_POLITICS_STEP1_DTO };

    StepDTO[] forPotPouriArray =
        { FOR_POTPOURI_STEP1_DTO };

    saveData(QUESTNR_SMD_SURVEY, forSocMediaArray);
    saveData(QUESTNR_PPL_IDENTIFY_YOURSELF, forPeopleArray);
    saveData(QUESTNR_POL_US_2024_ELECTIONS, forPoliticsArray);
    saveData(QUESTNR_PPO_QUIZ, forPotPouriArray);
  }

  private void saveData(String query, StepDTO[] options) {
    int displayOrder = 1;
    for (StepDTO dto : options) {
      Optional<StepDTO> oStep = stepController.findByNameMultilangFirstLike(
          dto.getNameMultilang().get(Constants.DEFAULT_LOCALE),
          USER_ADMINISTRATOR.getId());
      StepDTO stepForLink = (((oStep != null) && oStep.isPresent()) ? oStep.get() : saveDto(dto));

      if (query==null || query.isEmpty() || (stepForLink == null)) { // No Link to be created
        continue;
      }

      // Adding to the link Table Step-Questionnaire:
      saveLinkEntry(query, stepForLink, displayOrder, 0);
      displayOrder++;
    }
  }

  private StepDTO saveDto(StepDTO dto) {
    try {
      StepDTO newDto = stepController.executeCreateItem(dto, Constants.DEFAULT_LOCALE);
      log.info("New StepDTO item was created: ID={}", newDto.getId());
      return newDto;
    } catch (Exception e) {
      log.error("Cannot save StepDTO !", e);
      return null;
    }
  }

  private QuestionnaireStepLink saveLinkEntry(String query, StepDTO dtoForLink, int displayOrder, int tenantId) {
    USER_ADMINISTRATOR = userController.getFirstUserFromList("Greta", "Pyta");

    Optional<QuestionnaireDTO> optDTO = questionnaireController.fetchDTOFromCode(
        query, USER_ADMINISTRATOR.getId(),
        Constants.DEFAULT_LOCALE);
    if ((optDTO != null) && optDTO.isPresent()) {
      QuestionnaireDTO questionnaireDTO = optDTO.get();
      QuestionnaireStepLink newLink = stepController.executeCreateParentChildLink(
          questionnaireDTO,
          dtoForLink,
          displayOrder,
          tenantId );
      log.info("New QuestionnaireStepLink item was created for Step ID={} linking to Questionnaire ID={}.",
          dtoForLink.getId(), questionnaireDTO.getId());
      return newLink;
    } else {
      log.error("Cannot load Composite Key QuestionnaireStep: Questionnaire '{}' cannot be found !", query);
      return null;
    }
  }

  /*
   * Just 1 Step for (presumed) Social Media's Questionnaire.
  */
  public static StepDTO FOR_SOCIAL_MEDIA_STEP1_DTO = initCommonDTO();

  /*
   * 3 Steps for (presumed) People's Questionnaire.
  */
  public static StepDTO FOR_PEOPLE_STEP1_DTO = initCommonDTO();
  public static StepDTO FOR_PEOPLE_STEP2_DTO = initCommonDTO();
  public static StepDTO FOR_PEOPLE_STEP3_DTO = initCommonDTO();

  /*
   * 1 Step for Politics - US 2024 Pres. Elections.
   */
  public static StepDTO FOR_POLITICS_STEP1_DTO = initCommonDTO();

  public static StepDTO FOR_POTPOURI_STEP1_DTO = initCommonDTO();

  private void loadData() {
    // (1)
    Map<String, String> elements0 = new TreeMap<>();
    elements0.put("en", "Which Soc. Media usage step");
    elements0.put("pl", "Krok: jakich Mediów Społ. używasz");
    elements0.put("ru", "Шаг: Какая соц. Этап использования мультимедиа");
    // jsonNameTranslations = Converters.convertMapToJson(elements1);
    FOR_SOCIAL_MEDIA_STEP1_DTO.setNameMultilang(elements0);
    FOR_SOCIAL_MEDIA_STEP1_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (2)
    Map<String, String> elements1 = new TreeMap<>();
    elements1.put("en", "Person's Bio data step");
    elements1.put("pl", "Krok: dane osobowe");
    elements1.put("ru", "Шаг: Биологические данные человекаp");
    FOR_PEOPLE_STEP1_DTO.setNameMultilang(elements1);
    FOR_PEOPLE_STEP1_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (3)
    Map<String, String> elements2 = new TreeMap<>();
    elements2.put("en", "Person's professional activities step");
    elements2.put("pl", "Krok: aktywnowść zawodowa"); //!!!!! Polska litera 'o'
    elements2.put("ru", "Шаг: Профессиональная деятельность человека");
    FOR_PEOPLE_STEP2_DTO.setNameMultilang(elements2);
    FOR_PEOPLE_STEP2_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (4)
    Map<String, String> elements3 = new TreeMap<>();
    elements3.put("en", "Person's Soc. Media activities step");
    elements3.put("pl", "Krok: aktywność w Sieciach Socjalnych");
    elements3.put("ru", "Шаг: Соц. Медийная деятельность");
    FOR_PEOPLE_STEP3_DTO.setNameMultilang(elements3);
    FOR_PEOPLE_STEP3_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (5)
    Map<String, String> elements4 = new TreeMap<>();
    elements4.put("en", "US Presidential Elections 2024 step");
    elements4.put("pl", "Krok: wybory prezydenckie USA 2024");
    elements4.put("ru", "Шаг: Президентские выборы в США 2024 г.");
    FOR_POLITICS_STEP1_DTO.setNameMultilang(elements4);
    FOR_POLITICS_STEP1_DTO.setUserId(USER_ADMINISTRATOR.getId());

    // (6)
    Map<String, String> elements5 = new TreeMap<>();
    elements5.put("en", "Quiz 1 step");
    elements5.put("pl", "Krok: Zgadywanka 1");
    elements5.put("ru", "Шаг: Викторина номер 1");
    FOR_POTPOURI_STEP1_DTO.setNameMultilang(elements5);
    FOR_POTPOURI_STEP1_DTO.setUserId(USER_ADMINISTRATOR.getId());
  }

  private void testToString() {
    loadData();
    StepDTO[] socMedArray = {FOR_SOCIAL_MEDIA_STEP1_DTO};
    StepDTO[] peopleArray = {FOR_PEOPLE_STEP1_DTO, FOR_PEOPLE_STEP2_DTO, FOR_PEOPLE_STEP3_DTO};
    StepDTO[] politicsArray = {FOR_POLITICS_STEP1_DTO};
    StepDTO[] potpouriArray = {FOR_POTPOURI_STEP1_DTO};
    Arrays.stream(socMedArray).forEach(n -> DataLoaderQuestionnairesApp.logInfoLine(n.toString()));
    Arrays.stream(peopleArray).forEach(n -> DataLoaderQuestionnairesApp.logInfoLine(n.toString()));
    Arrays.stream(politicsArray).forEach(n -> DataLoaderQuestionnairesApp.logInfoLine(n.toString()));
    Arrays.stream(potpouriArray).forEach(n -> DataLoaderQuestionnairesApp.logInfoLine(n.toString()));
  }

  private static StepDTO initCommonDTO() {
    StepDTO ret = new StepDTO();
    ret.setReady2Show(true);
    ret.setCreated(LocalDateTime.now());
    ret.setUpdated(LocalDateTime.now());
    return ret;
  }
}