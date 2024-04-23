/**
 * This class implements a climate information application which
 * It allows users to select a country and view its climate data,
 * view that for another country, and compare the climate data
 * of both countries.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

public class MainApp extends Application {
    private Stage window;
    private Scene scene1, scene2, scene3, scene4, scene5, scene6;
    private Button startBtn, selectCountryBtn, selectCountryBtn2, anotherBtn, compareBtn;
    private ChoiceBox<String> countryChoiceBox, countryChoiceBox2;
    private String country1, country2;
    private Label label1, label2, label3, label4, label5;

    /**
     * This method is the entry point for the application;
     * it launches the application
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method initializes the UI and sets up the scenes
     * and their associated event handlers
     * @param primaryStage primary stage for the application
     * @throws Exception exception if scene cannot be created
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Edu-Clima");

        // Label
        label1 = new Label("Welcome to Edu-Clima");
        label2 = new Label("Climate Project");
        label3 = new Label();
        label4 = new Label();
        label5 = new Label();
        label1.setFont(new Font("Arial", 40));
        label2.setFont(new Font("Times New Roman", 20));
        label3.setFont(new Font("Arial", 30));
        label4.setFont(new Font("Arial", 30));
        label5.setFont(new Font("Arial", 25));

        // Buttons
        startBtn = new Button("START");
        startBtn.setOnAction(e -> {
            System.out.println("Start Button");
            window.setScene(scene2);
        });

        // Layout1
        StackPane layout1 = new StackPane();
        layout1.setPadding(new Insets(10, 10, 10, 10));
        layout1.getChildren().addAll(label1, label2, startBtn);
        StackPane.setAlignment(label1, Pos.TOP_CENTER);
        StackPane.setAlignment(label2, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(startBtn, Pos.CENTER);
        scene1 = new Scene(layout1, 600, 600);

        // Drop down menu - list of countries
        countryChoiceBox = new ChoiceBox<>();

        // getItems returns the ObservableList object which you can add items to
        CountryInfo.populateCountries(); // populating countries to be passed into ChoiceBox
        countryChoiceBox.getItems().addAll(CountryInfo.countries);
        countryChoiceBox.setMinWidth(200);
        countryChoiceBox.setValue("Ghana");

        // Button
        selectCountryBtn = new Button("SUBMIT");
        selectCountryBtn.setOnAction(e -> {
            System.out.println("Submit Button");
            try {
                country1 = countryChoiceBox.getValue();
                label3.setText(getChoice(countryChoiceBox));
            } catch (IllegalArgumentException e1) {
                e1.getStackTrace();
            }

            window.setScene(scene3);
        });

        // Layout2
        StackPane layout2 = new StackPane();
        layout2.setPadding(new Insets(10, 10, 10, 10));
        layout2.getChildren().addAll(countryChoiceBox ,selectCountryBtn);
        StackPane.setAlignment(countryChoiceBox, Pos.TOP_CENTER);
        scene2 = new Scene(layout2, 600, 600);

        // Buttons
        anotherBtn = new Button("another");
        anotherBtn.setOnAction(e -> {
            System.out.println("Another Button");
            window.setScene(scene4);
        });

        // Layout3
        StackPane layout3 = new StackPane();
        layout3.setPadding(new Insets(10, 10, 10, 10));
        layout3.getChildren().addAll(label3, anotherBtn);
        layout3.setMinHeight(600);
        StackPane.setAlignment(label3, Pos.TOP_CENTER);
        scene3 = new Scene(layout3);

        // ChoiceBox2
        // Drop down menu - list of countries
        countryChoiceBox2 = new ChoiceBox<>();
        // getItems returns the ObservableList object which you can add items to
        CountryInfo.populateCountries(); // populating countries to be passed into ChoiceBox
        countryChoiceBox2.getItems().addAll(CountryInfo.countries);
        countryChoiceBox2.setMinWidth(200);
        countryChoiceBox2.setValue("Ghana");

        // Button
        selectCountryBtn2 = new Button("SUBMIT");
        selectCountryBtn2.setOnAction(e -> {
            System.out.println("Submit Button");
            try {
                country2 = countryChoiceBox2.getValue();
                label4.setText(getChoice(countryChoiceBox2));
            } catch (IllegalArgumentException e2) {
                e2.getStackTrace();
            }

            window.setScene(scene5);
        });

        // Button
        compareBtn = new Button("Compare to your country");
        compareBtn.setOnAction(e -> {
            System.out.println("Compare Button");
            try {
                String tempStr = compareCountries(country1, country2);
                label5.setText(tempStr);
            } catch (NullPointerException e3) {
                e3.getStackTrace();
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            window.setScene(scene6);
        });

        // Layout4
        StackPane layout4 = new StackPane();
        layout4.setPadding(new Insets(10, 10, 10, 10));
        layout4.getChildren().addAll(countryChoiceBox2, selectCountryBtn2);
        StackPane.setAlignment(countryChoiceBox2, Pos.TOP_CENTER);
        scene4 = new Scene(layout4, 600, 600);

        // Layout5
        StackPane layout5 = new StackPane();
        layout5.setPadding(new Insets(10, 10, 10, 10));
        layout5.getChildren().addAll(label4, compareBtn);
        layout5.setMinHeight(600);
        StackPane.setAlignment(label4, Pos.TOP_CENTER);
        scene5 = new Scene(layout5);

        // Layout6
        StackPane layout6 = new StackPane();
        layout6.setPadding(new Insets(10, 10, 10, 10));
        layout6.getChildren().add(label5);
        layout6.setMinHeight(600);
        StackPane.setAlignment(label5, Pos.TOP_CENTER);
        scene6 = new Scene(layout6);

        window.setScene(scene1);
        window.show();
    }

    /**
     * Retrieves weather statistics for a selected country
     * from a ChoiceBox object, populates them into a map object,
     * then converts the values into a human-readable strmessage
     * @param choiceBox drop-down menu
     * @return String the human-readable string message
     */
    private String getChoice(ChoiceBox<String> choiceBox){
        String country = choiceBox.getValue();
        String country2 = "";
        String message = "Country : "+country+"\n";
        CountryStatistics stats = null;
        Map<String, String> map;
        ArrayList<String> keys = new ArrayList<>();
        keys.add("Temperature");
        keys.add("Condition");
        keys.add("Pressure");
        keys.add("Precipitation");
        keys.add("Humidity");
        keys.add("Emission");

        CountryInfo.populateCountryCodes();
        try {
            stats = new CountryStatistics(country, CountryInfo.countryCodes.get(country));
        } catch (IllegalArgumentException e) {
            e.getStackTrace();
        }

        assert stats != null;
        map = stats.getStatsMap();
        for (int i = 0; i < keys.size(); i++) {

            if (i==0) message += keys.get(i) + " : " + map.get(keys.get(i)) + " °C" + "\n";
            else if (i==2) message += keys.get(i) + " : " + map.get(keys.get(i)) + " mb" + "\n";
            else if (i==3) message += keys.get(i) + " : " + map.get(keys.get(i)) + " mm" + "\n";
            else if (i==4) message += keys.get(i) + " : " + map.get(keys.get(i)) + " %" + "\n";
            else if (i==5) message += keys.get(i) + " : " + map.get(keys.get(i)) + " CO2 equivalent (kg)" + "\n";
            else message += keys.get(i) + " : " + map.get(keys.get(i)) + "\n";
        }
        return message;
    }

    /**
     * Takes in 2 country names, creates 2 CountryStatistics objects and then
     * populates the compared statistics in a Map object, which are converted into
     * a human-readable string message.
     * @param country1 A string object representing the first country to be compared
     * @param country2 A string object representing the second country to be compared
     * @return msg A string object representing the compared statistics between two countries
     * @throws NullPointerException if country1 or country2 points to a null value
     */
    private String compareCountries(String country1, String country2) throws NullPointerException, URISyntaxException{
        String msg = "Countries : "+country1+" and "+country2+"\n";

        CompareTo compared = new CompareTo(
                new CountryStatistics(country1, CountryInfo.countryCodes.get(country1)),
                new CountryStatistics(country2, CountryInfo.countryCodes.get(country2))
        );
        compared.populateCompareMap();

        ArrayList<String> keys = new ArrayList<>();
        keys.add("Temperature");
        keys.add("Pressure");
        keys.add("Precipitation");
        keys.add("Humidity");
        keys.add("Emission");

        for (String key : keys) {
            msg += key + " : " + compared.getCompareMap().get(key) + "\n";
            System.out.println(msg);
        }
        return msg;
    }
}
