package com.az.gretapyta.questionnaires.application;

import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.controller.OptionController;
import com.az.gretapyta.questionnaires.controller.QuestionController;
import com.az.gretapyta.questionnaires.controller2.UserController;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import com.az.gretapyta.questionnaires.repository.OptionsRepository;
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

import static com.az.gretapyta.questionnaires.application.DataLoaderQuestionsApp.*;

@Log4j2
@Order(6)
@SpringBootApplication
public class DataLoaderOptionsApp implements ApplicationRunner {
  private final QuestionController questionController;
  private final OptionsRepository optionsRepository;
  private final OptionController optionController;
  private final UserController userController;

  private UserDTO USER_ADMINISTRATOR;

  @Value("${greta.defaults.load-init-data}")
  private boolean loadInitData;

  public static void main(String[] args) {
    SpringApplication.run(DataLoaderDrawersApp.class, args);
  }

  @Autowired
  public DataLoaderOptionsApp( QuestionController questionController,
                               OptionsRepository optionsRepository,
                               OptionController optionController,
                               UserController userController) {

    this.questionController = questionController;
    this.optionsRepository = optionsRepository;
    this.optionController = optionController;
    this.userController = userController;
  }

  @Override
  public void run(ApplicationArguments args) {
    if ((! loadInitData) || (optionsRepository.count() > 0)) {
      return;
    }
    USER_ADMINISTRATOR = userController.getFirstUserFromList("Greta", "Pyta");
    loadData();
    SaveAllItemsOnly();
    saveLinks(); // For creatings Links Question-Option.
  }

  private void SaveAllItemsOnly() {
    OptionDTO[] allItems =
        { OPT_NONE_HE,
          OPT_NONE_SHE,
          OPT_NONE_THEY,
          OPT_NO,
          OPT_YES,
          OPT_NOT_TO_SAY,
          OPT_DONT_KNOW,
          OPT_OTHERS,

          OPT_SEX_HE,
          OPT_SEX_SHE,

          OPT_SOCMED_FACEBOOK,
          OPT_SOCMED_TWITTER,
          OPT_SOCMED_INSTAGRAM,
          OPT_SOCMED_TIKTOK,
          OPT_SOCMED_TELEGRAM,

          OPT_AGERANGE_0_17,
          OPT_AGERANGE_18_30,
          OPT_AGERANGE_31_50,
          OPT_AGERANGE_51_64,
          OPT_AGERANGE_65_Over,

          OPT_USEL2024_BIDEN,
          OPT_USEL2024_TRUMP,
          OPT_USEL2024_OTHER_DEM,
          OPT_USEL2024_OTHER_REP,
          OPT_USEL2024_OTHER_OTHER,

          OPT_QUIZ_RIVER_MEKONG,
          OPT_QUIZ_RIVER_LENA,
          OPT_QUIZ_RIVER_YANGTZE,
          OPT_QUIZ_RIVER_YELLOW_RIVER,

          OPT_QUIZ_MOUNTAIN_ACONCAGUA,
          OPT_QUIZ_MOUNTAIN_OJOS_DEL_SALADO,
          OPT_QUIZ_MOUNTAIN_KILIMANGARO,
          OPT_QUIZ_MOUNTAIN_HUASCARAN,

          OPT_QUIZ_CAPITAL_KAMPALA,
          OPT_QUIZ_CAPITAL_DAKAR,
          OPT_QUIZ_CAPITAL_CAPE_TOWN,
          OPT_QUIZ_CAPITAL_ABUJA
        };

    saveData(null, allItems);
  }

