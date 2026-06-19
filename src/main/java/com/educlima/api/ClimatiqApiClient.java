package com.educlima.api;

import com.google.gson.JsonArray;
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
 * Client for the Climatiq API. Estimates the CO₂e emissions of 1 kWh of grid
 * electricity for a country, which EduClima uses as a comparable emissions proxy.
 *
 * <p>The estimate is a two-step flow: search for a grid-mix emission factor for
 * the region, then request an estimate using that factor. If no region-specific
 * factor exists, it falls back to a global grid-mix factor.
 *
 * @see <a href="https://www.climatiq.io/docs/guides/quickstart">API documentation</a>
 */
public final class ClimatiqApiClient {
    private static final String SEARCH_URL = "https://api.climatiq.io/data/v1/search";
    private static final String ESTIMATE_URL = "https://api.climatiq.io/data/v1/estimate";
    private static final String DATA_VERSION = "32.32";

    private final HttpClient http;
    private final String apiKey;
    private final Duration timeout;

    public ClimatiqApiClient(HttpClient http, String apiKey, Duration timeout) {
        this.http = Objects.requireNonNull(http, "http");
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey");
        this.timeout = Objects.requireNonNull(timeout, "timeout");
    }

    /**
     * Returns the CO₂e emissions (kg) for 1 kWh of grid electricity in the given
     * region, or {@code null} if no suitable emission factor could be found.
     */
    public Double fetchEmissionKg(String isoCode) throws IOException, InterruptedException {
        if (isoCode == null || isoCode.isBlank()) {
            throw new IllegalArgumentException("isoCode cannot be null or blank");
        }

        // Step 1: try a region-specific grid-mix factor, then a global fallback.
        String activityId = parseFirstActivityId(search(isoCode));
        boolean regional = activityId != null;
        if (activityId == null) {
            activityId = parseFirstActivityId(search(null));
        }
        if (activityId == null) {
            return null;
        }

        // Step 2: request the emissions estimate for that factor.
        String estimateBody = estimate(activityId, regional ? isoCode : null);
        return parseEmissionKg(estimateBody);
    }

    private String search(String isoCode) throws IOException, InterruptedException {
        StringBuilder params = new StringBuilder()
                .append("query=").append(URLEncoder.encode("grid mix", StandardCharsets.UTF_8))
                .append("&category=").append(URLEncoder.encode("Electricity", StandardCharsets.UTF_8))
                .append("&unit_type=").append(URLEncoder.encode("Energy", StandardCharsets.UTF_8))
                .append("&results_per_page=1")
                .append("&data_version=").append(URLEncoder.encode(DATA_VERSION, StandardCharsets.UTF_8));
        if (isoCode != null) {
            params.append("&region=").append(URLEncoder.encode(isoCode, StandardCharsets.UTF_8));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SEARCH_URL + "?" + params))
                .header("Authorization", "Bearer " + apiKey)
                .timeout(timeout)
                .GET()
                .build();
        return send(request, "search");
    }

    private String estimate(String activityId, String isoCode) throws IOException, InterruptedException {
        JsonObject emissionFactor = new JsonObject();
        emissionFactor.addProperty("activity_id", activityId);
        emissionFactor.addProperty("data_version", DATA_VERSION);
        if (isoCode != null) {
            emissionFactor.addProperty("region", isoCode);
            emissionFactor.addProperty("region_fallback", true);
        }

        JsonObject parameters = new JsonObject();
        parameters.addProperty("energy", 1);
        parameters.addProperty("energy_unit", "kWh");

        JsonObject requestBody = new JsonObject();
        requestBody.add("emission_factor", emissionFactor);
        requestBody.add("parameters", parameters);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ESTIMATE_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        return send(request, "estimate");
    }

    private String send(HttpRequest request, String stage) throws IOException, InterruptedException {
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("Climatiq " + stage + " returned HTTP " + response.statusCode());
        }
        return response.body();
    }

    /** Extracts the first {@code activity_id} from a Climatiq search response, or null. */
    static String parseFirstActivityId(String searchBody) {
        JsonObject root = JsonParser.parseString(searchBody).getAsJsonObject();
        if (!root.has("results") || !root.get("results").isJsonArray()) {
            return null;
        }
        JsonArray results = root.getAsJsonArray("results");
        if (results.isEmpty() || !results.get(0).isJsonObject()) {
            return null;
        }
        JsonElement activityId = results.get(0).getAsJsonObject().get("activity_id");
        return activityId == null || activityId.isJsonNull() ? null : activityId.getAsString();
    }

    /**
     * Extracts the total CO₂e (kg) from a Climatiq estimate response. Prefers the
     * {@code constituent_gases.co2e_total} field and falls back to top-level
     * {@code co2e}; returns null if neither is present.
     */
    static Double parseEmissionKg(String estimateBody) {
        JsonObject root = JsonParser.parseString(estimateBody).getAsJsonObject();
        if (root.has("constituent_gases") && root.get("constituent_gases").isJsonObject()) {
            JsonElement total = root.getAsJsonObject("constituent_gases").get("co2e_total");
            if (total != null && !total.isJsonNull()) {
                return total.getAsDouble();
            }
        }
        JsonElement co2e = root.get("co2e");
        return co2e == null || co2e.isJsonNull() ? null : co2e.getAsDouble();
    }
}
