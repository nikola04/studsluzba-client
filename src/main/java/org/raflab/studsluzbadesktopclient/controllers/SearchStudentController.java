package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.StudentIndexService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.DebouncedSearchHelper;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SearchStudentController {
    @Autowired
    private ApplicationContext context;

    private final StudentService studentService;
    private final StudentIndexService studentIndexService;
    private final ObservableList<StudentResponseDTO> studentObList = FXCollections.observableArrayList();

    @FXML
    public BorderPane searchStudentPane;
    @FXML
    public TextField srednjaSkolaTf;
    @FXML
    public TextField brojIndeksaTf;
    @FXML
    private TextField studentNameTf;
    @FXML
    private TextField studentLastNameTf;

    @FXML
    private TableView<StudentResponseDTO> studentTable;

    public SearchStudentController(StudentService studentService, StudentIndexService studentIndexService) {
        this.studentService = studentService;
        this.studentIndexService = studentIndexService;
    }

    public void initialize(){
        studentTable.setItems(studentObList);
        new DebouncedSearchHelper(
                Duration.millis(300),
                () -> handleSearchStudent(null),
                studentNameTf,
                studentLastNameTf,
                srednjaSkolaTf
        );

        this.handleSearchStudent(null);
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        Button button = actionEvent != null ? ((Button) actionEvent.getSource()) : null;
        if (button != null) button.setDisable(true);

        String name = studentNameTf.getText();
        String lastName = studentLastNameTf.getText();
        String highSchoolName = srednjaSkolaTf.getText();

        studentService.searchStudents(name.trim(), lastName.trim(), highSchoolName.trim())
            .doFinally(signalType -> Platform.runLater(() -> {
                if(button != null) button.setDisable(false);
            }))
            .subscribe(pagedResponse -> {
                List<StudentResponseDTO> studentList = pagedResponse.getContent();
                Platform.runLater(() -> studentObList.setAll(studentList));
        }, ErrorHandler::displayError);
    }

    public void handleSearchByIndex(ActionEvent actionEvent) {
        String indexNumber = brojIndeksaTf.getText();
        if (indexNumber == null || indexNumber.isBlank()){
            ErrorHandler.displayError(new InvalidDataException("field 'broj indeksa' should not be empty."));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        studentIndexService.findStudentIndexByIndexNumber(indexNumber.trim())
            .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
            .subscribe(student -> Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/studentProfile.fxml"));
                    loader.setControllerFactory(context::getBean);
                    Parent root = loader.load();

                    StudentController controller = loader.getController();
                    controller.setStudentIndex(student);

                    Stage stage = new Stage();
                    stage.setTitle("Student Profile");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);

                    stage.show();

                } catch (IOException e) {
                    ErrorHandler.displayError(e);
                }
            }), ErrorHandler::displayError);
    }
}
