package org.kogu.practice.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonConverter {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  private JsonConverter() { }

  public static <T> String toJson(T input) {
    try {
      return MAPPER.writeValueAsString(input);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T fromJson(Class<T> type, String json) {
    try {
      return MAPPER.readValue(json, type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public static <T> T fromJson(Class<T> type, InputStream is) {
    try {
      return MAPPER.readValue(is, type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> List<T> listFromJson(Class<T> type, String json) {
    CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, type);
    try {
      return MAPPER.readValue(json, collectionType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> List<T> listFromJson(Class<T> type, InputStream is) {
    CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, type);
    try {
      return MAPPER.readValue(is, collectionType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <K, V> Map<K, V> mapFromJson(Class<K> keyType, Class<V> valueType, String json) {
    MapType mapType = MAPPER.getTypeFactory().constructMapType(HashMap.class, keyType, valueType);
    try {
      return MAPPER.readValue(json, mapType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
