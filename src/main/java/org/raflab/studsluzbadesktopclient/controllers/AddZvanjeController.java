package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import org.raflab.studsluzbacommon.dto.response.NastavnikZvanjeResponseDTO;
import org.raflab.studsluzbacommon.dto.response.NaucnaOblastResponseDTO;
import org.raflab.studsluzbacommon.dto.response.UzaNaucnaOblastResponseDTO;
import org.raflab.studsluzbacommon.dto.response.ZvanjeResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class AddZvanjeController {
    private final NaucnaOblastService naucnaOblastService;
    private final UzaNaucnaOblastService uzaNaucnaOblastService;
    private final ZvanjeService zvanjeService;
    @FXML private ComboBox<ZvanjeResponseDTO> zvanjaCb;
    @FXML private DatePicker zvanjaDatumIzbora;
    @FXML private CheckBox zvanjaAktivno;
    @FXML public ComboBox<NaucnaOblastResponseDTO> zvanjaNaucnaOblastCb;
    @FXML public ComboBox<UzaNaucnaOblastResponseDTO> zvanjaUzaNaucnaOblastCb;
    @Setter
    private ObservableList<NastavnikZvanjeResponseDTO> nastavnikZvanjeObList;



    public AddZvanjeController(NaucnaOblastService naucnaOblastService, UzaNaucnaOblastService uzaNaucnaOblastService, ZvanjeService zvanjeService) {
        this.naucnaOblastService = naucnaOblastService;
        this.uzaNaucnaOblastService = uzaNaucnaOblastService;
        this.zvanjeService = zvanjeService;
    }


    public void initialize(){
        this.initializeElements();

        this.fetchComboBoxValues();
    }
    private void fetchComboBoxValues() {
        zvanjeService.fetchZvanje()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> zvanjaCb.getItems().setAll(list)));

        naucnaOblastService.fetchNaucnaOblast()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> zvanjaNaucnaOblastCb.getItems().setAll(list)));
        uzaNaucnaOblastService.fetchUzaNaucnaOblast()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> zvanjaUzaNaucnaOblastCb.getItems().setAll(list)));
    }


    private void initializeElements() {
        zvanjaAktivno = new CheckBox();
        zvanjaCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(ZvanjeResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getZvanje());
            }
        });
        zvanjaCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ZvanjeResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getZvanje());
            }
        });
        zvanjaNaucnaOblastCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(NaucnaOblastResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaucnaOblast());
            }
        });
        zvanjaNaucnaOblastCb.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NaucnaOblastResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaucnaOblast());
            }
        });
        zvanjaUzaNaucnaOblastCb.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UzaNaucnaOblastResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUzaNaucnaOblast());
            }
        });
        zvanjaUzaNaucnaOblastCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UzaNaucnaOblastResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUzaNaucnaOblast());
            }
        });
    }

    public void handleAddZvanje(ActionEvent actionEvent) {
        NastavnikZvanjeResponseDTO zvanje = new NastavnikZvanjeResponseDTO();
        zvanje.setZvanje(zvanjaCb.getSelectionModel().getSelectedItem());

        zvanje.setNaucnaOblast(zvanjaNaucnaOblastCb.getSelectionModel().getSelectedItem());
        zvanje.setUzaNaucnaOblast(zvanjaUzaNaucnaOblastCb.getSelectionModel().getSelectedItem());
        zvanje.setDatumIzbora(zvanjaDatumIzbora.getValue());
        zvanje.setAktivno(zvanjaAktivno.isSelected());
        if(zvanje.getZvanje() == null || zvanje.getNaucnaOblast() == null || zvanje.getUzaNaucnaOblast() == null )
        {
            ErrorHandler.displayError(new InvalidDataException("Please fill all fields"));
            return;
        }
        nastavnikZvanjeObList.add(zvanje);

    }

    public void handleCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) zvanjaCb.getScene().getWindow();
        stage.close();
    }
}
