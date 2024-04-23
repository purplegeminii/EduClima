import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *This class compares the statistics of two countries using the
 * CountryStatistics class and stores the comparative data in a HashMap.
 *
 * @author Delali Nsiah-Asare
 * @author Obed Babington
 * @author Ewurama Boateng
 */
public class CompareTo {
    private final CountryStatistics country1Stats;
    private final CountryStatistics country2Stats;
    private final Map<String, String> compareMap; // stores statistics of both countries in comparative form


    /**
     * Constructor for the CompareTo class that intializes country1stats and country2stats
     * @param country1 An instance of CountryStatistics for the first country.
     * @param country2 An instance of CountryStatistics for the second country
     */
    public CompareTo(CountryStatistics country1, CountryStatistics country2){
        this.country1Stats = country1;
        this.country2Stats = country2;
        this.compareMap = populateCompareMap();
    }

    /**
     * Populates the compareMap HashMap with the comparative statistics
     * @return A HashMap with the comparative statistics.
     */
    public Map<String, String> populateCompareMap(){
        Map<String, String> map = new HashMap<>();
        ArrayList<String> keys = new ArrayList<>();
        keys.add("Temperature");
        keys.add("Pressure");
        keys.add("Precipitation");
        keys.add("Humidity");
        keys.add("Emission");

        for (String key : keys) {
            String value = compare(key);
            map.put(key, value);
        }

        return map;
    }

    /**
     * Compares the given statistic for both countries and returns a message
     * with the comparison result
     * @param statistic The statistic to compare
     * @return msg A string with the comparison message
     * @throws NullPointerException if the statistic is not available for one of the countries.
     */
    public String compare(String statistic) throws NullPointerException{
        String msg = "";
        double stat1 = Double.parseDouble(this.country1Stats.getCountryStats(statistic));
        double stat2 = Double.parseDouble(this.country2Stats.getCountryStats(statistic));
        double value;
        if (stat1 > stat2){
            if (statistic.equals("Humidity")) {
                value = (stat1 - stat2);
                msg = this.country1Stats.getCountryStats("Country")+" has " + statistic + " " +
                        Math.round(value)+"% greater than "+this.country2Stats.getCountryStats("Country");
            } else {
                value = (1 - stat2/stat1)*100;
                msg = this.country1Stats.getCountryStats("Country")+" has " + statistic + " " +
                        Math.round(value)+"% greater than "+this.country2Stats.getCountryStats("Country");
            }

        } else if (stat1 < stat2) {
            if (statistic.equals("Humidity")) {
                value = (stat2 - stat1);
                msg = this.country1Stats.getCountryStats("Country")+" has " + statistic + " " +
                        Math.round(value)+"% less than "+this.country2Stats.getCountryStats("Country");
            } else {
                value = (1 - stat1/stat2)*100;
                msg = this.country1Stats.getCountryStats("Country")+" has " + statistic + " " +
                        Math.round(value)+"% less than "+this.country2Stats.getCountryStats("Country");
            }

        } else if (stat1 == stat2){
            msg = "The " + statistic + " values of " + this.country1Stats.getCountryStats("Country") + " and " +
                    this.country2Stats.getCountryStats("Country") + " are equal.";
        }
        return msg;
    }

    /**
     * Returns the comparative statistics HashMap
     * @return this.compareMap A HashMap with the comparative statistics.
     */
    public Map<String, String> getCompareMap(){
        return this.compareMap;
    }

}
