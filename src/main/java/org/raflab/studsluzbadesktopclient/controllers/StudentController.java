package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.raflab.studsluzbacommon.dto.response.*;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.raflab.studsluzbadesktopclient.services.StudentIndexService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class StudentController {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private StudentIndexService studentIndexService;
    @Autowired
    private SkolskaGodinaService skolskaGodinaService;

    public Label remainingRsdLabel;
    public Label remainingEurLabel;

    public TableView<UpisGodineResponse> upisTable;
    public TableView<ObnovaGodineResponse> obnovaTable;
    public TableView<IspitResponse> passedExamsTable;
    public TableView<IspitResponse> examsTable;
    public TableView<UplataResponse> paymentsTable;

    @FXML private TableColumn<IspitResponse, Long> idPassedExamColumn;
    @FXML private TableColumn<IspitResponse, String> predmetPassedExamColumn;
    @FXML private TableColumn<IspitResponse, String> nastavnikPassedExamColumn;
    @FXML private TableColumn<IspitResponse, Integer> espbPassedExamColumn;
    @FXML private TableColumn<IspitResponse, Long> rokPassedExamColumn;

    @FXML private TableColumn<IspitResponse, Long> idExamColumn;
    @FXML private TableColumn<IspitResponse, String> predmetExamColumn;
    @FXML private TableColumn<IspitResponse, String> nastavnikExamColumn;
    @FXML private TableColumn<IspitResponse, Integer> espbExamColumn;
    @FXML private TableColumn<IspitResponse, Long> rokExamColumn;

    private ObservableList<UplataResponse> payments;
    private ObservableList<IspitResponse> passedExams;
    private ObservableList<IspitResponse> exams;
    private ObservableList<UpisGodineResponse> upisGodine;
    private ObservableList<ObnovaGodineResponse> obnovaGodine;

    public Label nameLabel;
    public Label avgLabel;
    public Label espbLabel;

    private StudentIndeksResponseDTO studentIndex;
    private StudentResponseDTO student;

    public void initialize(){
        this.initExamsTable(idPassedExamColumn, predmetPassedExamColumn, nastavnikPassedExamColumn, espbPassedExamColumn, rokPassedExamColumn);
        this.initExamsTable(idExamColumn, predmetExamColumn, nastavnikExamColumn, espbExamColumn, rokExamColumn);
        payments = FXCollections.observableArrayList();
        passedExams = FXCollections.observableArrayList();
        exams = FXCollections.observableArrayList();
        upisGodine = FXCollections.observableArrayList();
        obnovaGodine = FXCollections.observableArrayList();
        paymentsTable.setItems(payments);
        passedExamsTable.setItems(passedExams);
        examsTable.setItems(exams);
        upisTable.setItems(upisGodine);
        obnovaTable.setItems(obnovaGodine);
    }

    private void initExamsTable(TableColumn<IspitResponse, Long> idColumn, TableColumn<IspitResponse, String> predmetColumn, TableColumn<IspitResponse, String> nastavnikColumn, TableColumn<IspitResponse, Integer> espbColumn, TableColumn<IspitResponse, Long> rokColumn){
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        predmetColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPredmet() != null)
                new SimpleStringProperty(cellData.getValue().getPredmet().getNaziv());
            return null;
        });
        nastavnikColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getNastavnik() != null)
                new SimpleStringProperty(cellData.getValue().getNastavnik().getIme() + " " + cellData.getValue().getNastavnik().getPrezime());
            return null;
        });
        espbColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPredmet() != null) {
                return new SimpleObjectProperty<>(cellData.getValue().getPredmet().getEspb());
            }else return null;
        });
        rokColumn.setCellValueFactory(cellData -> {
            if(cellData.getValue().getIspitniRok() != null){
                return new SimpleObjectProperty<>(cellData.getValue().getIspitniRok().getId());
            }else return null;
        });
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

    private void updatePaymentRemainingAmount(){
        studentIndexService.fetchUplataPreostaliIznos(studentIndex.getId()).subscribe(amount -> Platform.runLater(() -> {
            remainingRsdLabel.setText("RSD: " + String.format("%.2f", amount.getRsd()));
            remainingEurLabel.setText("EUR: " + String.format("%.2f", amount.getEur()));
        }), ErrorHandler::displayError);
    }

    private void updateUpisGodine(){
        upisGodine.clear();
        studentIndexService.fetchStudentUpisGodine(studentIndex.getId()).subscribe(upisGodine::add, ErrorHandler::displayError);
        upisTable.refresh();
    }

    private void updateObnovaGodine(){
        obnovaGodine.clear();
        studentIndexService.fetchStudentObnovaGodine(studentIndex.getId()).subscribe(obnovaGodine::add, ErrorHandler::displayError);
        obnovaTable.refresh();
    }

    private void updatePayments(){
        payments.clear();

        studentIndexService.fetchStudentUplata(studentIndex.getId()).subscribe(payments::add, ErrorHandler::displayError);
        this.updatePaymentRemainingAmount();

        paymentsTable.refresh();
    }

    private void updateExams(){
        passedExams.clear(); exams.clear();
        studentIndexService.fetchStudentPolozenIspit(studentIndex.getId()).subscribe(passedExams::add, ErrorHandler::displayError);
        studentIndexService.fetchStudentNepolozeniIspiti(studentIndex.getId()).subscribe(exams::add, ErrorHandler::displayError);
        passedExamsTable.refresh(); examsTable.refresh();
    }

    private void onStudentUpdate(){
        nameLabel.setText(student != null ? student.getIme() + " " + student.getPrezime() : "Student");
        espbLabel.setText("ESPB: " + (studentIndex != null ? studentIndex.getOstvarenoEspb() : 0));
        this.updateAverageOcena();
        this.updatePayments();
        this.updateExams();
        this.updateUpisGodine();
        this.updateObnovaGodine();
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
                    .subscribe(uplataId -> {
                        studentIndexService.fetchStudentUplata(studentIndex.getId(), uplataId)
                                .subscribe(payments::add);
                        this.updatePaymentRemainingAmount();
                    }, ErrorHandler::displayError);
            }catch (NumberFormatException e){
                ErrorHandler.displayError(new NumberFormatException("Invalid amount"));
            }
        });
    }

    public void handleDeletePayment(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        UplataResponse selected = paymentsTable.getSelectionModel().getSelectedItem();

        studentIndexService.deleteStudentUplata(studentIndex.getId(), selected.getId())
                .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                .subscribe(deleted -> {
                    if (!deleted) return;
                    payments.remove(selected);
                    this.updatePaymentRemainingAmount();
                }, ErrorHandler::displayError);
    }

    public void handleCreateUpis(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        skolskaGodinaService.fetchSkolskeGodine()
            .collectList()
            .doOnError(err -> Platform.runLater(() -> button.setDisable(false)))
            .subscribe(skolskaGodinaList -> Platform.runLater(() -> {
                Integer activeYear = skolskaGodinaList.stream()
                        .filter(SkolskaGodinaResponseDTO::getAktivan)
                        .map(SkolskaGodinaResponseDTO::getGodina)
                        .findFirst()
                        .orElse(0);

                List<SkolskaGodinaResponseDTO> filteredYears = skolskaGodinaList.stream()
                        .filter(g -> g.getGodina() > activeYear)
                        .sorted(Comparator.comparing(SkolskaGodinaResponseDTO::getGodina))
                        .toList();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potvrda upisa");
                alert.setHeaderText("Odaberite školsku godinu za upis");

                ComboBox<SkolskaGodinaResponseDTO> godinaComboBox = new ComboBox<>();
                godinaComboBox.getItems().addAll(filteredYears);
                godinaComboBox.setPromptText("Odaberi godinu...");
                godinaComboBox.setMaxWidth(Double.MAX_VALUE);

                godinaComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(SkolskaGodinaResponseDTO object) {
                        return object == null ? "" : (object.getGodina() + (object.getAktivan() ? " (active)": "")) ;
                    }
                    @Override
                    public SkolskaGodinaResponseDTO fromString(String string) {
                        return null;
                    }
                });

                skolskaGodinaList.stream()
                        .filter(SkolskaGodinaResponseDTO::getAktivan)
                        .findFirst()
                        .ifPresent(godinaComboBox::setValue);

                TextField napomenaField = new TextField();
                napomenaField.setPromptText("Optional...");

                VBox content = new VBox(10,
                        new Label("Školska godina:"), godinaComboBox,
                        new Label("Napomena:"), napomenaField
                );
                content.setStyle("-fx-padding: 10;");
                alert.getDialogPane().setContent(content);

                ButtonType buttonTypeConfirm = new ButtonType("Confirm");
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);

                alert.showAndWait().ifPresent(response -> {
                    if (response != buttonTypeConfirm) {
                        button.setDisable(false);
                        return;
                    }
                    String napomenaTekst = napomenaField.getText();
                    studentIndexService.createStudentUpisGodine(studentIndex.getId(), godinaComboBox.getSelectionModel().getSelectedItem().getId(), napomenaTekst)
                        .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                        .subscribe(upisGodinaId -> this.updateUpisGodine(), ErrorHandler::displayError);
                });
            }), ErrorHandler::displayError);
    }

    public void handleCreateObnova(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        skolskaGodinaService.fetchSkolskeGodine()
                .collectList()
                .doOnError(err -> Platform.runLater(() -> button.setDisable(false)))
                .subscribe(skolskaGodinaList -> Platform.runLater(() -> {
                    Integer activeYear = skolskaGodinaList.stream()
                            .filter(SkolskaGodinaResponseDTO::getAktivan)
                            .map(SkolskaGodinaResponseDTO::getGodina)
                            .findFirst()
                            .orElse(0);

                    List<SkolskaGodinaResponseDTO> filteredYears = skolskaGodinaList.stream()
                            .filter(g -> g.getGodina() > activeYear)
                            .sorted(Comparator.comparing(SkolskaGodinaResponseDTO::getGodina))
                            .toList();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Potvrda Obnove");
                    alert.setHeaderText("Odaberite školsku godinu za obnovu");

                    ComboBox<SkolskaGodinaResponseDTO> godinaComboBox = new ComboBox<>();
                    godinaComboBox.getItems().addAll(filteredYears);
                    godinaComboBox.setPromptText("Odaberi godinu...");
                    godinaComboBox.setMaxWidth(Double.MAX_VALUE);

                    godinaComboBox.setConverter(new StringConverter<>() {
                        @Override
                        public String toString(SkolskaGodinaResponseDTO object) {
                            return object == null ? "" : (object.getGodina() + (object.getAktivan() ? " (active)": "")) ;
                        }
                        @Override
                        public SkolskaGodinaResponseDTO fromString(String string) {
                            return null;
                        }
                    });

                    skolskaGodinaList.stream()
                            .filter(SkolskaGodinaResponseDTO::getAktivan)
                            .findFirst()
                            .ifPresent(godinaComboBox::setValue);

                    TextField napomenaField = new TextField();
                    napomenaField.setPromptText("Optional...");

                    VBox content = new VBox(10,
                            new Label("Školska godina:"), godinaComboBox,
                            new Label("Napomena:"), napomenaField
                    );
                    content.setStyle("-fx-padding: 10;");
                    alert.getDialogPane().setContent(content);

                    ButtonType buttonTypeConfirm = new ButtonType("Confirm");
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);

                    alert.showAndWait().ifPresent(response -> {
                        if (response != buttonTypeConfirm) {
                            button.setDisable(false);
                            return;
                        }
                        String napomenaTekst = napomenaField.getText();
                        studentIndexService.createStudentObnovaGodine(studentIndex.getId(), godinaComboBox.getSelectionModel().getSelectedItem().getId(), napomenaTekst)
                                .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                                .subscribe(upisGodinaId -> this.updateUpisGodine(), ErrorHandler::displayError);
                    });
                }), ErrorHandler::displayError);
    }
}
