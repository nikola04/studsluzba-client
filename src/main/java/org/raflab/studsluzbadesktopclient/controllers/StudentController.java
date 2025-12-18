package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.raflab.studsluzbacommon.dto.response.StudentIndeksResponseDTO;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.services.StudentIndexService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StudentController {
    public TableView examsTable;
    public ListView yearsList;
    public TableView paymentsTable;
    @Autowired
    private ApplicationContext context;

    @Autowired
    private StudentIndexService studentIndexService;
//    @Autowired
//    private StudentService studentService;

    public Label nameLabel;
    public Label avgLabel;
    public Label espbLabel;

    private StudentIndeksResponseDTO studentIndex;
    private StudentResponseDTO student;

    public void setStudentIndex(StudentIndeksResponseDTO studentIndex){
        this.studentIndex = studentIndex;
        if (studentIndex == null)
            this.student = null;
        else this.student = studentIndex.getStudent();

        this.onStudentUpdate();
    }

    public void setStudent(StudentResponseDTO student){
        this.student = student;
        this.onStudentUpdate();
    }

    private void updateAverageOcena(){
        avgLabel.setText("Average: ");
        studentIndexService.findStudentAverageOcena(studentIndex.getId()).subscribe(ocena -> Platform.runLater(() -> avgLabel.setText("Average: " + ocena)), ErrorHandler::displayError);
    }
    private void onStudentUpdate(){
        nameLabel.setText(student != null ? student.getIme() + " " + student.getPrezime() : "Student");
        espbLabel.setText("ESPB: " + (studentIndex != null ? studentIndex.getOstvarenoEspb() : 0));
        this.updateAverageOcena();
    }

    @FXML
    private void handleOpenEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editProfile.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            EditProfileController editController = loader.getController();
            editController.setStudentData(this.student);
            editController.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
