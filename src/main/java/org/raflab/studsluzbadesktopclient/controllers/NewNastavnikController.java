package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbacommon.dto.request.NastavnikObrazovanjeRequest;
import org.raflab.studsluzbacommon.dto.request.NastavnikRequest;
import org.raflab.studsluzbacommon.dto.request.NastavnikZvanjeRequest;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class NewNastavnikController {
    private final NastavnikService nastavnikService;
    private final ZvanjeService zvanjeService;
    private final VrstaStudijaService vrstaStudijaService;
    private final VisokoskolskaUstanovaService visokoskolskaUstanovaService;

    private final NavigationController navigationController;
    private final NaucnaOblastService naucnaOblastService;
    private final UzaNaucnaOblastService uzaNaucnaOblastService;

    public TabPane nastavnikTabPane;
    public DatePicker zvanjaDatumIzbora;
    public ComboBox<NaucnaOblastResponseDTO> zvanjaNaucnaOblastCb;
    public ComboBox<UzaNaucnaOblastResponseDTO> zvanjaUzaNaucnaOblastCb;
    public CheckBox zvanjeAktivno;

    @FXML private TextField imeTf;
    @FXML private TextField prezimeTf;
    @FXML private TextField srednjeImeTf;
    @FXML private TextField jmbgTf;
    @FXML private DatePicker datumRodjenjaDp;

    @FXML private RadioButton muskiRb;
    @FXML private ToggleGroup polGroup;

    @FXML private TextField emailTf;
    @FXML private TextField brojTelefonaTf;
    @FXML private TextField adresaTf;
    @FXML private ListView<NastavnikObrazovanjeResponseDTO> obrazovanjeLv;
    @FXML private ComboBox<ZvanjeResponseDTO> zvanjaCb;
    @FXML private ListView<NastavnikZvanjeResponseDTO> zvanjaLv;

    @FXML private ComboBox<VisokoskolskaUstanovaResponseDTO> visokoskolskaUstaovaCb;
    @FXML private ComboBox<VrstaStudijaResponseDTO> vrstaStudijaCb;

    public void initialize(){
        this.initializeElements();

        this.fetchComboBoxValues();

        zvanjaLv.setOnMouseClicked(event -> {
            if (event.getClickCount() != 2) return;

            NastavnikZvanjeResponseDTO selected = zvanjaLv.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            zvanjaLv.getItems().remove(selected);
        });

        obrazovanjeLv.setOnMouseClicked(event -> {
            if (event.getClickCount() != 2) return;

            NastavnikObrazovanjeResponseDTO selected = obrazovanjeLv.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            obrazovanjeLv.getItems().remove(selected);
        });

        nastavnikTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> navigationController.navigateTo("newNastavnik:tab:" + newIdx));
    }

    private void fetchComboBoxValues() {
        zvanjeService.fetchZvanje()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> zvanjaCb.getItems().setAll(list)));
        visokoskolskaUstanovaService.fetchVisokoskolskaUstanove()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> visokoskolskaUstaovaCb.getItems().setAll(list)));
        vrstaStudijaService.fetchVrstaStudija()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> vrstaStudijaCb.getItems().setAll(list)));
        naucnaOblastService.fetchNaucnaOblast()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> zvanjaNaucnaOblastCb.getItems().setAll(list)));
        uzaNaucnaOblastService.fetchUzaNaucnaOblast()
                .collectList()
                .subscribe(list -> Platform.runLater(() -> zvanjaUzaNaucnaOblastCb.getItems().setAll(list)));
    }

    private void initializeElements() {
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
            protected void updateItem(NastavnikZvanjeResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getZvanje().getZvanje() + " / " + item.getNaucnaOblast().getNaucnaOblast() + " / " + item.getUzaNaucnaOblast().getUzaNaucnaOblast());
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

    public NewNastavnikController(NastavnikService nastavnikService, ZvanjeService zvanjeService, VrstaStudijaService vrstaStudijaService, VisokoskolskaUstanovaService visokoskolskaUstanovaService, NavigationController navigationController, NaucnaOblastService naucnaOblastService, UzaNaucnaOblastService uzaNaucnaOblastService) {
        this.nastavnikService = nastavnikService;
        this.zvanjeService = zvanjeService;
        this.vrstaStudijaService = vrstaStudijaService;
        this.visokoskolskaUstanovaService = visokoskolskaUstanovaService;
        this.navigationController = navigationController;
        this.naucnaOblastService = naucnaOblastService;
        this.uzaNaucnaOblastService = uzaNaucnaOblastService;
    }

    public void handleSaveNastavnik(ActionEvent actionEvent) {
        String ime = imeTf.getText();
        String prezime = prezimeTf.getText();
        String srednjeIme = srednjeImeTf.getText();
        String jmbg = jmbgTf.getText();
        LocalDate datumRodjenja = datumRodjenjaDp.getValue();
        Character pol = muskiRb.isSelected() ? 'M' : 'F';
        String email = emailTf.getText();
        String brojTelefona = brojTelefonaTf.getText();
        String adresa = adresaTf.getText();

        if (ime.isBlank() || prezime.isBlank() || srednjeIme.isBlank() || jmbg.isBlank() || datumRodjenja == null || email.isBlank() || brojTelefona.isBlank() || adresa.isBlank()) {
            ErrorHandler.displayError(new IllegalStateException("Please fill all contact fields."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        NastavnikRequest request = new NastavnikRequest();
        request.setIme(ime);
        request.setPrezime(prezime);
        request.setSrednjeIme(srednjeIme);
        request.setJmbg(jmbg);
        request.setDatumRodjenja(datumRodjenja);
        request.setPol(pol);
        request.setEmail(email);
        request.setBrojTelefona(brojTelefona);
        request.setAdresa(adresa);

        request.setObrazovanje(obrazovanjeLv.getItems().stream().map(obrazovanje -> {
            NastavnikObrazovanjeRequest obrazovanjeRequest = new NastavnikObrazovanjeRequest();
            obrazovanjeRequest.setVisokoskolskaUstanovaId(obrazovanje.getVisokoskolskaUstanova().getId());
            obrazovanjeRequest.setVrstaStudijaId(obrazovanje.getVrstaStudija().getId());
            return obrazovanjeRequest;
        }).collect(Collectors.toSet()));

        request.setZvanja(zvanjaLv.getItems().stream().map(zvanje -> {
            NastavnikZvanjeRequest zvanjeRequest = new NastavnikZvanjeRequest();
            zvanjeRequest.setZvanjeId(zvanje.getZvanje().getId());
            zvanjeRequest.setDatumIzbora(zvanje.getDatumIzbora());
            zvanjeRequest.setNaucnaOblastId(zvanje.getNaucnaOblast().getId());
            zvanjeRequest.setUzaNaucnaOblastId(zvanje.getUzaNaucnaOblast().getId());
            zvanjeRequest.setAktivno(zvanje.getAktivno());
            return zvanjeRequest;
        }).collect(Collectors.toSet()));

        nastavnikService.createNastavnik(request)
            .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
            .subscribe(id -> Platform.runLater(this::clearForm), ErrorHandler::displayError);
    }

    @FXML
    private void handleAddZvanje() {
        ZvanjeResponseDTO selectedZvanje = zvanjaCb.getValue();
        NaucnaOblastResponseDTO naucnaOblast = zvanjaNaucnaOblastCb.getValue();
        UzaNaucnaOblastResponseDTO uzaNaucnaOblast = zvanjaUzaNaucnaOblastCb.getValue();
        LocalDate datumIzbora = zvanjaDatumIzbora.getValue();
        boolean aktivno = zvanjeAktivno.isSelected();

        if (selectedZvanje == null || naucnaOblast == null || uzaNaucnaOblast == null || datumIzbora == null) {
            ErrorHandler.displayError(new IllegalStateException("Please pick all fields."));
            return;
        }

        NastavnikZvanjeResponseDTO zvanje = new NastavnikZvanjeResponseDTO();
        zvanje.setZvanje(selectedZvanje);
        zvanje.setNaucnaOblast(naucnaOblast);
        zvanje.setUzaNaucnaOblast(uzaNaucnaOblast);
        zvanje.setDatumIzbora(datumIzbora);
        zvanje.setAktivno(aktivno);

        if (zvanjaLv.getItems().contains(zvanje)) return;
        zvanjaLv.getItems().add(zvanje);
    }

    @FXML
    private void handleAddObrazovanje() {
        if(vrstaStudijaCb.getValue() == null || visokoskolskaUstaovaCb.getValue() == null){
            ErrorHandler.displayError(new IllegalStateException("Please pick all fields."));
            return;
        }

        NastavnikObrazovanjeResponseDTO obrazovanje = new NastavnikObrazovanjeResponseDTO();
        obrazovanje.setVrstaStudija(vrstaStudijaCb.getValue());
        obrazovanje.setVisokoskolskaUstanova(visokoskolskaUstaovaCb.getValue());

        if (obrazovanjeLv.getItems().contains(obrazovanje)) return;
        obrazovanjeLv.getItems().add(obrazovanje);
    }

    private void clearForm(){
        imeTf.clear();
        prezimeTf.clear();
        srednjeImeTf.clear();
        jmbgTf.clear();
        datumRodjenjaDp.setValue(null);
        polGroup.selectToggle(muskiRb);
        emailTf.clear();
        brojTelefonaTf.clear();
        adresaTf.clear();

        obrazovanjeLv.getItems().clear();
        obrazovanjeLv.refresh();
        zvanjaLv.getItems().clear();
        zvanjaLv.refresh();
    }
}
