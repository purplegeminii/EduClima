package com.educlima.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WeatherApiClientTest {

    @Test
    void parsesAllCurrentFields() {
        String body = """
                {"location":{"name":"Reykjavik"},
                 "current":{"temp_c":12.5,"condition":{"text":"Partly cloudy"},
                            "pressure_mb":1009.0,"precip_mm":0.3,"humidity":77}}
                """;

        WeatherApiClient.Reading reading = WeatherApiClient.parse(body);

        assertEquals(12.5, reading.temperatureC());
        assertEquals("Partly cloudy", reading.condition());
        assertEquals(1009.0, reading.pressureMb());
        assertEquals(0.3, reading.precipMm());
        assertEquals(77, reading.humidity());
    }

    @Test
    void parsesNegativeTemperature() {
        // The previous regex-based parser silently dropped sub-zero temperatures.
        String body = """
                {"current":{"temp_c":-8.7,"condition":{"text":"Snow"},
                            "pressure_mb":1020.0,"precip_mm":1.2,"humidity":91}}
                """;

        WeatherApiClient.Reading reading = WeatherApiClient.parse(body);

        assertEquals(-8.7, reading.temperatureC());
    }

    @Test
    void returnsNullsWhenCurrentBlockIsMissing() {
        WeatherApiClient.Reading reading = WeatherApiClient.parse("{\"error\":{\"code\":1006}}");

        assertNull(reading.temperatureC());
        assertNull(reading.condition());
        assertNull(reading.pressureMb());
        assertNull(reading.precipMm());
        assertNull(reading.humidity());
    }
}
