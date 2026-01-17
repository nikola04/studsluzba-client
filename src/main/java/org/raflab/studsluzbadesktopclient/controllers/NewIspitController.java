package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.raflab.studsluzbacommon.dto.request.IspitRequest;
import org.raflab.studsluzbacommon.dto.response.DrziPredmetResponse;
import org.raflab.studsluzbacommon.dto.response.IspitniRokResponse;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbacommon.dto.response.PredmetResponse;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class NewIspitController {
    private final NavigationController navigationController;
    public TabPane ispitTabPane;
    @FXML
    private ComboBox<IspitniRokResponse> cmbIspitniRok;
    @FXML private ComboBox<PredmetResponse> cmbPredmet;
    @FXML private ComboBox<NastavnikResponseDTO> cmbNastavnik;
    public DatePicker dpDatum;
    public TextField txtVreme;
    public CheckBox chkZakljucen;

    private final PredmetService predmetService;
    private final IspitniRokService ispitniRokService; // Pretpostavljeni servis
    private final IspitService ispitService;

    public NewIspitController(NavigationController navigationController, PredmetService predmetService, IspitniRokService ispitniRokService, IspitService ispitService) {
        this.navigationController = navigationController;
        this.predmetService = predmetService;
        this.ispitniRokService = ispitniRokService;
        this.ispitService = ispitService;
    }

    public void initialize() {
        this.initializeComboBoxes();

        ispitniRokService.fetchIspitniRok().collectList().subscribe(rokovi -> Platform.runLater(() -> cmbIspitniRok.getItems().setAll(rokovi)), ErrorHandler::displayError);

        predmetService.fetchPredmeti().collectList().subscribe(predmeti -> Platform.runLater(() -> cmbPredmet.getItems().setAll(predmeti)), ErrorHandler::displayError);

        cmbPredmet.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                this.updateNastavniciForPredmet(newVal.getId());
            } else {
                cmbNastavnik.setDisable(true);
                cmbNastavnik.getItems().clear();
            }
        });
        ispitTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> navigationController.navigateTo("ispiti:tab:" + newIdx));
    }

    private void updateNastavniciForPredmet(Long id) {
        predmetService.getPredmetDrzi(id).collectList().subscribe(drziList -> Platform.runLater(() -> {
            List<NastavnikResponseDTO> nastavnici = drziList.stream()
                    .map(DrziPredmetResponse::getNastavnik)
                    .toList();
            cmbNastavnik.getItems().setAll(nastavnici);
            cmbNastavnik.setDisable(false);
            if (!nastavnici.isEmpty()) {
                cmbNastavnik.getSelectionModel().selectFirst();
            }
        }), ErrorHandler::displayError);
    }

    private void initializeComboBoxes() {
        cmbIspitniRok.setConverter(new StringConverter<>() {
            @Override
            public String toString(IspitniRokResponse item) {
                if (item == null) return "";
                return String.format("%s.%s. - %s.%s. (%d)",
                        item.getPocetak().getDayOfMonth(), item.getPocetak().getMonthValue(),
                        item.getKraj().getDayOfMonth(), item.getKraj().getMonthValue(),
                        item.getSkolskaGodina().getGodina());
            }
            @Override public IspitniRokResponse fromString(String s) { return null; }
        });

        cmbPredmet.setConverter(new StringConverter<>() {
            @Override
            public String toString(PredmetResponse item) { return item == null ? "" : item.getNaziv(); }
            @Override public PredmetResponse fromString(String s) { return null; }
        });

        cmbNastavnik.setConverter(new StringConverter<>() {
            @Override
            public String toString(NastavnikResponseDTO item) { return item == null ? "" : item.getIme() + " " + item.getPrezime(); }
            @Override public NastavnikResponseDTO fromString(String s) { return null; }
        });
    }

    public void handleCancel() {
        this.clearFields();
    }

    public void handleSave(ActionEvent actionEvent) {
        if(dpDatum.getValue() == null || txtVreme.getText().isBlank() || cmbPredmet.getValue() == null || cmbNastavnik.getValue() == null || cmbIspitniRok.getValue() == null) {
            ErrorHandler.displayError(new InvalidDataException("Please fill all fields."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);
        try {
            IspitRequest request = new IspitRequest();
            request.setDatumOdrzavanja(dpDatum.getValue());
            request.setVremePocetka(LocalTime.parse(txtVreme.getText()));
            request.setPredmetId(cmbPredmet.getValue().getId());
            request.setNastavnikId(cmbNastavnik.getValue().getId());
            request.setIspitniRokId(cmbIspitniRok.getValue().getId());
            request.setZakljucen(chkZakljucen.isSelected());

            ispitService.saveIspit(request)
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(res -> Platform.runLater(this::clearFields), ErrorHandler::displayError);

        } catch (DateTimeParseException e) {
            ErrorHandler.displayError(new InvalidDataException("Please enter valid time (HH:mm)."));
            button.setDisable(false);
        }
    }

    private void clearFields() {
        cmbPredmet.getSelectionModel().clearSelection();
        cmbIspitniRok.getSelectionModel().clearSelection();
        cmbNastavnik.getSelectionModel().clearSelection();
        dpDatum.setValue(null);
        txtVreme.clear();
    }
}
