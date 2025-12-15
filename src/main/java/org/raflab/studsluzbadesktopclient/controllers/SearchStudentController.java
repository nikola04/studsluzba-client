package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchStudentController {

    private final StudentService studentService;

    @FXML
    public BorderPane searchStudentPane;
    @FXML
    private TextField studentNameTf;
    @FXML
    private TextField studentLastNameTf;

    @FXML
    private TableView<StudentResponseDTO> studentTable;

    public SearchStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        ((Button) actionEvent.getSource()).setDisable(true);

        String name = studentNameTf.getText();
        String lastName = studentLastNameTf.getText();

        studentService.searchStudentAsync(name, lastName)
            .thenAccept(pagedResponse -> {
                List<StudentResponseDTO> studenti = pagedResponse.getContent();
                Platform.runLater(() -> studentTable.setItems(FXCollections.observableArrayList(studenti)));
            })
            .exceptionally(ex -> {
                ErrorHandler.displayError(ex);
                return null;
            })
            .thenRun(() -> Platform.runLater(() -> ((Button) actionEvent.getSource()).setDisable(false)));
    }
}
