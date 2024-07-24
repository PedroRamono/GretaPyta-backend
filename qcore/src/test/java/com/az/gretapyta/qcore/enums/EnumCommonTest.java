package com.az.gretapyta.qcore.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class EnumCommonTest {

  protected void whenEnumCode_thenEnumObjectAssert(EnumCommon[] values, Class expectedClass, String code) {
    EnumCommon enumCommon = EnumCommon.getEnumFromCode(values, code);
    assertNotNull(enumCommon);
    assertEquals(enumCommon.getClass(), expectedClass);
    assertEquals(enumCommon.getCode(), code);
  }

  protected void whenEnumLabel_thenEnumObjectAssert(EnumCommon[] values, Class expectedClass, String label) {
    EnumCommon enumCommon = EnumCommon.getEnumFromLabel(values, label);
    assertNotNull(enumCommon);
    assertEquals(enumCommon.getClass(), expectedClass);
    assertEquals(enumCommon.getLabel(), label);
  }

//  String testLabel = EnumCommon.getLabelFromCode(GenderTypes.values(),"NNN");
//  String testCode = EnumCommon.getCodeFromLabel(GenderTypes.values(),"Female");
//System.out.println("Enum GenderTypes test: code = 'NNN' produces label = '" + testLabel + "'.");
//System.out.println("Enum GenderTypes test: label = 'Female' produces code = '" + testCode + "'.");

}