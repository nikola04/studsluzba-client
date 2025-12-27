package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.NavigationHistoryService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class NavigationController {
    private final MainView mainView;
    private final MainWindowController mainWindowController;
    private final NavigationHistoryService historyService;

    public NavigationController(MainView mainView, NavigationHistoryService historyService, MainWindowController mainWindowController) {
        this.mainView = mainView;
        this.historyService = historyService;
        this.mainWindowController = mainWindowController;
    }

    public void navigateTo(String fxml) {
        historyService.push(fxml);
        if(!fxml.contains(":tab:")) loadView(fxml);
    }

    public void goBack() {
        String fxml = historyService.popBack();
        if (fxml != null) loadView(fxml);
    }

    public void goForward() {
        String fxml = historyService.popForward();
        if (fxml != null) loadView(fxml);
    }

    private void loadView(String state) {
        Platform.runLater(() -> {
            try {
                String fxmlPath;
                Integer tabIndex = null;

                if (state.contains(":tab:")) {
                    String[] parts = state.split(":tab:");
                    fxmlPath = parts[0];
                    tabIndex = Integer.parseInt(parts[1]);
                } else {
                    fxmlPath = state;
                }

                Node node = mainView.loadPane(fxmlPath);

                if (node != null) {
                    mainWindowController.setView(node);

                    if (tabIndex != null) {
                        final int targetTab = tabIndex;
                        Platform.runLater(() -> selectTabIfPresent(node, targetTab));
                    }
                }
            } catch (Exception e) {
                ErrorHandler.displayError(e);
            }
        });
    }
    private void selectTabIfPresent(Node node, int index) {
        if (node instanceof TabPane tp) {
            tp.getSelectionModel().select(index);
        } else {
            Node found = node.lookup(".tab-pane");
            if (found instanceof TabPane tpFound) {
                tpFound.getSelectionModel().select(index);
            }
        }
    }
}