package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.raflab.studsluzbacommon.dto.response.SkolskaGodinaResponseDTO;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class SkolskaGodinaController {
    private final SkolskaGodinaService skolskaGodinaService;
    private final ObservableList<SkolskaGodinaResponseDTO> skolskaGodinaObList ;
    @FXML
    private TableView<SkolskaGodinaResponseDTO> skolskaGodinaTable;
    public void initialize(){
        skolskaGodinaService.fetchSkolskeGodine().collectList().subscribe(skolskaGodinaObList::setAll, ErrorHandler::displayError);
        skolskaGodinaTable.setItems(skolskaGodinaObList);
    }

    @FXML
    public void handleAdd() {
    }

    @FXML
    public void handleSet() {
    }

    @FXML
    public void handleDelete() {
    }

    public SkolskaGodinaController(SkolskaGodinaService skolskaGodinaService) {
        this.skolskaGodinaService = skolskaGodinaService;
        this.skolskaGodinaObList = FXCollections.observableArrayList();
    }
}
