package com.az.gretapyta.qcore.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record SummaryByIdComponent(Integer id, Integer number) {

  public BigDecimal getAsScaledDownDecimal(int dividerBy10Places) {
    if (dividerBy10Places <= 0) {return new BigDecimal(number);}

    int divider = (int) Math.pow(10, dividerBy10Places);
    float intr = (float) number / divider;
    return new BigDecimal(intr).setScale(dividerBy10Places, RoundingMode.HALF_DOWN);
  }
}