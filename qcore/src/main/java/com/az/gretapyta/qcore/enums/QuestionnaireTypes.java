package com.az.gretapyta.qcore.enums;

public enum QuestionnaireTypes implements EnumCommon {
  QUIZ("QIZ", "Quiz"),
  QUESTIONNAIRE("QUE", "Questionnaire"),
  SURVEY("SRV", "Survey"),
  PREDICTION("PRD", "Prediction"),
  ONBOARDING("ONB", "Onboarding");

  private final String code;
  private final String label;

  QuestionnaireTypes(String code, String label) {
    this.label = label;
    this.code = code;
  }

  // Interface implementation part:
  @Override
  public String getCode() { return code; }
  @Override
  public String getLabel() { return label; }
}