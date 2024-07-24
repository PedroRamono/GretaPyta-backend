package com.az.gretapyta.questionnaires.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Map;

@Log4j2
public final class Converters {

  public static String convertMapToJson(Map<?, ?> map) throws Exception {
    try {
      return convertObjectToJson(map);

    } catch (JsonProcessingException e) {
      log.error("Map-to-JSON conversion error: ", e);
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String convertObjectToJson(Object value) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper.writeValueAsString(value);
  }

  static byte[] convertObjectToJsonByteArray(Object object) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper.writeValueAsBytes(object);
  }
}
