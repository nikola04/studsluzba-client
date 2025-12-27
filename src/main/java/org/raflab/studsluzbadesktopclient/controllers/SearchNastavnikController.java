package org.raflab.studsluzbadesktopclient.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbadesktopclient.services.NastavnikService;
import org.raflab.studsluzbadesktopclient.utils.DebouncedSearchHelper;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.raflab.studsluzbadesktopclient.utils.SpringContextHelper;
import org.springframework.stereotype.Component;

@Component
public class SearchNastavnikController {
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
        nastavnikTable.setRowFactory(tv -> {
            TableRow<NastavnikResponseDTO> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openNastavnikDetails(row.getItem());
                }
            });

            return row;
        });
    }
    private void openNastavnikDetails(NastavnikResponseDTO nastavnik) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/nastavnikProfile.fxml")
            );

            loader.setControllerFactory(clazz ->
                    SpringContextHelper.getContext().getBean(clazz)
            );

            Parent root = loader.load();

            NastavnikProfileController controller = loader.getController();
            controller.setNastavnik(nastavnik);

            Stage stage = new Stage();
            stage.setTitle("Profil nastavnika");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.displayError(e);
        }
    }
    public void handleSearchNastavnik(ActionEvent actionEvent) {
        Button button = actionEvent != null ? ((Button) actionEvent.getSource()) : null;
        if (button != null) button.setDisable(true);

        String name = nastavnikNameTf.getText();
        String lastName = nastavnikLastNameTf.getText();

        nastavnikService.searchNastavnik(name, lastName)
                .doFinally(signalType -> {if (button != null) button.setDisable(false);})
                .collectList()
                .subscribe(nastavnikObList::setAll, ErrorHandler::displayError);
    }

    public void handleDeleteNastavnik(ActionEvent actionEvent) {
        NastavnikResponseDTO selected =
                nastavnikTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            ErrorHandler.displayError(
                    new IllegalStateException("Morate selektovati nastavnika za brisanje.")
            );
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Brisanje nastavnika");
        confirm.setHeaderText("Da li ste sigurni?");
        confirm.setContentText(
                "Nastavnik: " + selected.getIme() + " " + selected.getPrezime()
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {

                nastavnikService.deleteNastavnik(selected.getId())
                        .subscribe(
                                r -> Platform.runLater(() ->
                                        nastavnikTable.getItems().remove(selected)
                                ),
                                ErrorHandler::displayError
                        );
            }
        });
    }
}
