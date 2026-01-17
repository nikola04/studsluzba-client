package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.raflab.studsluzbacommon.dto.request.StudentRequest;
import org.raflab.studsluzbacommon.dto.response.SrednjaSkolaResponseDTO;
import org.raflab.studsluzbacommon.dto.response.VisokoskolskaUstanovaResponseDTO;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.coder.CoderType;
import org.raflab.studsluzbadesktopclient.coder.SimpleCode;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.SrednjaSkolaService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.services.VisokoskolskaUstanovaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewStudentController {
    @FXML public ComboBox<SrednjaSkolaResponseDTO> srednjaSkolaCb;
    @FXML public ComboBox<VisokoskolskaUstanovaResponseDTO> visokoskolskaUstanovaCb;
    public TabPane podaciTabPane;
    @Autowired private NavigationController navController;
    @Autowired private StudentService studentService;
    @Autowired private VisokoskolskaUstanovaService visokoskolskaUstanovaService;
    @Autowired private SrednjaSkolaService srednjaSkolaService;
    @Autowired private CoderFactory coderFactory;

    @FXML
    private TextField imeTf;
    @FXML
    private TextField prezimeTf;
    @FXML
    private TextField srednjeImeTf;
    @FXML
    private RadioButton muski;
    @FXML
    private RadioButton zenski;
    @FXML
    private TextField jmbgTf;
    @FXML
    private DatePicker datumRodjenjaDp;
    @FXML
    ComboBox<SimpleCode> mestoRodjenjaCb;
    @FXML
    private TextField emailPrivatniTf;
    @FXML
    private TextField emailFakultetTf;
    @FXML
    TextField brojTelefonaTf;
    @FXML
    TextField adresaTf;
    @FXML
    ComboBox<SimpleCode> mestoStanovanjaCb;
    @FXML
    ComboBox<SimpleCode> drzavaRodjenjaCb;
    @FXML
    ComboBox<SimpleCode> drzavljanstvoCb;
    @FXML
    TextField nacionalnostTf;
    @FXML
    TextField brojLicneKarteTf;
    @FXML
    TextField godinaUpisaTf;
    @FXML
    TextField brojIndeksaTf;
    @FXML
    TextField godinaIndeksaTf;
    @FXML
    TextField uspehSrednjaSkolaTf;
    @FXML
    TextField uspehPrijemniTf;

    @FXML
    public void initialize(){
        drzavaRodjenjaCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.DRZAVA).getCodes()));
        drzavaRodjenjaCb.setValue(new SimpleCode("Serbia"));

        drzavljanstvoCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.DRZAVA).getCodes()));
        drzavljanstvoCb.setValue(new SimpleCode("Serbia"));

        mestoRodjenjaCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.MESTO).getCodes()));
        mestoRodjenjaCb.setValue(new SimpleCode("Beograd"));

        mestoStanovanjaCb.setItems(FXCollections.observableArrayList(coderFactory.getSimpleCoder(CoderType.MESTO).getCodes()));
        mestoStanovanjaCb.setValue(new SimpleCode("Beograd"));

        srednjaSkolaService.fetchSrednjaSkola()
                .collectList()
                .subscribe(lista -> Platform.runLater(() -> srednjaSkolaCb.setItems(FXCollections.observableArrayList(lista))), ErrorHandler::displayError);

        srednjaSkolaCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(SrednjaSkolaResponseDTO object) {
                return object == null ? "" : object.getNaziv();
            }
            @Override
            public SrednjaSkolaResponseDTO fromString(String string) { return null; }
        });

        visokoskolskaUstanovaService.fetchVisokoskolskaUstanove()
                .collectList()
                .subscribe(items -> Platform.runLater(() -> {
                    ObservableList<VisokoskolskaUstanovaResponseDTO> list = FXCollections.observableArrayList(items);
                    list.add(0, null);
                    visokoskolskaUstanovaCb.setItems(list);
                }), ErrorHandler::displayError);

        visokoskolskaUstanovaCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(VisokoskolskaUstanovaResponseDTO object) {
                return object == null ? "Nije izabrano" : object.getNaziv();
            }
            @Override
            public VisokoskolskaUstanovaResponseDTO fromString(String string) { return null; }
        });

        podaciTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> navController.navigateTo("newStudent:tab:" + newIdx));
    }

    public void handleSaveStudent(ActionEvent event) {
        if (srednjaSkolaCb.getValue() == null || datumRodjenjaDp.getValue() == null || mestoRodjenjaCb.getValue() == null || drzavaRodjenjaCb.getValue() == null || drzavljanstvoCb.getValue() == null || mestoStanovanjaCb.getValue() == null){
            ErrorHandler.displayError(new InvalidDataException("Please fill all fields on all tabs"));
            return;
        }

        Button button = (Button) event.getSource();
        button.setDisable(true);

        try {
            StudentRequest student = new StudentRequest();

            student.setIme(imeTf.getText());
            student.setPrezime(prezimeTf.getText());
            student.setSrednjeIme(srednjeImeTf.getText());
            student.setJmbg(jmbgTf.getText());
            student.setDatumRodjenja(datumRodjenjaDp.getValue());
            student.setMestoRodjenja(mestoRodjenjaCb.getValue().getCode());
            student.setDrzavaRodjenja(drzavaRodjenjaCb.getValue().getCode());
            student.setPol(muski.isSelected() ? 'M' : 'F');
            student.setDrzavljanstvo(drzavljanstvoCb.getValue().getCode());
            student.setNacionalnost(nacionalnostTf.getText());
            student.setBrojLicneKarte(brojLicneKarteTf.getText());
            student.setGodinaUpisa(Integer.parseInt(godinaUpisaTf.getText()));
            student.setPrivatniEmail(emailPrivatniTf.getText());
            student.setFakultetEmail(emailFakultetTf.getText());
            student.setBrojTelefonaMobilni(brojTelefonaTf.getText());
            student.setAdresaStanovanja(adresaTf.getText());
            student.setMestoStanovanja(mestoStanovanjaCb.getValue().getCode());
            student.setSrednjaSkolaId(srednjaSkolaCb.getValue().getId());
            student.setVisokoskolskaUstanovaId(visokoskolskaUstanovaCb.getValue() == null ? null : visokoskolskaUstanovaCb.getValue().getId());
            student.setUspehSrednjaSkola(Double.parseDouble(uspehSrednjaSkolaTf.getText()));
            student.setUspehPrijemni(Double.parseDouble(uspehPrijemniTf.getText()));

            studentService.saveStudent(student)
                    .doFinally(actionType -> button.setDisable(false))
                    .subscribe(data -> Platform.runLater(this::resetForm), ErrorHandler::displayError);

        }catch (Exception e){
            ErrorHandler.displayError(e);
            button.setDisable(false);
        }
    }

    private void resetForm() {
        imeTf.clear();
        prezimeTf.clear();
        srednjeImeTf.clear();
        jmbgTf.clear();
        nacionalnostTf.clear();
        brojLicneKarteTf.clear();
        godinaUpisaTf.clear();

        emailPrivatniTf.clear();
        emailFakultetTf.clear();
        brojTelefonaTf.clear();
        adresaTf.clear();
        uspehSrednjaSkolaTf.clear();
        uspehPrijemniTf.clear();

        if (brojIndeksaTf != null) brojIndeksaTf.clear();
        if (godinaIndeksaTf != null) godinaIndeksaTf.clear();

        datumRodjenjaDp.setValue(null);

        muski.setSelected(true);
        zenski.setSelected(false);

        mestoRodjenjaCb.setValue(null);
        drzavaRodjenjaCb.setValue(null);
        drzavljanstvoCb.setValue(null);
        mestoStanovanjaCb.setValue(null);

        srednjaSkolaCb.setValue(null);
        visokoskolskaUstanovaCb.setValue(null);
    }
}
