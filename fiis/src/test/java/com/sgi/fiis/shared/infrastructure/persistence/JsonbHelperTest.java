package com.sgi.fiis.shared.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonbHelper Unit Tests")
@SuppressWarnings("all")
class JsonbHelperTest {

    @Test
    @DisplayName("Should serialize map to json")
    void toJsonShouldSerialize() {
        Map<String, String> map = Map.of("es", "Hola", "en", "Hello");
        String json = JsonbHelper.toJson(map);
        assertTrue(json.contains("\"es\":\"Hola\""));
        assertTrue(json.contains("\"en\":\"Hello\""));
    }

    @Test
    @DisplayName("Should deserialize json to map")
    void fromJsonShouldDeserialize() {
        String json = "{\"es\":\"Hola\",\"en\":\"Hello\"}";
        Map<String, String> map = JsonbHelper.fromJson(json);
        assertEquals(2, map.size());
        assertEquals("Hola", map.get("es"));
        assertEquals("Hello", map.get("en"));
    }

    @Test
    @DisplayName("Should handle empty json")
    void fromJsonEmptyJsonShouldReturnDefaultMap() {
        Map<String, String> map1 = JsonbHelper.fromJson(null);
        Map<String, String> map2 = JsonbHelper.fromJson("");

        assertEquals(1, map1.size());
        assertEquals("", map1.get("es"));
        
        assertEquals(1, map2.size());
        assertEquals("", map2.get("es"));
    }

    @Test
    @DisplayName("Should handle legacy plain text")
    void fromJsonLegacyTextShouldReturnFallbackMap() {
        String legacyText = "Este es un texto plano";
        Map<String, String> map = JsonbHelper.fromJson(legacyText);
        assertEquals(1, map.size());
        assertEquals("Este es un texto plano", map.get("es"));
    }

    @Test
    @DisplayName("Should get text by locale")
    void getTextShouldReturnCorrectText() {
        String json = "{\"es\":\"Hola\",\"en\":\"Hello\"}";
        assertEquals("Hola", JsonbHelper.getText(json, "es"));
        assertEquals("Hello", JsonbHelper.getText(json, "en"));
        // fallback to es
        assertEquals("Hola", JsonbHelper.getText(json, "fr"));
    }

    @Test
    @DisplayName("Should set text by locale")
    void setTextShouldAddOrUpdateText() {
        String json = "{\"es\":\"Hola\"}";
        String newJson = JsonbHelper.setText(json, "en", "Hello");
        
        Map<String, String> map = JsonbHelper.fromJson(newJson);
        assertEquals(2, map.size());
        assertEquals("Hola", map.get("es"));
        assertEquals("Hello", map.get("en"));
    }

    @Test
    @DisplayName("Should verify constructor is private")
    void testConstructorIsPrivate() throws NoSuchMethodException {
        java.lang.reflect.Constructor<JsonbHelper> constructor = JsonbHelper.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));

    }
}

