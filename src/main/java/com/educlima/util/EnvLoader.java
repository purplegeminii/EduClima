package com.educlima.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads key-value pairs from the project's {@code .env} file, falling back to
 * real environment variables. Process environment variables take precedence so
 * that deployments can override file-based defaults.
 */
public final class EnvLoader {
    private static final String ENV_FILE_NAME = ".env";
    private static final Map<String, String> ENV_VALUES = parse(readEnvFile());

    private EnvLoader() {
    }

    /**
     * Returns the value for {@code key}, checking the process environment first
     * and then the {@code .env} file.
     *
     * @throws IllegalStateException if the key is absent or blank in both sources
     */
    public static String get(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = ENV_VALUES.get(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing configuration value '" + key + "'. "
                            + "Set it in your .env file (see .env.example) or as an environment variable.");
        }
        return value;
    }

    private static List<String> readEnvFile() {
        Path envPath = Path.of(ENV_FILE_NAME);
        if (!Files.exists(envPath)) {
            return List.of();
        }
        try {
            return Files.readAllLines(envPath);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read .env file", e);
        }
    }

    /**
     * Parses {@code KEY=value} lines into a map. Blank lines and {@code #} comments
     * are ignored, as is surrounding whitespace and optional surrounding quotes.
     * Exposed for testing.
     */
    static Map<String, String> parse(List<String> lines) {
        Map<String, String> values = new HashMap<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int separator = trimmed.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            String key = trimmed.substring(0, separator).trim();
            String value = stripQuotes(trimmed.substring(separator + 1).trim());
            values.put(key, value);
        }
        return values;
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
