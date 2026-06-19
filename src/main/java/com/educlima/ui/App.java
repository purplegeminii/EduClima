package com.educlima.ui;

import com.educlima.data.ClimateService;
import com.educlima.data.CountryRepository;
import com.educlima.logic.ClimateComparator;
import com.educlima.model.ClimateData;
import com.educlima.model.ComparisonLine;
import com.educlima.model.Country;
import com.educlima.model.Metric;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EduClima desktop application.
 *
 * <p>A single responsive screen: two country slots side by side (stacked when the
 * window is narrow), each with a type-to-search picker that loads that country's
 * climate in the background the moment it is chosen. A comparison panel updates
 * automatically once both slots hold data. The UI never blocks on the network.
 */
public class App extends Application {

    private static final double STACK_THRESHOLD = 560;

    private final CountryRepository countries = CountryRepository.getInstance();
    private final ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "educlima-fetch");
        thread.setDaemon(true);
        return thread;
    });

    private ClimateService service;
    private String configError;

    private Slot slotA;
    private Slot slotB;
    private final VBox slotsHolder = new VBox();
    private final VBox comparisonBox = new VBox(6);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            service = ClimateService.fromEnvironment();
        } catch (RuntimeException e) {
            configError = e.getMessage();
        }

        slotA = new Slot("First country");
        slotB = new Slot("Second country");

        VBox root = new VBox(18, header(), slotsHolder, comparisonSection());
        root.setPadding(new Insets(24));
        VBox.setVgrow(slotsHolder, Priority.ALWAYS);
        refreshComparison();

        Scene scene = new Scene(root, 760, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/com/educlima/app.css")).toExternalForm());
        scene.widthProperty().addListener((obs, old, width) -> layoutSlots(width.doubleValue()));
        layoutSlots(scene.getWidth());

        stage.setTitle("EduClima");
        stage.setMinWidth(420);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

    // --- Top-level layout --------------------------------------------------

    private Node header() {
        Label title = new Label("EduClima");
        title.getStyleClass().add("title");
        Label tagline = new Label("Explore and compare countries' climate");
        tagline.getStyleClass().add("tagline");
        VBox titles = new VBox(2, title, tagline);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button swap = new Button("Swap");
        swap.setOnAction(e -> swap());
        Button clear = new Button("Clear");
        clear.setOnAction(e -> clearAll());

        HBox bar = new HBox(10, titles, spacer, swap, clear);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private Node comparisonSection() {
        Label heading = new Label("Comparison");
        heading.getStyleClass().add("section-heading");
        comparisonBox.getStyleClass().add("comparison");
        return new VBox(6, heading, comparisonBox);
    }

    /** Lays the two slots side by side, or stacked when the window is narrow. */
    private void layoutSlots(double width) {
        boolean stacked = width < STACK_THRESHOLD;
        Region container = stacked
                ? new VBox(14, slotA.root(), slotB.root())
                : new HBox(14, slotA.root(), slotB.root());
        for (Node child : List.of(slotA.root(), slotB.root())) {
            if (stacked) {
                VBox.setVgrow(child, Priority.ALWAYS);
            } else {
                HBox.setHgrow(child, Priority.ALWAYS);
            }
        }
        slotsHolder.getChildren().setAll(container);
        VBox.setVgrow(container, Priority.ALWAYS);
    }

    private void refreshComparison() {
        comparisonBox.getChildren().clear();
        ClimateData a = slotA == null ? null : slotA.data;
        ClimateData b = slotB == null ? null : slotB.data;
        if (a != null && b != null) {
            for (ComparisonLine line : ClimateComparator.compare(a, b)) {
                Label label = new Label("•  " + line.message());
                label.setWrapText(true);
                label.getStyleClass().add("compare-line");
                comparisonBox.getChildren().add(label);
            }
        } else {
            Label hint = new Label("Pick two countries to see how they compare.");
            hint.getStyleClass().add("hint");
            comparisonBox.getChildren().add(hint);
        }
    }

    private void swap() {
        Country countryA = slotA.country;
        ClimateData dataA = slotA.data;
        slotA.setState(slotB.country, slotB.data);
        slotB.setState(countryA, dataA);
        refreshComparison();
    }

    private void clearAll() {
        slotA.reset();
        slotB.reset();
        refreshComparison();
    }

    private static String describe(Throwable error) {
        if (error instanceof java.io.IOException) {
            return "Network problem: " + error.getMessage()
                    + "\nPlease check your connection and try again.";
        }
        return error == null ? "An unexpected error occurred." : error.getMessage();
    }

    // --- A single country slot: picker + card ------------------------------

    private final class Slot {
        private final CountryPicker picker;
        private final StackPane card = new StackPane();
        private final VBox root;

        private Country country;
        private ClimateData data;
        private Task<ClimateData> inFlight;

        Slot(String placeholder) {
            picker = new CountryPicker(countries.names(), placeholder);
            picker.setOnCountryChosen(this::choose);

            card.getStyleClass().add("card");
            card.setMinHeight(230);
            showPlaceholder();

            root = new VBox(10, picker.node(), card);
            VBox.setVgrow(card, Priority.ALWAYS);
        }

        Node root() {
            return root;
        }

        private void choose(String name) {
            countries.findByName(name).ifPresent(chosen -> {
                country = chosen;
                load();
            });
        }

        private void load() {
            if (service == null) {
                data = null;
                showError(configError != null ? configError : "App is not configured with API keys.");
                refreshComparison();
                return;
            }
            if (inFlight != null) {
                inFlight.cancel();
            }
            showLoading(country.name());

            Task<ClimateData> task = new Task<>() {
                @Override
                protected ClimateData call() throws Exception {
                    return service.fetch(country);
                }
            };
            inFlight = task;
            task.setOnSucceeded(e -> {
                if (task != inFlight) {
                    return; // superseded by a newer selection
                }
                data = task.getValue();
                showData();
                refreshComparison();
            });
            task.setOnFailed(e -> {
                if (task != inFlight) {
                    return;
                }
                data = null;
                showError(describe(task.getException()));
                refreshComparison();
            });
            executor.execute(task);
        }

        /** Applies a country + data pair directly (used by swap), without refetching. */
        private void setState(Country country, ClimateData data) {
            if (inFlight != null) {
                inFlight.cancel();
                inFlight = null;
            }
            this.country = country;
            this.data = data;
            picker.selectSilently(country == null ? null : country.name());
            if (data != null) {
                showData();
            } else {
                showPlaceholder();
            }
        }

        private void reset() {
            if (inFlight != null) {
                inFlight.cancel();
                inFlight = null;
            }
            country = null;
            data = null;
            picker.selectSilently(null);
            showPlaceholder();
        }

        // -- card states --

        private void showPlaceholder() {
            Label label = new Label("Choose a country to see its climate.");
            label.getStyleClass().add("hint");
            label.setWrapText(true);
            setCard(Pos.CENTER, label);
        }

        private void showLoading(String name) {
            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setMaxSize(48, 48);
            Label label = new Label("Fetching " + name + "…");
            label.getStyleClass().add("hint");
            VBox box = new VBox(14, spinner, label);
            box.setAlignment(Pos.CENTER);
            setCard(Pos.CENTER, box);
        }

        private void showData() {
            VBox content = new VBox(8);
            Label title = new Label(data.country().name());
            title.getStyleClass().add("card-title");
            content.getChildren().add(title);
            for (Metric metric : Metric.values()) {
                content.getChildren().add(metricRow(metric.label(), data.displayValue(metric)));
            }
            setCard(Pos.TOP_LEFT, content);
        }

        private void showError(String message) {
            Label heading = new Label("Couldn't load " + (country == null ? "country" : country.name()));
            heading.getStyleClass().add("card-title");
            Label detail = new Label(message);
            detail.getStyleClass().add("error");
            detail.setWrapText(true);
            Button retry = new Button("Try again");
            retry.setOnAction(e -> load());
            VBox box = new VBox(10, heading, detail, retry);
            box.setAlignment(Pos.CENTER_LEFT);
            setCard(Pos.TOP_LEFT, box);
        }

        private void setCard(Pos alignment, Node content) {
            card.getChildren().setAll(content);
            StackPane.setAlignment(content, alignment);
        }

        private Node metricRow(String label, String value) {
            Label name = new Label(label);
            name.getStyleClass().add("metric-label");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label val = new Label(value);
            val.getStyleClass().add("metric-value");
            HBox row = new HBox(8, name, spacer, val);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("metric-row");
            return row;
        }
    }
}
