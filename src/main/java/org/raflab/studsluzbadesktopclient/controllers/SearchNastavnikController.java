package org.raflab.studsluzbadesktopclient.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbadesktopclient.services.NastavnikService;
import org.raflab.studsluzbadesktopclient.utils.DebouncedSearchHelper;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchNastavnikController {
    private PauseTransition searchDebounce;

    private final NastavnikService nastavnikService;
    private final ObservableList<NastavnikResponseDTO> nastavnikObList = FXCollections.observableArrayList();

    @FXML
    public BorderPane searchNastavnikPane;

    @FXML
    private TextField nastavnikNameTf;
    @FXML
    private TextField nastavnikLastNameTf;

    @FXML
    private TableView<NastavnikResponseDTO> nastavnikTable;

    public SearchNastavnikController(NastavnikService nastavnikService) {
        this.nastavnikService = nastavnikService;
    }

    public void initialize(){
        nastavnikTable.setItems(nastavnikObList);
        new DebouncedSearchHelper(
                Duration.millis(300),
                () -> handleSearchNastavnik(null),
                nastavnikNameTf,
                nastavnikLastNameTf
        );

        this.handleSearchNastavnik(null);
    }

    public void handleSearchNastavnik(ActionEvent actionEvent) {
        Button button = actionEvent != null ? ((Button) actionEvent.getSource()) : null;
        if (button != null) button.setDisable(true);

        String name = nastavnikNameTf.getText();
        String lastName = nastavnikLastNameTf.getText();

        nastavnikService.searchNastavnik(name, lastName)
                .subscribe(nastavnikList -> {
                    Platform.runLater(() ->
                            nastavnikObList.setAll(nastavnikList)
                    );
                }, ErrorHandler::displayError, () -> {
                    if (button != null) button.setDisable(false);
                });
    }

}
