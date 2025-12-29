package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import org.raflab.studsluzbacommon.dto.response.NacinFinansiranja;
import org.raflab.studsluzbacommon.dto.response.StudentResponseDTO;
import org.raflab.studsluzbacommon.dto.response.StudijskiProgramResponseDTO;
import org.raflab.studsluzbadesktopclient.services.NacinFinansiranjaService;
import org.raflab.studsluzbadesktopclient.services.StudentIndexService;
import org.raflab.studsluzbadesktopclient.services.StudijskiProgramService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NewStudentIndexController {
    private final StudijskiProgramService studijskiProgramService;
    private final NacinFinansiranjaService nacinFinansiranjaService;
    private final StudentIndexService studentIndexService;
    @Setter
    private StudentResponseDTO student;

    public TextField godinaTf;
    public ComboBox<StudijskiProgramResponseDTO> studProgramCb;
    public ComboBox<NacinFinansiranja> nacinFinansiranjaCb;
    public DatePicker vaziOdDp;
    public CheckBox aktivanChb;

    public NewStudentIndexController(StudijskiProgramService studijskiProgramService, NacinFinansiranjaService nacinFinansiranjaService, StudentIndexService studentIndexService) {
        this.studijskiProgramService = studijskiProgramService;
        this.nacinFinansiranjaService = nacinFinansiranjaService;
        this.studentIndexService = studentIndexService;
    }

    public void initialize(){
        studProgramCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(StudijskiProgramResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        studProgramCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(StudijskiProgramResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        nacinFinansiranjaCb.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(NacinFinansiranja item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNacin());
            }
        });
        nacinFinansiranjaCb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(NacinFinansiranja item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNacin());
            }
        });

        studijskiProgramService.fetchStudijskiProgram().collectList().subscribe(programs -> studProgramCb.getItems().setAll(programs), ErrorHandler::displayError);
        nacinFinansiranjaService.fetchNaciniFinansiranja().collectList().subscribe(finance -> nacinFinansiranjaCb.getItems().setAll(finance), ErrorHandler::displayError);
    }

    public void handleCancel() {
        this.closeWindow();
    }

    public void handleSave() {
        String godinaText = godinaTf.getText();
        LocalDate vaziOd = vaziOdDp.getValue();
        StudijskiProgramResponseDTO studProgram = studProgramCb.getSelectionModel().getSelectedItem();
        NacinFinansiranja nacinFinansiranja = nacinFinansiranjaCb.getSelectionModel().getSelectedItem();
        boolean aktivan = aktivanChb.isSelected();

        if(godinaText.isBlank() || studProgram == null || nacinFinansiranja == null || vaziOd == null) {
            ErrorHandler.displayError(new IllegalStateException("Please fill all fields."));
            return;
        }

        try{
            Integer godina = Integer.parseInt(godinaText);

            studentIndexService.createStudentIndex(student.getId(), godina, studProgram.getId(), nacinFinansiranja.getId(), aktivan, vaziOd).subscribe(id -> Platform.runLater(this::closeWindow), ErrorHandler::displayError);
        }catch (NumberFormatException e){
            ErrorHandler.displayError(e);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) godinaTf.getScene().getWindow();
        stage.close();
    }
}
