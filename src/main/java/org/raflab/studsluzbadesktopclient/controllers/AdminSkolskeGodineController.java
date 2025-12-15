package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.raflab.studsluzbacommon.dto.response.SkolskaGodinaResponseDTO;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component

public class AdminSkolskeGodineController {
    private SkolskaGodinaService skolskaGodinaService;
    private ObservableList<SkolskaGodinaResponseDTO> skolskeGodine ;
    @FXML
    private TableView<SkolskaGodinaResponseDTO> skolskeGodineTable;
    public void initialize(){
        skolskaGodinaService.getAllSkolskeGodine().thenAccept(skolskaGodinaList->{
            skolskeGodine.setAll(skolskaGodinaList);
            System.out.println(skolskaGodinaList);
        }).exceptionally(ex -> {
            System.out.println(ex);
            ErrorHandler.displayError(ex);

            return null;
        });
        skolskeGodineTable.setItems(skolskeGodine);

    }
    @FXML
    public void handleAdd(ActionEvent actionEvent) {

    }
    @FXML
    public void handleEdit(ActionEvent actionEvent) {
    }
    @FXML
    public void handleDelete(ActionEvent actionEvent) {
    }

    public AdminSkolskeGodineController(SkolskaGodinaService skolskaGodinaService) {
        this.skolskaGodinaService = skolskaGodinaService;
        this.skolskeGodine = FXCollections.observableArrayList();
    }
}
