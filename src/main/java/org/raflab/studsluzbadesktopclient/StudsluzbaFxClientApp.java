package org.raflab.studsluzbadesktopclient;

import ch.qos.logback.classic.joran.PropertiesConfigurator;
import org.raflab.studsluzbadesktopclient.utils.SpringContextHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
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
    public void init() {
        SpringApplication app = new SpringApplication(StudsluzbaFxClientApp.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        springContext = app.run();
        SpringContextHelper.setContext(springContext);
    }

    @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("RAF Studentska služba");
    	MainView mainView = springContext.getBean(MainView.class);
    	primaryStage.setScene(mainView.createScene());
    	primaryStage.show();
    }
    
    @Override
    public void stop() {
    	springContext.close();
    	Platform.exit();
    }
}