  private void saveLinks() {
    OptionDTO[] forSocialMediaArray =
        { OPT_SOCMED_FACEBOOK,
          OPT_SOCMED_TWITTER,
          OPT_SOCMED_INSTAGRAM,
          OPT_SOCMED_TIKTOK,
          OPT_SOCMED_TELEGRAM };

    OptionDTO[] forGenderArray =
        { OPT_SEX_HE,
          OPT_SEX_SHE,
          OPT_NOT_TO_SAY };

    OptionDTO[] forAgeRangesArray =
        { OPT_AGERANGE_0_17,
          OPT_AGERANGE_18_30,
          OPT_AGERANGE_31_50,
          OPT_AGERANGE_51_64,
          OPT_AGERANGE_65_Over };

    OptionDTO[] forMaritalStatus =
        { OPT_NO,
          OPT_YES,
          OPT_NOT_TO_SAY };

    OptionDTO[] forUSElections2024Array =
        { OPT_USEL2024_BIDEN,
          OPT_USEL2024_TRUMP,
          OPT_USEL2024_OTHER_DEM,
          OPT_USEL2024_OTHER_REP,
          OPT_USEL2024_OTHER_OTHER };

    OptionDTO[] forQuizRiversInAsiaArray =
        { OPT_QUIZ_RIVER_MEKONG,
          OPT_QUIZ_RIVER_LENA,
          OPT_QUIZ_RIVER_YANGTZE,
          OPT_QUIZ_RIVER_YELLOW_RIVER };

    OptionDTO[] forQuizMountainsInSAmericaArray =
        { OPT_QUIZ_MOUNTAIN_ACONCAGUA,
          OPT_QUIZ_MOUNTAIN_OJOS_DEL_SALADO,
          OPT_QUIZ_MOUNTAIN_KILIMANGARO,
          OPT_QUIZ_MOUNTAIN_HUASCARAN };

    OptionDTO[] forQuizCapitolsInAfricaArray =
        { OPT_QUIZ_CAPITAL_KAMPALA,
          OPT_QUIZ_CAPITAL_DAKAR,
          OPT_QUIZ_CAPITAL_CAPE_TOWN,
          OPT_QUIZ_CAPITAL_ABUJA };

    saveData(QUESTION_SMD_USAGE, forSocialMediaArray);
    saveData(QUESTION_PPL_YOURSELF_SEX, forGenderArray);
    saveData(QUESTION_PPL_YOURSELF_AGE, forAgeRangesArray);
    saveData(QUESTION_PPL_YOURSELF_MARITAL, forMaritalStatus);

    saveData(QUESTION_POL_US_PRESIDENT_2024_PREDICT, forUSElections2024Array);
    saveData(QUESTION_POL_US_PRESIDENT_2024_WANT, forUSElections2024Array);

    saveData(QUESTION_PPO_QUIZ1_RIVER, forQuizRiversInAsiaArray);
    saveData(QUESTION_PPO_QUIZ1_MOUNTAIN, forQuizMountainsInSAmericaArray);
    saveData(QUESTION_PPO_QUIZ1_CAPITOL, forQuizCapitolsInAfricaArray);
  }

  private void saveData(String query, OptionDTO[] list) {
    int displayOrder = 1;
    for(OptionDTO dto : list) {
      Optional<OptionDTO> oOption = optionController.fetchDTOFromCode( dto.getCode(),
                                                                       USER_ADMINISTRATOR.getId(),
                                                                       Constants.DEFAULT_LOCALE );
      OptionDTO dtoForLink = (((oOption != null) && oOption.isPresent()) ? oOption.get() : saveDto(dto));

      if (query == null || query.isEmpty()) { // No Link to be created
        continue;
      }

      // Add the link Table Option-Question:
      saveLinkEntry(query, dtoForLink, displayOrder, 0);
      displayOrder++;
    }
  }

  private OptionDTO saveDto(OptionDTO dto) {
    try {
      OptionDTO newDto = optionController.executeCreateItem(dto, Constants.DEFAULT_LOCALE);
      log.info("New OptionDTO item was created: ID={}", newDto.getId());
      return newDto;
    } catch (Exception e) {
      log.error("Cannot save OptionDTO !", e);
      log.error("Cannot save OptionDTO !", e);
      return null;
    }
  }

  private QuestionOptionLink saveLinkEntry(String query, OptionDTO dtoForLink, int displayOrder, int tenantId) {
    // Adding to the link Table Option-Question:
    Optional<QuestionDTO> questionDTO = questionController.fetchDTOFromCode( query,
                                                                             USER_ADMINISTRATOR.getId(),
                                                                             Constants.DEFAULT_LOCALE );
    if ((questionDTO != null) && questionDTO.isPresent()) {
      QuestionOptionLink newLink = optionController.executeCreateParentChildLink(questionDTO.get(), dtoForLink, displayOrder, tenantId);
      log.info("New QuestionOptionLink item was created for Option ID={} linking to Question ID={}.",
          dtoForLink.getId(), questionDTO.get().getId());
      return newLink;
    } else {
      log.error("Cannot load Composite Key QuestionOption: Question '{}' cannot be found !", query);
      return null;
    }
  }


  //--/
  public static final String OPT_CODE_NONE_HE = "OPT_NONE_HE";
  public static OptionDTO OPT_NONE_HE = initCommonDTO();

  public static final String OPT_CODE_NONE_SHE = "OPT_NONE_SHE";
  public static OptionDTO OPT_NONE_SHE = initCommonDTO();

  public static final String OPT_CODE_NONE_THEY = "OPT_NONE_THEY";
  public static OptionDTO OPT_NONE_THEY = initCommonDTO();

  public static final String OPT_CODE_NO = "OPT_NO";
  public static OptionDTO OPT_NO = initCommonDTO();
  public static final String OPT_CODE_YES = "OPT_YES";
  public static OptionDTO OPT_YES = initCommonDTO();
  public static final String OPT_CODE_NOT_TO_SAY = "OPT_NOT_TO_SAY";
  public static OptionDTO OPT_NOT_TO_SAY = initCommonDTO();
  public static final String OPT_CODE_DONT_KNOW = "OPT_DONT_KNOW";
  public static OptionDTO OPT_DONT_KNOW = initCommonDTO();

