package org.raflab.studsluzbadesktopclient;

import org.raflab.studsluzbadesktopclient.controllers.NavigationController;
import org.raflab.studsluzbadesktopclient.utils.SpringContextHelper;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * JavaFX App
 */
@SpringBootApplication
public class StudsluzbaFxClientApp extends Application {

    protected ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        System.setProperty("glass.accessible.forceless", "true");
        System.setProperty("glass.accessible.force", "false");
        Application.launch(StudsluzbaFxClientApp.class, args);
    }

    @Override
    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(StudsluzbaFxClientApp.class);
        builder.headless(false);
        builder.web(WebApplicationType.NONE);
        springContext = builder.run();

        SpringContextHelper.setContext(springContext);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("RAF Studentska služba");

            MainView mainView = springContext.getBean(MainView.class);
            NavigationController navCtrl = springContext.getBean(NavigationController.class);

            Scene scene = mainView.createScene();

            mainView.registerNavigationInputs(scene, navCtrl);

            primaryStage.setScene(scene);

            navCtrl.navigateTo("searchStudent");

            primaryStage.show();
            primaryStage.toFront();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
        System.exit(0);
    }
}