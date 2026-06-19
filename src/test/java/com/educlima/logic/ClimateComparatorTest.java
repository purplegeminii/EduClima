package com.educlima.logic;

import com.educlima.model.ClimateData;
import com.educlima.model.ComparisonLine;
import com.educlima.model.Country;
import com.educlima.model.Metric;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClimateComparatorTest {

    private static final Country GHANA = new Country("Ghana", "GH");
    private static final Country NORWAY = new Country("Norway", "NO");

    private static ClimateData data(Country country, Double temp, Double pressure,
                                    Double precip, Integer humidity, Double co2e) {
        return new ClimateData(country, temp, "Clear", pressure, precip, humidity, co2e);
    }

    private static Map<Metric, String> byMetric(ClimateData a, ClimateData b) {
        return ClimateComparator.compare(a, b).stream()
                .collect(Collectors.toMap(ComparisonLine::metric, ComparisonLine::message));
    }

    @Test
    void comparesEveryComparableMetric() {
        List<ComparisonLine> lines = ClimateComparator.compare(
                data(GHANA, 30.0, 1010.0, 0.0, 80, 0.4),
                data(NORWAY, 5.0, 1005.0, 2.0, 60, 0.02));

        long comparable = java.util.Arrays.stream(Metric.values()).filter(Metric::isComparable).count();
        assertEquals(comparable, lines.size());
    }

    @Test
    void temperatureUsesAbsoluteDifferenceAndHandlesNegatives() {
        Map<Metric, String> result = byMetric(
                data(GHANA, 10.0, 1000.0, 0.0, 50, 0.1),
                data(NORWAY, -5.0, 1000.0, 0.0, 50, 0.1));

        // 10 vs -5 => 15 °C apart; Ghana is warmer.
        assertEquals("Ghana has Temperature 15 °C greater than Norway.", result.get(Metric.TEMPERATURE));
    }

    @Test
    void humidityUsesPercentagePoints() {
        Map<Metric, String> result = byMetric(
                data(GHANA, 20.0, 1000.0, 0.0, 80, 0.1),
                data(NORWAY, 20.0, 1000.0, 0.0, 50, 0.1));

        assertEquals("Ghana has Humidity 30 percentage points greater than Norway.",
                result.get(Metric.HUMIDITY));
    }

    @Test
    void pressureUsesRelativePercentage() {
        Map<Metric, String> result = byMetric(
                data(GHANA, 20.0, 1000.0, 0.0, 50, 0.1),
                data(NORWAY, 20.0, 500.0, 0.0, 50, 0.1));

        // (1 - 500/1000) * 100 = 50%
        assertEquals("Ghana has Pressure 50% greater than Norway.", result.get(Metric.PRESSURE));
    }

    @Test
    void equalValuesReportEquality() {
        Map<Metric, String> result = byMetric(
                data(GHANA, 25.0, 1000.0, 1.0, 70, 0.1),
                data(NORWAY, 25.0, 1000.0, 1.0, 70, 0.1));

        assertEquals("The Temperature values of Ghana and Norway are equal.",
                result.get(Metric.TEMPERATURE));
    }

    @Test
    void zeroPrecipitationDoesNotDivideByZero() {
        Map<Metric, String> result = byMetric(
                data(GHANA, 20.0, 1000.0, 0.0, 50, 0.1),
                data(NORWAY, 20.0, 1000.0, 12.0, 50, 0.1));

        // larger is 12, smaller is 0 => 100% greater for Norway.
        assertEquals("Norway has Precipitation 100% greater than Ghana.",
                result.get(Metric.PRECIPITATION));
    }

    @Test
    void missingDataIsReportedNotCrashed() {
        Map<Metric, String> result = byMetric(
                data(GHANA, 20.0, 1000.0, 0.0, 50, null),
                data(NORWAY, 20.0, 1000.0, 0.0, 50, 0.1));

        assertTrue(result.get(Metric.EMISSION).contains("unavailable"),
                () -> "Expected an unavailable message, got: " + result.get(Metric.EMISSION));
    }
}
