package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.raflab.studsluzbacommon.dto.response.PredmetResponse;
import org.raflab.studsluzbacommon.dto.response.StudijskiProgramResponseDTO;
import org.raflab.studsluzbacommon.dto.response.VrstaStudijaResponseDTO;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.services.StudijskiProgramService;
import org.raflab.studsluzbadesktopclient.services.VrstaStudijaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class StudijskiProgramDetailsController {
    private final VrstaStudijaService vrstaStudijaService;
    private final StudijskiProgramService studijskiProgramService;
    private final PredmetService predmetService;

    public TableView<PredmetResponse> predmetTable;
    public TextField txtOznaka;
    public TextField txtNaziv;
    public TextField txtZvanje;
    public TextField txtGodinaAkreditacije;
    public ComboBox<VrstaStudijaResponseDTO> cmbVrstaStudija;
    public TextField txtTrajanjeGodina;
    public TextField txtTrajanjeSemestara;
    public TextField txtUkupnoEspb;
    private StudijskiProgramResponseDTO studijskiProgram;

    private final ObservableList<VrstaStudijaResponseDTO> vrstaStudijaObList = FXCollections.observableArrayList();
    private final ObservableList<PredmetResponse> predmetObList = FXCollections.observableArrayList();

    public StudijskiProgramDetailsController(VrstaStudijaService vrstaStudijaService, StudijskiProgramService studijskiProgramService, PredmetService predmetService) {
        this.vrstaStudijaService = vrstaStudijaService;
        this.studijskiProgramService = studijskiProgramService;
        this.predmetService = predmetService;
    }

    public void initialize(){
        cmbVrstaStudija.setItems(vrstaStudijaObList);
        predmetTable.setItems(predmetObList);

        cmbVrstaStudija.setConverter(new StringConverter<>() {
            @Override
            public String toString(VrstaStudijaResponseDTO object) {
                return (object == null) ? "" : object.getNaziv();
            }

            @Override
            public VrstaStudijaResponseDTO fromString(String string) {
                return null;
            }
        });

        vrstaStudijaService.fetchVrstaStudija().collectList().subscribe(vrstaStudijaList -> Platform.runLater(() -> vrstaStudijaObList.setAll(vrstaStudijaList)), ErrorHandler::displayError);
    }

    public void handleCancel() {
        this.setStudijskiProgramFields();
        this.closeWindow();
    }

    public void handleSave(ActionEvent actionEvent) {
        String oznaka = txtOznaka.getText();
        String naziv = txtNaziv.getText();
        String zvanje = txtZvanje.getText();
        String godinaAkreditacijeTxt = txtGodinaAkreditacije.getText();
        VrstaStudijaResponseDTO vrstaStudija = cmbVrstaStudija.getSelectionModel().getSelectedItem();
        String trajanjeGodinaTxt = txtTrajanjeGodina.getText();
        String trajanjeSemestaraTxt = txtTrajanjeSemestara.getText();
        String ukupnoEspbTxt = txtUkupnoEspb.getText();

        if (oznaka.isBlank() || naziv.isBlank() || zvanje.isBlank() || godinaAkreditacijeTxt.isBlank() || vrstaStudija == null || trajanjeGodinaTxt.isBlank() || trajanjeSemestaraTxt.isBlank() || ukupnoEspbTxt.isBlank()) {
            ErrorHandler.displayError(new IllegalStateException("Please fill all fields."));
            return;
        }

        try {
            Integer godinaAkreditacije = Integer.parseInt(godinaAkreditacijeTxt);
            Integer trajanjeGodina = Integer.parseInt(trajanjeGodinaTxt);
            Integer trajanjeSemestara = Integer.parseInt(trajanjeSemestaraTxt);
            Integer ukupnoEspb = Integer.parseInt(ukupnoEspbTxt);

            Button button = (Button) actionEvent.getSource();
            button.setDisable(true);

            studijskiProgramService.updateStudijskiProgram(studijskiProgram.getId(), naziv, oznaka, zvanje, godinaAkreditacije, trajanjeGodina, trajanjeSemestara, ukupnoEspb, vrstaStudija.getId())
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(studijskiProgram -> Platform.runLater(() -> {
                        this.setStudijskiProgram(studijskiProgram);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Studijski program updated successfully.");
                        alert.showAndWait();
                    }), ErrorHandler::displayError);
        } catch (NumberFormatException e) {
            ErrorHandler.displayError(e);
        }
    }

    private void handleStudProgramUpdate(){
        this.setStudijskiProgramFields();
        studijskiProgramService.fetchStudijskiProgramPredmet(studijskiProgram.getId()).collectList().subscribe(predmetList -> Platform.runLater(() -> predmetObList.setAll(predmetList)), ErrorHandler::displayError);
    }

    public void handleDeletePredmet() {
        PredmetResponse selected = predmetTable.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Predmet deletion");
        confirm.setHeaderText("Please confirm deletion of predmet:");
        confirm.setContentText(selected.getNaziv());

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;

            predmetService.deletePredmet(selected.getId()).subscribe(deleted -> Platform.runLater(() -> predmetObList.remove(selected)), ErrorHandler::displayError);
        });
    }

    public void handleAddPredmet() {
    }

    public void setStudijskiProgram(StudijskiProgramResponseDTO studijskiProgram){
        this.studijskiProgram = studijskiProgram;
        this.handleStudProgramUpdate();
    }

    private void setStudijskiProgramFields(){
        txtOznaka.setText(studijskiProgram.getOznaka());
        txtNaziv.setText(studijskiProgram.getNaziv());
        txtZvanje.setText(studijskiProgram.getZvanje());
        txtGodinaAkreditacije.setText(studijskiProgram.getGodinaAkreditacije().toString());
        cmbVrstaStudija.setValue(studijskiProgram.getVrstaStudija());
        txtTrajanjeGodina.setText(studijskiProgram.getTrajanjeGodina().toString());
        txtTrajanjeSemestara.setText(studijskiProgram.getTrajanjeSemestara().toString());
        txtUkupnoEspb.setText(studijskiProgram.getUkupnoEspb().toString());
    }

    private void closeWindow() {
        Stage stage = (Stage) predmetTable.getScene().getWindow();
        stage.close();
    }
}
