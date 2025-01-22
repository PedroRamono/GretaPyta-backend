package com.az.gretapyta.questionnaires.application;

import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.enums.EnumCommon;
import com.az.gretapyta.qcore.enums.UserQuestionnaireStatuses;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.controller.OptionController;
import com.az.gretapyta.questionnaires.controller.QuestionController;
import com.az.gretapyta.questionnaires.controller.QuestionnaireController;
import com.az.gretapyta.questionnaires.controller.StepController;
import com.az.gretapyta.questionnaires.controller2.*;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto2.*;
import com.az.gretapyta.questionnaires.model2.GenericValue;
import com.az.gretapyta.questionnaires.repository2.UserQuestionnairesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/*
 * To create some Users taking some Questionnaires.
 *
 */
@Log4j2
@Order(7)
@SpringBootApplication
public class DataLoader2SampleQuestionnairesTaken implements ApplicationRunner {

  private final UserQuestionnairesRepository userQuestionnairesRepository;
  private final UserController userController;
  private final QuestionnaireController questionnaireController;
  private final OptionController optionController;
  private final StepController stepController;
  private final UserQuestionnaireController userQuestionnaireController;
  private final QuestionController questionController;
  private final AnswerSelectedController answerSelectedController;
  private final AnswerProvidedController answerProvidedController;
  private final QuestionAnswerController questionAnswerController;

  @Value("${greta.defaults.load-init-data}")
  private boolean loadInitData;

  private UserDTO USER_ADMINISTRATOR;

  @Autowired
  public DataLoader2SampleQuestionnairesTaken( UserQuestionnairesRepository userQuestionnairesRepository,
                                               UserController userController,
                                               QuestionnaireController questionnaireController,
                                               StepController stepController,
                                               QuestionController questionController,
                                               OptionController optionController,
                                               UserQuestionnaireController userQuestionnaireController,

                                               QuestionAnswerController questionAnswerController,
                                               AnswerProvidedController answerProvidedController,
                                               AnswerSelectedController answerSelectedController
  ) {
    this.userQuestionnairesRepository = userQuestionnairesRepository;
    this.userController = userController;
    this.questionnaireController = questionnaireController;
    this.stepController = stepController;
    this.questionController = questionController;
    this.optionController = optionController;
    this.userQuestionnaireController = userQuestionnaireController;

    this.questionAnswerController = questionAnswerController;
    this.answerProvidedController = answerProvidedController;
    this.answerSelectedController = answerSelectedController;
  }

