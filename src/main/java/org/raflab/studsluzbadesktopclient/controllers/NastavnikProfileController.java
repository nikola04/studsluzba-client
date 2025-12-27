package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbacommon.dto.request.*;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.services.NastavnikService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class NastavnikProfileController {

    private final NastavnikService nastavnikService;
    private NastavnikResponseDTO nastavnik;

    @FXML private TextField imeTf;
    @FXML private TextField prezimeTf;
    @FXML private TextField srednjeImeTf;
    @FXML private TextField jmbgTf;
    @FXML private DatePicker datumRodjenjaDp;
    @FXML private TextField emailTf;
    @FXML private TextField brojTelefonaTf;
    @FXML private TextField adresaTf;

    @FXML private TableView<NastavnikZvanjeResponseDTO> zvanjaTv;
    @FXML private TableColumn<NastavnikZvanjeResponseDTO, String> zvanjeCol;
    @FXML private TableColumn<NastavnikZvanjeResponseDTO, String> naucnaOblastCol;
    @FXML private TableColumn<NastavnikZvanjeResponseDTO, String> uzaNaucnaOblastCol;
    @FXML private TableColumn<NastavnikZvanjeResponseDTO, LocalDate> datumIzboraCol;
    @FXML private TableColumn<NastavnikZvanjeResponseDTO, Boolean> aktivnoCol;

    @FXML private TableView<NastavnikObrazovanjeResponseDTO> obrazovanjeTv;
    @FXML private TableColumn<NastavnikObrazovanjeResponseDTO, String> ustanovaCol;
    @FXML private TableColumn<NastavnikObrazovanjeResponseDTO, String> vrstaStudijaCol;

    public NastavnikProfileController(NastavnikService nastavnikService) {
        this.nastavnikService = nastavnikService;
    }

    @FXML
    public void initialize() {
        zvanjeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getZvanje().getZvanje()));
        naucnaOblastCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNaucnaOblast().getNaucnaOblast()));
        uzaNaucnaOblastCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUzaNaucnaOblast().getUzaNaucnaOblast()));
        datumIzboraCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getDatumIzbora()));
        aktivnoCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getAktivno()));
        ustanovaCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getVisokoskolskaUstanova().getNaziv()));
        vrstaStudijaCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getVrstaStudija().getNaziv()));
    }

    public void setNastavnik(NastavnikResponseDTO n) {
        this.nastavnik = n;

        imeTf.setText(n.getIme());
        prezimeTf.setText(n.getPrezime());
        srednjeImeTf.setText(n.getSrednjeIme());
        jmbgTf.setText(n.getJmbg());
        datumRodjenjaDp.setValue(n.getDatumRodjenja());
        emailTf.setText(n.getEmail());
        brojTelefonaTf.setText(n.getBrojTelefona());
        adresaTf.setText(n.getAdresa());

        jmbgTf.setDisable(true);

        if (n.getZvanja() != null) zvanjaTv.getItems().setAll(n.getZvanja());
        if (n.getObrazovanja() != null) obrazovanjeTv.getItems().setAll(n.getObrazovanja());
    }

    @FXML
    private void handleSave() {
        if (nastavnik == null) {
            ErrorHandler.displayError(new IllegalStateException("Nastavnik not loaded"));
            return;
        }

        NastavnikRequest request = new NastavnikRequest();

        request.setIme(imeTf.getText());
        request.setPrezime(prezimeTf.getText());
        request.setSrednjeIme(srednjeImeTf.getText());
        request.setJmbg(jmbgTf.getText());
        request.setDatumRodjenja(datumRodjenjaDp.getValue());
        request.setEmail(emailTf.getText());
        request.setBrojTelefona(brojTelefonaTf.getText());
        request.setAdresa(adresaTf.getText());
        request.setPol(nastavnik.getPol());

        request.setZvanja(zvanjaTv.getItems().stream().map(z -> {
            NastavnikZvanjeRequest r = new NastavnikZvanjeRequest();
            r.setDatumIzbora(z.getDatumIzbora());
            r.setZvanjeId(z.getZvanje().getId());
            r.setNaucnaOblastId(z.getNaucnaOblast().getId());
            r.setUzaNaucnaOblastId(z.getUzaNaucnaOblast().getId());
            r.setAktivno(z.getAktivno());
            return r;
        }).collect(Collectors.toSet()));

        request.setObrazovanje(obrazovanjeTv.getItems().stream().map(o -> {
            NastavnikObrazovanjeRequest r = new NastavnikObrazovanjeRequest();
            r.setVisokoskolskaUstanovaId(o.getVisokoskolskaUstanova().getId());
            r.setVrstaStudijaId(o.getVrstaStudija().getId());
            return r;
        }).collect(Collectors.toSet()));

        nastavnikService.updateNastavnik(nastavnik.getId(), request).subscribe(r -> Platform.runLater(() -> imeTf.getScene().getWindow().hide()), ErrorHandler::displayError);
    }

    @FXML
    private void handleClose() {
        imeTf.getScene().getWindow().hide();
    }
    @FXML
    public void handleAddZvanje(ActionEvent actionEvent) {
    }
    @FXML
    public void handleDeleteZvanje() {
        NastavnikZvanjeResponseDTO nastavnikZvanje = zvanjaTv.getSelectionModel().getSelectedItem();
        zvanjaTv.getItems().remove(nastavnikZvanje);
    }
    @FXML
    public void handleAddObrazovanje(ActionEvent actionEvent) {
    }
    @FXML
    public void handleDeleteObrazovanje() {
        NastavnikObrazovanjeResponseDTO nastavnikObrazovanje = obrazovanjeTv.getSelectionModel().getSelectedItem();
        obrazovanjeTv.getItems().remove(nastavnikObrazovanje);
    }
}
