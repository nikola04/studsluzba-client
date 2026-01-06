package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import org.raflab.studsluzbacommon.dto.response.PredmetResponse;
import org.raflab.studsluzbacommon.dto.response.StudijskiProgramResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class NewPredmetController {
    private final PredmetService predmetService;
    public TextField txtSifra;
    public TextField txtNaziv;
    public TextField txtEspb;
    public TextField txtFondPredavanja;
    public TextField txtFondVezbe;
    public CheckBox chkObavezan;
    public TextArea txtOpis;
    @Setter
    private StudijskiProgramResponseDTO studijskiProgram;
    @Setter
    private ObservableList<PredmetResponse> predmetObList;

    public NewPredmetController(PredmetService predmetService) {
        this.predmetService = predmetService;
    }


    public void handleCancel() {
        Stage stage = (Stage) txtSifra.getScene().getWindow();
        stage.close();
    }

    public void handleSave(ActionEvent actionEvent) {
        String sifra = txtSifra.getText();
        String naziv = txtNaziv.getText();
        String espbText = txtEspb.getText();
        String fondPredavanjaText = txtFondPredavanja.getText();
        String fondVezbeText = txtFondVezbe.getText();
        boolean obavezan = chkObavezan.isSelected();
        String opis = txtOpis.getText().isBlank() ? null : txtOpis.getText();

        if (sifra.isBlank() || naziv.isBlank() || espbText.isBlank() || fondPredavanjaText.isBlank() || fondVezbeText.isBlank()){
            ErrorHandler.displayError(new InvalidDataException("Please fill all fields."));
            return;
        }

        try{
            Integer espb = Integer.parseInt(espbText);
            Integer fondPredavanja = Integer.parseInt(fondPredavanjaText);
            Integer fondVezbe = Integer.parseInt(fondVezbeText);

            Button button = (Button) actionEvent.getSource();
            button.setDisable(true);

            predmetService.createPredmet(naziv, espb, obavezan, opis, sifra, studijskiProgram.getId(), fondVezbe, fondPredavanja)
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(predmetId -> {
                        PredmetResponse predmet = new PredmetResponse();
                        predmet.setId(predmetId);
                        predmet.setSifra(sifra);
                        predmet.setNaziv(naziv);
                        predmet.setEspb(espb);
                        predmet.setFondCasovaPredavanja(fondPredavanja);
                        predmet.setFondCasovaVezbe(fondVezbe);
                        predmet.setObavezan(obavezan);
                        predmet.setOpis(opis);

                        Platform.runLater(() -> predmetObList.add(predmet));
                        Platform.runLater(this::handleCancel);
                    }, ErrorHandler::displayError);
        }catch (NumberFormatException e){
            ErrorHandler.displayError(e);
        }
    }
}
