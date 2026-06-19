package com.educlima.model;

/**
 * Immutable snapshot of the climate statistics for a single country.
 *
 * <p>Any field may be {@code null} when the corresponding value could not be
 * retrieved or parsed; callers must handle missing data gracefully.
 *
 * @param country       the country these statistics describe
 * @param temperatureC  current temperature in degrees Celsius (may be negative)
 * @param condition     textual weather condition, e.g. "Partly cloudy"
 * @param pressureMb    atmospheric pressure in millibars
 * @param precipMm      precipitation in millimetres
 * @param humidity      relative humidity as a percentage
 * @param co2eKg        grid-electricity CO₂e emissions per kWh, in kilograms
 */
public record ClimateData(
        Country country,
        Double temperatureC,
        String condition,
        Double pressureMb,
        Double precipMm,
        Integer humidity,
        Double co2eKg
) {
    /**
     * Returns the numeric value for a comparable metric, or {@code null} if it is
     * unavailable or the metric is not numeric.
     */
    public Double numericValue(Metric metric) {
        return switch (metric) {
            case TEMPERATURE -> temperatureC;
            case PRESSURE -> pressureMb;
            case PRECIPITATION -> precipMm;
            case HUMIDITY -> humidity == null ? null : humidity.doubleValue();
            case EMISSION -> co2eKg;
            case CONDITION -> null;
        };
    }

    /** Returns a display string for a metric's value, including its unit, or "Unavailable". */
    public String displayValue(Metric metric) {
        if (metric == Metric.CONDITION) {
            return condition == null ? "Unavailable" : condition;
        }
        Double value = numericValue(metric);
        if (value == null) {
            return "Unavailable";
        }
        String unit = metric.unit().isEmpty() ? "" : " " + metric.unit();
        return formatNumber(value) + unit;
    }

    private static String formatNumber(double value) {
        // Render whole numbers without a trailing ".0", everything else to 2 d.p.
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.2f", value);
    }
}
