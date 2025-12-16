package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.raflab.studsluzbacommon.dto.response.SkolskaGodinaResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class SkolskaGodinaController {
    private final SkolskaGodinaService skolskaGodinaService;
    private final ObservableList<SkolskaGodinaResponseDTO> skolskaGodinaObList ;
    public TextField novaGodinaTf;

    @FXML
    private TableView<SkolskaGodinaResponseDTO> skolskaGodinaTable;
    public void initialize(){
        skolskaGodinaService.fetchSkolskeGodine().collectList().subscribe(skolskaGodinaObList::setAll, ErrorHandler::displayError);
        skolskaGodinaTable.setItems(skolskaGodinaObList);
    }

    public SkolskaGodinaController(SkolskaGodinaService skolskaGodinaService) {
        this.skolskaGodinaService = skolskaGodinaService;
        this.skolskaGodinaObList = FXCollections.observableArrayList();
    }

    @FXML
    public void handleCreate(ActionEvent actionEvent) {
        String tfValue = novaGodinaTf.getText();
        try{
            Integer year = Integer.parseInt(tfValue);
            boolean aktivan = false;
            Button button = (Button) actionEvent.getSource();
            button.setDisable(true);

            skolskaGodinaService.createSkolskaGodina(year, aktivan).subscribe(skolskaGodinaId -> {
               SkolskaGodinaResponseDTO skolskaGodina = new SkolskaGodinaResponseDTO();
               skolskaGodina.setId(skolskaGodinaId);
               skolskaGodina.setGodina(year);
               skolskaGodina.setAktivan(aktivan);

               skolskaGodinaObList.add(skolskaGodina);
               novaGodinaTf.clear();

            }, ErrorHandler::displayError);
            button.setDisable(false);
        }catch (NumberFormatException e){
            ErrorHandler.displayError(new InvalidDataException("Field 'godina' must be a valid Integer"));
        }
    }

    @FXML
    public void handleSet() {
        SkolskaGodinaResponseDTO selected = skolskaGodinaTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        skolskaGodinaService.updateSkolskaGodina(selected.getId(), selected.getGodina(), true).subscribe(skolskaGodina -> Platform.runLater(() -> {
            skolskaGodinaObList.forEach(sGod -> sGod.setAktivan(false));

            selected.setGodina(skolskaGodina.getGodina());
            selected.setAktivan(skolskaGodina.getAktivan());

            skolskaGodinaTable.refresh();
        }), ErrorHandler::displayError);
    }

    @FXML
    public void handleDelete() {
        SkolskaGodinaResponseDTO selected = skolskaGodinaTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        skolskaGodinaService.deleteSkolskaGodina(selected.getId()).subscribe(success -> skolskaGodinaObList.remove(selected), ErrorHandler::displayError);
    }
}
