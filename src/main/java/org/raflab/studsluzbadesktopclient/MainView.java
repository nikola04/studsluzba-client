package org.raflab.studsluzbadesktopclient;

import java.io.IOException;
import java.util.Objects;

import org.raflab.studsluzbadesktopclient.controllers.NavigationController;
import org.raflab.studsluzbadesktopclient.utils.ErrorHandler;
import org.springframework.stereotype.Component;


import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
            BorderPane mainLayout = loader.load();
			this.scene = new Scene(mainLayout,1000,800);
			scene.getStylesheets().add(Objects.requireNonNull(MainView.class.getResource("/css/stylesheet.css")).toExternalForm());
		} catch (IOException e) {
			ErrorHandler.displayError(e);
		}
		return this.scene;
	}

	public void registerNavigationInputs(Scene scene, NavigationController navCtrl) {
		scene.setOnMouseClicked(event -> {
			System.out.println(event.getButton());
			if (event.getButton() == MouseButton.BACK) {
				navCtrl.goBack();
				event.consume();
			}else if (event.getButton() == MouseButton.FORWARD) {
				navCtrl.goForward();
				event.consume();
			}
		});

		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.isControlDown()) {
				if (event.getCode() == KeyCode.OPEN_BRACKET) {
					navCtrl.goBack();
					event.consume();
				} else if (event.getCode() == KeyCode.CLOSE_BRACKET) {
					navCtrl.goForward();
					event.consume();
				}
			}
		});

		scene.setOnSwipeLeft(e -> navCtrl.goForward());
		scene.setOnSwipeRight(e -> navCtrl.goBack());
	}

	public Node loadPane(String fxml) {
		FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/"+fxml+".fxml"));
		try {
			return loader.load();
		} catch (IOException e) {
			ErrorHandler.displayError(e);
			return null;
		}
	}

	public <T> void openModal(String fxml, String title, java.util.function.Consumer<T> controllerConsumer) {
		this.openModal(fxml, title, null, null, controllerConsumer);
	}

	public <T> void openModal(String fxml, String title, Integer width, Integer height, java.util.function.Consumer<T> controllerConsumer) {
		FXMLLoader loader = appFXMLLoader.getLoader("/fxml/" + fxml + ".fxml");
		try {
			Parent parent = loader.load();

			if (controllerConsumer != null) {
				T controller = loader.getController();
				controllerConsumer.accept(controller);
			}

			Scene scene = (width != null && height != null) ? new Scene(parent, width, height) : new Scene(parent);
			Stage stage = new Stage();
			if (title != null) stage.setTitle(title);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			ErrorHandler.displayError(e);
		}
	}
}
