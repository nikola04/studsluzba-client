package org.raflab.studsluzbadesktopclient.controllers;

import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

@Component
public class MenuBarController { 

	final MainView mainView;
	final StudentService studentService;

	@FXML
	private MenuBar menuBar;

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
	public void openAdminSifarnici() {
		mainView.changeRoot("adminSifarnici");
	}
	public void openAdminSkolskeGodine() {
		mainView.changeRoot("adminSkolskeGodine");
	}

	@FXML
    public void initialize() {		

    }
}