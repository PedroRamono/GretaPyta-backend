package com.az.gretapyta.qcore.util;

import java.util.Locale;

public abstract class Constants {
  public static final String DEFAULT_LOCALE = "en";
  public static final String BASE_DATE_FORMAT = "MM-dd-yyyy";
  public static final String BASE_DATE_TIME_FORMAT = "MM-dd-yyyy HH:mm";

  // For Localized messages:
  public static final String I18N_MESSAGES_RESOURCES_PATH = "i18n/messages";

  public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 x 1hr

  // Currently supported Locales (languages):
  public static final String LOCALE_KEY_EN = "en";
  public static final String LOCALE_KEY_PL = "pl";
  public static final String LOCALE_KEY_RU = "ru";
}