package org.raflab.studsluzbadesktopclient.utils;

import javafx.animation.PauseTransition;
import javafx.scene.control.TextField;
import javafx.util.Duration;


public class DebouncedSearchHelper {
    private final PauseTransition debounce;

    public DebouncedSearchHelper(Duration delay, Runnable action, TextField... fields) {
        this.debounce = new PauseTransition(delay);
        this.debounce.setOnFinished(e -> action.run());

        for (TextField field : fields) {
            field.textProperty().addListener((obs, oldVal, newVal) -> {
                debounce.playFromStart();
            });
        }
    }
}
