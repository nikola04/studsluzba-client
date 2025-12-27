package org.raflab.studsluzbadesktopclient.controllers;

import javafx.scene.Node;
import org.springframework.stereotype.Component;


import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

@Component
public class MainWindowController {
	@FXML
	private BorderPane mainPane;

	public void setView(Node node) {
		mainPane.setCenter(node);
	}
}