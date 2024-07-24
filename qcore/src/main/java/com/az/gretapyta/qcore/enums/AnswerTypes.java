package com.az.gretapyta.qcore.enums;

import java.util.Set;

public enum AnswerTypes implements EnumCommon {
  TEXT("TXT", "Text"),
  DATE("DAT", "Date"),
  MONEY("MON", "Money"),
  NUMBER_INTEGER("NBI", "Number-Integer"),
  NUMBER_DECIMAL("NBD", "Number-Decimal"),
  YES_NO("BYN", "Y/N Choice"),
  RADIO_BUTTONS("RDB", "Radio Buttons"),
  MULTI_CHOICE("MLC", "Multiple choices"),
  LIST_CHOICE("LIC", "1 Choice from List");

  private final String code;
  private final String label;

  AnswerTypes(String code, String label) {
    this.label = label;
    this.code = code;
  }

  // Interface implementation part:
  @Override
  public String getCode() { return code; }
  @Override
  public String getLabel() { return label; }

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