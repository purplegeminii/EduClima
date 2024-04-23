import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * This class contains the code for an API that provides statistics
 * about the C02 of a given country. It has a single
 * method that takes the country's two-letter code as a query, and
 * returns its respective CO2 emission statistics in real time
 * Link to API Documentation: https://www.climatiq.io/docs/guides/quickstart
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

        String MY_API_KEY = "W12ANG109DMSH5JEKE7K78NRMWAN";
        String url = "https://beta3.api.climatiq.io/search";
        String query = "grid mix";
        String query_params = "query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&region="+countryCode;
        String authorization_header = "Bearer: " + MY_API_KEY;
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest climatiqRequest = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url + "?" + query_params))
                .header("Authorization", authorization_header)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();

        climatiqResponse = client.send(climatiqRequest,
                HttpResponse.BodyHandlers.ofString());

        return climatiqResponse;
    }

}