  public static final String OPT_CODE_OTHERS = "OPT_OTHERS";
  public static OptionDTO OPT_OTHERS = initCommonDTO();

  //-- Gender: /---------------------------------//
  public static final String OPT_CODE_SEX_HE = "OPT_SEX_HE";
  public static OptionDTO OPT_SEX_HE = initCommonDTO();
  public static final String OPT_CODE_SEX_SHE = "OPT_SEX_SHE";
  public static OptionDTO OPT_SEX_SHE = initCommonDTO();
  //-- Gender: /---------------------------------//

  //-- Social Media: /---------------------------------//
  public static final String OPT_CODE_SOCMED_FACEBOOK = "OPT_SOCMED_FG";
  public static OptionDTO OPT_SOCMED_FACEBOOK = initCommonDTO();
  public static final String OPT_CODE_SOCMED_TWITTER = "OPT_SOCMED_X";
  public static OptionDTO OPT_SOCMED_TWITTER = initCommonDTO();
  public static final String OPT_CODE_SOCMED_INSTAGRAM = "OPT_SOCMED_INST";
  public static OptionDTO OPT_SOCMED_INSTAGRAM = initCommonDTO();
  public static final String OPT_CODE_SOCMED_TIKTOK = "OPT_SOCMED_TKTK";
  public static OptionDTO OPT_SOCMED_TIKTOK = initCommonDTO();
  public static final String OPT_CODE_SOCMED_TELEGRAM = "OPT_SOCMED_TLGR";
  public static OptionDTO OPT_SOCMED_TELEGRAM = initCommonDTO();
  //-- Social Media: /---------------------------------//

  //-- Age ranges: /-----------------------------------//
  public static final String OPT_CODE_AGERANGE_0_17 = "OPT_AGERNG_1";
  public static OptionDTO OPT_AGERANGE_0_17 = initCommonDTO();
  public static final String OPT_CODE_AGERANGE_18_30 = "OPT_AGERNG_2";
  public static OptionDTO OPT_AGERANGE_18_30 = initCommonDTO();
  public static final String OPT_CODE_AGERANGE_31_50 = "OPT_AGERNG_3";
  public static OptionDTO OPT_AGERANGE_31_50 = initCommonDTO();
  public static final String OPT_CODE_AGERANGE_51_64 = "OPT_AGERNG_4";
  public static OptionDTO OPT_AGERANGE_51_64 = initCommonDTO();
  public static final String OPT_CODE_AGERANGE_65_Over = "OPT_AGERNG_5";
  public static OptionDTO OPT_AGERANGE_65_Over = initCommonDTO();
  //-- Age ranges: /-----------------------------------//

  //-- US 2024 Presidential Elections: /----------------//
  public static final String OPT_CODE_USEL2024_BIDEN = "OPT_USEL24_BDN";
  public static OptionDTO OPT_USEL2024_BIDEN = initCommonDTO();
  public static final String OPT_CODE_USEL2024_TRUMP = "OPT_USEL24_TRP";
  public static OptionDTO OPT_USEL2024_TRUMP = initCommonDTO();
  public static final String OPT_CODE_USEL2024_OTHER_DEM = "OPT_USEL24_OTDM";
  public static OptionDTO OPT_USEL2024_OTHER_DEM = initCommonDTO();
  public static final String OPT_CODE_USEL2024_OTHER_REP = "OPT_USEL24_OTRP";
  public static OptionDTO OPT_USEL2024_OTHER_REP = initCommonDTO();
  public static final String OPT_CODE_USEL2024_OTHER_OTHER = "OPT_USEL24_OTOT";
  public static OptionDTO OPT_USEL2024_OTHER_OTHER = initCommonDTO();
  //-- US 2024 Presidential Elections: /----------------//

  //-- Quiz - Geography: Rivers in Asia /---------------//
  public static final String OPT_CODE_QUIZ_RIVER_MEKONG = "OPT_QUIZ_RVR_MKG";
  public static OptionDTO OPT_QUIZ_RIVER_MEKONG = initCommonDTO();

  public static final String OPT_CODE_QUIZ_RIVER_LENA = "OPT_QUIZ_RVR_LNA";
  public static OptionDTO OPT_QUIZ_RIVER_LENA = initCommonDTO();

  public static final String OPT_CODE_QUIZ_RIVER_YANGTZE = "OPT_QUIZ_RVR_YGZ";
  public static OptionDTO OPT_QUIZ_RIVER_YANGTZE = initCommonDTO();

