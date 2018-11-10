package com.scanii.client.misc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JSON {
  private static ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // crazy enough, this is the right way to configure jackson to serialize date/times as ISO8601
    // see https://github.com/FasterXML/jackson-databind/issues/1786
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public static <T> T load(String js, Class<T> valueType) {
    try {
      return mapper.readValue(js, valueType);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T> T load(Path file, Class<T> valueType) {
    try {
      return load(new String(Files.readAllBytes(file)), valueType);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }


  public static JsonNode load(Path file) {
    try {
      return mapper.readTree(Files.readAllBytes(file));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }

  public static JsonNode load(String js) {
    try {
      return mapper.readTree(js);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }

  public static String dump(Object o) {
    try {
      return mapper.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


  public static Map<String, Object> map(String js) {
    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
    };
    try {
      return mapper.readValue(js, typeRef);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static ObjectNode object() {
    return mapper.createObjectNode();
  }

}
