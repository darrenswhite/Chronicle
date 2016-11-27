package com.darrenswhite.chronicle.simulator.rank;

import com.darrenswhite.chronicle.simulator.rank.ui.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Darren White
 */
public class RankSimulator extends Application {

	public static final String APP_NAME = "Ramus' Chronicle Simulator";

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainPane pane = new MainPane();
		Scene scene = new Scene(pane);

		primaryStage.setResizable(false);
		primaryStage.setTitle(APP_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}