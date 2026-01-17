package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.raflab.studsluzbacommon.dto.request.IspitIzlazakRequest;
import org.raflab.studsluzbacommon.dto.response.IspitIzlazakResponse;
import org.raflab.studsluzbacommon.dto.response.IspitPrijavaResponse;
import org.raflab.studsluzbacommon.dto.response.IspitResponse;
import org.raflab.studsluzbacommon.dto.response.IspitRezultatResponse;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.StudentIndexService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.raflab.studsluzbadesktopclient.utils.JasperReportUtils;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IspitDetailsController {

    private final IspitService ispitService;
    private final StudentIndexService studentIndexService;
    @FXML private Label lblPredmet;
    @FXML private Label lblNastavnik;
    @FXML private Label lblIspitniRok;
    @FXML private Label lblDatum;
    @FXML private Label lblVreme;
    @FXML private Label lblStatus;

    // Prijava Tab
    @FXML private TextField txtIndeksPrijava;
    @FXML private TableView<IspitPrijavaResponse> tblPrijave;
    @FXML private TableColumn<IspitPrijavaResponse, String> colPrijavaIndeks, colPrijavaStudent, colPrijavaDatum;

    // Izlazak Tab
    @FXML private TextField txtIndeksIzlazak, txtPoeni, txtNapomena;
    @FXML private TableView<IspitIzlazakResponse> tblIzlasci;
    @FXML private TableColumn<IspitIzlazakResponse, String> colIzlazakIndeks, colIzlazakStudent, colIzlazakPoeni, colIzlazakNapomena, colIzlazakPonisten;

    // Rezultati
    @FXML private TableView<IspitRezultatResponse> tblRezultati;
    @FXML private TableColumn<IspitRezultatResponse, String> colRezultatIndeks, colRezultatStudent, colRezultatPredispitne, colRezultatIspit, colRezultatUkupno;

    private IspitResponse trenutniIspit;

    public IspitDetailsController(IspitService ispitService, StudentIndexService studentIndexService) {
        this.ispitService = ispitService;
        this.studentIndexService = studentIndexService;
    }

    public void initialize() {
        this.setupTables();
    }

    private void setupTables() {
        colRezultatIndeks.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStudentIndeks().getStudijskiProgram().getOznaka() + " " +
                        d.getValue().getStudentIndeks().getBroj() + "/" + d.getValue().getStudentIndeks().getGodina()));

        colRezultatStudent.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStudentIndeks().getStudent().getIme() + " " +
                        d.getValue().getStudentIndeks().getStudent().getPrezime()));

        colRezultatPredispitne.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBrojPoenaPredispitne().toString()));
        colRezultatIspit.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBrojPoenaIspit().toString()));
        colRezultatUkupno.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBrojPoenaUkupno().toString()));

        // Kolone za Prijave
        colPrijavaIndeks.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentIndeks().getStudijskiProgram().getOznaka() + " " + d.getValue().getStudentIndeks().getBroj() + "/" + d.getValue().getStudentIndeks().getGodina()));
        colPrijavaStudent.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentIndeks().getStudent().getIme() + " " + d.getValue().getStudentIndeks().getStudent().getPrezime()));
        colPrijavaDatum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDatumPrijave().toString()));

        // Kolone za Izlaske
        colIzlazakIndeks.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIspitPrijava().getStudentIndeks().getStudijskiProgram().getOznaka() + " " + d.getValue().getIspitPrijava().getStudentIndeks().getBroj() + "/" + d.getValue().getIspitPrijava().getStudentIndeks().getGodina()));
        colIzlazakStudent.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIspitPrijava().getStudentIndeks().getStudent().getIme() + " " + d.getValue().getIspitPrijava().getStudentIndeks().getStudent().getPrezime()));
        colIzlazakPoeni.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBrojPoena().toString()));
        colIzlazakNapomena.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNapomena()));
        colIzlazakPonisten.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPonisten() ? "Da" : "Ne"));
    }

    private void updateIspitData() {
        if (trenutniIspit == null) return;

        lblPredmet.setText(trenutniIspit.getPredmet().getNaziv());

        lblNastavnik.setText(trenutniIspit.getNastavnik().getIme() + " " +
                trenutniIspit.getNastavnik().getPrezime());

        lblIspitniRok.setText(trenutniIspit.getIspitniRok().getPocetak().getMonth().toString() + ", " + trenutniIspit.getIspitniRok().getSkolskaGodina().getGodina());

        if (trenutniIspit.getDatumOdrzavanja() != null) {
            lblDatum.setText(trenutniIspit.getDatumOdrzavanja().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        }

        if (trenutniIspit.getVremePocetka() != null) {
            lblVreme.setText(trenutniIspit.getVremePocetka().toString());
        }

        lblStatus.setText(trenutniIspit.getZakljucen() ? "ZAKLJUČEN" : "OTVOREN");
    }

    private void updateIspitTables() {
        tblPrijave.getItems().clear();
        ispitService.fetchIspitPrijava(trenutniIspit.getId()).subscribe(item -> Platform.runLater(() -> tblPrijave.getItems().add(item)), ErrorHandler::displayError);
        tblIzlasci.getItems().clear();
        ispitService.fetchIspitIzlazak(trenutniIspit.getId()).subscribe(item -> Platform.runLater(() -> tblIzlasci.getItems().add(item)), ErrorHandler::displayError);
    }

    private void updateIspitRezultatiTable(){
        tblRezultati.getItems().clear();
        ispitService.fetchIspitRezultat(trenutniIspit.getId()).subscribe(item -> Platform.runLater(() -> tblRezultati.getItems().add(item)), ErrorHandler::displayError);
    }

    public void setIspit(IspitResponse ispit) {
        this.trenutniIspit = ispit;
        this.updateIspitData();
        this.updateIspitTables();
        this.updateIspitRezultatiTable();
    }

    public void handleGenerateIzvestaj(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        Map<String, Object> params = new HashMap<>();
        params.put("nazivPredmeta", trenutniIspit.getPredmet().getNaziv());
        params.put("nastavnikImePrezime", trenutniIspit.getNastavnik().getIme() + " " + trenutniIspit.getNastavnik().getPrezime());
        params.put("ispitniRok", trenutniIspit.getIspitniRok().getPocetak() + "  -  " + trenutniIspit.getIspitniRok().getKraj());
        params.put("datumIspita", trenutniIspit.getDatumOdrzavanja() != null ?
                trenutniIspit.getDatumOdrzavanja().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")) : "-");

        JasperReportUtils.runTask(() -> {
            List<IspitRezultatResponse> rezultati = ispitService.fetchIspitRezultatSync(trenutniIspit.getId());

            List<Map<String, ?>> flatData = rezultati.stream().map(r -> {
                Map<String, Object> item = new HashMap<>();
                item.put("studentIndeks", r.getStudentIndeks().getStudijskiProgram().getOznaka() + " " + r.getStudentIndeks().getBroj() + "/" + r.getStudentIndeks().getGodina());
                item.put("studentImePrezime", r.getStudentIndeks().getStudent().getIme() + " " + r.getStudentIndeks().getStudent().getPrezime());
                item.put("brojPoenaPredispitne", r.getBrojPoenaPredispitne());
                item.put("brojPoenaIspit", r.getBrojPoenaIspit());
                item.put("brojPoenaUkupno", r.getBrojPoenaUkupno());

                return item;
            }).collect(Collectors.toList());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(flatData);

            JasperReport jasperReport = JasperReportUtils.getReport("ispit_zapisnik");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            JasperReportUtils.exportPdf(button.getScene().getWindow(), jasperPrint, "ispit_zapisnik");
        }, (v) -> System.out.println("Exported!"), () -> button.setDisable(false));
    }

    @FXML
    public void handleSavePrijava() {
        String studentIndeks = txtIndeksPrijava.getText().trim();

        if (studentIndeks.isEmpty()) {
            ErrorHandler.displayError(new Exception("Morate uneti indeks studenta."));
            return;
        }

        if (trenutniIspit == null) {
            ErrorHandler.displayError(new Exception("Ispit nije učitan."));
            return;
        }

        studentIndexService.saveIspitPrijava(studentIndeks, trenutniIspit.getId())
                .subscribe(novaPrijava -> Platform.runLater(() -> {
                    tblPrijave.getItems().add(novaPrijava);
                    txtIndeksPrijava.clear();

                    tblPrijave.scrollTo(novaPrijava);
                }), ErrorHandler::displayError);
    }

    @FXML
    public void handleSaveIzlazak() {
        String studentIndeks = txtIndeksIzlazak.getText().trim();
        String poeniRaw = txtPoeni.getText().trim();
        String napomena = txtNapomena.getText().trim();

        if (studentIndeks.isEmpty() || poeniRaw.isEmpty()) {
            ErrorHandler.displayError(new InvalidDataException("All fields are required."));
            return;
        }

        try {
            Double poeni = Double.parseDouble(poeniRaw);

            IspitIzlazakRequest request = new IspitIzlazakRequest();
            request.setBrojPoena(poeni);
            request.setNapomena(napomena.isEmpty() ? null : napomena);
            request.setPonisten(false);

            studentIndexService.saveIspitIzlazak(studentIndeks, trenutniIspit.getId(), request)
                    .subscribe(noviIzlazak -> Platform.runLater(() -> {
                        tblIzlasci.getItems().add(noviIzlazak);
                        txtIndeksIzlazak.clear();
                        txtPoeni.clear();
                        txtNapomena.clear();
                    }), ErrorHandler::displayError);
        } catch (NumberFormatException e) {
            ErrorHandler.displayError(new InvalidDataException("Poeni moraju biti broj (npr. 85.5)."));
        }
    }

    public void handleDeleteIzlazak(ActionEvent actionEvent) {
        IspitIzlazakResponse selected = tblIzlasci.getSelectionModel().getSelectedItem();

        if (selected == null) {
            ErrorHandler.displayError(new InvalidDataException("Please select izlazak for deletion."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ispit Izlazak deletion");
        confirm.setHeaderText("Please confirm deletion of izlazak:");
        confirm.setContentText(selected.getIspitPrijava().getStudentIndeks().getStudent().getIme() + " " + selected.getIspitPrijava().getStudentIndeks().getStudent().getPrezime());

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                button.setDisable(false);
                return;
            }
            studentIndexService.deleteIspitIzlazak(selected.getIspitPrijava().getStudentIndeks().getId(), selected.getIspitPrijava().getIspit().getId())
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(r -> Platform.runLater(() -> tblIzlasci.getItems().remove(selected)), ErrorHandler::displayError);
        });
    }

    public void handleDeletePrijava(ActionEvent actionEvent) {
        IspitPrijavaResponse selected = tblPrijave.getSelectionModel().getSelectedItem();

        if (selected == null) {
            ErrorHandler.displayError(new InvalidDataException("Please select izlazak for deletion."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ispit Prijava deletion");
        confirm.setHeaderText("Please confirm deletion of prijava:");
        confirm.setContentText(selected.getStudentIndeks().getStudent().getIme() + " " + selected.getStudentIndeks().getStudent().getPrezime());

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                button.setDisable(false);
                return;
            }
            studentIndexService.deleteIspitPrijava(selected.getStudentIndeks().getId(), selected.getIspit().getId())
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(r -> Platform.runLater(() -> tblPrijave.getItems().remove(selected)), ErrorHandler::displayError);
        });
    }
}