package com.sgi.fiis.shared.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.fiis.shared.domain.exception.JsonbSerializationException;

import java.util.HashMap;
import java.util.Map;

public class JsonbHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonbHelper() {
    }

    public static String toJson(Map<String, String> map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new JsonbSerializationException("Failed to serialize JSONB map", e);
        }
    }

    public static Map<String, String> fromJson(String json) {
        if (json == null || json.isBlank()) {
            Map<String, String> defaultMap = new HashMap<>();
            defaultMap.put("es", "");
            return defaultMap;
        }
        if (!json.trim().startsWith("{")) {
            Map<String, String> fallbackMap = new HashMap<>();
            fallbackMap.put("es", json);
            return fallbackMap;
        }
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            Map<String, String> fallbackMap = new HashMap<>();
            fallbackMap.put("es", json);
            return fallbackMap;
        }
    }

    public static String getText(String json, String locale) {
        Map<String, String> map = fromJson(json);
        return map.getOrDefault(locale, map.getOrDefault("es", ""));
    }

    public static String setText(String json, String locale, String value) {
        Map<String, String> map = fromJson(json);
        map.put(locale, value);
        return toJson(map);
    }
}
