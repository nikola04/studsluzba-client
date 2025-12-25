package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.raflab.studsluzbacommon.dto.response.StudentIndeksResponseDTO;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbacommon.dto.response.UplataResponse;
import org.raflab.studsluzbadesktopclient.services.StudentIndexService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class StudentController {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private StudentIndexService studentIndexService;

    public Label remainingRsdLabel;
    public Label remainingEurLabel;

    public TableView examsTable;
    public ListView yearsList;
    public TableView<UplataResponse> paymentsTable;

    private ObservableList<UplataResponse> payments;

    public Label nameLabel;
    public Label avgLabel;
    public Label espbLabel;

    private StudentIndeksResponseDTO studentIndex;
    private StudentResponseDTO student;

    public void initialize(){
        payments = FXCollections.observableArrayList();
        paymentsTable.setItems(payments);
    }

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
    private void updatePayments(){
        payments.clear();

        studentIndexService.fetchUplataPreostaliIznos(studentIndex.getId()).subscribe(amount -> Platform.runLater(() -> {
            remainingRsdLabel.setText("RSD: " + String.format("%.2f", amount.getRsd()));
            remainingEurLabel.setText("EUR: " + String.format("%.2f", amount.getEur()));
        }), ErrorHandler::displayError);
        studentIndexService.fetchStudentUplata(studentIndex.getId()).subscribe(payments::add, ErrorHandler::displayError);

        paymentsTable.refresh();
    }

    private void onStudentUpdate(){
        nameLabel.setText(student != null ? student.getIme() + " " + student.getPrezime() : "Student");
        espbLabel.setText("ESPB: " + (studentIndex != null ? studentIndex.getOstvarenoEspb() : 0));
        this.updateAverageOcena();
        this.updatePayments();
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

    public void handleCreateUplata(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();

        TextInputDialog dialog = new TextInputDialog("0.00");
        dialog.setTitle("New Uplata");
        dialog.setHeaderText("Enter new amount:");
        dialog.setContentText("Amount in RSD");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(stringAmount -> {
            try {
                double amount = Double.parseDouble(stringAmount.replace(",", "."));
                button.setDisable(true);

                studentIndexService.createStudentUplata(studentIndex.getId(), amount)
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(uplataId -> studentIndexService.fetchStudentUplata(studentIndex.getId(), uplataId)
                        .subscribe(payments::add), ErrorHandler::displayError);
            }catch (NumberFormatException e){
                ErrorHandler.displayError(new NumberFormatException("Invalid amount"));
            }
        });
    }
}
