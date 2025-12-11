package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.coder.CoderType;
import org.raflab.studsluzbadesktopclient.coder.SimpleCode;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.time.ZoneId;
import java.util.*;

@Component
public class StudentController {

    private final StudentService studentService;
    private final CoderFactory coderFactory;

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
    private DatePicker datumAktivacijeDp;
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

//    @FXML
//    ComboBox<StudProgram> studProgramCb;

//    @FXML
//    ComboBox<SrednjaSkola> srednjaSkolaCb;

//    @FXML
//    ComboBox<VisokoskolskaUstanova> visokoskolskaUstanovaCb;


    public StudentController(StudentService studentService,CoderFactory coderFactory) {
        this.studentService = studentService;
        this.coderFactory = coderFactory;
    }

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
    }

    public void handleOpenModalSrednjeSkole(ActionEvent ae) {
        //mainViewManager.openModal("addSrednjaSkola");
    }

    public void handleOpenModalVisokoskolskeUstanove(ActionEvent ae) {
        //mainViewManager.openModal("addVisaUstanovaForStudent");
    }

    public void handleSaveStudent(ActionEvent event) {
        StudentDTO studentDTO = new StudentDTO();

        studentDTO.setIme(imeTf.getText());
        studentDTO.setPrezime(prezimeTf.getText());
        studentDTO.setSrednjeIme(srednjeImeTf.getText());
        studentDTO.setPol(muski.isSelected() ? "M" : "Z");
        studentDTO.setGodinaUpisa(Integer.parseInt(godinaUpisaTf.getText()));
        studentDTO.setAdresa(adresaTf.getText());
        studentDTO.setJmbg(jmbgTf.getText());
        studentDTO.setDatumRodjenja(
                Date.from(datumRodjenjaDp.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        studentDTO.setMestoRodjenja(mestoRodjenjaCb.getValue().getCode());
        studentDTO.setEmailPrivatni(emailPrivatniTf.getText());
        studentDTO.setEmailFakultet(emailFakultetTf.getText());
        studentDTO.setBrojTelefona(brojTelefonaTf.getText());
        studentDTO.setMestoStanovanja(mestoStanovanjaCb.getValue().getCode());

        studentDTO.setDrzavaRodjenja(drzavaRodjenjaCb.getValue().getCode());
        studentDTO.setDrzavljanstvo(drzavljanstvoCb.getValue().getCode());
        studentDTO.setNacionalnost(nacionalnostTf.getText());

        studentService.saveStudent(studentDTO);
        resetForm();
    }

    public void handleIzvestaj() throws JRException {

        List<StudentDTO> studenti = studentService.sviStudenti();

        JasperReport report = JasperCompileManager.compileReport(
                MainView.class.getResourceAsStream("/reports/sviStudenti.jrxml")
        );

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(studenti);
        JasperPrint jp = JasperFillManager.fillReport(report, new HashMap<>(), dataSource);

        JasperExportManager.exportReportToPdfFile(jp, "sviStudenti.pdf");
    }


    private void resetForm() {
        imeTf.clear();
        prezimeTf.clear();
        srednjeImeTf.clear();

        muski.setSelected(false);
        zenski.setSelected(false);

        jmbgTf.clear();
        datumRodjenjaDp.setValue(null);
        datumAktivacijeDp.setValue(null);

        mestoRodjenjaCb.setValue(null);
        emailPrivatniTf.clear();
        emailFakultetTf.clear();
        brojTelefonaTf.clear();
        adresaTf.clear();

        mestoStanovanjaCb.setValue(null);
        drzavaRodjenjaCb.setValue(null);
        drzavljanstvoCb.setValue(null);

        nacionalnostTf.clear();
        brojLicneKarteTf.clear();
        godinaUpisaTf.clear();
        brojIndeksaTf.clear();
        godinaIndeksaTf.clear();
        uspehSrednjaSkolaTf.clear();
        uspehPrijemniTf.clear();
    }


}