  public static void main(String[] args) {
    SpringApplication.run(DataLoader2SampleQuestionnairesTaken.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
///    boolean isTest = (dataSourceUrl.contains(TEST_DATABASE_SCHEMA));
    if ((! loadInitData) || (userQuestionnairesRepository.count() > 0)) {
      return;
    }
    USER_ADMINISTRATOR = userController.getFirstUserFromList("Greta", "Pyta");
    loadData();
    saveAllItems();
  }

  private void saveAllItems() {
    UserQuestionnaireDTO[] forPoliticsArray =
        { POLITICS1_US2024_1,
            POLITICS1_US2024_2,
            POLITICS1_US2024_3 };

    UserQuestionnaireDTO[] forSocialMediaArray =
        { SOCIAL_MEDIA1,
            SOCIAL_MEDIA2,
            SOCIAL_MEDIA3 };

    UserQuestionnaireDTO[] forPeopleArray =
        { PEOPLE_IDENTIFY_YOURSELF1,
            PEOPLE_IDENTIFY_YOURSELF2,
            PEOPLE_IDENTIFY_YOURSELF3 };

    saveData(forSocialMediaArray);
    saveData(forPoliticsArray);
    saveData(forPeopleArray);
  }

  private void saveData(UserQuestionnaireDTO[] quest) {
    for (UserQuestionnaireDTO n : quest) {
      UserQuestionnaireDTO newObj = saveEntityItem(n);
    }
  }

  private UserQuestionnaireDTO saveEntityItem(UserQuestionnaireDTO dto) {

    String langCode = dto.getAnswerLang();
    QuestionnaireDTO questionnaireDTO = questionnaireController.fetchDTOFromId(
        dto.getQuestionnaireDTO(),
        USER_ADMINISTRATOR.getId(),
        langCode);
    UserDTO userDTO = userController.fetchDTOFromId(dto.getUserDTO());
    //(2)
    InetAddress ipAddressFrom = null;
    try {
      ipAddressFrom = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      // ipAddressFrom = InetAddress.getByName("172.168.29.33");
      // TODO ...
    }

    //(1) Create new UserQuestionnaireDTO to get ID.
    UserQuestionnaireDTO newUserQuestionnaireDTO = processSavingUserQuestionnaire( questionnaireDTO,
        userDTO,
        (ipAddressFrom != null ? ipAddressFrom.getHostAddress() : null),
        langCode );

    if (newUserQuestionnaireDTO == null) {
      return null;
    }

    //(2) Process QuestionAnswerDTO list:
    List <QuestionAnswerDTO> questionAnswersDTOList = dto.getQuestionAnswersDTO();
    for (QuestionAnswerDTO n : questionAnswersDTOList) {

      //(2a) Create new QuestionAnswerDTO to get ID.
      QuestionAnswerDTO newQuestionAnswerDTO = processSavingQuestionAnswer( newUserQuestionnaireDTO,
          n.getQuestionDTO(),
          langCode);

      List<AnswerSelectedDTO> answerSelectedDTOList = n.getAnswerSelectionsDTO();
      AnswerProvidedDTO answerProvidedDTO = n.getAnswerProvidedDTO();
      //(2b-1) Process AnswerProvidedDTO:
      if (answerProvidedDTO != null) {
        AnswerProvidedDTO newAnswerProvidedDTO = processASavingAnswerProvided( newQuestionAnswerDTO,
            answerProvidedDTO.getAnswer(),
            langCode);

        n.setAnswerProvidedDTO(newAnswerProvidedDTO);
        //(2b-2) Process AnswerSelectedDTO list:
      } else if( ! ((answerSelectedDTOList ==null) || answerSelectedDTOList.isEmpty())) {
        List<OptionDTO> optionDTOList = new ArrayList<>();
        // Collect OptionDTO list:
        for (AnswerSelectedDTO m : answerSelectedDTOList) {
          OptionDTO optionDTO = optionController.fetchDTOFromId(m.getOptionDTO(), USER_ADMINISTRATOR.getId(), langCode);
          optionDTOList.add(optionDTO);
        }

        // Process OptionDTO list:
        List<AnswerSelectedDTO> newAnswerSelectedDTOList =
            processSavingSelectedAnswersToQuestion( newQuestionAnswerDTO,
                optionDTOList,
                langCode );
        n.setAnswerSelectionsDTO(newAnswerSelectedDTOList);
      }
    }
    return newUserQuestionnaireDTO;
  }

  public static UserQuestionnaireDTO POLITICS1_US2024_1;
  public static UserQuestionnaireDTO POLITICS1_US2024_2;
  public static UserQuestionnaireDTO POLITICS1_US2024_3;

  public static UserQuestionnaireDTO SOCIAL_MEDIA1;
  public static UserQuestionnaireDTO SOCIAL_MEDIA2;
  public static UserQuestionnaireDTO SOCIAL_MEDIA3;

  public static UserQuestionnaireDTO PEOPLE_IDENTIFY_YOURSELF1;
  public static UserQuestionnaireDTO PEOPLE_IDENTIFY_YOURSELF2;
  public static UserQuestionnaireDTO PEOPLE_IDENTIFY_YOURSELF3;

  private void loadData() {
    try {
      UserDTO USER_ANONYMOUS_EN = userController.fetchDTOByAnonymousFlag("en");
      UserDTO USER_SAMPLE1_EN   = userController.getFirstUserFromList("Johny", "Walker");
      UserDTO USER_SAMPLE2_PL   = userController.getFirstUserFromList("Wanda", "Trzeboszewska");

      //---/ Questionnaire (1) /-------------------------------------------------------//
      // for lang: EN
      Optional<QuestionnaireDTO> OPT_QUESTNR_POL_US_2024_ELECTIONS_EN =
          questionnaireController.fetchDTOFromCode( DataLoaderQuestionnairesApp.QUESTNR_POL_US_2024_ELECTIONS,
              USER_ADMINISTRATOR.getId(),
              "en");
      QuestionnaireDTO QUESTNR_POL_US_2024_ELECTIONS_EN = OPT_QUESTNR_POL_US_2024_ELECTIONS_EN.get();
      // for lang: PL
      Optional<QuestionnaireDTO> OPT_QUESTNR_POL_US_2024_ELECTIONS_PL =
          questionnaireController.fetchDTOFromCode( DataLoaderQuestionnairesApp.QUESTNR_POL_US_2024_ELECTIONS,
              USER_ADMINISTRATOR.getId(),
              USER_SAMPLE2_PL.getPreferredLang());
      QuestionnaireDTO QUESTNR_POL_US_2024_ELECTIONS_PL = OPT_QUESTNR_POL_US_2024_ELECTIONS_PL.get();
      //---/ Questionnaire (1) /-------------------------------------------------------//

      //---/ Questionnaire (2) /-------------------------------------------------------//
      // for lang: EN
      Optional<QuestionnaireDTO> OPT_QUESTNR_SMD_SURVEY_EN =
          questionnaireController.fetchDTOFromCode( DataLoaderQuestionnairesApp.QUESTNR_SMD_SURVEY,
              USER_ADMINISTRATOR.getId(),
              "en");
      QuestionnaireDTO QUESTNR_SMD_SURVEY_EN = OPT_QUESTNR_SMD_SURVEY_EN.get();

      // for lang: PL
      Optional<QuestionnaireDTO> OPT_QUESTNR_SMD_SURVEY_PL =
          questionnaireController.fetchDTOFromCode( DataLoaderQuestionnairesApp.QUESTNR_SMD_SURVEY,
              USER_ADMINISTRATOR.getId(),
              USER_SAMPLE2_PL.getPreferredLang());
      QuestionnaireDTO QUESTNR_SMD_SURVEY_PL = OPT_QUESTNR_SMD_SURVEY_PL.get();
      //---/ Questionnaire (2) /-------------------------------------------------------//

      //---/ Questionnaire (3) /-------------------------------------------------------//
      // for lang: EN
      Optional<QuestionnaireDTO> OPT_QUESTNR_PPL_IDENTIFY_YOURSELF_EN =
          questionnaireController.fetchDTOFromCode( DataLoaderQuestionnairesApp.QUESTNR_PPL_IDENTIFY_YOURSELF,
              USER_ADMINISTRATOR.getId(),
              "en");
      QuestionnaireDTO QUESTNR_PPL_IDENTIFY_YOURSELF_EN = OPT_QUESTNR_PPL_IDENTIFY_YOURSELF_EN.get();

      // for lang: PL
      Optional<QuestionnaireDTO> OPT_QUESTNR_PPL_IDENTIFY_YOURSELF_PL =
          questionnaireController.fetchDTOFromCode( DataLoaderQuestionnairesApp.QUESTNR_PPL_IDENTIFY_YOURSELF,
              USER_ADMINISTRATOR.getId(),
              USER_SAMPLE2_PL.getPreferredLang());
      QuestionnaireDTO QUESTNR_PPL_IDENTIFY_YOURSELF_PL = OPT_QUESTNR_PPL_IDENTIFY_YOURSELF_PL.get();
      //---/ Questionnaire (3) /-------------------------------------------------------//

      POLITICS1_US2024_1 = createUserAnsweringQuestionnaire(USER_ANONYMOUS_EN, QUESTNR_POL_US_2024_ELECTIONS_EN,
          new String[]{DataLoaderOptionsApp.OPT_CODE_USEL2024_BIDEN});
      POLITICS1_US2024_2 = createUserAnsweringQuestionnaire(USER_SAMPLE1_EN, QUESTNR_POL_US_2024_ELECTIONS_EN,
          new String[]{DataLoaderOptionsApp.OPT_CODE_USEL2024_TRUMP});
      POLITICS1_US2024_3 = createUserAnsweringQuestionnaire(USER_SAMPLE2_PL, QUESTNR_POL_US_2024_ELECTIONS_PL,
          new String[]{DataLoaderOptionsApp.OPT_CODE_USEL2024_OTHER_REP});

      SOCIAL_MEDIA1 = createUserAnsweringQuestionnaire(USER_ANONYMOUS_EN, QUESTNR_SMD_SURVEY_EN,
          new String[]{DataLoaderOptionsApp.OPT_CODE_SOCMED_TWITTER});
      SOCIAL_MEDIA2 = createUserAnsweringQuestionnaire(USER_SAMPLE1_EN, QUESTNR_SMD_SURVEY_EN,
          new String[]{DataLoaderOptionsApp.OPT_CODE_SOCMED_FACEBOOK, DataLoaderOptionsApp.OPT_CODE_SOCMED_TWITTER});
      SOCIAL_MEDIA3 = createUserAnsweringQuestionnaire(USER_SAMPLE2_PL, QUESTNR_SMD_SURVEY_PL,
          new String[]{DataLoaderOptionsApp.OPT_CODE_SOCMED_INSTAGRAM, DataLoaderOptionsApp.OPT_CODE_SOCMED_TIKTOK, DataLoaderOptionsApp.OPT_CODE_SOCMED_TELEGRAM});

      PEOPLE_IDENTIFY_YOURSELF1 = createUserAnsweringQuestionnaire(USER_ANONYMOUS_EN, QUESTNR_PPL_IDENTIFY_YOURSELF_EN, null);
      PEOPLE_IDENTIFY_YOURSELF2 = createUserAnsweringQuestionnaire(USER_SAMPLE1_EN, QUESTNR_PPL_IDENTIFY_YOURSELF_EN, null);
      PEOPLE_IDENTIFY_YOURSELF3 = createUserAnsweringQuestionnaire(USER_SAMPLE2_PL, QUESTNR_PPL_IDENTIFY_YOURSELF_EN, null);

    } catch (NotFoundException e) {
      log.error(e);
    }
  }

  @Transactional
  private UserQuestionnaireDTO createUserAnsweringQuestionnaire( UserDTO userDTO,
                                                                 QuestionnaireDTO questionnaireDTO,
                                                                 String[] desiredCodes) {

    //(1)
    InetAddress ipAddressFrom;
    try {
      ipAddressFrom = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      ipAddressFrom = null;
      log.warn("Cannot obtain Local IP Address: ", e);
    }

    //(2)
    Set<QuestionDTO> questionsDTO = questionnaireController.getAllQuestionsForQuestionnaire(
        questionnaireDTO.getId(),
        USER_ADMINISTRATOR.getId(),
        userDTO.getPreferredLang());

    //(3)
    UserQuestionnaireDTO userQuestionnaireDTO =
        UserQuestionnaireController.createUserQuestionnaireDTO( userDTO,
            questionnaireDTO,
            userDTO.getPreferredLang(),
            (ipAddressFrom != null ? ipAddressFrom.getHostAddress() : null),
            UserQuestionnaireStatuses.UNKNOWN,
            Collections.emptyList() );

    //(4)
    List<QuestionAnswerDTO> questionAnswersDTOList =
        buildAnswers2QuestionsList( userQuestionnaireDTO,
            questionsDTO,
            desiredCodes );

    userQuestionnaireDTO.setQuestionAnswersDTO(questionAnswersDTOList);
    return userQuestionnaireDTO;
  }

  private List<QuestionAnswerDTO> buildAnswers2QuestionsList( UserQuestionnaireDTO userQuestionnaireDTO,
                                                              Set<QuestionDTO> questionsDTO,
                                                              String[] desiredCodes ) {

    List<QuestionAnswerDTO> ret = new ArrayList<>();
    for (QuestionDTO questionDTO : questionsDTO) {
      QuestionAnswerDTO questionAnswerDTO = getQuestionAnswerDTO(userQuestionnaireDTO, questionDTO, desiredCodes);
      ret.add(questionAnswerDTO);
    }
    return ret;
  }

  private QuestionAnswerDTO getQuestionAnswerDTO( UserQuestionnaireDTO userQuestionnaireDto,
                                                  QuestionDTO questionDto,
                                                  String[] desiredCodes ) {

    QuestionAnswerDTO questionAnswerDto = createNewQuestionAnswerDTO( userQuestionnaireDto,
        questionDto.getId(),
        Collections.emptyList(),
       null );

    boolean isUserInput = false;
    EnumCommon enumCommon =
        EnumCommon.getEnumFromCode(AnswerTypes.values(), questionDto.getAnswerType());
    if (enumCommon != null) {
      AnswerTypes answerTypes = (AnswerTypes)enumCommon; // cast to AnswerTypes.
      isUserInput = AnswerTypes.isOfUserInputType(answerTypes);
    }

    if (isUserInput) {
      AnswerProvidedDTO answerProvidedDTO = getProvidedAnswer(questionAnswerDto);
      questionAnswerDto.setAnswerProvidedDTO(answerProvidedDTO);
    } else {
      List<AnswerSelectedDTO> list = getSelectedAnswers(questionDto, questionAnswerDto, desiredCodes);
      questionAnswerDto.setAnswerSelectionsDTO(list);
    }
    return questionAnswerDto;
  }

  private List<AnswerSelectedDTO> getSelectedAnswers( QuestionDTO questionDTO,
                                                      QuestionAnswerDTO questionAnswerDTO,
                                                      String[] desiredCodes) {
    List<OptionDTO> options = questionDTO.getOptions();
    if ((options == null) || options.isEmpty()) {
      return Collections.emptyList();
    }

    List<AnswerSelectedDTO> ret = new ArrayList<>();
    boolean isMultichoice = false;
    EnumCommon enumCommon =
        EnumCommon.getEnumFromCode(AnswerTypes.values(), questionDTO.getAnswerType());
    if (enumCommon != null) {
      AnswerTypes answerTypes = (AnswerTypes)enumCommon; // cast to AnswerTypes.
      isMultichoice = AnswerTypes.isMultiSelectionChoice(answerTypes);
    }

    if ( ! ((desiredCodes == null) || desiredCodes.length == 0)) {
      for (String n : desiredCodes) {
        Optional<OptionDTO> answerOpt = options
            .stream()
            .filter(d -> d.getCode().equalsIgnoreCase(n))
            .findFirst();
        if (answerOpt.isPresent()) {
          AnswerSelectedDTO answerDTO = new AnswerSelectedDTO(questionAnswerDTO.getId(), answerOpt.get().getId());
          ret.add(answerDTO);
        }
      }
    }

    // Check if desired answer(s) were provided, otherwise add some default
    if (ret.isEmpty()) {
      AnswerSelectedDTO answer0 = new AnswerSelectedDTO(questionAnswerDTO.getId(), options.get(0).getId());
      ret.add(answer0);
      // For multi-choice some more
      if (isMultichoice) {
        if (options.size() > 1) {
          AnswerSelectedDTO answer1 = new AnswerSelectedDTO(questionAnswerDTO.getId(), options.get(1).getId());
          ret.add(answer1);
        }
        if (options.size() > 2) { // last one
          AnswerSelectedDTO answer99 = new AnswerSelectedDTO(questionAnswerDTO.getId(), options.get(options.size() -1).getId());
          ret.add(answer99);
        }
      }
    }
    return ret;
  }

  private AnswerProvidedDTO getProvidedAnswer(QuestionAnswerDTO questionAnswerDTO) {
    int questionId = questionAnswerDTO.getQuestionDTO();
    QuestionDTO questionDTO =questionController.fetchDTOFromId(questionId, USER_ADMINISTRATOR.getId(), Constants.DEFAULT_LOCALE);
    String valueType = questionDTO.getAnswerType(); // AnswerTypes.NUMBER_INTEGER.getCode();
    String valueAsStr = "12";
    GenericValue value = new GenericValue(valueType, valueAsStr);
    return new AnswerProvidedDTO(questionAnswerDTO.getId(), value);
  }

  //========================================================================//
  //===/ Saving part /======================================================//
  private List<AnswerSelectedDTO> processSavingSelectedAnswersToQuestion( QuestionAnswerDTO questionAnswerDTO,
                                                                          List<OptionDTO> listDto,
                                                                          String langCode ) {
    List<AnswerSelectedDTO> ret = new ArrayList<>();
    for (OptionDTO n : listDto) {
      AnswerSelectedDTO newDto = processSavingAnswerSelected(questionAnswerDTO, n, langCode);
      ret.add(newDto);
    }
    return ret;
  }

  private UserQuestionnaireDTO processSavingUserQuestionnaire( QuestionnaireDTO questionnaireDTO,
                                                               UserDTO userDTO,
                                                               String ipAddressFrom,
                                                               String langCode) {

    UserQuestionnaireDTO dto =
        UserQuestionnaireController.createUserQuestionnaireDTO( userDTO,
            questionnaireDTO,
            langCode,
            ipAddressFrom,
            UserQuestionnaireStatuses.COMPLETED,
            Collections.emptyList() );
    try {
      UserQuestionnaireDTO newDto = userQuestionnaireController.executeCreateItem(dto, langCode);
      log.info("New UserQuestionnaireDTO item was created: ID={}", newDto.getId());
      return newDto;
    } catch (Exception e) {
      log.error(e);
      log.error("Cannot save UserQuestionnaireDTO for User ID: {} (language={})", userDTO.getId(), langCode);
      return null;
    }
  }

  private QuestionAnswerDTO processSavingQuestionAnswer( UserQuestionnaireDTO userQuestionnaireDto,
                                                         int questionId, // QuestionDTO questionDto,
                                                         String langCode ) {

    QuestionAnswerDTO dto = createNewQuestionAnswerDTO( userQuestionnaireDto,
                                                        questionId,
                                                        Collections.emptyList(),
                                       null );

    try {
      QuestionAnswerDTO newDto = questionAnswerController.executeCreateItem(dto, langCode);
      log.info("New QuestionAnswerDTO item was created: ID={}", newDto.getId());
      return newDto;
    } catch (Exception e) {
      log.error(e);
      log.error("Cannot save QuestionAnswerDTO for Question ID: {} (language={})", questionId, langCode);
      return null;
    }
  }

  private QuestionAnswerDTO createNewQuestionAnswerDTO( UserQuestionnaireDTO userQuestionnaireDto,
                                                        int questionId,
                                                        List<AnswerSelectedDTO> answerSelectionsDTO,
                                                        AnswerProvidedDTO answerProvidedDTO ) {
    return QuestionAnswerController.createQuestionAnswerDTO( userQuestionnaireDto,
        questionId,
        answerSelectionsDTO,
        answerProvidedDTO );
  }

  private AnswerProvidedDTO processASavingAnswerProvided( QuestionAnswerDTO questionAnswerDTO,
                                                          GenericValue genericValue,
                                                          String langCode ) {

    AnswerProvidedDTO dto = new AnswerProvidedDTO(questionAnswerDTO.getId(), genericValue);
    try {
      AnswerProvidedDTO newDto = answerProvidedController.executeCreateItem(dto, langCode);
      log.info("New AnswerProvidedDTO item was created: ID={}", newDto.getId());
      return newDto;
    } catch (Exception e) {
      log.error(e);
      log.error("Cannot save AnswerProvidedDTO for QuestionAnswer ID: {} (language={})", dto.getQuestionAnswerDTO().toString(), langCode);
      return null;
    }
  }

  private AnswerSelectedDTO processSavingAnswerSelected( QuestionAnswerDTO questionAnswerDTO,
                                                         OptionDTO optionDTO,
                                                         String langCode ) {

    AnswerSelectedDTO dto = new AnswerSelectedDTO(questionAnswerDTO.getId(), optionDTO.getId());
    try {
      AnswerSelectedDTO newDto = answerSelectedController.executeCreateItem(dto, langCode);
      log.info("New AnswerSelectedDTO item was created: ID={}", newDto.getId());
      return newDto;
    } catch (Exception e) {
      log.error(e);
      log.error("Cannot save AnswerSelectedDTO for QuestionAnswerDTO ID: {} (language={})", questionAnswerDTO.getId().toString(), langCode);
      return null;
    }
  }
}