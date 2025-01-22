package com.az.gretapyta.qcore.enums;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public interface EnumCommon {
  String getCode();
  String getLabel();
  String getLabel(String langCode);

  static String getLabelFromCode(EnumCommon[] enumCommon,
                                        String code) {
    return getEnumAttributeFromPredicate(
        enumCommon,
        p -> p.getCode().equalsIgnoreCase(code),
        EnumCommon::getLabel);
  }

  static String getCodeFromLabel(EnumCommon[] enumCommon,
                                        String label) {
    return getEnumAttributeFromPredicate(
        enumCommon,
        p -> p.getLabel().equalsIgnoreCase(label),
        EnumCommon::getCode);
  }

  static EnumCommon getEnumFromCode(EnumCommon[] enumCommon, String code) {
    return getEnumFromPredicate(enumCommon, p -> p.getCode().equalsIgnoreCase(code) );
  }

  static EnumCommon getEnumFromLabel(EnumCommon[] enumCommon, String label) {
    return getEnumFromPredicate(enumCommon, p -> p.getLabel().equalsIgnoreCase(label) );
  }

  private static EnumCommon getEnumFromPredicate( EnumCommon[] enumCommon,
                                                   Predicate<EnumCommon> tester) {
    return Arrays.stream(enumCommon)
              .filter(tester)
              .findFirst()
              .orElse(null);
  }

  private static String getEnumAttributeFromPredicate( EnumCommon[] enumCommon,
                                                      Predicate<EnumCommon> tester,
                                                      Function<EnumCommon, String> mapper ) {
    return
        Arrays.stream(enumCommon)
            .filter(tester)
            .findFirst()
            .map(mapper)
            .orElse(null);
  }
}