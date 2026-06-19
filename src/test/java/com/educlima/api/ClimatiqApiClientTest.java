package com.educlima.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClimatiqApiClientTest {

    @Test
    void extractsFirstActivityId() {
        String body = """
                {"results":[{"activity_id":"electricity-supply_grid-source_residual_mix"},
                            {"activity_id":"electricity-supply_grid-source_production_mix"}]}
                """;

        assertEquals("electricity-supply_grid-source_residual_mix",
                ClimatiqApiClient.parseFirstActivityId(body));
    }

    @Test
    void returnsNullActivityIdWhenNoResults() {
        assertNull(ClimatiqApiClient.parseFirstActivityId("{\"results\":[]}"));
        assertNull(ClimatiqApiClient.parseFirstActivityId("{\"message\":\"not found\"}"));
    }

    @Test
    void prefersConstituentGasesTotal() {
        String body = """
                {"co2e":0.42,"co2e_unit":"kg",
                 "constituent_gases":{"co2e_total":0.55,"co2":0.5}}
                """;

        assertEquals(0.55, ClimatiqApiClient.parseEmissionKg(body));
    }

    @Test
    void fallsBackToTopLevelCo2e() {
        assertEquals(0.42, ClimatiqApiClient.parseEmissionKg("{\"co2e\":0.42,\"co2e_unit\":\"kg\"}"));
    }

    @Test
    void returnsNullWhenNoEmissionPresent() {
        assertNull(ClimatiqApiClient.parseEmissionKg("{\"unrelated\":true}"));
    }
}
