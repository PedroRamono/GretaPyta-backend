package com.az.gretapyta.qcore.enums;

import com.az.gretapyta.qcore.util.Constants;

import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;
import static java.util.Map.entry;

public enum SearchOperation implements EnumCommon {

  IS_TRUE("isTrue",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Is True"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Is True"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Is True") )
  ),
  EQUAL("equals",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Equal"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Equal"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Equal") )
  ),
  EQUAL_IGNORE_CASE("equalsIgnoreCase",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Equal Ignore Case"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Equal Ignore Case"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Equal Ignore Case") )
  ),
  LIKE("like",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Like"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Like"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Like") )
  ),
  LIKE_IGNORE_CASE("likeIgnoreCase",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Like - ignore Case"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Like - ignore Case"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Like - ignore Case") )
  ),
  LESS_THAN("lessThan",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Less Than"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Less Than"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Less Than") )
  ),
  LESS_THAN_OR_EQUAL("lessThanOrEqual",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Less Than or Equal"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Less Than or Equal"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Less Than or Equal") )
  ),
  GREATER_THAN("greaterThan",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Greater Than"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Greater Than"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Greater Than") )
  ),
  GREATER_THEN_OR_EQUAL("greaterThanOrEqual",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Greater Than or Equal"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Greater Than or Equal"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Greater Than or Equal") )
  ),
  IN("isIn",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Is In"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Is In"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Is In") )
  ),
  NOT_EQUAL("notEqual",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Not Equal"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Not Equal"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Not Equal") )
  ),
  NOT_IN("isNotIn",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Is Not In"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Is Not In"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Is Not In") )
  ),
  MATCH_START("matchStart",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Match Start"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Match Start"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Match Start") )
  ),
  MATCH_END("matchEnd",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Match End"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Match End"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Match End") )
  ),

  MATCH_IN_LANG_MAP("matchInLangMap",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Match text"),
          entry(Constants.LOCALE_KEY_PL, "(pl) Match text"),
          entry(Constants.LOCALE_KEY_RU, "(ru) Match text") )
  );

  private final String code;
  private final Map<String, String> nameMultilang;

  SearchOperation(String code, Map<String, String> nameMultilang) {
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
