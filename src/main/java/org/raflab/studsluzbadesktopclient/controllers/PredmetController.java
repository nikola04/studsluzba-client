package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.NastavnikService;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.raflab.studsluzbadesktopclient.services.StudijskiProgramService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class PredmetController {
    private final StudijskiProgramService studijskiProgramService;
    private final PredmetService predmetService;
    private final NastavnikService nastavnikService;
    private final SkolskaGodinaService skolskaGodinaService;
    public TableView<DrziPredmetResponse> tblDrziPredmet;
    public TableColumn<DrziPredmetResponse, String> colIme;
    public TableColumn<DrziPredmetResponse, String> colPrezime;
    public TableColumn<DrziPredmetResponse, String> colSkolskaGodina;
    public ComboBox<NastavnikResponseDTO> cmbNastavnik;
    public ComboBox<SkolskaGodinaResponseDTO> cmbSkolskaGodina;
    private PredmetResponse predmet;

    public TextField txtSifra;
    public TextField txtNaziv;
    public TextField txtEspb;
    public TextField txtFondPredavanja;
    public TextField txtFondVezbe;
    public CheckBox chkObavezan;
    public TextArea txtOpis;
    public ComboBox<StudijskiProgramResponseDTO> studProgramCb;

    private final ObservableList<DrziPredmetResponse> drziPredmetObList = FXCollections.observableArrayList();

    public PredmetController(StudijskiProgramService studijskiProgramService, PredmetService predmetService, NastavnikService nastavnikService, SkolskaGodinaService skolskaGodinaService) {
        this.studijskiProgramService = studijskiProgramService;
        this.predmetService = predmetService;
        this.nastavnikService = nastavnikService;
        this.skolskaGodinaService = skolskaGodinaService;
    }

    public void initialize(){
        studProgramCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(StudijskiProgramResponseDTO item) {
                return (item == null) ? "" : "[" + item.getOznaka() + "] " + item.getNaziv();
            }

            @Override
            public StudijskiProgramResponseDTO fromString(String s) {
                return null;
            }
        });

        cmbNastavnik.setConverter(new StringConverter<>() {
            @Override
            public String toString(NastavnikResponseDTO item) {
                return (item == null) ? "" : item.getIme() + " " + item.getPrezime();
            }

            @Override
            public NastavnikResponseDTO fromString(String s) {
                return null;
            }
        });

        cmbSkolskaGodina.setConverter(new StringConverter<>() {
            @Override
            public String toString(SkolskaGodinaResponseDTO item) {
                return (item == null) ? "" : item.getGodina().toString();
            }

            @Override
            public SkolskaGodinaResponseDTO fromString(String s) {
                return null;
            }
        });

        colIme.setCellValueFactory(cellData -> {
            if (cellData.getValue().getNastavnik() != null) {
                return new SimpleStringProperty(cellData.getValue().getNastavnik().getIme());
            }
            return new SimpleStringProperty("");
        });

        colPrezime.setCellValueFactory(cellData -> {
            if (cellData.getValue().getNastavnik() != null) {
                return new SimpleStringProperty(cellData.getValue().getNastavnik().getPrezime());
            }
            return new SimpleStringProperty("");
        });

        colSkolskaGodina.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSkolskaGodinaId().toString()));

        tblDrziPredmet.setItems(drziPredmetObList);

        nastavnikService.fetchNastavnik().collectList().subscribe(nastavnikList -> Platform.runLater(() -> cmbNastavnik.getItems().setAll(nastavnikList)), ErrorHandler::displayError);
        skolskaGodinaService.fetchSkolskeGodine().collectList().subscribe(skolskaGodinaList -> Platform.runLater(() -> cmbSkolskaGodina.getItems().setAll(skolskaGodinaList)), ErrorHandler::displayError);
        studijskiProgramService.fetchStudijskiProgram().collectList().subscribe(programs -> studProgramCb.getItems().setAll(programs), ErrorHandler::displayError);
    }

    public void setPredmet(PredmetResponse predmet){
        this.predmet = predmet;
        this.updateFormData();

        predmetService.getPredmetDrzi(predmet.getId()).collectList().subscribe(drziPredmetList -> Platform.runLater(() -> drziPredmetObList.setAll(drziPredmetList)), ErrorHandler::displayError);
    }

    private void updateFormData() {
        txtSifra.setText(predmet.getSifra());
        txtNaziv.setText(predmet.getNaziv());
        txtEspb.setText(predmet.getEspb().toString());
        txtFondPredavanja.setText(predmet.getFondCasovaPredavanja().toString());
        txtFondVezbe.setText(predmet.getFondCasovaVezbe().toString());
        txtOpis.setText(predmet.getOpis());
        chkObavezan.setSelected(predmet.getObavezan());
        studProgramCb.getSelectionModel().select(predmet.getStudijskiProgram());
    }

    public void handleCancel() {
        this.closeWindow();
    }

    public void handleSave(ActionEvent actionEvent) {
        String sifra = txtSifra.getText();
        String naziv = txtNaziv.getText();
        String espbTxt = txtEspb.getText();
        String fondPredavanjaTxt = txtFondPredavanja.getText();
        String fondVezbeTxt = txtFondVezbe.getText();
        String opis = txtOpis.getText();
        Boolean obavezan = chkObavezan.isSelected();
        StudijskiProgramResponseDTO studijskiProgram = studProgramCb.getValue();
        if (sifra.isBlank() || naziv.isBlank() || espbTxt.isBlank() || fondVezbeTxt.isBlank() || fondPredavanjaTxt.isBlank() || opis.isBlank() || studijskiProgram == null){
            ErrorHandler.displayError(new InvalidDataException("All fields are required."));
            return;
        }

        try {
            Integer espb = Integer.parseInt(espbTxt);
            Integer fondPredavanja = Integer.parseInt(fondPredavanjaTxt);
            Integer fondVezbe = Integer.parseInt(fondVezbeTxt);

            Button button = (Button) actionEvent.getSource();
            button.setDisable(true);

            predmetService.updatePredmet(predmet.getId(), naziv, espb, obavezan, opis, sifra, studijskiProgram.getId(), fondVezbe, fondPredavanja)
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe((updated) -> Platform.runLater(() -> {
                        this.setPredmet(updated);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Predmet updated successfully.");
                        alert.showAndWait();
                    }), ErrorHandler::displayError);

        }catch (NumberFormatException e){
            ErrorHandler.displayError(e);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) txtSifra.getScene().getWindow();
        stage.close();
    }

    public void handleAddDrziPredmet() {
        NastavnikResponseDTO nastavnik = cmbNastavnik.getValue();
        SkolskaGodinaResponseDTO skolskaGod = cmbSkolskaGodina.getValue();
        predmetService.createDrziPredmet(predmet.getId(), nastavnik.getId(), skolskaGod.getId()).subscribe(id -> Platform.runLater(() -> {
            DrziPredmetResponse drziPredmet = new DrziPredmetResponse();

            drziPredmet.setId(id);
            drziPredmet.setPredmet(this.predmet);
            drziPredmet.setNastavnik(nastavnik);
            drziPredmet.setSkolskaGodinaId(skolskaGod.getId());
            drziPredmet.setPredmetId(this.predmet.getId());
            drziPredmet.setNastavnikId(nastavnik.getId());

            drziPredmetObList.add(drziPredmet);
            tblDrziPredmet.refresh();
        }), ErrorHandler::displayError);
    }

    public void handleRemoveDrziPredmet() {
        DrziPredmetResponse selected = tblDrziPredmet.getSelectionModel().getSelectedItem();
        if(selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Angazovanje deletion");
        confirm.setHeaderText("Please confirm deletion of angazovanje:");
        confirm.setContentText(selected.getNastavnik().getIme() + " " + selected.getNastavnik().getPrezime() + " / " + selected.getSkolskaGodinaId());

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK)
                return;

            predmetService.deleteDrziPredmet(predmet.getId(), selected.getNastavnikId(), selected.getSkolskaGodinaId()).subscribe(succ -> Platform.runLater(() -> {
                drziPredmetObList.remove(selected);
                tblDrziPredmet.refresh();
            }), ErrorHandler::displayError);
        });
    }
}
