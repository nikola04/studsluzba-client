package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.raflab.studsluzbacommon.dto.response.StudentIndeksResponseDTO;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.exceptions.InvalidDataException;
import org.raflab.studsluzbadesktopclient.services.StudentIndexService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.DebouncedSearchHelper;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchStudentController {
    private final MainView mainView;

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

    @FXML public Button prevBtn;
    @FXML public TextField currentPageTf;
    @FXML public Label totalPagesLabel;
    @FXML public Button nextBtn;
    @FXML public ComboBox<Integer> pageSizeCb;

    @FXML
    private TableView<StudentResponseDTO> studentTable;

    private int currentPage = 0;
    private int totalPages = 1;

    public SearchStudentController(StudentService studentService, StudentIndexService studentIndexService, MainView mainView) {
        this.studentService = studentService;
        this.studentIndexService = studentIndexService;
        this.mainView = mainView;
    }

    public void initialize(){
        studentTable.setItems(studentObList);
        pageSizeCb.setItems(FXCollections.observableArrayList(5, 10, 20, 50));
        pageSizeCb.setValue(10);

        new DebouncedSearchHelper(
                Duration.millis(300),
                () -> handleSearchStudent(true),
                studentNameTf,
                studentLastNameTf,
                srednjaSkolaTf
        );

        studentTable.setRowFactory(tv -> {
            TableRow<StudentResponseDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    StudentResponseDTO rowData = row.getItem();
                    this.handleStudentSelection(rowData);
                }
            });
            return row;
        });

        currentPageTf.setOnAction(e -> {
            try {
                int targetPage = Integer.parseInt(currentPageTf.getText()) - 1;
                if (targetPage >= 0 && targetPage < totalPages) {
                    currentPage = targetPage;
                    this.updateCurrentPage();
                    handleSearchStudent(false);
                }
            } catch (NumberFormatException ex) {
                this.updateCurrentPage();
            }
        });

        pageSizeCb.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.equals(oldVal)) return;
            this.handleSearchStudent(true);
        });
        this.handleSearchStudent(true);
    }

    private void handleStudentSelection(StudentResponseDTO selectedStudent) {
        studentIndexService.fetchStudentIndexByStudentId(selectedStudent.getId())
                .collectList()
                .subscribe(indices -> Platform.runLater(() -> {
                    if (indices.isEmpty()) {
                        mainView.openModal("editProfile", "Student Profile", (EditProfileController controller) -> {
                            controller.setStudentData(selectedStudent);
                            controller.setParentController(null);
                        });
                        return;
                    }

                    ListView<StudentIndeksResponseDTO> listView = new ListView<>();
                    listView.setItems(FXCollections.observableArrayList(indices));
                    listView.setPrefHeight(150);

                    listView.setCellFactory(lv -> new ListCell<>() {
                        @Override
                        protected void updateItem(StudentIndeksResponseDTO item, boolean empty) {
                            super.updateItem(item, empty);
                            if(item == null)
                                setText(null);
                            else setText(item.getStudijskiProgram().getOznaka() + " " + String.format("%03d", item.getBroj()) + "/" + item.getGodina());
                        }
                    });

                    Alert dialog = new Alert(Alert.AlertType.NONE);
                    dialog.setTitle("Choose Index");
                    dialog.setHeaderText("Student: " + selectedStudent.getIme() + " " + selectedStudent.getPrezime());

                    ButtonType closeButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getButtonTypes().add(closeButton);

                    VBox container = new VBox(10, new Label("Double click to open:"), listView);
                    container.setPadding(new javafx.geometry.Insets(10));
                    dialog.getDialogPane().setContent(container);

                    listView.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && listView.getSelectionModel().getSelectedItem() != null) {
                            StudentIndeksResponseDTO selected = listView.getSelectionModel().getSelectedItem();
                            dialog.close();
                            this.openStudentIndex(selected);
                        }
                    });

                    dialog.showAndWait();

                    studentTable.getSelectionModel().clearSelection();

                }), ErrorHandler::displayError);
    }

    public void handleSearchStudent(boolean toResetPage) {
        if (toResetPage) currentPage = 0;
        this.updateCurrentPage();

        String name = studentNameTf.getText();
        String lastName = studentLastNameTf.getText();
        String highSchoolName = srednjaSkolaTf.getText();

        Integer pageSize = pageSizeCb.getValue();

        studentService.searchStudents(name.trim(), lastName.trim(), highSchoolName.trim(), currentPage, pageSize)
            .subscribe(pagedResponse -> {
                List<StudentResponseDTO> studentList = pagedResponse.getContent();
                currentPage = pagedResponse.getPage();
                totalPages = pagedResponse.getTotalPages();

                Platform.runLater(() -> {
                    this.updateCurrentPage();
                    this.updateTotalPages();
                    studentObList.setAll(studentList);
                });
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

        studentIndexService.fetchStudentIndexByIndexNumber(indexNumber.trim())
            .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
            .subscribe(student -> Platform.runLater(() -> openStudentIndex(student)), ErrorHandler::displayError);
    }

    private void openStudentIndex(StudentIndeksResponseDTO student){
        mainView.openModal("studentProfile", "Student Index", (StudentController controller) -> controller.setStudentIndex(student));
    }

    private void updateCurrentPage(){
        currentPageTf.setText(String.valueOf(currentPage + 1));
    }

    private void updateTotalPages(){
        totalPagesLabel.setText(" od " + totalPages);
    }

    public void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            this.updateCurrentPage();
            handleSearchStudent(false);
        }
    }

    public void handleNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            this.updateCurrentPage();
            this.handleSearchStudent(false);
        }
    }

    public void handleDeleteStudent(ActionEvent actionEvent) {
        StudentResponseDTO selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if(selectedStudent == null) {
            ErrorHandler.displayError(new IllegalStateException("Select student to delete"));
            return;
        }

        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Student deletion");
        confirm.setHeaderText("Please confirm deletion of student:");
        confirm.setContentText(selectedStudent.getIme() + " " + selectedStudent.getPrezime());

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                button.setDisable(false);
                return;
            }

            studentService.deleteStudent(selectedStudent.getId())
                .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                .subscribe(deleted -> Platform.runLater(() -> studentObList.remove(selectedStudent)), ErrorHandler::displayError);
        });
    }
}
