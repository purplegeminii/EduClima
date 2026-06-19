package com.educlima.data;

import com.educlima.api.ClimatiqApiClient;
import com.educlima.api.WeatherApiClient;
import com.educlima.model.ClimateData;
import com.educlima.model.Country;
import com.educlima.util.EnvLoader;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

/**
 * Orchestrates the weather and emissions API clients to build a single
 * {@link ClimateData} for a country. This is the one place the UI talks to for data.
 */
public final class ClimateService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final WeatherApiClient weatherClient;
    private final ClimatiqApiClient climatiqClient;

    public ClimateService(WeatherApiClient weatherClient, ClimatiqApiClient climatiqClient) {
        this.weatherClient = Objects.requireNonNull(weatherClient, "weatherClient");
        this.climatiqClient = Objects.requireNonNull(climatiqClient, "climatiqClient");
    }

    /**
     * Builds a service from configuration in the environment / {@code .env} file,
     * sharing a single {@link HttpClient} across both API clients.
     *
     * @throws IllegalStateException if required API keys are missing
     */
    public static ClimateService fromEnvironment() {
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();

        WeatherApiClient weather = new WeatherApiClient(
                http,
                EnvLoader.get("WEATHERAPI_RAPIDAPI_KEY"),
                EnvLoader.get("WEATHERAPI_RAPIDAPI_HOST"),
                REQUEST_TIMEOUT);

        ClimatiqApiClient climatiq = new ClimatiqApiClient(
                http,
                EnvLoader.get("CLIMATIQ_API_KEY"),
                REQUEST_TIMEOUT);

        return new ClimateService(weather, climatiq);
    }

    /**
     * Fetches current weather and emissions for a country and assembles them into
     * a {@link ClimateData}. Emissions are best-effort: if Climatiq has no factor
     * for the country, the emissions field is left null rather than failing the
     * whole lookup.
     *
     * @throws IOException          if the weather request fails
     * @throws InterruptedException if the calling thread is interrupted
     */
    public ClimateData fetch(Country country) throws IOException, InterruptedException {
        Objects.requireNonNull(country, "country");

        WeatherApiClient.Reading weather = WeatherApiClient.parse(weatherClient.fetchRaw(country.name()));

        Double co2eKg;
        try {
            co2eKg = climatiqClient.fetchEmissionKg(country.isoCode());
        } catch (IOException e) {
            // Emissions are supplementary; don't let a Climatiq failure hide the weather.
            System.getLogger(ClimateService.class.getName())
                    .log(System.Logger.Level.WARNING, "Emissions lookup failed for " + country.name(), e);
            co2eKg = null;
        }

        return new ClimateData(
                country,
                weather.temperatureC(),
                weather.condition(),
                weather.pressureMb(),
                weather.precipMm(),
                weather.humidity(),
                co2eKg);
    }
}
