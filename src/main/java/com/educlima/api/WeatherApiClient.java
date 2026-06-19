package com.educlima.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

/**
 * Client for WeatherAPI's "current weather" endpoint (accessed via RapidAPI).
 *
 * <p>Network access and JSON parsing are kept separate: {@link #fetchRaw(String)}
 * performs the HTTP call, while {@link #parse(String)} is a pure function that can
 * be unit-tested with recorded fixtures.
 *
 * @see <a href="https://rapidapi.com/weatherapi/api/weatherapi-com">API documentation</a>
 */
public final class WeatherApiClient {
    private static final String ENDPOINT = "https://weatherapi-com.p.rapidapi.com/current.json";

    private final HttpClient http;
    private final String apiKey;
    private final String apiHost;
    private final Duration timeout;

    public WeatherApiClient(HttpClient http, String apiKey, String apiHost, Duration timeout) {
        this.http = Objects.requireNonNull(http, "http");
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey");
        this.apiHost = Objects.requireNonNull(apiHost, "apiHost");
        this.timeout = Objects.requireNonNull(timeout, "timeout");
    }

    /**
     * Retrieves the raw JSON body of the current weather for the given country.
     *
     * @throws IOException          if the request fails or returns a non-2xx status
     * @throws InterruptedException if the calling thread is interrupted
     */
    public String fetchRaw(String countryName) throws IOException, InterruptedException {
        if (countryName == null || countryName.isBlank()) {
            throw new IllegalArgumentException("countryName cannot be null or blank");
        }
        String query = URLEncoder.encode(countryName, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT + "?q=" + query))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", apiHost)
                .timeout(timeout)
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("Weather API returned HTTP " + response.statusCode()
                    + " for \"" + countryName + "\"");
        }
        return response.body();
    }

    /**
     * Parses a WeatherAPI current-weather payload. Missing fields are left null.
     */
    public static Reading parse(String body) {
        JsonObject root = JsonParser.parseString(body).getAsJsonObject();
        JsonObject current = root.has("current") && root.get("current").isJsonObject()
                ? root.getAsJsonObject("current")
                : new JsonObject();

        Double temperatureC = asDouble(current.get("temp_c"));
        Double pressureMb = asDouble(current.get("pressure_mb"));
        Double precipMm = asDouble(current.get("precip_mm"));
        Integer humidity = asInteger(current.get("humidity"));

        String condition = null;
        JsonElement conditionElement = current.get("condition");
        if (conditionElement != null && conditionElement.isJsonObject()) {
            condition = asString(conditionElement.getAsJsonObject().get("text"));
        }

        return new Reading(temperatureC, condition, pressureMb, precipMm, humidity);
    }

    private static Double asDouble(JsonElement element) {
        return element == null || element.isJsonNull() ? null : element.getAsDouble();
    }

    private static Integer asInteger(JsonElement element) {
        return element == null || element.isJsonNull() ? null : element.getAsInt();
    }

    private static String asString(JsonElement element) {
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    /** The subset of weather fields EduClima displays. */
    public record Reading(
            Double temperatureC,
            String condition,
            Double pressureMb,
            Double precipMm,
            Integer humidity) {
    }
}
