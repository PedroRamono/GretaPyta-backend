package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.qcore.enums.EnumCommon;

public enum UserRoles implements EnumCommon {
  ADMIN("AM", "Administrator"),
  USER_CLIENT("CU", "Commercial USer"),
  USER_CASUAL("LU", "Logged USer"),
  USER_ANONYMOUS("AU", "Anonymous User");

  private final String code;
  private final String label;

  UserRoles(String code, String label) {
    this.label = label;
    this.code = code;
  }

  // Interface implementation part:
  @Override
  public String getCode() { return code; }
  @Override
  public String getLabel() { return label; }
}