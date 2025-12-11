package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchStudentController {

    private final StudentService studentService;

    @FXML
    private TextField imeStudentaTf;

    @FXML
    private TableView<StudentDTO> tabelaStudenti;

    public SearchStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        List<StudentDTO> studenti = null;
        if(imeStudentaTf.getText().isEmpty())
            studenti = studentService.sviStudenti();
        else
            studenti = studentService.searchStudent(imeStudentaTf.getText());

        tabelaStudenti.setItems(FXCollections.observableArrayList(studenti));
    }
}
