package com.educlima.model;

/**
 * A country the user can look up, paired with its ISO 3166-1 alpha-2 code.
 *
 * @param name    the human-readable country name shown in the UI
 * @param isoCode the two-letter ISO country code used for the Climatiq region query
 */
public record Country(String name, String isoCode) {
    public Country {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Country name cannot be null or blank");
        }
        if (isoCode == null || isoCode.isBlank()) {
            throw new IllegalArgumentException("ISO code cannot be null or blank for " + name);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
