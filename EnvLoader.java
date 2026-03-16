import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading key-value pairs from the project's .env file.
 */
public final class EnvLoader {
    private static final String ENV_FILE_NAME = ".env";
    private static final Map<String, String> ENV_VALUES = loadEnvFile();

    private EnvLoader() {}

    public static String get(String key) {
        String value = System.getenv(key);

        if (value == null || value.isBlank()) {
            value = ENV_VALUES.get(key);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing environment variable: " + key);
        }

        return value;
    }

    private static Map<String, String> loadEnvFile() {
        Map<String, String> values = new HashMap<>();
        Path envPath = Paths.get(ENV_FILE_NAME);

        if (!Files.exists(envPath)) {
            return values;
        }

        try {
            List<String> lines = Files.readAllLines(envPath);

            for (String line : lines) {
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmedLine.indexOf('=');

                if (separatorIndex <= 0) {
                    continue;
                }

                String key = trimmedLine.substring(0, separatorIndex).trim();
                String value = trimmedLine.substring(separatorIndex + 1).trim();
                values.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read .env file.", e);
        }

        return values;
    }
}