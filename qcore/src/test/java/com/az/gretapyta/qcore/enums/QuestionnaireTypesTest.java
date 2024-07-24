package com.az.gretapyta.qcore.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QuestionnaireTypesTest extends EnumCommonTest {

  @DisplayName("When Quiz enum code, then Quiz enum object is returned.")
  @Test
  void test1() {
    whenEnumCode_thenEnumObjectAssert(QuestionnaireTypes.values(), QuestionnaireTypes.class, "QIZ");
  }

  @DisplayName("When Quiz enum label, then Quiz enum object is returned.")
  @Test
  void test2() {
    whenEnumLabel_thenEnumObjectAssert(QuestionnaireTypes.values(), QuestionnaireTypes.class, "Quiz");
  }
}