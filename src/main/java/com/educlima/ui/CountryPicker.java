package com.educlima.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * An editable combo box that filters its list to substring matches as the user
 * types and commits on selection.
 *
 * <p>Filtering changes the {@link FilteredList} predicate; it never replaces the
 * backing list. That matters: replacing the items list on each keystroke (an
 * earlier approach) wiped the selected value out from under the selection handler,
 * so picks silently failed to register. Committing on {@code selectedItemProperty}
 * — the signal a list pick actually drives — makes selection reliable.
 */
final class CountryPicker {
    private final ComboBox<String> combo;
    private final ObservableList<String> master;
    private final FilteredList<String> filtered;

    private Consumer<String> onChosen = name -> { };
    private boolean suppress;
    private String lastFired;

    CountryPicker(List<String> names, String placeholder) {
        master = FXCollections.observableArrayList(names);
        filtered = new FilteredList<>(master, s -> true);
        combo = new ComboBox<>(filtered);
        combo.setEditable(true);
        combo.setVisibleRowCount(10);
        combo.setPromptText(placeholder);
        combo.setMaxWidth(Double.MAX_VALUE);

        TextField editor = combo.getEditor();

        editor.textProperty().addListener((obs, oldText, newText) -> {
            if (suppress) {
                return;
            }
            String selected = combo.getSelectionModel().getSelectedItem();
            // Don't refilter right after an item was picked (text == selection).
            if (selected != null && selected.equals(newText)) {
                return;
            }
            // Defer to dodge JDK-8081700 (mutating the list during a change event).
            Platform.runLater(() -> {
                String typed = editor.getText() == null ? "" : editor.getText();
                String lower = typed.toLowerCase(Locale.ROOT);
                filtered.setPredicate(item -> typed.isEmpty()
                        || item.toLowerCase(Locale.ROOT).contains(lower));
                if (combo.isFocused() && !typed.isEmpty() && !filtered.isEmpty()) {
                    combo.show();
                }
            });
        });

        combo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, name) -> {
            if (name != null) {
                Platform.runLater(() -> filtered.setPredicate(s -> true));
            }
            if (!suppress && name != null && master.contains(name)) {
                fire(name);
            }
        });

        // Covers typing an exact name and pressing Enter (no list selection change).
        combo.setOnAction(e -> {
            if (suppress) {
                return;
            }
            String value = combo.getValue();
            if (value != null && master.contains(value)) {
                fire(value);
            }
        });
    }

    ComboBox<String> node() {
        return combo;
    }

    void setOnCountryChosen(Consumer<String> handler) {
        this.onChosen = handler;
    }

    /** Sets the selection programmatically without triggering a load. */
    void selectSilently(String name) {
        suppress = true;
        filtered.setPredicate(s -> true);
        if (name == null) {
            combo.getSelectionModel().clearSelection();
            combo.setValue(null);
            combo.getEditor().clear();
        } else {
            combo.getSelectionModel().select(name);
            combo.getEditor().setText(name);
        }
        lastFired = name;
        suppress = false;
    }

    /** Notifies the handler once per distinct consecutive selection. */
    private void fire(String name) {
        if (name.equals(lastFired)) {
            return;
        }
        lastFired = name;
        onChosen.accept(name);
    }
}
