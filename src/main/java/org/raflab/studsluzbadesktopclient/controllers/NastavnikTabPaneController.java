package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class NastavnikTabPaneController {
    private final NastavnikService nastavnikService;
    private final CoderFactory coderFactory;
    private final ZvanjeService zvanjeService;
    private final VrstaStudijaService vrstaStudijaService;
    private final VisokoskolskaUstanovaService visokoskolskaUstanovaService;

    private final NastavnikObrazovanjeService obrazovenjeService;
    private final SrednjaSkolaService srednjaSkolaService;

    @FXML private TextField imeTf;
    @FXML private TextField prezimeTf;
    @FXML private TextField srednjeImeTf;
    @FXML private TextField jmbgTf;
    @FXML private DatePicker datumRodjenjaDp;

    @FXML private RadioButton muskiRb;
    @FXML private RadioButton zenskiRb;
    @FXML private ToggleGroup polGroup;

    @FXML private TextField emailTf;
    @FXML private TextField brojTelefonaTf;
    @FXML private TextField adresaTf;
    @FXML private ListView<NastavnikObrazovanjeResponseDTO> obrazovanjeLv;
    private NastavnikResponseDTO currentNastavnik;
    @FXML private ComboBox<ZvanjeResponseDTO> zvanjaCb;
    @FXML private ListView<ZvanjeResponseDTO> zvanjaLv;

    @FXML private ComboBox<VisokoskolskaUstanovaResponseDTO> visokoskolskaUstaovaCb;
    @FXML private ComboBox<VrstaStudijaResponseDTO> vrstaStudijaCb;

    public void initialize(){
        zvanjeService.fetchZvanje()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> zvanjaCb.getItems().setAll(list)));
        visokoskolskaUstanovaService.fetchVisokoskolskaUstanove()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> visokoskolskaUstaovaCb.getItems().setAll(list)));
        vrstaStudijaService.fetchVrstaStudija()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> vrstaStudijaCb.getItems().setAll(list)));


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
        zvanjaLv.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ZvanjeResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getZvanje());
            }
        });
        visokoskolskaUstaovaCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(VisokoskolskaUstanovaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        visokoskolskaUstaovaCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(VisokoskolskaUstanovaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        obrazovanjeLv.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NastavnikObrazovanjeResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getVrstaStudija().getNaziv() + " / " + item.getVisokoskolskaUstanova().getNaziv());
            }
        });
    }
    public NastavnikTabPaneController(NastavnikService nastavnikService, CoderFactory coderFactory, ZvanjeService zvanjeService, VrstaStudijaService vrstaStudijaService, VisokoskolskaUstanovaService visokoskolskaUstanovaService, NastavnikObrazovanjeService obrazovenjeService, SrednjaSkolaService srednjaSkolaService) {
        this.nastavnikService = nastavnikService;
        this.coderFactory = coderFactory;
        this.zvanjeService = zvanjeService;
        this.vrstaStudijaService = vrstaStudijaService;
        this.visokoskolskaUstanovaService = visokoskolskaUstanovaService;
        this.obrazovenjeService = obrazovenjeService;
        this.srednjaSkolaService = srednjaSkolaService;
    }
    @FXML
    private void handleAddObrazovanje() {
        if(vrstaStudijaCb.getValue() == null || visokoskolskaUstaovaCb.getValue() == null){
            ErrorHandler.displayError(new IllegalStateException("Morate izabrati vrstu studija i visokoskolsku ustanovu."));
            return;
        }

        NastavnikObrazovanjeResponseDTO obrazovanje = new NastavnikObrazovanjeResponseDTO();
        obrazovanje.setVrstaStudija(vrstaStudijaCb.getValue());
        obrazovanje.setVisokoskolskaUstanova(visokoskolskaUstaovaCb.getValue());

        if (obrazovanjeLv.getItems().contains(obrazovanje)) return;
        obrazovanjeLv.getItems().add(obrazovanje);
    }

    public void handleSaveNastavnik(ActionEvent actionEvent) {
//        if (!validateForm()) return;
//
//        NastavnikRequestDTO request = buildRequest();
//        nastavnikService.createNastavnik(request)
//                .subscribe(
//                        r -> showInfo("Nastavnik uspešno sačuvan."),
//                        e -> showError(e.getMessage())
//                );
    }

    public void handleIzvestaj(ActionEvent actionEvent) {
    }

    public void handleAddZvanje(ActionEvent actionEvent) {
        ZvanjeResponseDTO selected = zvanjaCb.getValue();
        if (selected == null) {
            ErrorHandler.displayError(new IllegalStateException("Morate izabrati zvanje."));
            return;
        }

        if (zvanjaLv.getItems().contains(selected)) return;
        zvanjaLv.getItems().add(selected);
    }
}
