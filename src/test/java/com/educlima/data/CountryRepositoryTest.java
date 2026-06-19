package com.educlima.data;

import com.educlima.model.Country;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountryRepositoryTest {

    private final CountryRepository repository = CountryRepository.getInstance();

    @Test
    void loadsCountriesFromResource() {
        assertFalse(repository.all().isEmpty());
        assertTrue(repository.all().size() > 150);
    }

    @Test
    void looksUpByNameWithIsoCode() {
        Optional<Country> ghana = repository.findByName("Ghana");
        assertTrue(ghana.isPresent());
        assertEquals("GH", ghana.get().isoCode());
    }

    @Test
    void dedupesTimorLeste() {
        assertTrue(repository.findByName("Timor-Leste").isPresent());
        assertFalse(repository.findByName("East Timor").isPresent());
    }

    @Test
    void namesAreSortedAndUnique() {
        var names = repository.names();
        assertEquals(names.stream().distinct().count(), names.size(), "names should be unique");
        var sorted = names.stream().sorted().toList();
        assertEquals(sorted, names, "names should already be alphabetical");
    }
}
