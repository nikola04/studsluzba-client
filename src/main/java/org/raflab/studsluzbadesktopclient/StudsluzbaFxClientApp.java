package org.raflab.studsluzbadesktopclient;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
@SpringBootApplication
public class StudsluzbaFxClientApp extends Application {
	
	protected ConfigurableApplicationContext springContext;   

    public static void main(String[] args) {
    	launch(StudsluzbaFxClientApp.class);
    }

    @Override
    public void init() throws Exception {	   
	    springContext = SpringApplication.run(StudsluzbaFxClientApp.class);
	}   

    @Override
    public void start(Stage primaryStage) throws IOException {
    	primaryStage.setTitle("RAF Studentska služba");
    	MainView mainView = springContext.getBean(MainView.class);
    	primaryStage.setScene(mainView.createScene());
    	primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
    	springContext.close();
    	Platform.exit();
    }
}