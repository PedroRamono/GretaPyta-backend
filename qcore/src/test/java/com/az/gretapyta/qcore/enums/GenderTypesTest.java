package com.az.gretapyta.qcore.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GenderTypesTest extends EnumCommonTest {

//  // TEST: /////////////////////////////////////////
//  String testLabel = EnumCommon.getLabelFromCode(GenderTypes.values(),"NNN");
//  String testCode = EnumCommon.getCodeFromLabel(GenderTypes.values(),"Female");
//System.out.println("Enum GenderTypes test: code = 'NNN' produces label = '" + testLabel + "'.");
//System.out.println("Enum GenderTypes test: label = 'Female' produces code = '" + testCode + "'.");


  @DisplayName("When Male enum code, then Male enum object is returned.")
  @Test
  void test1() {
    whenEnumCode_thenEnumObjectAssert(GenderTypes.values(), GenderTypes.class, "MLE");
  }

  @DisplayName("When Male enum label, then Male enum object is returned.")
  @Test
  void test2() {
    whenEnumLabel_thenEnumObjectAssert(GenderTypes.values(), GenderTypes.class,"Male");
  }

  @DisplayName("When Female enum code, then Female enum object is returned.")
  @Test
  void test3() {
    whenEnumCode_thenEnumObjectAssert(GenderTypes.values(), GenderTypes.class, "FML");
  }

  @DisplayName("When Female enum label, then Female enum object is returned.")
  @Test
  void test4() {
    whenEnumLabel_thenEnumObjectAssert(GenderTypes.values(), GenderTypes.class, "Female");
  }

  @DisplayName("When undeclared enum code, then undeclared enum object is returned.")
  @Test
  void test5() {
    whenEnumCode_thenEnumObjectAssert(GenderTypes.values(), GenderTypes.class, "NNN");
  }

  @DisplayName("When undeclared enum label, then undeclared enum object is returned.")
  @Test
  void test6() {
    whenEnumLabel_thenEnumObjectAssert(GenderTypes.values(), GenderTypes.class, "Undeclared");
  }
}