  public static final String OPT_CODE_QUIZ_RIVER_YELLOW_RIVER = "OPT_QUIZ_RVR_YRV";
  public static OptionDTO OPT_QUIZ_RIVER_YELLOW_RIVER = initCommonDTO();
  //-- Quiz - Geography: Rivers in Asia /---------------//

  //-- Quiz - Geography: Mountains in South America /---//
  public static final String OPT_CODE_QUIZ_MOUNTAIN_ACONCAGUA = "OPT_QUIZ_MTN_ACG";
  public static OptionDTO OPT_QUIZ_MOUNTAIN_ACONCAGUA = initCommonDTO();

  public static final String OPT_CODE_QUIZ_MOUNTAIN_OJOS_DEL_SALADO = "OPT_QUIZ_MTN_ODS";
  public static OptionDTO OPT_QUIZ_MOUNTAIN_OJOS_DEL_SALADO = initCommonDTO();

  public static final String OPT_CODE_QUIZ_MOUNTAIN_KILIMANGARO = "OPT_QUIZ_MTN_KLG";
  public static OptionDTO OPT_QUIZ_MOUNTAIN_KILIMANGARO = initCommonDTO();

  public static final String OPT_CODE_QUIZ_MOUNTAIN_HUASCARAN = "OPT_QUIZ_MTN_HCR";
  public static OptionDTO OPT_QUIZ_MOUNTAIN_HUASCARAN = initCommonDTO();
  //-- Quiz - Geography: Mountains in South America /---//

  //-- Quiz - Geography: Capitals in Africa /-----------//
  public static final String OPT_CODE_QUIZ_CAPITAL_KAMPALA = "OPT_QUIZ_CPT_KPL";
  public static OptionDTO OPT_QUIZ_CAPITAL_KAMPALA = initCommonDTO();

  public static final String OPT_CODE_QUIZ_CAPITAL_DAKAR = "OPT_QUIZ_CPT_DKR";
  public static OptionDTO OPT_QUIZ_CAPITAL_DAKAR = initCommonDTO();

  public static final String OPT_CODE_CAPITAL_CAPE_TOWN = "OPT_QUIZ_CPT_CTN";
  public static OptionDTO OPT_QUIZ_CAPITAL_CAPE_TOWN = initCommonDTO();

  public static final String OPT_CODE_QUIZ_CAPITAL_ABUJA = "OPT_QUIZ_CPT_ABJ";
  public static OptionDTO OPT_QUIZ_CAPITAL_ABUJA = initCommonDTO();
  //-- Quiz - Geography: Capitals in Africa /-----------//

