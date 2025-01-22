package com.az.gretapyta.qcore.enums;

import com.az.gretapyta.qcore.util.Constants;

import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;
import static java.util.Map.entry;

// Yes Santa, there are only 2 biological genders:
public enum GenderTypes implements EnumCommon {
  MALE("MLE",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Male"),
          entry(Constants.LOCALE_KEY_PL, "Mężczyzna"),
          entry(Constants.LOCALE_KEY_RU, "Мужской") )
  ),
  FEMALE("FML",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Female"),
          entry(Constants.LOCALE_KEY_PL, "Kobieta"),
          entry(Constants.LOCALE_KEY_RU, "Женский") )
  ),
  NOT_DECLARED("NNN",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Undeclared"),
          entry(Constants.LOCALE_KEY_PL, "Niezadeklarowany"),
          entry(Constants.LOCALE_KEY_RU, "Необъявленный") )
  );

  private final String code;
  private final Map<String, String> nameMultilang;

  GenderTypes(String code, Map<String, String> nameMultilang) {
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