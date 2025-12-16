package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class SifrarnikController {
    public TextField novoVrstaPredispitneTf;
    public TableView<PredispitneVrstaResponseDTO> vrstaPredispitneTable;

    public TextField novoVisokoskolskaUstanovaTf;
    public TableView<VisokoskolskaUstanovaResponseDTO> visokoskolskaUstanovaTable;

    public TextField novoVrstaStudijaOznakaTf;
    public TextField novoVrstaStudijaNazivTf;
    public TableView<VrstaStudijaResponseDTO> vrstaStudijaTable;

    public TextField novoUzaNaucnaOblastTf;
    public TableView<UzaNaucnaOblastResponseDTO> uzaNaucnaOblastTable;

    public TextField novoNaucnaOblastTf;
    public TableView<NaucnaOblastResponseDTO> naucnaOblastTable;

    public TextField novoZvanjeTf;
    public TableView<ZvanjeResponseDTO> zvanjeTable;

    public TextField novoTipSkoleTf;
    public TableView<TipSkoleResponseDTO> tipSkoleTable;

    private final ObservableList<VrstaStudijaResponseDTO> vrstaStudijaObList = FXCollections.observableArrayList();
    private final ObservableList<VisokoskolskaUstanovaResponseDTO> visokoskolskaUstanovaObList = FXCollections.observableArrayList();
    private final ObservableList<TipSkoleResponseDTO> tipSkoleObList = FXCollections.observableArrayList();
    private final ObservableList<ZvanjeResponseDTO> zvanjeObList = FXCollections.observableArrayList();
    private final ObservableList<PredispitneVrstaResponseDTO> predispitneVrstaObList = FXCollections.observableArrayList();
    private final ObservableList<NaucnaOblastResponseDTO> naucnaOblastObList = FXCollections.observableArrayList();
    private final ObservableList<UzaNaucnaOblastResponseDTO> uzaNaucnaOblastObList = FXCollections.observableArrayList();

    private final VisokoskolskaUstanovaService visokoskolskaUstanovaService;
    private final VrstaStudijaService vrstaStudijaService;
    private final TipSkoleService tipSkoleService;
    private final ZvanjeService zvanjeService;
    private final PredispitneVrstaService predispitneVrstaService;
    private final NaucnaOblastService naucnaOblastService;
    private final UzaNaucnaOblastService uzaNaucnaOblastService;

    public SifrarnikController(VrstaStudijaService vrstaStudijaService, TipSkoleService tipSkoleService, VisokoskolskaUstanovaService visokoskolskaUstanovaService, ZvanjeService zvanjeService, PredispitneVrstaService predispitneVrstaService, NaucnaOblastService naucnaOblastService, UzaNaucnaOblastService uzaNaucnaOblastService) {
        this.vrstaStudijaService = vrstaStudijaService;
        this.tipSkoleService = tipSkoleService;
        this.visokoskolskaUstanovaService = visokoskolskaUstanovaService;
        this.zvanjeService = zvanjeService;
        this.predispitneVrstaService = predispitneVrstaService;
        this.naucnaOblastService = naucnaOblastService;
        this.uzaNaucnaOblastService = uzaNaucnaOblastService;
    }

    public void initialize(){
        vrstaStudijaTable.setItems(vrstaStudijaObList);
        visokoskolskaUstanovaTable.setItems(visokoskolskaUstanovaObList);
        tipSkoleTable.setItems(tipSkoleObList);
        zvanjeTable.setItems(zvanjeObList);
        vrstaPredispitneTable.setItems(predispitneVrstaObList);
        naucnaOblastTable.setItems(naucnaOblastObList);
        uzaNaucnaOblastTable.setItems(uzaNaucnaOblastObList);

        vrstaStudijaService.fetchVrstaStudija().collectList().subscribe(list -> Platform.runLater(() -> vrstaStudijaObList.setAll(list)), ErrorHandler::displayError);

        tipSkoleService.fetchTipSkole().collectList().subscribe(list -> Platform.runLater(() -> tipSkoleObList.setAll(list)), ErrorHandler::displayError);

        visokoskolskaUstanovaService.fetchVisokoskolskaUstanove().collectList().subscribe(list -> Platform.runLater(() -> visokoskolskaUstanovaObList.setAll(list)), ErrorHandler::displayError);

        zvanjeService.fetchZvanje().collectList().subscribe(list -> Platform.runLater(() -> zvanjeObList.setAll(list)), ErrorHandler::displayError);

        predispitneVrstaService.fetchPredispitneVrsta().collectList().subscribe(list -> Platform.runLater(() -> predispitneVrstaObList.setAll(list)),ErrorHandler::displayError);

        naucnaOblastService.fetchNaucnaOblast().collectList().subscribe(list -> Platform.runLater(() -> naucnaOblastObList.setAll(list)), ErrorHandler::displayError);

        uzaNaucnaOblastService.fetchUzaNaucnaOblast().collectList().subscribe(list -> Platform.runLater(() -> uzaNaucnaOblastObList.setAll(list)), ErrorHandler::displayError);
    }

    public void handleCreateZvanje(ActionEvent actionEvent) {
        String zvanje = novoZvanjeTf.getText();
        if (zvanje == null || zvanje.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'zvanje' cannot be empty"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        zvanjeService.createZvanje(zvanje).subscribe(zvanjeResponseDTO -> {
            novoZvanjeTf.clear();
            zvanjeObList.add(zvanjeResponseDTO);
        }, ErrorHandler::displayError, () -> button.setDisable(false));
    }

    public void handleDeleteZvanje() {
        ZvanjeResponseDTO selected = zvanjeTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        zvanjeService.deleteZvanje(selected).subscribe(success -> {
            if (success){
                Platform.runLater(() -> zvanjeObList.remove(selected));
                return;
            }
            ErrorHandler.displayError(new RuntimeException("Failed to delete zvanje"));
        }, ErrorHandler::displayError);
    }

    public void handleCreateVrstaPredispitne(ActionEvent actionEvent) {
        String vrsta = novoVrstaPredispitneTf.getText();
        if (vrsta == null || vrsta.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'vrsta' cannot be empty"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        predispitneVrstaService.createPredispitneVrsta(vrsta).subscribe(vrstaResponseDTO -> {
            novoVrstaPredispitneTf.clear();
            predispitneVrstaObList.add(vrstaResponseDTO);
        }, ErrorHandler::displayError, () -> button.setDisable(false));
    }

    public void handleDeleteVrstaPredispitne() {
        PredispitneVrstaResponseDTO selected = vrstaPredispitneTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        predispitneVrstaService.deletePredispitneVrsta(selected).subscribe(success -> {
            if (success){
                Platform.runLater(() -> predispitneVrstaObList.remove(selected));
                return;
            }
            ErrorHandler.displayError(new RuntimeException("Failed to delete Predispitne Vrsta"));
        }, ErrorHandler::displayError);
    }

    public void handleCreateVisokoskolskaUstanova(ActionEvent actionEvent) {
        String naziv = novoVisokoskolskaUstanovaTf.getText();
        if (naziv == null || naziv.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'naziv' cannot be empty"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        visokoskolskaUstanovaService.createVisokoskolskaUstanova(naziv).subscribe(id -> {
            VisokoskolskaUstanovaResponseDTO vrsta = new VisokoskolskaUstanovaResponseDTO();
            vrsta.setId(id);
            vrsta.setNaziv(naziv);

            novoVisokoskolskaUstanovaTf.clear();
            visokoskolskaUstanovaObList.add(vrsta);
        }, ErrorHandler::displayError, () -> button.setDisable(false));
    }

    public void handleDeleteVisokoskolskaUstanova() {
        VisokoskolskaUstanovaResponseDTO selected = visokoskolskaUstanovaTable.getSelectionModel().getSelectedItem();
        visokoskolskaUstanovaService.deleteVisokoskolskaUstanova(selected).subscribe(success -> {
            if (success){
                Platform.runLater(() -> visokoskolskaUstanovaObList.remove(selected));
                return;
            }
            ErrorHandler.displayError(new RuntimeException("Failed to delete visokoskolska ustanova"));
        }, ErrorHandler::displayError);
    }

    public void handleCreateVrstaStudija(ActionEvent actionEvent) {
        String naziv = novoVrstaStudijaNazivTf.getText();
        String oznaka = novoVrstaStudijaOznakaTf.getText();
        if (naziv == null || naziv.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'naziv' cannot be empty"));
            return;
        }
        if (oznaka == null || oznaka.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'oznaka' cannot be empty"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        vrstaStudijaService.createVrstaStudija(naziv, oznaka).subscribe(id -> {
            VrstaStudijaResponseDTO vrsta = new VrstaStudijaResponseDTO();
            vrsta.setId(id);
            vrsta.setNaziv(naziv);
            vrsta.setOznaka(oznaka);

            novoVrstaStudijaNazivTf.clear();
            novoVrstaStudijaOznakaTf.clear();

            vrstaStudijaObList.add(vrsta);
        }, ErrorHandler::displayError, () -> button.setDisable(false));
    }

    public void handleDeleteVrstaStudija() {
        VrstaStudijaResponseDTO selected = vrstaStudijaTable.getSelectionModel().getSelectedItem();
        vrstaStudijaService.deleteVrstaStudija(selected).subscribe(success -> {
            if (success){
                Platform.runLater(() -> vrstaStudijaObList.remove(selected));
                return;
            }
            ErrorHandler.displayError(new RuntimeException("Failed to delete vrsta studija"));
        }, ErrorHandler::displayError);
    }

    public void handleCreateUzaNaucnaOblast(ActionEvent actionEvent) {
        String oblast = novoUzaNaucnaOblastTf.getText();
        if (oblast == null || oblast.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'oblast' cannot be empty"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        uzaNaucnaOblastService.createUzaNaucnaOblast(oblast).subscribe(uzaNaucnaOblastResponseDTO -> {
            novoUzaNaucnaOblastTf.clear();
            uzaNaucnaOblastObList.add(uzaNaucnaOblastResponseDTO);
        }, ErrorHandler::displayError, () -> button.setDisable(false));
    }

    public void handleDeleteUzaNaucnaOblast() {
        UzaNaucnaOblastResponseDTO selected = uzaNaucnaOblastTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        uzaNaucnaOblastService.deleteUzaNaucnaOblast(selected).subscribe(success -> {
            if (success){
                Platform.runLater(() -> uzaNaucnaOblastObList.remove(selected));
                return;
            }
            ErrorHandler.displayError(new RuntimeException("Failed to delete Uza Naucna Oblast"));
        }, ErrorHandler::displayError);
    }

    public void handleCreateNaucnaOblast(ActionEvent actionEvent) {
        String oblast = novoNaucnaOblastTf.getText();
        if (oblast == null || oblast.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'oblast' cannot be empty"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        naucnaOblastService.createNaucnaOblast(oblast).subscribe(naucnaOblastResponseDTO -> {
            novoNaucnaOblastTf.clear();
            naucnaOblastObList.add(naucnaOblastResponseDTO);
        }, ErrorHandler::displayError, () -> button.setDisable(false));
    }

    public void handleDeleteNaucnaOblast() {
        NaucnaOblastResponseDTO selected = naucnaOblastTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        naucnaOblastService.deleteNaucnaOblast(selected).subscribe(success -> {
            if (success){
                Platform.runLater(() -> naucnaOblastObList.remove(selected));
                return;
            }
            ErrorHandler.displayError(new RuntimeException("Failed to delete Naucna Oblast"));
        }, ErrorHandler::displayError);
    }

    public void handleCreateTipSkole(ActionEvent actionEvent) {
        String tip = novoTipSkoleTf.getText();
        if (tip == null || tip.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("Field 'tipSkole' cannot be empty"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        tipSkoleService.createTipSkole(tip).subscribe(tipSkoleResponseDTO -> {
            novoTipSkoleTf.clear();
            tipSkoleObList.add(tipSkoleResponseDTO);
        }, ErrorHandler::displayError, () -> button.setDisable(false));
    }

    public void handleDeleteTipSkole() {
        TipSkoleResponseDTO selected = tipSkoleTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        tipSkoleService.deleteTipSkole(selected).subscribe(success -> {
            if (success){
                Platform.runLater(() -> tipSkoleObList.remove(selected));
                return;
            }
            ErrorHandler.displayError(new RuntimeException("Failed to delete tip skole"));
        }, ErrorHandler::displayError);
    }
}

