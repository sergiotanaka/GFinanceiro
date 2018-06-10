package org.pinguin.pomodoro.application;

import org.pinguin.pomodoro.gui.main.MainPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PomodoroApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Pomodoro Timer");
		primaryStage.setScene(new Scene(new MainPane()));
		primaryStage.sizeToScene();
		Platform.runLater(primaryStage::centerOnScreen);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
