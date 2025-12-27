package org.raflab.studsluzbadesktopclient.controllers;

import org.springframework.stereotype.Component;

@Component
public class MenuBarController {
	private final NavigationController navController;

	public MenuBarController(NavigationController navController){
		this.navController = navController;
	}

	public void openNewNastavnik() {
		navController.navigateTo("newNastavnik");
	}
	public void openSearchStudent() {
		navController.navigateTo("searchStudent");
	}
	public void openSearchNastavnik() {
		navController.navigateTo("searchNastavnik");
	}
	public void openNewStudent() {
		navController.navigateTo("newStudent");
	}
	public void openSifarnik() {
		navController.navigateTo("sifarnik");
	}
	public void openSkolskaGodina() {
		navController.navigateTo("skolskaGodina");
	}
}