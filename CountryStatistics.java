import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class retrieves weather and climate statistics for a
 * given country and stores the data in a  map
 *
 * @author Delali Nsiah-Asare
   @author Obed Babington
   @author Ewurama Boateng
*/
public class CountryStatistics {
    private final Map<String, String> statisticMap;

    /**
     * Constructor that initiatilizes the statisticsMap by calling
     * countryStatistics() method.
     * @param countryName the country name to retrieve weather data for
     * @param countryCode the country code to retrieve climate data for
     */
    public CountryStatistics(String countryName, String countryCode) {
        this.statisticMap = countryStatistics(countryName, countryCode);
    }

     /**
      * getter method for statistics map
      * @return statisticMap a map of weather and climate statistics of a given country
      */
      public Map<String, String> getStatsMap(){
          return this.statisticMap;
      }
    /**
     * Retrieves weather and climate statistics for the given countries and
     * returns them as a HashMap
     * @param countryName the country name to retrieve weather data for
     * @param countryCode the country code to retrieve climate data for
     * @return map A map of weather and climate statistics for the given countries
     */
    public Map<String, String> countryStatistics(String countryName, String countryCode){
        Map<String, String> map = new HashMap<>();
        String tempC, condition, pressureMB, precipMM, humidity, co2eTotal;

        try {
            HttpResponse<String> response = WeatherResponseAPI.getResponse(countryName);
            HttpResponse<String> response2 = ClimatiqResponseAPI.getResponse(countryCode);

            // Extract specific data using regex
            Pattern tempCPattern = Pattern.compile("\"temp_c\":(\\d+\\.?\\d*)");
            Pattern conditionPattern = Pattern.compile("\"text\":\"(.*?)\"");
            Pattern pressureMBPattern = Pattern.compile("\"pressure_mb\":(\\d+\\.?\\d*)");
            Pattern precipMMPattern = Pattern.compile("\"precip_mm\":(\\d+\\.?\\d*)");
            Pattern humidityPattern = Pattern.compile("\"humidity\":(\\d+)");
            Pattern co2e_totalPattern = Pattern.compile("\"co2e_total\":(\\d+\\.?\\d*)");
            Matcher tempCMatcher = tempCPattern.matcher(response.body());
            Matcher conditionMatcher = conditionPattern.matcher(response.body());
            Matcher pressureMatcher = pressureMBPattern.matcher(response.body());
            Matcher precipMMMatcher = precipMMPattern.matcher(response.body());
            Matcher humidityMatcher = humidityPattern.matcher(response.body());
            Matcher co2e_totalMatcher = co2e_totalPattern.matcher(response2.body());

            // if all regex patterns are matched, retrieve data and put it in the map
            if (tempCMatcher.find() && conditionMatcher.find() && pressureMatcher.find()
                    && precipMMMatcher.find() && humidityMatcher.find() && co2e_totalMatcher.find()) {
                tempC = tempCMatcher.group(1);
                condition = conditionMatcher.group(1);
                pressureMB = pressureMatcher.group(1);
                precipMM = precipMMMatcher.group(1);
                humidity = humidityMatcher.group(1);
                co2eTotal = co2e_totalMatcher.group(1);

                map.put("Country", countryName);
                map.put("Temperature", tempC);
                map.put("Condition", condition);
                map.put("Pressure", pressureMB);
                map.put("Precipitation", precipMM);
                map.put("Humidity", humidity);
                map.put("Emission", co2eTotal);
                
            } else {
                System.err.println("Error: Unable to extract data from the JSON response.");
            }

        // deal with caught exceptions by printing corresponding messages to users
        } catch (IOException e) {
            System.err.println(
                    "Error: Failed to connect to the Weather API. Please check your internet connection and try again.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Error: The operation was interrupted. Please try again.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: An unexpected error occurred.");
            e.printStackTrace();
        }

        return map;
    }

    /**
     * This method returns a particular statistic information given its identifier (as key)
     * @param statisticsCategory the category such as Pressure, stored as a key in the statisticsMap
     * @return the statistic information pertaining to that category
     */
    public String getCountryStats(String statisticsCategory){
        return this.statisticMap.get(statisticsCategory);
    }

}
