package com.az.gretapyta.qcore.util;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Log4j2
public final class CommonUtilities {

  public static <T> T deepCopy(final T object) {
    try {
      // use serialization to create a deep copy
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(object);
      oos.flush();
      oos.close();
      bos.close();

      ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
      Object obj = new ObjectInputStream(bais).readObject();
      // @SupressWarnings("unchecked")
      Class<T> typeclass = (Class<T>) obj.getClass();
      bais.close();

      if (object.getClass() == typeclass) {
        return (T) obj;
      } else {
        throw new ClassNotFoundException("Source Class " + object.getClass() + " and Destination Class " + typeclass + " differ!");
      }
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String getTranslatedText(Map<String, String> translationsMap, String langCode, String attribName) {
    if (translationsMap == null) {
      log.debug( "==> Translations Map for Attribute: '{}' is empty !", attribName);
      return "";
    }
    String ret = translationsMap.get(langCode);
    if (ret != null) {
      return ret;
    } else {
      log.error( "Attribute: '{}' ==> " +"Translation for '{}' DOES NOT exist !", attribName, langCode);
      ret = translationsMap.get(DEFAULT_LOCALE);
      if (ret != null) {
        return ret;
      } else {
        log.error( "Attribute: '{}' ==> " + "Translation in default language DOES NOT exist !", attribName);
        return "<null>";
      }
    }
  }

  public static String getTranslatableMessage(String messKey, Locale locale) {
    try {
      ResourceBundle resourceBundle= ResourceBundle.getBundle( Constants.I18N_MESSAGES_RESOURCES_PATH, locale);
      return resourceBundle.getString(messKey);
    } catch (Exception e) {
      System.out.println("Resources exception: " + e);
      return null;
    }
  }

  // Overload
  public static String getTranslatableMessage(String messKey, String langCode) {
    return getTranslatableMessage(messKey, Locale.forLanguageTag(langCode));
  }

  public static int getAge(LocalDate birthday) {
    if (birthday == null) {
      return 0;
    } else {
      // Actually completed years of age:
      return java.time.Period.between(birthday, java.time.LocalDate.now()).getYears();
    }
  }

  public static Map<Integer, Integer> convertRawArrayOfIdsToMap(Object[] from) {
    Map<Integer, Integer> ret = new TreeMap<>();

    if (from == null) { return ret; }

    for (Object o : from) {
      if (!o.getClass().isArray()) {
        log.error("Error: RawArrayOfIdToMapConverter element is not an Array !");
        return ret;
      }
      Object[] rawElem = (Object[]) o; // type cast
      if (rawElem.length != 2) {
        log.error("Error: RawArrayOfIdToMapConverter elements are not of length 2, instead of length {}", + rawElem.length);
        continue;
      }
      if (((rawElem[0] == null) || (!(rawElem[0] instanceof Integer)))
          || ((rawElem[1] == null) || (!(rawElem[1] instanceof Integer)))) {
        log.error("Error: RawArrayOfIdToMapConverter elements are not Integer !");
        continue;
      }

      ret.put((Integer) rawElem[0], (Integer) rawElem[1]);
    }
    return ret;
  }
}