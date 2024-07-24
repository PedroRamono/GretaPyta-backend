package com.az.gretapyta.qcore.enums;

public enum GenderTypes implements EnumCommon {
  MALE("MLE", "Male"),
  FEMALE("FML", "Female"),
  NOT_DECLARED("NNN", "Undeclared");

  private final String code;
  private final String label;

  GenderTypes(String code, String label) {
    this.label = label;
    this.code = code;
  }

  // Interface implementation part:
  @Override
  public String getCode() { return code; }
  @Override
  public String getLabel() { return label; }
}