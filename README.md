# EduClima

A JavaFX desktop app for exploring and comparing countries' climate data. Pick a
country to see its current weather (temperature, conditions, pressure,
precipitation, humidity) and grid-electricity CO₂ emissions, then pick a second
country to see a side-by-side comparison.

Data sources:
- **[WeatherAPI](https://rapidapi.com/weatherapi/api/weatherapi-com)** (via RapidAPI) — current weather.
- **[Climatiq](https://www.climatiq.io/docs)** — CO₂e emissions for grid electricity.

## Requirements

- **JDK 21+** (the build targets a Java 21 toolchain).
- No separate JavaFX SDK download needed — Gradle pulls JavaFX in automatically.

## Configuration

The app reads API keys from a `.env` file in the project root (or from real
environment variables, which take precedence). Copy the example and fill in your keys:

```bash
cp .env.example .env
```

```
WEATHERAPI_RAPIDAPI_KEY=your-rapidapi-key
WEATHERAPI_RAPIDAPI_HOST=weatherapi-com.p.rapidapi.com
CLIMATIQ_API_KEY=your-climatiq-key
```

- Get a WeatherAPI (RapidAPI) key: https://rapidapi.com/weatherapi/api/weatherapi-com
- Get a Climatiq key: https://www.climatiq.io/docs/guides/quickstart

`.env` is gitignored and should never be committed.

## Running

```bash
./gradlew run
```

(The app launches to a welcome screen without keys; lookups will show a clear
error message until the keys are configured.)

## Testing & building

```bash
./gradlew test     # run the unit tests
./gradlew build    # compile, test, and assemble
```

## Project structure

```
src/main/java/com/educlima/
  ui/        App (JavaFX entry point), ClimateFormatter
  data/      CountryRepository (country list), ClimateService (orchestration)
  api/       WeatherApiClient, ClimatiqApiClient (HTTP + JSON parsing)
  logic/     ClimateComparator (country-vs-country comparison)
  model/     Country, ClimateData, Metric, ComparisonLine (typed records)
  util/      EnvLoader
src/main/resources/com/educlima/
  countries.csv   country names + ISO codes
  app.css         UI styling
src/test/java/com/educlima/   unit tests (JUnit 5)
```

Network calls run on a background thread, so the UI never freezes; lookups show
explicit loading and error states.

## Authors

Delali Nsiah-Asare · Obed Babington · Ewurama Boateng
