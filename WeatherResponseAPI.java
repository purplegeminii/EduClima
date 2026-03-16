import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * This class contains the code for an API that provides statistics
 * about the weather conditions of a given country. It has a single
 * method that takes the country's name as a query, and returns its
 * respective weather statistics in real time
 * Link to API Documentation: <a href="https://rapidapi.com/weatherapi/api/weatherapi-com">link</a>
 *
 * @author Delali Nsiah-Asare
 * @author Obed Babington
 * @author Ewurama Boateng
 */
public class WeatherResponseAPI {

    /**
     * Sends an HTTP request to the WeatherAPI to retrieve real-time
     * weather statistics for the specified country
     *
     * @param countryName the name of the country for which to retrieve weather statistics
     * @return weatherResponse an HTTP response containing requested weather statistics
     * @throws IOException if an I/O error occurs while sending the request/receiving response
     * @throws InterruptedException if the thread is interrupted while waiting for the response
     * @throws IllegalArgumentException if countryName is null or empty
     * @throws URISyntaxException if URL is not conforming
     * @throws Exception for any other type of exception
     */
    public static HttpResponse<String> getResponse(String countryName) throws IOException, InterruptedException, IllegalArgumentException, URISyntaxException, Exception {
        HttpResponse<String> weatherResponse;
        int timeoutSeconds = 10;
        String rapidApiKey = System.getenv("WEATHERAPI_RAPIDAPI_KEY");
        String rapidApiHost = System.getenv("WEATHERAPI_RAPIDAPI_HOST");
        HttpRequest weatherRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://weatherapi-com.p.rapidapi.com/current.json?q=" + countryName))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", rapidApiHost)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        weatherResponse = HttpClient.newHttpClient().send(weatherRequest,
                HttpResponse.BodyHandlers.ofString());
        return weatherResponse;
    }

}
