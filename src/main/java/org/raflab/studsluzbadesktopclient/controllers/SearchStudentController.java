package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchStudentController {

    private final StudentService studentService;

    @FXML
    private TextField imeStudentaTf;
    @FXML
    private TextField prezimeStudentaTf;

    @FXML
    private TableView<StudentDTO> tabelaStudenti;

    public SearchStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        ((Button) actionEvent.getSource()).setDisable(true);

        String ime = imeStudentaTf.getText();
        String prezime = prezimeStudentaTf.getText();

        studentService.searchStudentAsync(ime, prezime)
            .thenAccept(pagedResponse -> {
                List<StudentDTO> studenti = pagedResponse.getContent();
                Platform.runLater(() -> tabelaStudenti.setItems(FXCollections.observableArrayList(studenti)));
            })
            .exceptionally(ex -> {
                ErrorHandler.displayError(ex);
                return null;
            })
            .thenRun(() -> Platform.runLater(() -> ((Button) actionEvent.getSource()).setDisable(false)));
    }
}
