package com.educlima.data;

import com.educlima.model.Country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides the list of supported countries and their ISO codes, loaded once from
 * the {@code countries.csv} classpath resource.
 *
 * <p>This replaces the previous mutable {@code static} fields with immutable,
 * lazily-loaded data held in a single shared instance.
 */
public final class CountryRepository {
    private static final String RESOURCE = "/com/educlima/countries.csv";
    private static final CountryRepository INSTANCE = new CountryRepository(load());

    private final List<Country> countries;
    private final Map<String, Country> byName;

    private CountryRepository(List<Country> countries) {
        this.countries = List.copyOf(countries);
        Map<String, Country> index = new LinkedHashMap<>();
        for (Country country : countries) {
            index.put(country.name(), country);
        }
        this.byName = Map.copyOf(index);
    }

    public static CountryRepository getInstance() {
        return INSTANCE;
    }

    /** All supported countries, in alphabetical order. */
    public List<Country> all() {
        return countries;
    }

    /** Country names, in alphabetical order — convenient for UI dropdowns. */
    public List<String> names() {
        return countries.stream().map(Country::name).toList();
    }

    /** Looks up a country by its exact display name. */
    public Optional<Country> findByName(String name) {
        return Optional.ofNullable(byName.get(name));
    }

    private static List<Country> load() {
        List<Country> result = new ArrayList<>();
        try (InputStream in = CountryRepository.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource: " + RESOURCE);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line = reader.readLine(); // header
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    int comma = line.indexOf(',');
                    if (comma <= 0) {
                        continue;
                    }
                    String name = line.substring(0, comma).trim();
                    String isoCode = line.substring(comma + 1).trim();
                    result.add(new Country(name, isoCode));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + RESOURCE, e);
        }
        return result;
    }
}
