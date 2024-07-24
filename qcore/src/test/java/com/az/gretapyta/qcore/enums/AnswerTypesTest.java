package com.az.gretapyta.qcore.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnswerTypesTest extends EnumCommonTest {

  @DisplayName("When Radio-buttons code, then Radio Buttons Enum object is returned.")
  @Test
  void test1() {
    whenEnumCode_thenEnumObjectAssert(AnswerTypes.values(), AnswerTypes.class, "RDB"); // Radio Buttons
  }

  @DisplayName("When Multi-select Enum object, then Multi-select check utility returns true.")
  @Test
  void test2() {
    assertTrue(AnswerTypes.isMultiSelectionChoice(AnswerTypes.LIST_CHOICE));
    assertFalse(AnswerTypes.isMultiSelectionChoice(AnswerTypes.RADIO_BUTTONS));
  }

  @DisplayName("When User-input Enum object, then User-input check utility returns true.")
  @Test
  void test3() {
    assertTrue(AnswerTypes.isOfUserInputType(AnswerTypes.NUMBER_INTEGER));
    assertFalse(AnswerTypes.isOfUserInputType(AnswerTypes.RADIO_BUTTONS));
  }
}