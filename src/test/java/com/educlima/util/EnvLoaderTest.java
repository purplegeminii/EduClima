package com.educlima.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EnvLoaderTest {

    @Test
    void parsesKeyValuePairsIgnoringCommentsAndBlanks() {
        Map<String, String> values = EnvLoader.parse(List.of(
                "# a comment",
                "",
                "API_KEY=abc123",
                "HOST = example.com ",
                "QUOTED=\"with spaces\"",
                "SINGLE='single quoted'"));

        assertEquals("abc123", values.get("API_KEY"));
        assertEquals("example.com", values.get("HOST"));
        assertEquals("with spaces", values.get("QUOTED"));
        assertEquals("single quoted", values.get("SINGLE"));
    }

    @Test
    void skipsMalformedLines() {
        Map<String, String> values = EnvLoader.parse(List.of(
                "no_equals_sign",
                "=missing_key",
                "GOOD=ok"));

        assertEquals(1, values.size());
        assertEquals("ok", values.get("GOOD"));
        assertFalse(values.containsKey(""));
    }
}
