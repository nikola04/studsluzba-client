package org.raflab.studsluzbadesktopclient.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

@Component
public class MainWindowController {
	
    private ApplicationContext context;
	
	@FXML
	private BorderPane mainPane;
}