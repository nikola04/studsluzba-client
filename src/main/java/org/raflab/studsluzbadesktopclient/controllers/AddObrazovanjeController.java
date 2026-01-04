package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.Setter;
import org.raflab.studsluzbacommon.dto.response.NastavnikObrazovanjeResponseDTO;
import org.raflab.studsluzbacommon.dto.response.NastavnikResponseDTO;
import org.raflab.studsluzbacommon.dto.response.VisokoskolskaUstanovaResponseDTO;
import org.raflab.studsluzbacommon.dto.response.VrstaStudijaResponseDTO;
import org.raflab.studsluzbadesktopclient.services.VisokoskolskaUstanovaService;
import org.raflab.studsluzbadesktopclient.services.VrstaStudijaService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AddObrazovanjeController {
    private final VisokoskolskaUstanovaService visokoskolskaUstanovaService;
    private final NavigationController navigationController;
    private final VrstaStudijaService vrstaStudijaService;
    @FXML
    private ComboBox<VisokoskolskaUstanovaResponseDTO> visokoskolskaUstanovaCb;
    @FXML
    private ComboBox<VrstaStudijaResponseDTO> vrstaStudijaCb;
    @Setter
    private ObservableList<NastavnikObrazovanjeResponseDTO> nastavnikObrazovanjeObList;
    @Setter
    private NastavnikResponseDTO nastavnik;

    public AddObrazovanjeController(VisokoskolskaUstanovaService visokoskolskaUstanovaService, NavigationController navigationController, VrstaStudijaService vrstaStudijaService) {
        this.visokoskolskaUstanovaService = visokoskolskaUstanovaService;
        this.navigationController = navigationController;
        this.vrstaStudijaService = vrstaStudijaService;
    }
    public void initialize(){
        this.initializeElements();
        this.fetchComboBoxValues();
    }

    private void fetchComboBoxValues() {
        visokoskolskaUstanovaService.fetchVisokoskolskaUstanove()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> visokoskolskaUstanovaCb.getItems().setAll(list)));
        vrstaStudijaService.fetchVrstaStudija()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> vrstaStudijaCb.getItems().setAll(list)));
    }

    private void initializeElements() {
        visokoskolskaUstanovaCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(VisokoskolskaUstanovaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        visokoskolskaUstanovaCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(VisokoskolskaUstanovaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        vrstaStudijaCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(VrstaStudijaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        vrstaStudijaCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(VrstaStudijaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
    }

    public void handleAddObrazovanja(ActionEvent actionEvent) {
    NastavnikObrazovanjeResponseDTO obrazovanje = new NastavnikObrazovanjeResponseDTO();
    obrazovanje.setVisokoskolskaUstanova(visokoskolskaUstanovaCb.getSelectionModel().getSelectedItem());
    obrazovanje.setVrstaStudija(vrstaStudijaCb.getSelectionModel().getSelectedItem());
    nastavnikObrazovanjeObList.add(obrazovanje);
    obrazovanje.setNastavnik(nastavnik);

    }

    public void handleCancel(ActionEvent actionEvent) {
    Stage stage = (Stage) vrstaStudijaCb.getScene().getWindow();
    stage.close();
    }
}