  private void loadData() {
    UserDTO USER_ADMINISTRATOR = userController.getFirstUserFromList("Greta", "Pyta");

    // (1)
    Map<String, String> elements0 = new TreeMap<>();
    elements0.put("en", "none");
    elements0.put("pl", "żadnego");
    elements0.put("ru", "никто");
    OPT_NONE_HE.setCode(OPT_CODE_NONE_HE);
    OPT_NONE_HE.setNameMultilang(elements0);
    OPT_NONE_HE.setUserId(USER_ADMINISTRATOR.getId());

    //(2)
    Map<String, String> elements1 = new TreeMap<>();
    elements1.put("en", "none");
    elements1.put("pl", "żadnej");
    elements1.put("ru", "никто");
    OPT_NONE_SHE.setCode(OPT_CODE_NONE_SHE);
    OPT_NONE_SHE.setNameMultilang(elements1);
    OPT_NONE_SHE.setUserId(USER_ADMINISTRATOR.getId());

    //(3)
    Map<String, String> elements2 = new TreeMap<>();
    elements2.put("en", "none");
    elements2.put("pl", "żadnych");
    elements2.put("ru", "никто");
    OPT_NONE_THEY.setCode(OPT_CODE_NONE_THEY);
    OPT_NONE_THEY.setNameMultilang(elements2);
    OPT_NONE_THEY.setUserId(USER_ADMINISTRATOR.getId());

    //(4)
    Map<String, String> elements3 = new TreeMap<>();
    elements3.put("en", "No");
    elements3.put("pl", "Nie");
    elements3.put("ru", "Нет");
    // jsonNameTranslations = Converters.convertMapToJson(elements1);
    OPT_NO.setCode(OPT_CODE_NO);
    OPT_NO.setNameMultilang(elements3);
    OPT_NO.setUserId(USER_ADMINISTRATOR.getId());

    //(5)
    Map<String, String> elements4 = new TreeMap<>();
    elements4.put("en", "Yes");
    elements4.put("pl", "Tak");
    elements4.put("ru", "Да");
    OPT_YES.setCode(OPT_CODE_YES);
    OPT_YES.setNameMultilang(elements4);
    OPT_YES.setUserId(USER_ADMINISTRATOR.getId());

    //(6)
    Map<String, String> elements5 = new TreeMap<>();
    elements5.put("en", "I'd rather not say");
    elements5.put("pl", "wolę nie powiedzieć");
    elements5.put("ru", "я бы не сказал");
    OPT_NOT_TO_SAY.setCode(OPT_CODE_NOT_TO_SAY);
    OPT_NOT_TO_SAY.setNameMultilang(elements5);
    OPT_NOT_TO_SAY.setUserId(USER_ADMINISTRATOR.getId());

    //(7)
    Map<String, String> elements6 = new TreeMap<>();
    elements6.put("en", "I don't know");
    elements6.put("pl", "nie wiem");
    elements6.put("ru", "Я не знаю");
    OPT_DONT_KNOW.setCode(OPT_CODE_DONT_KNOW);
    OPT_DONT_KNOW.setNameMultilang(elements6);
    OPT_DONT_KNOW.setUserId(USER_ADMINISTRATOR.getId());

    //(8)
    Map<String, String> elements7 = new TreeMap<>();
    elements7.put("en", "others");
    elements7.put("pl", "inne");
    elements7.put("ru", "другие");
    OPT_OTHERS.setCode(OPT_CODE_OTHERS);
    OPT_OTHERS.setNameMultilang(elements7);
    OPT_OTHERS.setUserId(USER_ADMINISTRATOR.getId());

    //(9)
    Map<String, String> elements8 = new TreeMap<>();
    elements8.put("en", "Male");
    elements8.put("pl", "Mężczyzna");
    elements8.put("ru", "Мужской");
    OPT_SEX_HE.setCode(OPT_CODE_SEX_HE);
    OPT_SEX_HE.setNameMultilang(elements8);
    OPT_SEX_HE.setUserId(USER_ADMINISTRATOR.getId());

    //(10)
    Map<String, String> elements9 = new TreeMap<>();
    elements9.put("en", "Female");
    elements9.put("pl", "Kobieta");
    elements9.put("ru", "Женский");
    OPT_SEX_SHE.setCode(OPT_CODE_SEX_SHE);
    OPT_SEX_SHE.setNameMultilang(elements9);
    OPT_SEX_SHE.setUserId(USER_ADMINISTRATOR.getId());

    //-- Social Media: /---------------------------------//
    //(11)
    Map<String, String> elements10 = new TreeMap<>();
    elements10.put("en", "Facebook");
    elements10.put("pl", "Facebook");
    elements10.put("ru", "Фейсбук");
    OPT_SOCMED_FACEBOOK.setCode(OPT_CODE_SOCMED_FACEBOOK);
    OPT_SOCMED_FACEBOOK.setNameMultilang(elements10);
    OPT_SOCMED_FACEBOOK.setUserId(USER_ADMINISTRATOR.getId());
    //(12)
    Map<String, String> elements11 = new TreeMap<>();
    elements11.put("en", "'X' (formerly Tweeter)");
    elements11.put("pl", "'X' (uprzednio Tweeter)");
    elements11.put("ru", "'X' (ранее Твитер)");
    OPT_SOCMED_TWITTER.setCode(OPT_CODE_SOCMED_TWITTER);
    OPT_SOCMED_TWITTER.setNameMultilang(elements11);
    OPT_SOCMED_TWITTER.setUserId(USER_ADMINISTRATOR.getId());
    //(13)
    Map<String, String> elements12 = new TreeMap<>();
    elements12.put("en", "Instagram");
    elements12.put("pl", "Instagram");
    elements12.put("ru", "Инстаграм");
    OPT_SOCMED_INSTAGRAM.setCode(OPT_CODE_SOCMED_INSTAGRAM);
    OPT_SOCMED_INSTAGRAM.setNameMultilang(elements12);
    OPT_SOCMED_INSTAGRAM.setUserId(USER_ADMINISTRATOR.getId());
    //(14)
    Map<String, String> elements13 = new TreeMap<>();
    elements13.put("en", "TikTok");
    elements13.put("pl", "TikTok");
    elements13.put("ru", "ТИК Так");
    OPT_SOCMED_TIKTOK.setCode(OPT_CODE_SOCMED_TIKTOK);
    OPT_SOCMED_TIKTOK.setNameMultilang(elements13);
    OPT_SOCMED_TIKTOK.setUserId(USER_ADMINISTRATOR.getId());
    //(15)
    Map<String, String> elements14 = new TreeMap<>();
    elements14.put("en", "Telegram");
    elements14.put("pl", "Telegram");
    elements14.put("ru", "Телеграм");
    OPT_SOCMED_TELEGRAM.setCode(OPT_CODE_SOCMED_TELEGRAM);
    OPT_SOCMED_TELEGRAM.setNameMultilang(elements14);
    OPT_SOCMED_TELEGRAM.setUserId(USER_ADMINISTRATOR.getId());
    //-- Social Media: /---------------------------------//

    //-- Age ranges: /-----------------------------------//
    //(16)
    Map<String, String> elements15 = new TreeMap<>();
    elements15.put("en", "less than 18 yrs.");
    elements15.put("pl", "poniżej 18 lat");
    elements15.put("ru", "менее 18 лет");
    OPT_AGERANGE_0_17.setCode(OPT_CODE_AGERANGE_0_17);
    OPT_AGERANGE_0_17.setNameMultilang(elements15);
    OPT_AGERANGE_0_17.setUserId(USER_ADMINISTRATOR.getId());
    //(17)
    Map<String, String> elements16 = new TreeMap<>();
    elements16.put("en", "18 - 30 yrs.");
    elements16.put("pl", "pomiędzy 18 a 30 lat");
    elements16.put("ru", "18 - 30 лет");
    OPT_AGERANGE_18_30.setCode(OPT_CODE_AGERANGE_18_30);
    OPT_AGERANGE_18_30.setNameMultilang(elements16);
    OPT_AGERANGE_18_30.setUserId(USER_ADMINISTRATOR.getId());
    //(18)
    Map<String, String> elements17 = new TreeMap<>();
    elements17.put("en", "31 - 50 yrs.");
    elements17.put("pl", "pomiędzy 31 a 50 lat");
    elements17.put("ru", "31 - 50 лет");
    OPT_AGERANGE_31_50.setCode(OPT_CODE_AGERANGE_31_50);
    OPT_AGERANGE_31_50.setNameMultilang(elements17);
    OPT_AGERANGE_31_50.setUserId(USER_ADMINISTRATOR.getId());
    //(19)
    Map<String, String> elements18 = new TreeMap<>();
    elements18.put("en", "51 - 64 yrs.");
    elements18.put("pl", "pomiędzy 51 a 64 lata");
    elements18.put("ru", "51 - 64 лет");
    OPT_AGERANGE_51_64.setCode(OPT_CODE_AGERANGE_51_64);
    OPT_AGERANGE_51_64.setNameMultilang(elements18);
    OPT_AGERANGE_51_64.setUserId(USER_ADMINISTRATOR.getId());
    //(20)
    Map<String, String> elements19 = new TreeMap<>();
    elements19.put("en", "over 64 years");
    elements19.put("pl", "ponad 64 lata");
    elements19.put("ru", "старше 64 лет");
    OPT_AGERANGE_65_Over.setCode(OPT_CODE_AGERANGE_65_Over);
    OPT_AGERANGE_65_Over.setNameMultilang(elements19);
    OPT_AGERANGE_65_Over.setUserId(USER_ADMINISTRATOR.getId());
    //-- Age ranges: /-----------------------------------//

    //-- US 2024 Presidential Elections: /----------------//
    //(21)
    Map<String, String> elements20 = new TreeMap<>();
    elements20.put("en", "Joe Biden");
    elements20.put("pl", "Joe Biden");
    elements20.put("ru", "Джо Байден");
    OPT_USEL2024_BIDEN.setCode(OPT_CODE_USEL2024_BIDEN);
    OPT_USEL2024_BIDEN.setNameMultilang(elements20);
    OPT_USEL2024_BIDEN.setUserId(USER_ADMINISTRATOR.getId());
    //(22)
    Map<String, String> elements21 = new TreeMap<>();
    elements21.put("en", "Donald Trump");
    elements21.put("pl", "Donald Trump");
    elements21.put("ru", "Джо Байден");
    OPT_USEL2024_TRUMP.setCode(OPT_CODE_USEL2024_TRUMP);
    OPT_USEL2024_TRUMP.setNameMultilang(elements21);
    OPT_USEL2024_TRUMP.setUserId(USER_ADMINISTRATOR.getId());
    //(23)
    Map<String, String> elements22 = new TreeMap<>();
    elements22.put("en", "other candidate (Democrats)");
    elements22.put("pl", "inny kandydat (Partia Demokratyczna)");
    elements22.put("ru", "другой кандидат (демократы)");
    OPT_USEL2024_OTHER_DEM.setCode(OPT_CODE_USEL2024_OTHER_DEM);
    OPT_USEL2024_OTHER_DEM.setNameMultilang(elements22);
    OPT_USEL2024_OTHER_DEM.setUserId(USER_ADMINISTRATOR.getId());
    //(24)
    Map<String, String> elements23 = new TreeMap<>();
    elements23.put("en", "other candidate (Republicans)");
    elements23.put("pl", "inny kandydat (Partia Republikańska)");
    elements23.put("ru", "другой кандидат (республиканцы)");
    OPT_USEL2024_OTHER_REP.setCode(OPT_CODE_USEL2024_OTHER_REP);
    OPT_USEL2024_OTHER_REP.setNameMultilang(elements23);
    OPT_USEL2024_OTHER_REP.setUserId(USER_ADMINISTRATOR.getId());
    //(25)
    Map<String, String> elements24 = new TreeMap<>();
    elements24.put("en", "other candidate");
    elements24.put("pl", "inny kandydat");
    elements24.put("ru", "другой кандидат");
    OPT_USEL2024_OTHER_OTHER.setCode(OPT_CODE_USEL2024_OTHER_OTHER);
    OPT_USEL2024_OTHER_OTHER.setNameMultilang(elements24);
    OPT_USEL2024_OTHER_OTHER.setUserId(USER_ADMINISTRATOR.getId());
    //-- US 2024 Presidential Elections: /----------------//

    //-- Quiz - Geography: Rivers in Asia /---------------//
    //(26)
    Map<String, String> elements25 = new TreeMap<>();
    elements25.put("en", "Mekong");
    elements25.put("pl", "Mekong");
    elements25.put("ru", "Меконг");
    OPT_QUIZ_RIVER_MEKONG.setCode(OPT_CODE_QUIZ_RIVER_MEKONG);
    OPT_QUIZ_RIVER_MEKONG.setNameMultilang(elements25);
    OPT_QUIZ_RIVER_MEKONG.setPreferredAnswer(false);
    OPT_QUIZ_RIVER_MEKONG.setUserId(USER_ADMINISTRATOR.getId());
    //(27)
    Map<String, String> elements26 = new TreeMap<>();
    elements26.put("en", "Lena");
    elements26.put("pl", "Lena");
    elements26.put("ru", "Лена");
    OPT_QUIZ_RIVER_LENA.setCode(OPT_CODE_QUIZ_RIVER_LENA);
    OPT_QUIZ_RIVER_LENA.setNameMultilang(elements26);
    OPT_QUIZ_RIVER_LENA.setPreferredAnswer(false);
    OPT_QUIZ_RIVER_LENA.setUserId(USER_ADMINISTRATOR.getId());
    //(28)
    Map<String, String> elements27 = new TreeMap<>();
    elements27.put("en", "Yangtze");
    elements27.put("pl", "Jangcy");
    elements27.put("ru", "Янцзы");
    OPT_QUIZ_RIVER_YANGTZE.setCode(OPT_CODE_QUIZ_RIVER_YANGTZE);
    OPT_QUIZ_RIVER_YANGTZE.setNameMultilang(elements27);
    OPT_QUIZ_RIVER_YANGTZE.setPreferredAnswer(true);
    OPT_QUIZ_RIVER_YANGTZE.setUserId(USER_ADMINISTRATOR.getId());
    //(29)
    Map<String, String> elements28 = new TreeMap<>();
    elements28.put("en", "Yellow River");
    elements28.put("pl", "Żółta Rzeka");
    elements28.put("ru", "Желтая река");
    OPT_QUIZ_RIVER_YELLOW_RIVER.setCode(OPT_CODE_QUIZ_RIVER_YELLOW_RIVER);
    OPT_QUIZ_RIVER_YELLOW_RIVER.setNameMultilang(elements28);
    OPT_QUIZ_RIVER_YELLOW_RIVER.setPreferredAnswer(false);
    OPT_QUIZ_RIVER_YELLOW_RIVER.setUserId(USER_ADMINISTRATOR.getId());
    //-- Quiz - Geography: Rivers in Asia /---------------//

    //-- Quiz - Geography: Mountains in South America /---//
    //(30)
    Map<String, String> elements29 = new TreeMap<>();
    elements29.put("en", "Aconcagua");
    elements29.put("pl", "Aconcagua");
    elements29.put("ru", "Аконкагуа");
    OPT_QUIZ_MOUNTAIN_ACONCAGUA.setCode(OPT_CODE_QUIZ_MOUNTAIN_ACONCAGUA);
    OPT_QUIZ_MOUNTAIN_ACONCAGUA.setNameMultilang(elements29);
    OPT_QUIZ_MOUNTAIN_ACONCAGUA.setPreferredAnswer(true);
    OPT_QUIZ_MOUNTAIN_ACONCAGUA.setUserId(USER_ADMINISTRATOR.getId());
    //(31)
    Map<String, String> elements30 = new TreeMap<>();
    elements30.put("en", "Ojos del Salado");
    elements30.put("pl", "Ojos del Salado");
    elements30.put("ru", "Глаза Саладо");
    OPT_QUIZ_MOUNTAIN_OJOS_DEL_SALADO.setCode(OPT_CODE_QUIZ_MOUNTAIN_OJOS_DEL_SALADO);
    OPT_QUIZ_MOUNTAIN_OJOS_DEL_SALADO.setNameMultilang(elements30);
    OPT_QUIZ_MOUNTAIN_OJOS_DEL_SALADO.setPreferredAnswer(false);
    OPT_QUIZ_MOUNTAIN_OJOS_DEL_SALADO.setUserId(USER_ADMINISTRATOR.getId());
    //(32)
    Map<String, String> elements31 = new TreeMap<>();
    elements31.put("en", "Kilimangaro");
    elements31.put("pl", "Kilimandzaro");
    elements31.put("ru", "Килимангаро");
    OPT_QUIZ_MOUNTAIN_KILIMANGARO.setCode(OPT_CODE_QUIZ_MOUNTAIN_KILIMANGARO);
    OPT_QUIZ_MOUNTAIN_KILIMANGARO.setNameMultilang(elements31);
    OPT_QUIZ_MOUNTAIN_KILIMANGARO.setPreferredAnswer(false);
    OPT_QUIZ_MOUNTAIN_KILIMANGARO.setUserId(USER_ADMINISTRATOR.getId());
    //(33)
    Map<String, String> elements32 = new TreeMap<>();
    elements32.put("en", "Huascarán");
    elements32.put("pl", "Huascarán");
    elements32.put("ru", "Уаскаран");
    OPT_QUIZ_MOUNTAIN_HUASCARAN.setCode(OPT_CODE_QUIZ_MOUNTAIN_HUASCARAN);
    OPT_QUIZ_MOUNTAIN_HUASCARAN.setNameMultilang(elements32);
    OPT_QUIZ_MOUNTAIN_HUASCARAN.setPreferredAnswer(false);
    OPT_QUIZ_MOUNTAIN_HUASCARAN.setUserId(USER_ADMINISTRATOR.getId());
    //-- Quiz - Geography: Mountains in South America /---//

    //-- Quiz - Geography: Capitals in Africa /-----------//
    //(34)
    Map<String, String> elements33 = new TreeMap<>();
    elements33.put("en", "Kampala");
    elements33.put("pl", "Kampala");
    elements33.put("ru", "Кампала");
    OPT_QUIZ_CAPITAL_KAMPALA.setCode(OPT_CODE_QUIZ_CAPITAL_KAMPALA);
    OPT_QUIZ_CAPITAL_KAMPALA.setNameMultilang(elements33);
    OPT_QUIZ_CAPITAL_KAMPALA.setPreferredAnswer(false);
    OPT_QUIZ_CAPITAL_KAMPALA.setUserId(USER_ADMINISTRATOR.getId());
    //(35)
    Map<String, String> elements34 = new TreeMap<>();
    elements34.put("en", "Dakar");
    elements34.put("pl", "Dakar");
    elements34.put("ru", "Дакар");
    OPT_QUIZ_CAPITAL_DAKAR.setCode(OPT_CODE_QUIZ_CAPITAL_DAKAR);
    OPT_QUIZ_CAPITAL_DAKAR.setNameMultilang(elements34);
    OPT_QUIZ_CAPITAL_DAKAR.setPreferredAnswer(true);
    OPT_QUIZ_CAPITAL_DAKAR.setUserId(USER_ADMINISTRATOR.getId());
    //(36)
    Map<String, String> elements35 = new TreeMap<>();
    elements35.put("en", "Cape Town");
    elements35.put("pl", "Kapsztad");
    elements35.put("ru", "Кейптаун");
    OPT_QUIZ_CAPITAL_CAPE_TOWN.setCode(OPT_CODE_CAPITAL_CAPE_TOWN);
    OPT_QUIZ_CAPITAL_CAPE_TOWN.setNameMultilang(elements35);
    OPT_QUIZ_CAPITAL_CAPE_TOWN.setPreferredAnswer(false);
    OPT_QUIZ_CAPITAL_CAPE_TOWN.setUserId(USER_ADMINISTRATOR.getId());
    //(37)
    Map<String, String> elements36 = new TreeMap<>();
    elements36.put("en", "Abuja");
    elements36.put("pl", "Abudża");
    elements36.put("ru", "Абуджа");
    OPT_QUIZ_CAPITAL_ABUJA.setCode(OPT_CODE_QUIZ_CAPITAL_ABUJA);
    OPT_QUIZ_CAPITAL_ABUJA.setNameMultilang(elements36);
    OPT_QUIZ_CAPITAL_ABUJA.setPreferredAnswer(false);
    OPT_QUIZ_CAPITAL_ABUJA.setUserId(USER_ADMINISTRATOR.getId());
    //-- Quiz - Geography: Capitals in Africa /-----------//
  }

  private static OptionDTO initCommonDTO() {
    OptionDTO ret = new OptionDTO();
    ret.setReady2Show(true);
    ret.setCreated(LocalDateTime.now());
    ret.setUpdated(LocalDateTime.now());
    return ret;
  }
}