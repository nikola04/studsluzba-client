package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Setter;
import org.raflab.studsluzbacommon.dto.response.SrednjaSkolaResponseDTO;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbacommon.dto.response.VisokoskolskaUstanovaResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.SrednjaSkolaService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.services.VisokoskolskaUstanovaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EditProfileController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private VisokoskolskaUstanovaService visokoskolskaUstanovaService;
    @Autowired
    private SrednjaSkolaService srednjaSkolaService;
    @Setter
    private StudentController parentController;

    @FXML private TextField firstNameTf, lastNameTf, middleNameTf, jmbgTf, idCardTf, birthPlaceTf, privateEmailTf, mobilePhoneTf, addressTf;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<Character> genderCb;
    @FXML private ComboBox<SrednjaSkolaResponseDTO> highSchoolCb;
    @FXML private ComboBox<VisokoskolskaUstanovaResponseDTO> universityCb;

    private StudentResponseDTO student;

    @FXML
    public void initialize() {
        genderCb.getItems().addAll('M', 'F');

        highSchoolCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(SrednjaSkolaResponseDTO s) {
                return s == null ? "" : s.getNaziv();
            }

            @Override
            public SrednjaSkolaResponseDTO fromString(String s) {
                return null;
            }
        });

        universityCb.setConverter(new StringConverter<>() {
            @Override
            public String toString(VisokoskolskaUstanovaResponseDTO v) {
                return v == null ? "" : v.getNaziv();
            }

            @Override
            public VisokoskolskaUstanovaResponseDTO fromString(String s) {
                return null;
            }
        });

        loadComboBoxData();
    }

    private void loadComboBoxData() {
        highSchoolCb.getItems().clear();
         srednjaSkolaService.fetchSrednjaSkola().subscribe(item -> Platform.runLater(() -> highSchoolCb.getItems().add(item)));

         universityCb.getItems().clear();
         universityCb.getItems().add(null);
         visokoskolskaUstanovaService.fetchVisokoskolskaUstanove().subscribe(item -> Platform.runLater(() -> universityCb.getItems().add(item)));
    }

    public void setStudentData(StudentResponseDTO student) {
        this.student = student;
        firstNameTf.setText(student.getIme());
        lastNameTf.setText(student.getPrezime());
        middleNameTf.setText(student.getSrednjeIme());
        jmbgTf.setText(student.getJmbg());
        idCardTf.setText(student.getBrojLicneKarte());
        birthPlaceTf.setText(student.getMestoRodjenja());
        privateEmailTf.setText(student.getPrivatniEmail());
        mobilePhoneTf.setText(student.getBrojTelefonaMobilni());
        addressTf.setText(student.getAdresa());
        birthDatePicker.setValue(student.getDatumRodjenja());
        genderCb.setValue(student.getPol());

        highSchoolCb.setValue(student.getSrednjaSkola());
        universityCb.setValue(student.getVisokoskolskaUstanova());
    }

    @FXML
    private void handleSave() {
        String firstName = firstNameTf.getText();
        String lastName = lastNameTf.getText();
        String middleName = middleNameTf.getText();
        String jmbg = jmbgTf.getText();
        LocalDate birthDate = birthDatePicker.getValue();
        String idCard = idCardTf.getText();
        String birthPlace = birthPlaceTf.getText();
        String privateEmail = privateEmailTf.getText();
        String mobilePhone = mobilePhoneTf.getText();
        String address = addressTf.getText();
        Character gender = genderCb.getValue();
        SrednjaSkolaResponseDTO highSchool = highSchoolCb.getValue();
        VisokoskolskaUstanovaResponseDTO university = universityCb.getValue();

        if (firstName.isBlank() || lastName.isBlank() || birthPlace.isBlank() || privateEmail.isBlank() || mobilePhone.isBlank() || address.isBlank()) {
            ErrorHandler.displayError(new InvalidDataException("All fields are required"));
            return;
        }

        studentService.updateStudentById(
            student.getId(),
            firstName,
            lastName,
            middleName,
            jmbg,
            student.getGodinaUpisa(),         // Postojeća vrednost
            birthDate,
            birthPlace,
            student.getMestoPrebivalista(),   // Postojeća vrednost
            student.getDrzavaRodjenja(),      // Postojeća vrednost
            student.getDrzavljanstvo(),       // Postojeća vrednost
            student.getNacionalnost(),        // Postojeća vrednost
            gender,
            address,
            mobilePhone,
            student.getBrojTelefonaFiksni(),  // Postojeća vrednost
            student.getFakultetEmail(),       // Postojeća vrednost
            privateEmail,
            idCard,
            student.getLicnuKartuIzdao(),     // Postojeća vrednost
            student.getMestoStanovanja(),     // Postojeća vrednost
            student.getAdresaStanovanja(),    // Postojeća vrednost
            student.getUspehSrednjaSkola(),   // Postojeća vrednost
            student.getUspehPrijemni(),       // Postojeća vrednost
            highSchool.getId(),
            university == null ? null : university.getId()
        ).subscribe(updatedStudent -> Platform.runLater(() -> {
            if (parentController != null) {
                parentController.setStudent(updatedStudent);
            }
            closeWindow();
        }), ErrorHandler::displayError);
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        Stage stage = (Stage) firstNameTf.getScene().getWindow();
        stage.close();
    }
}