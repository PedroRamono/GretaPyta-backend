package com.az.gretapyta.qcore.enums;

import com.az.gretapyta.qcore.util.Constants;

import java.util.Map;
import java.util.Set;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;
import static java.util.Map.entry;

public enum AnswerTypes implements EnumCommon {
  TEXT("TXT",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Text"),
          entry(Constants.LOCALE_KEY_PL, "Tekst"),
          entry(Constants.LOCALE_KEY_RU, "Текст") )
  ),
  DATE("DAT",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Date"),
          entry(Constants.LOCALE_KEY_PL, "Data"),
          entry(Constants.LOCALE_KEY_RU, "Дата") )
  ),
  MONEY("MON",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Money"),
          entry(Constants.LOCALE_KEY_PL, "Pieniądze"),
          entry(Constants.LOCALE_KEY_RU, "Деньги") )
  ),
  NUMBER_INTEGER("NBI",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Number-Integer"),
          entry(Constants.LOCALE_KEY_PL, "Liczba całkowita"),
          entry(Constants.LOCALE_KEY_RU, "Число-Целое") )
  ),
  NUMBER_DECIMAL("NBD",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Number-Decimal"),
          entry(Constants.LOCALE_KEY_PL, "Liczba dziesiętna"),
          entry(Constants.LOCALE_KEY_RU, "Число-десятичное") )
  ),
  YES_NO("BYN",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Yes/No Choice"),
          entry(Constants.LOCALE_KEY_PL, "Wybór Tak/Nie"),
          entry(Constants.LOCALE_KEY_RU, "Выбор Да/Нет") )
  ),
  RADIO_BUTTONS("RDB",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Exclusive Choice"),
          entry(Constants.LOCALE_KEY_PL, "Pojedyńczy wybór"),
          entry(Constants.LOCALE_KEY_RU, "Эксклюзивный выбор") )),
  MULTI_CHOICE("MLC",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Multiple Choices"),
          entry(Constants.LOCALE_KEY_PL, "Wybór wielokrotny"),
          entry(Constants.LOCALE_KEY_RU, "Множественный выбор") )
  ),
  LIST_CHOICE("LIC",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Exclusive Choice from List"),
          entry(Constants.LOCALE_KEY_PL, "Pojedyńczy wybór z listy"),
          entry(Constants.LOCALE_KEY_RU, "Эксклюзивный выбор из списка") )
  );

  private final String code;
  private final Map<String, String> nameMultilang;

  AnswerTypes(String code,Map<String, String> nameMultilang) {
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

  // Specific implementation part:
  public static boolean isOfUserInputType(String enumCode) {
    return isOfUserInputType((AnswerTypes) EnumCommon.getEnumFromCode(AnswerTypes.values(), enumCode));
  }

  public static boolean isOfUserInputType(AnswerTypes e) {
    AnswerTypes[] list = { TEXT,
        DATE,
        MONEY,
        NUMBER_INTEGER,
        NUMBER_DECIMAL }; // Do not duplicate eny entry.
    return isInList(e, list);
  }

  public static boolean isMultiSelectionChoice(String enumCode) {
    return isMultiSelectionChoice((AnswerTypes) EnumCommon.getEnumFromCode(AnswerTypes.values(), enumCode));
  }

  public static boolean isMultiSelectionChoice(AnswerTypes e) {
    AnswerTypes[] list = { MULTI_CHOICE,
                           LIST_CHOICE }; // Do not duplicate eny entry.
    return isInList(e, list);
  }

  public static boolean isInList(AnswerTypes e, EnumCommon[] list) {
    Set<EnumCommon> set = Set.of(list);
    return set.contains(e);
  }
}