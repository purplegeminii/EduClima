import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains the code for an API that provides statistics
 * about the C02 of a given country. It has a single
 * method that takes the country's two-letter code as a query, and
 * returns its respective CO2 emission statistics in real time
 * Link to API Documentation: <a href="https://www.climatiq.io/docs/guides/quickstart">link</a>
 *
 * @author Delali Nsiah-Asare
 * @author Obed Babington
 * @author Ewurama Boateng
 */
public class ClimatiqResponseAPI {
    /**
     * @param countryCode the country code for which to retrieve statistics
     * @return climatiqResponse an HTTP response containing requested climatiq statistics
     * @throws IOException if an I/O error occurs while sending the request/receiving response
     * @throws InterruptedException if the thread is interrupted while waiting for the response
     * @throws IllegalArgumentException if countryCode is null or empty
     * @throws URISyntaxException if URL is not conforming
     * @throws Exception for any other type of exception
     */
    public static HttpResponse<String> getResponse(String countryCode) throws IOException, InterruptedException, IllegalArgumentException, URISyntaxException, Exception {
        HttpResponse<String> climatiqResponse;
        int timeoutSeconds = 10;
        String dataVersion = "32.32";

        if (countryCode == null || countryCode.isBlank()) {
            throw new IllegalArgumentException("countryCode cannot be null or empty");
        }

        String MY_API_KEY = EnvLoader.get("CLIMATIQ_API_KEY");
        String searchUrl = "https://api.climatiq.io/data/v1/search";
        String estimateUrl = "https://api.climatiq.io/data/v1/estimate";
        String authorization_header = "Bearer " + MY_API_KEY;
        String searchParams = "query=" + URLEncoder.encode("grid mix", StandardCharsets.UTF_8)
                + "&region=" + URLEncoder.encode(countryCode, StandardCharsets.UTF_8)
                + "&category=" + URLEncoder.encode("Electricity", StandardCharsets.UTF_8)
                + "&unit_type=" + URLEncoder.encode("Energy", StandardCharsets.UTF_8)
                + "&results_per_page=1"
                + "&data_version=" + URLEncoder.encode(dataVersion, StandardCharsets.UTF_8);
        String fallbackSearchParams = "query=" + URLEncoder.encode("grid mix", StandardCharsets.UTF_8)
            + "&category=" + URLEncoder.encode("Electricity", StandardCharsets.UTF_8)
            + "&unit_type=" + URLEncoder.encode("Energy", StandardCharsets.UTF_8)
            + "&results_per_page=1"
            + "&data_version=" + URLEncoder.encode(dataVersion, StandardCharsets.UTF_8);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest searchRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(searchUrl + "?" + searchParams))
                    .header("Authorization", authorization_header)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .build();
            HttpResponse<String> searchResponse = client.send(searchRequest, HttpResponse.BodyHandlers.ofString());
            String activityId = extractFirstMatch(searchResponse.body(), "\\\"activity_id\\\":\\\"([^\\\"]+)\\\"");
            boolean useRegionalFactor = true;

            if (activityId == null || activityId.isBlank()) {
                HttpRequest fallbackSearchRequest = HttpRequest.newBuilder()
                        .uri(java.net.URI.create(searchUrl + "?" + fallbackSearchParams))
                        .header("Authorization", authorization_header)
                        .timeout(Duration.ofSeconds(timeoutSeconds))
                        .build();
                HttpResponse<String> fallbackSearchResponse = client.send(fallbackSearchRequest, HttpResponse.BodyHandlers.ofString());
                activityId = extractFirstMatch(fallbackSearchResponse.body(), "\\\"activity_id\\\":\\\"([^\\\"]+)\\\"");
                useRegionalFactor = false;

                if (activityId == null || activityId.isBlank()) {
                    return fallbackSearchResponse;
                }
            }

            String requestBody;

            if (useRegionalFactor) {
                requestBody = "{" +
                        "\"emission_factor\":{" +
                        "\"activity_id\":\"" + activityId + "\"," +
                        "\"region\":\"" + countryCode + "\"," +
                        "\"data_version\":\"" + dataVersion + "\"," +
                        "\"region_fallback\":true" +
                        "}," +
                        "\"parameters\":{" +
                        "\"energy\":1," +
                        "\"energy_unit\":\"kWh\"" +
                        "}" +
                        "}";
            } else {
                requestBody = "{" +
                        "\"emission_factor\":{" +
                        "\"activity_id\":\"" + activityId + "\"," +
                        "\"data_version\":\"" + dataVersion + "\"" +
                        "}," +
                        "\"parameters\":{" +
                        "\"energy\":1," +
                        "\"energy_unit\":\"kWh\"" +
                        "}" +
                        "}";
            }

            HttpRequest climatiqRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(estimateUrl))
                    .header("Authorization", authorization_header)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .build();

            climatiqResponse = client.send(climatiqRequest, HttpResponse.BodyHandlers.ofString());
        }

        return climatiqResponse;
    }

    private static String extractFirstMatch(String input, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

}
