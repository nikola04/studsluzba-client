package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbacommon.dto.request.NastavnikRequestDTO;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.services.*;
import org.springframework.stereotype.Component;

@Component
public class NastavnikTabPaneController {
    private final NastavnikService nastavnikService;
    private final CoderFactory coderFactory;
    private final ZvanjeService zvanjeService;
    private final VrstaStudijaService vrstaStudijaService;
    private final VisokoskolskaUstanovaService visokoskolskaUstanovaService;

    private final NastavnikObrazovanjeService obrazovenjeService;

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
    @FXML private ComboBox<NastavnikObrazovanjeResponseDTO> obrazovanjeCb;
    @FXML private ListView<NastavnikObrazovanjeResponseDTO> obrazovanjeLv;
    private NastavnikResponseDTO currentNastavnik;
    @FXML private ComboBox<ZvanjeResponseDTO> zvanjaCb;
    @FXML private ListView<ZvanjeResponseDTO> zvanjaLv;
    @FXML private ComboBox<VisokoskolskaUstanovaResponseDTO> visokoskolskaUstanovaCb;
    @FXML private ComboBox<VrstaStudijaResponseDTO> vrstaStudijaCb;

    public void initialize(){
        zvanjeService.fetchZvanje()
                .collectList()
                .subscribe(list ->
                        Platform.runLater(() -> zvanjaCb.getItems().setAll(list))
                );
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
    }
    public NastavnikTabPaneController(NastavnikService nastavnikService, CoderFactory coderFactory, ZvanjeService zvanjeService, VrstaStudijaService vrstaStudijaService, VisokoskolskaUstanovaService visokoskolskaUstanovaService, NastavnikObrazovanjeService obrazovenjeService) {
        this.nastavnikService = nastavnikService;
        this.coderFactory = coderFactory;
        this.zvanjeService = zvanjeService;
        this.vrstaStudijaService = vrstaStudijaService;
        this.visokoskolskaUstanovaService = visokoskolskaUstanovaService;
        this.obrazovenjeService = obrazovenjeService;
    }
    @FXML
    private void handleAddObrazovanje() {
        // privremeno – da FXML može da se učita
        System.out.println("Dodaj obrazovanje kliknuto");
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
        if (selected == null) return;

        if (!zvanjaLv.getItems().contains(selected)) {
            zvanjaLv.getItems().add(selected);
        }
    }
}
