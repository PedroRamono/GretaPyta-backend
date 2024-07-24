package com.az.gretapyta.qcore.enums;

public enum UserQuestionnaireStatuses implements EnumCommon {
  COMPLETED("CPL", "Completed"),
  SAVED("SVD", "Saved - unfinished"),
  ABANDONED("ABD", "Abandoned"),
  UNKNOWN("NNN", "Unknown");

  private final String code;
  private final String label;

  UserQuestionnaireStatuses(String code, String label) {
    this.label = label;
    this.code = code;
  }

  // Interface implementation part:
  @Override
  public String getCode() { return code; }
  @Override
  public String getLabel() { return label; }
}