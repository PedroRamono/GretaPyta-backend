package com.az.gretapyta.qcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SummaryByIdComponentTest {

  @DisplayName("Basic check of types and values.")
  @Test
  @Order(value = 1)
  void test1() {
    SummaryByIdComponent component = new SummaryByIdComponent(3, 12345);

    assertNotNull(component);
    assertEquals(component.getClass(), SummaryByIdComponent.class);
    assertEquals(component.id(), 3);
    assertEquals(component.number(), 12345);
  }

  @DisplayName("when divider is not zero, then scaled down by tens to BigDecimal.")
  @Test
  @Order(value = 2)
  void test2() {
    SummaryByIdComponent component = new SummaryByIdComponent(1, 12345);

    // Number shifted by 3 (dec.) places to right:
    assertEquals(component.getAsScaledDownDecimal(3).getClass(), BigDecimal.class);
    assertEquals(component.getAsScaledDownDecimal(3).stripTrailingZeros().scale(), 3);
    assertEquals(component.getAsScaledDownDecimal(3).toString(), "12.345");
  }
}