package org.raflab.studsluzbadesktopclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.coder.CoderType;
import org.raflab.studsluzbadesktopclient.coder.SimpleCode;
import org.springframework.stereotype.Component;

@Component
public class AdminSifrarniciController {

    private final CoderFactory coderFactory;

    // ===== UI KOMPONENTE =====

    @FXML
    private ComboBox<CoderType> sifarnikCb;

    @FXML
    private TableView<SimpleCode> sifarnikTable;

    @FXML
    private TableColumn<SimpleCode, String> codeCol;

    @FXML
    private TextField newCodeTf;

    @FXML
    private Button addBtn;

    @FXML
    private Button deleteBtn;

    // ===== KONSTRUKTOR (Spring DI) =====

    public AdminSifrarniciController(CoderFactory coderFactory) {
        this.coderFactory = coderFactory;
    }

    // ===== INIT =====

    @FXML
    public void initialize() {

    }

    // ===== AKCIJE =====

    @FXML
    public void handleLoadSifarnik(ActionEvent e) {
        CoderType selected = sifarnikCb.getValue();
        if (selected == null) return;

        sifarnikTable.setItems(
                FXCollections.observableArrayList(
                        coderFactory.getSimpleCoder(selected).getCodes()
                )
        );
    }

    @FXML
    public void handleAdd(ActionEvent e) {
        CoderType selected = sifarnikCb.getValue();
        String value = newCodeTf.getText();

        if (selected == null || value == null || value.isBlank()) {
            return;
        }

        SimpleCode code = new SimpleCode(value);
        //coderFactory.getSimpleCoder(selected).addCode(code);

        sifarnikTable.getItems().add(code);
        newCodeTf.clear();
    }

    @FXML
    public void handleDelete(ActionEvent e) {
        SimpleCode selectedCode = sifarnikTable.getSelectionModel().getSelectedItem();
        CoderType selectedType = sifarnikCb.getValue();

        if (selectedCode == null || selectedType == null) return;

        //coderFactory.getSimpleCoder(selectedType).removeCode(selectedCode);
        sifarnikTable.getItems().remove(selectedCode);
    }
    @FXML
    public void handleEdit(ActionEvent actionEvent) {
    }
}

