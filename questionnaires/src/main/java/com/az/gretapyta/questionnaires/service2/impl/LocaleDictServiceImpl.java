package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.model.DictionaryEntry;
import com.az.gretapyta.qcore.model.EntityDictionary;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.service2.LocaleDictService;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class LocaleDictServiceImpl implements LocaleDictService {
  private static final String DEFAULT_DATA_TYPE_4CLIENT = "string";

  private static final String DTYPE_4CLIENT_STRING = "string";
  private static final String DTYPE_4CLIENT_NUMBER = "number";
  private static final String DTYPE_4CLIENT_DATE = "date";
  private static final String DTYPE_4CLIENT_BOOLEAN = "boolean";
  private static final String DTYPE_4CLIENT_ARRAY = "array";

  public static final String ENTITY_DRAWER = "Drawer";
  public static final String ENTITY_QUESTIONNAIRE = "Questionnaire";
  public static final String ENTITY_STEP = "Step";
  public static final String ENTITY_QUESTION = "Question";
  public static final String ENTITY_OPTION = "Option";

  public static final String ENTITY_USER = "User";
  public static final String ENTITY_USER_MESSAGE = "UserMessage";

  public List<EntityDictionary> getAllEntitiesDictionary(String langCode) {
    List<EntityDictionary> ret = new ArrayList<>();

    ret.add(buildDrawerEntityDictionary(langCode));
    ret.add(buildQuestionnaireEntityDictionary(langCode));
    ret.add(buildStepEntityDictionary(langCode));
    ret.add(buildQuestionEntityDictionary(langCode));
    ret.add(buildOptionEntityDictionary(langCode));

    ret.add(buildUserEntityDictionary(langCode));
    ret.add(buildUserMessageEntityDictionary(langCode));

    return ret;
  }

  public Optional<EntityDictionary> getEntityDictionary(String entityName, String langCode) {
    return getAllEntitiesDictionary(langCode)
        .stream().filter(d -> entityName.equalsIgnoreCase(d.getEntityName()))
        .findFirst();
  }

  //---/ Serving Part /------------------------------------//
  // (1)
  private static EntityDictionary buildDrawerEntityDictionary(String langCode) {
    EntityDictionary ret = new EntityDictionary(ENTITY_DRAWER);
    String txt;
    //(1)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.id", langCode);
    addEntry2Dictionary(ret, "id", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.id", txt, txt,"");
    //(2)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.code", langCode);
    addEntry2Dictionary(ret, "code", DTYPE_4CLIENT_STRING, "entity._generic.caption.code", txt, txt, "");
    //(3)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.name", langCode);
    addEntry2Dictionary(ret, "name", DTYPE_4CLIENT_STRING,"entity._generic.caption.name", txt, txt, "");
    //(4)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.created", langCode);
    addEntry2Dictionary(ret, "created", DTYPE_4CLIENT_DATE,"entity._generic.caption.created", txt, txt, "");
    //(5)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.updated", langCode);
    addEntry2Dictionary(ret, "updated", DTYPE_4CLIENT_DATE,"entity._generic.caption.updated", txt, txt, "");
    //(6)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.ready_to_show", langCode);
    addEntry2Dictionary(ret, "ready2Show", DTYPE_4CLIENT_BOOLEAN,"entity._generic.caption.ready_to_show", txt, txt, "");
    //(7)
    txt = CommonUtilities.getTranslatableMessage("entity.drawer.caption.questionnaires", langCode);
    addEntry2Dictionary(ret, "questionnaires", DTYPE_4CLIENT_ARRAY,"entity.drawer.caption.questionnaires", txt, txt, "");

    return ret;
  }

  // (2)
  private static EntityDictionary buildQuestionnaireEntityDictionary(String langCode) {
    EntityDictionary ret = new EntityDictionary(ENTITY_QUESTIONNAIRE);
    String txt;
    //(1)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.id", langCode);
    addEntry2Dictionary(ret, "id", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.id", txt, txt,"");
    //(2)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.code", langCode);
    addEntry2Dictionary(ret, "code", DTYPE_4CLIENT_STRING, "entity._generic.caption.code", txt, txt, "");
    //(3)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.name", langCode);
    addEntry2Dictionary(ret, "name", DTYPE_4CLIENT_STRING,"entity._generic.caption.name", txt, txt, "");
    //(4)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.description", langCode);
    addEntry2Dictionary(ret, "description", DTYPE_4CLIENT_STRING,"entity._generic.caption.description", txt, txt, "");
    //(5)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.created", langCode);
    addEntry2Dictionary(ret, "created", DTYPE_4CLIENT_DATE,"entity._generic.caption.created", txt, txt, "");
    //(6)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.updated", langCode);
    addEntry2Dictionary(ret, "updated", DTYPE_4CLIENT_DATE,"entity._generic.caption.updated", txt, txt, "");
    //(7)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.ready_to_show", langCode);
    addEntry2Dictionary(ret, "ready2Show", DTYPE_4CLIENT_BOOLEAN,"entity._generic.caption.ready_to_show", txt, txt, "");
    //(8)
    txt = CommonUtilities.getTranslatableMessage("entity.questionnaire.caption.type", langCode);
    addEntry2Dictionary(ret, "questionnaireType", DTYPE_4CLIENT_STRING,"entity.questionnaire.caption.type", txt, txt, "");
    //(9)
    txt = CommonUtilities.getTranslatableMessage("entity.questionnaire.caption.url-id-name", langCode);
    addEntry2Dictionary(ret, "urlIdName", DTYPE_4CLIENT_STRING,"entity.questionnaire.caption.url-id-name", txt, txt, "");
    //(10)
    txt = CommonUtilities.getTranslatableMessage("entity.questionnaire.caption.commercial-usage", langCode);
    addEntry2Dictionary(ret, "commercialUsage", DTYPE_4CLIENT_BOOLEAN,"entity.questionnaire.caption.commercial-usage", txt, txt, "");
    //(11)
    txt = CommonUtilities.getTranslatableMessage("entity.questionnaire.caption.stats-count", langCode);
    addEntry2Dictionary(ret, "statsCntr", DTYPE_4CLIENT_NUMBER,"entity.questionnaire.caption.stats-count", txt, txt, "");
    //(12)
    txt = CommonUtilities.getTranslatableMessage("entity.questionnaire.caption.steps", langCode);
    addEntry2Dictionary(ret, "steps", DTYPE_4CLIENT_ARRAY,"entity.questionnaire.caption.steps", txt, txt, "");

    return ret;
  }

  // (3)
  private static EntityDictionary buildStepEntityDictionary(String langCode) {
    EntityDictionary ret = new EntityDictionary(ENTITY_STEP);
    String txt;
    //(1)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.id", langCode);
    addEntry2Dictionary(ret, "id", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.id", txt, txt,"");
    //(2)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.name", langCode);
    addEntry2Dictionary(ret, "name", DTYPE_4CLIENT_STRING,"entity._generic.caption.name", txt, txt, "");
    //(3)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.created", langCode);
    addEntry2Dictionary(ret, "created", DTYPE_4CLIENT_DATE,"entity._generic.caption.created", txt, txt, "");
    //(4)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.updated", langCode);
    addEntry2Dictionary(ret, "updated", DTYPE_4CLIENT_DATE,"entity._generic.caption.updated", txt, txt, "");
    //(5)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.ready_to_show", langCode);
    addEntry2Dictionary(ret, "ready2Show", DTYPE_4CLIENT_BOOLEAN,"entity._generic.caption.ready_to_show", txt, txt, "");
    //(6)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.order", langCode);
    addEntry2Dictionary(ret, "displayOrder", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.order", txt, txt, "");
    //(7)
    txt = CommonUtilities.getTranslatableMessage("entity.step.caption.questions", langCode);
    addEntry2Dictionary(ret, "questions", DTYPE_4CLIENT_ARRAY,"entity.step.caption.questions", txt, txt, "");

    return ret;
  }

  // (4)
  private static EntityDictionary buildQuestionEntityDictionary(String langCode) {
    EntityDictionary ret = new EntityDictionary(ENTITY_QUESTION);
    String txt;
    //(1)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.id", langCode);
    addEntry2Dictionary(ret, "id", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.id", txt, txt,"");
    //(2)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.created", langCode);
    addEntry2Dictionary(ret, "created", DTYPE_4CLIENT_DATE,"entity._generic.caption.created", txt, txt, "");
    //(4)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.updated", langCode);
    addEntry2Dictionary(ret, "updated", DTYPE_4CLIENT_DATE,"entity._generic.caption.updated", txt, txt, "");
    //(5)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.ready_to_show", langCode);
    addEntry2Dictionary(ret, "ready2Show", DTYPE_4CLIENT_BOOLEAN,"entity._generic.caption.ready_to_show", txt, txt, "");
    //(6)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.code", langCode);
    addEntry2Dictionary(ret, "code", DTYPE_4CLIENT_STRING, "entity._generic.caption.code", txt, txt, "");
    //(7)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.order", langCode);
    addEntry2Dictionary(ret, "displayOrder", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.order", txt, txt, "");
    //(8)
    txt = CommonUtilities.getTranslatableMessage("entity.question.caption.question_intro", langCode);
    addEntry2Dictionary(ret, "title", DTYPE_4CLIENT_STRING,"entity.question.caption.question_intro", txt, txt, "");
    //(9)
    txt = CommonUtilities.getTranslatableMessage("entity.question.caption.question_asked", langCode);
    addEntry2Dictionary(ret, "questionAsked", DTYPE_4CLIENT_STRING, "entity.question.caption.question_asked", txt, txt, "");
    //(10)
    txt = CommonUtilities.getTranslatableMessage("entity.question.caption.answer_type", langCode);
    addEntry2Dictionary(ret, "answerType", DTYPE_4CLIENT_STRING, "entity.question.caption.answer_type", txt, txt, "");
    //(11)
    txt = CommonUtilities.getTranslatableMessage("entity.question.caption.answer_options", langCode);
    addEntry2Dictionary(ret, "options", DTYPE_4CLIENT_ARRAY,"entity.question.caption.answer_options", txt, txt, "");

    return ret;
  }

  // (5)
  private static EntityDictionary buildOptionEntityDictionary(String langCode) {
    EntityDictionary ret = new EntityDictionary(ENTITY_OPTION);
    String txt;
    //(1)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.id", langCode);
    addEntry2Dictionary(ret, "id", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.id", txt, txt,"");
    //(2)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.created", langCode);
    addEntry2Dictionary(ret, "created", DTYPE_4CLIENT_DATE,"entity._generic.caption.created", txt, txt, "");
    //(3)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.updated", langCode);
    addEntry2Dictionary(ret, "updated", DTYPE_4CLIENT_DATE,"entity._generic.caption.updated", txt, txt, "");
    //(4)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.ready_to_show", langCode);
    addEntry2Dictionary(ret, "ready2Show", DTYPE_4CLIENT_BOOLEAN,"entity._generic.caption.ready_to_show", txt, txt, "");
    //(5)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.name", langCode);
    addEntry2Dictionary(ret, "name", DTYPE_4CLIENT_STRING,"entity._generic.caption.name", txt, txt, "");
    //(6)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.code", langCode);
    addEntry2Dictionary(ret, "code", DTYPE_4CLIENT_STRING, "entity._generic.caption.code", txt, txt, "");
    //(7)
    txt = CommonUtilities.getTranslatableMessage("entity.option.caption.help_text", langCode);
    addEntry2Dictionary(ret, "help", DTYPE_4CLIENT_STRING,"entity.option.caption.help_text", txt, txt, "");
    //(8)
    txt = CommonUtilities.getTranslatableMessage("entity.option.caption.is_preferred_answer", langCode);
    addEntry2Dictionary(ret, "preferredAnswer", DTYPE_4CLIENT_BOOLEAN,"entity.option.caption.is_preferred_answer", txt, txt, "");
    //(9)
    txt = CommonUtilities.getTranslatableMessage("entity.option.caption.usage_stats_number", langCode);
    addEntry2Dictionary(ret, "statsCntr", DTYPE_4CLIENT_NUMBER,"entity.option.caption.usage_stats_number", txt, txt,"");

    return ret;
  }

  // (2-1)
  private static EntityDictionary buildUserEntityDictionary(String langCode) {
    EntityDictionary ret = new EntityDictionary(ENTITY_USER);
    String txt;
    //(1)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.id", langCode);
    addEntry2Dictionary(ret, "id", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.id", txt, txt, "");
    //(2)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.created", langCode);
    addEntry2Dictionary(ret, "created", DTYPE_4CLIENT_DATE,"entity._generic.caption.created", txt, txt, "");
    //(3)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.updated", langCode);
    addEntry2Dictionary(ret, "updated", DTYPE_4CLIENT_DATE,"entity._generic.caption.updated", txt, txt, "");
    //(4)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.first_name", langCode);
    addEntry2Dictionary(ret, "firstName", DTYPE_4CLIENT_STRING,"entity.user.caption.first_name", txt, txt, "");
    //(5)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.middle_name", langCode);
    addEntry2Dictionary(ret, "middleName", DTYPE_4CLIENT_STRING,"entity.user.caption.middle_name", txt, txt, "");
    //(6)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.last_name", langCode);
    addEntry2Dictionary(ret, "lastName", DTYPE_4CLIENT_STRING,"entity.user.caption.last_name", txt, txt, "");
    //(7)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.email", langCode);
    addEntry2Dictionary(ret, "emailAddress", DTYPE_4CLIENT_STRING,"entity.user.caption.email", txt, txt, "");
    //(8)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.gender", langCode);
    addEntry2Dictionary(ret, "gender", DTYPE_4CLIENT_STRING,"entity.user.caption.gender", txt, txt, "");
    //(9)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.dob", langCode);
    addEntry2Dictionary(ret, "birthday", DTYPE_4CLIENT_DATE,"entity.user.caption.dob", txt, txt, "");
    //(10)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.age", langCode);
    addEntry2Dictionary(ret, "age", DTYPE_4CLIENT_NUMBER,"entity.user.caption.age", txt, txt, "");
    //(11)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.login", langCode);
    addEntry2Dictionary(ret, "loginName", DTYPE_4CLIENT_STRING,"entity.user.caption.login", txt, txt, "");
    //(12)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.preferred_lang", langCode);
    addEntry2Dictionary(ret, "preferredLang", DTYPE_4CLIENT_STRING,"entity.user.caption.preferred_lang", txt, txt, "");
    //(13)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.is_anonymous_user", langCode);
    addEntry2Dictionary(ret, "anonymousUser", DTYPE_4CLIENT_BOOLEAN, "entity.user.caption.is_anonymous_user", txt, txt, "");
    //(14)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.role", langCode);
    addEntry2Dictionary(ret, "role", DTYPE_4CLIENT_STRING,"entity.user.caption.role", txt, txt, "");
    //(15)
    txt = CommonUtilities.getTranslatableMessage("entity.user.caption.questionnaires_taken", langCode);
    addEntry2Dictionary(ret, "userQuestionnairesDTO", DTYPE_4CLIENT_ARRAY,"entity.user.caption.questionnaires_taken", txt, txt, "");

    return ret;
  }

  // (2-2)
  private static EntityDictionary buildUserMessageEntityDictionary(String langCode) {
    EntityDictionary ret = new EntityDictionary(ENTITY_USER_MESSAGE);
    String txt;
    //(1)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.id", langCode);
    addEntry2Dictionary(ret, "id", DTYPE_4CLIENT_NUMBER,"entity._generic.caption.id", txt, txt,"");
    //(2)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.created", langCode);
    addEntry2Dictionary(ret, "created", DTYPE_4CLIENT_DATE,"entity._generic.caption.created", txt, txt, "");
    //(3)
    txt = CommonUtilities.getTranslatableMessage("entity._generic.caption.updated", langCode);
    addEntry2Dictionary(ret, "updated", DTYPE_4CLIENT_DATE,"entity._generic.caption.updated", txt, txt, "");
    //(4)
    txt = CommonUtilities.getTranslatableMessage("entity.user_message.caption.message", langCode);
    addEntry2Dictionary(ret, "message", DTYPE_4CLIENT_STRING,"entity.user_message.caption.message", txt, txt, "");
    //(5)
    txt = CommonUtilities.getTranslatableMessage("entity.user_message.caption.visibility_level", langCode);
    addEntry2Dictionary(ret, "visibilityLevel", DTYPE_4CLIENT_STRING,"entity.user_message.caption.visibility_level", txt, txt, "");
    //(6)
    txt = CommonUtilities.getTranslatableMessage("entity.user_message.caption.for_user", langCode);
    addEntry2Dictionary(ret, "forUserId", DTYPE_4CLIENT_NUMBER,"entity.user_message.caption.for_user", txt, txt, "");

    return ret;
  }

  private static void addEntry2Dictionary( EntityDictionary dictionary,
                                    String attribName,
                                    String dataType4Client,
                                    String txtKey,
                                    String caption,
                                    String title,
                                    String placeholder ) {

    dictionary.add(attribName, new DictionaryEntry (
        attribName,
        dataType4Client,
        txtKey,
        caption,
        title,
        placeholder
    ));
  }
  //---/ Serving PArt /------------------------------------//
}