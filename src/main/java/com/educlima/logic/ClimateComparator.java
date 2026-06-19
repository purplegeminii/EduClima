package com.educlima.logic;

import com.educlima.model.ClimateData;
import com.educlima.model.ComparisonLine;
import com.educlima.model.Metric;

import java.util.ArrayList;
import java.util.List;

/**
 * Produces human-readable comparisons between two countries' {@link ClimateData}.
 *
 * <p>How a difference is expressed depends on the metric's {@link Metric.CompareMode}:
 * <ul>
 *   <li><b>Relative</b> (pressure, precipitation, emissions): percentage difference
 *       relative to the larger value.</li>
 *   <li><b>Absolute</b> (temperature): difference in the metric's own unit — a
 *       relative percentage is meaningless once temperatures can be zero or negative.</li>
 *   <li><b>Points</b> (humidity): absolute difference in percentage points, since the
 *       value is already a percentage.</li>
 * </ul>
 */
public final class ClimateComparator {

    private ClimateComparator() {
    }

    /** Compares two countries across every comparable metric, in a stable display order. */
    public static List<ComparisonLine> compare(ClimateData first, ClimateData second) {
        List<ComparisonLine> lines = new ArrayList<>();
        for (Metric metric : Metric.values()) {
            if (metric.isComparable()) {
                lines.add(new ComparisonLine(metric, compareMetric(metric, first, second)));
            }
        }
        return lines;
    }

    private static String compareMetric(Metric metric, ClimateData first, ClimateData second) {
        String nameA = first.country().name();
        String nameB = second.country().name();
        Double a = first.numericValue(metric);
        Double b = second.numericValue(metric);

        if (a == null || b == null) {
            return metric.label() + " data is unavailable for comparison.";
        }
        if (a.doubleValue() == b.doubleValue()) {
            return "The " + metric.label() + " values of " + nameA + " and " + nameB + " are equal.";
        }

        String higher = a > b ? nameA : nameB;
        String lower = a > b ? nameB : nameA;
        double magnitude = switch (metric.compareMode()) {
            case RELATIVE -> relativeDifferencePercent(a, b);
            case ABSOLUTE, POINTS -> Math.abs(a - b);
            case NONE -> 0;
        };
        String quantity = switch (metric.compareMode()) {
            case RELATIVE -> Math.round(magnitude) + "%";
            case POINTS -> formatNumber(magnitude) + " percentage points";
            case ABSOLUTE -> formatNumber(magnitude) + " " + metric.unit();
            case NONE -> "";
        };

        return higher + " has " + metric.label() + " " + quantity + " greater than " + lower + ".";
    }

    /** Percentage by which the larger of two non-negative values exceeds the smaller. */
    private static double relativeDifferencePercent(double a, double b) {
        double larger = Math.max(a, b);
        double smaller = Math.min(a, b);
        if (larger == 0) {
            return 0;
        }
        return (1 - smaller / larger) * 100;
    }

    private static String formatNumber(double value) {
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.1f", value);
    }
}
