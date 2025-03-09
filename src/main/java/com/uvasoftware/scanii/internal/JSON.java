package com.uvasoftware.scanii.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

class JSON {
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // crazy enough, this is the right way to configure jackson to serialize date/times as ISO8601
    // see https://github.com/FasterXML/jackson-databind/issues/1786
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.registerModule(new JavaTimeModule());
  }

  static <T> T load(String js, Class<T> valueType) {
    try {
      return mapper.readValue(js, valueType);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  static JsonNode load(String js) {
    try {
      return mapper.readTree(js);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }
}
