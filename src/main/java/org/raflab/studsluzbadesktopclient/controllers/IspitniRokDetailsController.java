package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbacommon.dto.response.IspitResponse;
import org.raflab.studsluzbacommon.dto.response.IspitniRokResponse;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class IspitniRokDetailsController {
    private final MainView mainView;
    @FXML
    private Label lblNaziv, lblPocetak, lblKraj, lblSkolskaGodina;

    @FXML private TableView<IspitResponse> tblIspiti;
    @FXML private TableColumn<IspitResponse, String> colPredmet, colNastavnik, colDatum, colVreme, colStatus;

    private final IspitService ispitService;
    private final IspitniRokService ispitniRokService;

    private IspitniRokResponse trenutniRok;

    public IspitniRokDetailsController(IspitService ispitService, IspitniRokService ispitniRokService, MainView mainView) {
        this.ispitService = ispitService;
        this.ispitniRokService = ispitniRokService;
        this.mainView = mainView;
    }

    public void initialize() {
        colPredmet.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPredmet().getNaziv()));
        colNastavnik.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNastavnik().getIme() + " " + cellData.getValue().getNastavnik().getPrezime()));
        colDatum.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDatumOdrzavanja().toString()));
        colVreme.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVremePocetka().toString()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getZakljucen() ? "Zaključen" : "Otvoren"));

        tblIspiti.setRowFactory(tv -> {
            TableRow<IspitResponse> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()))
                    this.handleIspitSelection(row.getItem());
            });
            return row;
        });
    }

    private void handleIspitSelection(IspitResponse item) {
        mainView.openModal("ispitDetails", "Ispit Details", (IspitDetailsController controller) -> controller.setIspit(item));
    }

    private void updateIspitniRokData(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", new Locale("en", "SR"));
        String mesec = trenutniRok.getPocetak().format(formatter);

        lblNaziv.setText(mesec.substring(0, 1).toUpperCase() + mesec.substring(1));
        lblPocetak.setText(trenutniRok.getPocetak().toString());
        lblKraj.setText(trenutniRok.getKraj().toString());
        lblSkolskaGodina.setText(trenutniRok.getSkolskaGodina().getGodina().toString());
    }

    private void updateIspitiTable() {
        tblIspiti.getItems().clear();
        ispitniRokService.fetchIspitByRokId(trenutniRok.getId()).subscribe(ispitResponse -> Platform.runLater(() -> tblIspiti.getItems().add(ispitResponse)), ErrorHandler::displayError);
    }

    public void setIspitniRok(IspitniRokResponse ispitniRok) {
        this.trenutniRok = ispitniRok;
        this.updateIspitniRokData();
        this.updateIspitiTable();
    }

    public void handleDeleteIspit(ActionEvent actionEvent) {
        IspitResponse selected = tblIspiti.getSelectionModel().getSelectedItem();

        if (selected == null) {
            ErrorHandler.displayError(new InvalidDataException("Please select ispit for deletion."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ispit deletion");
        confirm.setHeaderText("Please confirm deletion of ispit:");
        confirm.setContentText(selected.getPredmet().getNaziv() + " - " + selected.getDatumOdrzavanja() + ", Nastavnik: " + selected.getNastavnik().getIme() + " " + selected.getNastavnik().getPrezime());

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                button.setDisable(false);
                return;
            }
            ispitService.delete(selected.getId())
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(r -> Platform.runLater(() -> tblIspiti.getItems().remove(selected)), ErrorHandler::displayError);
        });
    }
}
