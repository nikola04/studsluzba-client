package org.raflab.studsluzbadesktopclient.controllers;

import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;

@Component
public class MenuBarController { 

	final MainView mainView;
	final StudentService studentService;

    public MenuBarController(StudentService studentService, MainView mainView){
        this.studentService = studentService;
        this.mainView = mainView;
    }

	public void openSearchStudent() {
		mainView.changeRoot("searchStudent");
	}

	public void openNewStudent() {
		mainView.changeRoot("newStudent");
	}
	public void openSifarnik() {
		mainView.changeRoot("sifarnik");
	}
	public void openSkolskaGodina() {
		mainView.changeRoot("skolskaGodina");
	}

	@FXML
    public void initialize() {		

    }
}