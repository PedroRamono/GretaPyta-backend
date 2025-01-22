package com.az.gretapyta.qcore.enums;

import com.az.gretapyta.qcore.util.Constants;

import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;
import static java.util.Map.entry;

public enum QuestionnaireTypes implements EnumCommon {
  QUIZ("QIZ",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Quiz"),
          entry(Constants.LOCALE_KEY_PL, "Kwiz"),
          entry(Constants.LOCALE_KEY_RU, "Контрольный опрос") )
      ),
  QUESTIONNAIRE("QUE",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Questionnaire"),
          entry(Constants.LOCALE_KEY_PL, "Kwestionariusz"),
          entry(Constants.LOCALE_KEY_RU, "Вопросник") )
  ),
  SURVEY("SRV",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Survey"),
          entry(Constants.LOCALE_KEY_PL, "Ankieta"),
          entry(Constants.LOCALE_KEY_RU, "Опрос") )
  ),
  PREDICTION("PRD",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Prediction"),
          entry(Constants.LOCALE_KEY_PL, "Prognoza"),
          entry(Constants.LOCALE_KEY_RU, "Прогноз") )
  ),
  ONBOARDING("ONB",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Onboarding"),
          entry(Constants.LOCALE_KEY_PL, "Ankieta Pracownicza"),
          entry(Constants.LOCALE_KEY_RU, "Адаптация") )
  );

  private final String code;
  private final Map<String, String> nameMultilang;

  QuestionnaireTypes(String code, Map<String, String> nameMultilang) {
    this.code = code;
    this.nameMultilang = nameMultilang;
  }

  // Interface implementation part:
  @Override
  public String getCode() { return code; }

  @Override
  public String getLabel() {
    return getLabel(DEFAULT_LOCALE);
  }

  @Override
  public String getLabel(String langCode) {
    if (nameMultilang.containsKey(langCode)) {
      return (nameMultilang.get(langCode).isEmpty() ?
          getCode() :
          nameMultilang.get(langCode)); // To check/signal missing translation
    }
    return code;
  }
}