package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private final ObservableList<StudentResponseDTO> studentObList = FXCollections.observableArrayList();

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

    public void initialize(){
        studentTable.setItems(studentObList);
        this.handleSearchStudent(null);
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        Button button = actionEvent != null ? ((Button) actionEvent.getSource()) : null;
        if (button != null) button.setDisable(true);

        String name = studentNameTf.getText();
        String lastName = studentLastNameTf.getText();

        studentService.searchStudents(name, lastName).subscribe(pagedResponse -> {
            List<StudentResponseDTO> studentList = pagedResponse.getContent();
            Platform.runLater(() -> studentObList.setAll(studentList));
        }, ErrorHandler::displayError, () -> {
            if (button != null) button.setDisable(false);
        });
    }
}
