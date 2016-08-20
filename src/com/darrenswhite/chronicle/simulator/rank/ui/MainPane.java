package com.darrenswhite.chronicle.simulator.rank.ui;

import com.darrenswhite.chronicle.simulator.rank.Rank;
import com.darrenswhite.chronicle.simulator.rank.Simulation;
import com.darrenswhite.chronicle.simulator.rank.Simulator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * @author Darren White
 */
public class MainPane extends GridPane {

	public MainPane() {
		init();
	}

	private String getStats(String name, Simulation s) {
		return String.format("| %-10s | %-5s | %-5s | %-5s |\n", name, s.getTotalGames(), s.getWins(), s.getLosses());
	}

	private void init() {
		setPadding(new Insets(10, 10, 10, 10));
		setHgap(10);
		setVgap(10);

		Label lblStartRank = new Label("Start Rank");
		ComboBox<Rank.League> startLeague = new ComboBox<>(FXCollections.observableArrayList(Rank.League.values()).filtered(l -> l != Rank.League.NONE));
		startLeague.setValue(Rank.League.BRONZE);
		TextField startPosition = new TextField("10");
		Label lblEndRank = new Label("Desired Rank");
		ComboBox<Rank.League> endLeague = new ComboBox<>(FXCollections.observableArrayList(Rank.League.values()));
		TextField endPosition = new TextField();
		Label lblWinRate = new Label("Win Rate %");
		TextField txtWinRate = new TextField();
		Label lblGames = new Label("# Games");
		TextField txtGames = new TextField();
		Label lblRuns = new Label("# Simulations");
		TextField txtRuns = new TextField("100");
		Button btnRun = new Button("Simulate");
		TextArea results = new TextArea();

		results.setEditable(false);
		btnRun.setOnAction(e -> new Thread(() -> {
			btnRun.setDisable(true);

			Rank startRank = new Rank(startLeague.getValue(), Integer.parseInt(startPosition.getText()));
			Rank endRank = endLeague.getValue() == Rank.League.NONE || endLeague.getValue() == null ? null : new Rank(endLeague.getValue(), endPosition.getText().trim().isEmpty() ? -1 : Integer.parseInt(endPosition.getText()));
			double winRate = txtWinRate.getText().trim().isEmpty() ? -1 : (Double.parseDouble(txtWinRate.getText()) / 100D);
			long games = txtGames.getText().trim().isEmpty() ? -1 : Long.parseLong(txtGames.getText());
			int runs = Integer.parseInt(txtRuns.getText());

			results.clear();

			Simulator sim = new Simulator(startRank, endRank, winRate, games, runs);

			sim.run();

			results.setText(sim.getOutput());

			btnRun.setDisable(false);
		}).start());
		btnRun.setDefaultButton(true);
		btnRun.setMaxWidth(Double.MAX_VALUE);

		int row = 0;

		add(lblStartRank, 0, row);
		add(startLeague, 1, row);
		add(startPosition, 2, row++);
		add(lblEndRank, 0, row);
		add(endLeague, 1, row);
		add(endPosition, 2, row++);
		add(lblWinRate, 0, row);
		add(txtWinRate, 1, row++, 2, 1);
		add(lblGames, 0, row);
		add(txtGames, 1, row++, 2, 1);
		add(lblRuns, 0, row);
		add(txtRuns, 1, row++, 2, 1);
		add(btnRun, 0, row++, 3, 1);
		add(results, 3, 0, 1, row);
	}
}