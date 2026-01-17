package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.raflab.studsluzbacommon.dto.request.IspitniRokRequest;
import org.raflab.studsluzbacommon.dto.response.IspitniRokResponse;
import org.raflab.studsluzbacommon.dto.response.SkolskaGodinaResponseDTO;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class IspitniRokoviController {
    private final NavigationController navigationController;
    private final MainView mainView;
    @FXML private TableView<IspitniRokResponse> tblIspitniRokovi;
    @FXML private TableColumn<IspitniRokResponse, String> colNaziv;
    @FXML private TableColumn<IspitniRokResponse, String> colPocetak;
    @FXML private TableColumn<IspitniRokResponse, String> colKraj;
    @FXML private TableColumn<IspitniRokResponse, String> colSkolskaGodina;
    @FXML private ComboBox<SkolskaGodinaResponseDTO> cmbSkolskaGodina;
    @FXML private DatePicker dpPocetak;
    @FXML private DatePicker dpKraj;
    @FXML private TabPane rokTabPane;

    private final SkolskaGodinaService skolskaGodinaService;
    private final IspitniRokService ispitniRokService;

    private final ObservableList<IspitniRokResponse> rokObList = FXCollections.observableArrayList();

    public IspitniRokoviController(NavigationController navigationController, SkolskaGodinaService skolskaGodinaService, IspitniRokService ispitniRokService, MainView mainView) {
        this.skolskaGodinaService = skolskaGodinaService;
        this.ispitniRokService = ispitniRokService;
        this.navigationController = navigationController;
        this.mainView = mainView;
    }

    public void initialize() {
        this.initializeConverters();

        this.tblIspitniRokovi.setItems(rokObList);

        skolskaGodinaService.fetchSkolskeGodine()
                .collectList()
                .subscribe(godine -> Platform.runLater(() -> {
                    cmbSkolskaGodina.getItems().setAll(godine);
                    godine.stream().filter(SkolskaGodinaResponseDTO::getAktivan).findFirst().ifPresent(g -> cmbSkolskaGodina.getSelectionModel().select(g));
                }), ErrorHandler::displayError);

        rokObList.clear();
        ispitniRokService.fetchIspitniRok().subscribe(ispitniRok -> Platform.runLater(() -> rokObList.add(ispitniRok)));

        tblIspitniRokovi.setRowFactory(tv -> {
            TableRow<IspitniRokResponse> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()))
                    this.handleRokSelection(row.getItem());
            });
            return row;
        });

        rokTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> navigationController.navigateTo("rokovi:tab:" + newIdx));
    }

    private void handleRokSelection(IspitniRokResponse ispitniRok) {
        mainView.openModal("ispitniRok", "Ispitni Rok", (IspitniRokDetailsController controller) -> controller.setIspitniRok(ispitniRok));
    }

    private void initializeConverters() {
        cmbSkolskaGodina.setConverter(new StringConverter<>() {
            @Override
            public String toString(SkolskaGodinaResponseDTO item) {
                return (item == null) ? "" : item.getGodina().toString() + (item.getAktivan() ? " (Aktivna)" : "");
            }
            @Override public SkolskaGodinaResponseDTO fromString(String s) { return null; }
        });

        colNaziv.setCellValueFactory(cellData -> {
            LocalDate pocetak = cellData.getValue().getPocetak();
            if (pocetak != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", new Locale("en", "RS"));
                String nazivMeseca = pocetak.format(formatter);

                nazivMeseca = nazivMeseca.substring(0, 1).toUpperCase() + nazivMeseca.substring(1);

                return new SimpleStringProperty(nazivMeseca);
            }
            return new SimpleStringProperty("");
        });

        colPocetak.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPocetak().toString()));

        colKraj.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKraj().toString()));

        colSkolskaGodina.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSkolskaGodina().getGodina().toString()));

    }

    public void handleCancel() {
        this.clearForm();
    }

    public void handleSave(ActionEvent actionEvent) {
        LocalDate pocetak = dpPocetak.getValue();
        LocalDate kraj = dpKraj.getValue();
        SkolskaGodinaResponseDTO godina = cmbSkolskaGodina.getValue();

        if (pocetak == null || kraj == null || godina == null) {
            ErrorHandler.displayError(new InvalidDataException("Please fill all fields."));
            return;
        }

        if (kraj.isBefore(pocetak)) {
            ErrorHandler.displayError(new InvalidDataException("Datum kraja ne može biti pre datuma početka."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        IspitniRokRequest request = new IspitniRokRequest();
        request.setPocetak(pocetak);
        request.setKraj(kraj);
        request.setSkolskaGodinaId(godina.getId());

        ispitniRokService.saveIspitniRok(request)
                .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                .subscribe(res -> Platform.runLater(this::clearForm), ErrorHandler::displayError);
    }

    private void clearForm() {
        cmbSkolskaGodina.getSelectionModel().clearSelection();
        dpPocetak.setValue(null);
        dpKraj.setValue(null);
    }

    public void handleDeleteRok(ActionEvent actionEvent) {
        IspitniRokResponse selected = tblIspitniRokovi.getSelectionModel().getSelectedItem();

        if (selected == null) {
            ErrorHandler.displayError(new InvalidDataException("Please select rok for deletion."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ispitni Rok deletion");
        confirm.setHeaderText("Please confirm deletion of rok:");
        confirm.setContentText(selected.getPocetak() + " - " + selected.getKraj() + ", Skolska Godina: " + selected.getSkolskaGodina().getGodina());

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                button.setDisable(false);
                return;
            }
            ispitniRokService.delete(selected.getId())
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(r -> Platform.runLater(() -> tblIspitniRokovi.getItems().remove(selected)), ErrorHandler::displayError);
        });
    }
}
