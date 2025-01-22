package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.qcore.enums.EnumCommon;
import com.az.gretapyta.qcore.util.Constants;

import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;
import static java.util.Map.entry;

public enum UserRoles implements EnumCommon {

  ADMIN("AM",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Administrator"),
          entry(Constants.LOCALE_KEY_PL, "Administrator"),
          entry(Constants.LOCALE_KEY_RU, "Администратор") )
  ),
  ADMIN_DEMO("AD",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Administrator (only for Demo)"),
          entry(Constants.LOCALE_KEY_PL, "Administrator (tylko dla Prezentacji)"),
          entry(Constants.LOCALE_KEY_RU, "Администратор (только для демо)") )
  ),
  USER_CLIENT("CU",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Commercial USer"),
          entry(Constants.LOCALE_KEY_PL, "Użytkownik komercyjny"),
          entry(Constants.LOCALE_KEY_RU, "Коммерческий пользователь") )
  ),
  USER_CASUAL("LU",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Logged USer"),
          entry(Constants.LOCALE_KEY_PL, "Zalogowany użytkownik"),
          entry(Constants.LOCALE_KEY_RU, "Зарегистрированный пользователь") )
  ),
  USER_BLOCKED("BU",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Blocked USer"),
          entry(Constants.LOCALE_KEY_PL, "Zablokowany użytkownik"),
          entry(Constants.LOCALE_KEY_RU, "Заблокированный пользователь") )
  ),
  USER_ANONYMOUS("AU",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Anonymous User"),
          entry(Constants.LOCALE_KEY_PL, "Użytkownik anonimowy"),
          entry(Constants.LOCALE_KEY_RU, "Logged USer") )
  );

  private final String code;
  private final Map<String, String> nameMultilang;

  UserRoles(String code, Map<String, String> nameMultilang) {
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