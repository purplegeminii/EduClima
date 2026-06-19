package com.educlima.model;

/**
 * One line of a country-vs-country comparison: which metric it concerns and a
 * human-readable description of how the two countries differ on it.
 */
public record ComparisonLine(Metric metric, String message) {
}
