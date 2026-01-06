package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.raflab.studsluzbacommon.dto.response.StudijskiProgramResponseDTO;
import org.raflab.studsluzbacommon.dto.response.VrstaStudijaResponseDTO;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.StudijskiProgramService;
import org.raflab.studsluzbadesktopclient.services.VrstaStudijaService;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class StudijskiProgramController {
    private final VrstaStudijaService vrstaStudijaService;
    private final StudijskiProgramService studijskiProgramService;
    private final NavigationController navigationController;
    private final MainView mainView;
    public TableView<StudijskiProgramResponseDTO> studijskiProgramTable;
    public TextField txtOznaka;
    public TextField txtNaziv;
    public TextField txtZvanje;
    public TextField txtGodinaAkreditacije;
    public ComboBox<VrstaStudijaResponseDTO> cmbVrstaStudija;
    public TextField txtTrajanjeGodina;
    public TextField txtTrajanjeSemestara;
    public TextField txtUkupnoEspb;
    public TabPane studProgramTabPane;

    private final ObservableList<VrstaStudijaResponseDTO> vrstaStudijaObList = FXCollections.observableArrayList();
    private final ObservableList<StudijskiProgramResponseDTO> studijskiProgramObList = FXCollections.observableArrayList();

    public StudijskiProgramController(VrstaStudijaService vrstaStudijaService, StudijskiProgramService studijskiProgramService, NavigationController navigationController, MainView mainView) {
        this.vrstaStudijaService = vrstaStudijaService;
        this.studijskiProgramService = studijskiProgramService;
        this.navigationController = navigationController;
        this.mainView = mainView;
    }

    public void initialize(){
        studijskiProgramTable.setItems(studijskiProgramObList);
        cmbVrstaStudija.setItems(vrstaStudijaObList);

        cmbVrstaStudija.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(VrstaStudijaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });
        cmbVrstaStudija.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(VrstaStudijaResponseDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNaziv());
            }
        });

        vrstaStudijaService.fetchVrstaStudija().collectList().subscribe(vrstaStudijaList -> Platform.runLater(() -> vrstaStudijaObList.setAll(vrstaStudijaList)), ErrorHandler::displayError);
        studijskiProgramService.fetchStudijskiProgram().collectList().subscribe(studijskiProgramList -> Platform.runLater(() -> studijskiProgramObList.setAll(studijskiProgramList)), ErrorHandler::displayError);

        studijskiProgramTable.setRowFactory(tv -> {
            TableRow<StudijskiProgramResponseDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.handleStudProgramClick(row.getItem());
                }
            });
            return row;
        });

        studProgramTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> navigationController.navigateTo("studijskiProgram:tab:" + newIdx));
    }

    private void handleStudProgramClick(StudijskiProgramResponseDTO item) {
        mainView.openModal("studijskiProgramDetails", "Studijski Program Details", (StudijskiProgramDetailsController controller) -> controller.setStudijskiProgram(item));
    }

    public void handleSaveProgram(ActionEvent actionEvent) {
        String oznaka = txtOznaka.getText();
        String naziv = txtNaziv.getText();
        String zvanje = txtZvanje.getText();
        String godinaAkreditacijeTxt = txtGodinaAkreditacije.getText();
        VrstaStudijaResponseDTO vrstaStudija = cmbVrstaStudija.getSelectionModel().getSelectedItem();
        String trajanjeGodinaTxt = txtTrajanjeGodina.getText();
        String trajanjeSemestaraTxt = txtTrajanjeSemestara.getText();
        String ukupnoEspbTxt = txtUkupnoEspb.getText();

        if (oznaka.isBlank() || naziv.isBlank() || zvanje.isBlank() || godinaAkreditacijeTxt.isBlank() || vrstaStudija == null || trajanjeGodinaTxt.isBlank() || trajanjeSemestaraTxt.isBlank() || ukupnoEspbTxt.isBlank()) {
            ErrorHandler.displayError(new IllegalStateException("Please fill all fields."));
            return;
        }

        try {
            Integer godinaAkreditacije = Integer.parseInt(godinaAkreditacijeTxt);
            Integer trajanjeGodina = Integer.parseInt(trajanjeGodinaTxt);
            Integer trajanjeSemestara = Integer.parseInt(trajanjeSemestaraTxt);
            Integer ukupnoEspb = Integer.parseInt(ukupnoEspbTxt);

            Button button = (Button) actionEvent.getSource();
            button.setDisable(true);

            studijskiProgramService.createStudijskiProgram(naziv, oznaka, zvanje, godinaAkreditacije, trajanjeGodina, trajanjeSemestara, ukupnoEspb, vrstaStudija.getId())
                    .doFinally(signalType -> Platform.runLater(() -> button.setDisable(false)))
                    .subscribe(studijskiProgramId -> {
                        StudijskiProgramResponseDTO studijskiProgram = new StudijskiProgramResponseDTO();
                        studijskiProgram.setId(studijskiProgramId);
                        studijskiProgram.setNaziv(naziv);
                        studijskiProgram.setOznaka(oznaka);
                        studijskiProgram.setZvanje(zvanje);
                        studijskiProgram.setGodinaAkreditacije(godinaAkreditacije);
                        studijskiProgram.setTrajanjeGodina(trajanjeGodina);
                        studijskiProgram.setTrajanjeSemestara(trajanjeSemestara);
                        studijskiProgram.setUkupnoEspb(ukupnoEspb);

                        Platform.runLater(() -> {
                            this.clearForm();
                            studijskiProgramObList.add(studijskiProgram);
                            studijskiProgramTable.refresh();
                        });
                    }, ErrorHandler::displayError);
        } catch (NumberFormatException e) {
            ErrorHandler.displayError(e);
        }
    }

    public void handleDeleteProgram() {
        StudijskiProgramResponseDTO selected = studijskiProgramTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Studijski Program deletion");
        confirm.setHeaderText("Please confirm deletion of studijski program:");
        confirm.setContentText(selected.getNaziv() + " [" + selected.getOznaka() + "]");

        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;

            studijskiProgramService.deleteStudijskiProgram(selected.getId()).subscribe(success -> Platform.runLater(() -> {
                studijskiProgramObList.remove(selected);
                studijskiProgramTable.refresh();
            }), ErrorHandler::displayError);
        });
    }

    private void clearForm() {
        txtOznaka.clear();
        txtNaziv.clear();
        txtZvanje.clear();
        txtGodinaAkreditacije.clear();
        cmbVrstaStudija.getSelectionModel().clearSelection();
        txtTrajanjeGodina.clear();
        txtTrajanjeSemestara.clear();
        txtUkupnoEspb.clear();
    }
}
