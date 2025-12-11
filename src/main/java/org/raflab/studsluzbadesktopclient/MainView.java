package org.raflab.studsluzbadesktopclient;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class MainView {

	private final ContextFXMLLoader appFXMLLoader;
	private Scene scene;

    public MainView(ContextFXMLLoader appFXMLLoader) {
        this.appFXMLLoader = appFXMLLoader;
	}

    public Scene createScene() {
	  try {		  
		  FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/main.fxml"));
		  BorderPane borderPane = loader.load();
		  this.scene = new Scene(borderPane,1000,800);
		  scene.getStylesheets().add(Objects.requireNonNull(MainView.class.getResource("/css/stylesheet.css")).toExternalForm());
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  return this.scene;
	 }
	
	public void changeRoot(String fxml) {
		FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/"+fxml+".fxml"));
		try {
			scene.setRoot(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Node loadPane(String fxml) {
		FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/"+fxml+".fxml"));
		try {
			return loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void openModal(String fxml) {
		FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/"+fxml+".fxml"));
		try {
			Parent parent = loader.load();
			Scene scene = new Scene(parent, 400, 300);
	        Stage stage = new Stage();
	        stage.initModality(Modality.APPLICATION_MODAL);
	        
	        stage.setScene(scene);
	        stage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openModal(String fxml, String title) {
		FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/"+fxml+".fxml"));
		try {
			Parent parent = loader.load();
			Scene scene = new Scene(parent, 400, 300);
	        Stage stage = new Stage();
	        stage.setTitle(title);
	        stage.initModality(Modality.APPLICATION_MODAL);
	        
	        stage.setScene(scene);
	        stage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openModal(String fxml, String title,  int weight, int height) {
		FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/"+fxml+".fxml"));
		try {
			Parent parent = loader.load();
			Scene scene = new Scene(parent, weight, height);
	        Stage stage = new Stage();
	        stage.setTitle(title);
	        stage.initModality(Modality.APPLICATION_MODAL);
	        
	        stage.setScene(scene);
	        stage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
