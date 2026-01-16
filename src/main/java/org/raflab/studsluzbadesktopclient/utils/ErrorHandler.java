package org.raflab.studsluzbadesktopclient.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.raflab.studsluzbadesktopclient.exceptions.ConflictException;
import org.raflab.studsluzbadesktopclient.exceptions.ResourceNotFoundException;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.exceptions.ServerCommunicationException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

public class ErrorHandler {
    public static void displayError(Throwable ex) {
        // Run on JavaFX UI Thread
        Platform.runLater(() -> {
            String title = "Error!";
            String message = ex.getLocalizedMessage();
            String header = formatErrorHeader(ex);
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private static String formatErrorHeader(Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

        if (cause instanceof InvalidDataException)
            return "Invalid data.";
        if (cause instanceof NullPointerException)
            return "Null pointer.";
        if (cause instanceof NumberFormatException)
            return "Invalid number format.";
        if (cause instanceof IllegalStateException)
            return "Illegal state.";
        if (cause instanceof ResourceNotFoundException)
            return "Resource not found.";
        if (cause instanceof ServerCommunicationException || cause instanceof WebClientRequestException)
            return "Communication failed.";
        if (cause instanceof ConflictException)
            return "Conflict.";
        ex.printStackTrace();
        return "Unexpected error.";
    }
}
