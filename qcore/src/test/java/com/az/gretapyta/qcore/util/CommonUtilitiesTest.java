package com.az.gretapyta.qcore.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommonUtilitiesTest {

  @DisplayName("When converting raw Array of 2-elem. Arrays of Integers, then Map with key as Integer is created.")
  @Test
  @Order(value = 1)
  void test1() {
    Object[][] mainRawArray = new Object[][]{{1, 100}, {2, 200}, {3, 300}};
    Map<Integer, Integer> out = CommonUtilities.convertRawArrayOfIdsToMap(mainRawArray);

    assertNotNull(out);
    assertEquals(out.size(), mainRawArray.length);

    int rawIndex = 0;
    for (Map.Entry<Integer, Integer> entry : out.entrySet()) {
      assertEquals(entry.getKey(), mainRawArray[rawIndex][0]); // ID
      assertEquals(entry.getValue(), mainRawArray[rawIndex][1]);  // Val.
      rawIndex++;
    }
  }
}