package com.educlima.model;

/**
 * The climate metrics EduClima reports, with their display labels, units, and
 * how two countries should be compared on each one.
 *
 * <p>Replaces the stringly-typed map keys ("Temperature", "Pressure", ...) that
 * were previously duplicated across several classes.
 */
public enum Metric {
    TEMPERATURE("Temperature", "°C", CompareMode.ABSOLUTE),
    CONDITION("Condition", "", CompareMode.NONE),
    PRESSURE("Pressure", "mb", CompareMode.RELATIVE),
    PRECIPITATION("Precipitation", "mm", CompareMode.RELATIVE),
    HUMIDITY("Humidity", "%", CompareMode.POINTS),
    EMISSION("Emission", "kg CO₂e", CompareMode.RELATIVE);

    /** How a metric's difference between two countries is expressed. */
    public enum CompareMode {
        /** Relative percentage difference, e.g. "20% greater". */
        RELATIVE,
        /** Absolute difference in the metric's own unit, e.g. "3.2 °C higher". */
        ABSOLUTE,
        /** Absolute difference expressed in percentage points (for values already in %). */
        POINTS,
        /** Not comparable (e.g. a textual weather condition). */
        NONE
    }

    private final String label;
    private final String unit;
    private final CompareMode compareMode;

    Metric(String label, String unit, CompareMode compareMode) {
        this.label = label;
        this.unit = unit;
        this.compareMode = compareMode;
    }

    public String label() {
        return label;
    }

    public String unit() {
        return unit;
    }

    public CompareMode compareMode() {
        return compareMode;
    }

    public boolean isComparable() {
        return compareMode != CompareMode.NONE;
    }
}
