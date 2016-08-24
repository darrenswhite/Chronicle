package com.darrenswhite.chronicle.simulator.rank;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * @author Darren White
 */
public class Simulator implements Runnable {

	private final Rank startRank;
	private final int runs;

	private Rank endRank;
	private double winRate;
	private long games;

	private Simulation[] simulations;

	public Simulator(Rank startRank, Rank endRank, double winRate, long games, int runs) {
		this.startRank = startRank;
		this.endRank = endRank;
		this.winRate = winRate;
		this.runs = runs;
		this.games = games;
	}

	private long calculateTotal(Function<Simulation, Long> f) {
		checkSimulations();

		long val = 0;

		for (Simulation sim : simulations) {
			val += f.apply(sim);
		}

		return val;
	}

	private void checkSimulations() {
		if (simulations == null) {
			throw new IllegalStateException("Must run simulator first!");
		}
	}

	public long getAverageGames() {
		return getTotalGames() / simulations.length;
	}

	public double getAverageWinRate() {
		return getTotalWinRate() / simulations.length;
	}

	public <T extends Number> T getPercentile(double percent, Comparator<Simulation> cmp, Function<Simulation, T> f) {
		checkSimulations();

		Arrays.sort(simulations, cmp);

		int i, n = simulations.length - 1;
		long sum, total;

		for (i = 0, total = 0; i < n; i++) {
			total += f.apply(simulations[i]).longValue();
		}

		for (i = 0, sum = 0; i < n && sum < percent * total; i++) {
			sum += f.apply(simulations[i]).longValue();
		}

		return f.apply(simulations[i]);
	}

	public long getPercentileGames(double percent) {
		return getPercentile(percent, (s1, s2) -> Long.compare(s1.getWins(), s2.getWins()), Simulation::getTotalGames);
	}

	public Rank getPercentileRank(double percent) {
		return getPercentile(percent, (s1, s2) -> (s2.getCurrentRank().compareTo(s1.getCurrentRank())), Simulation::getCurrentRank);
	}

	public double getPercentileWinRate(double percent) {
		return getPercentile(percent, (s1, s2) -> (Double.compare(s1.getWinRate(), s2.getWinRate())), s -> s.getWinRate() * 100D);
	}

	public long getTotalGames() {
		return calculateTotal(Simulation::getTotalGames);
	}

	public long getTotalLosses() {
		return calculateTotal(Simulation::getLosses);
	}

	public long getTotalRank() {
		return calculateTotal((s) -> (long) s.getCurrentRank().hashCode());
	}

	private long getTotalWinRate() {
		return calculateTotal((s) -> (long) (s.getWinRate() * 100D));
	}

	public long getTotalWins() {
		return calculateTotal(Simulation::getWins);
	}

	public String getOutput() {
		StringBuilder sb = new StringBuilder();

		if (games == -1) {
			sb.append("Average: ").append(getAverageGames()).append('\n');
			sb.append("Minimum: ").append(getPercentileGames(0.00)).append('\n');
			sb.append("Maximum: ").append(getPercentileGames(1.00)).append('\n');
			sb.append("10%: ").append(getPercentileGames(0.10)).append('\n');
			sb.append("25%: ").append(getPercentileGames(0.25)).append('\n');
			sb.append("50%: ").append(getPercentileGames(0.50)).append('\n');
			sb.append("75%: ").append(getPercentileGames(0.75)).append('\n');
			sb.append("90%: ").append(getPercentileGames(0.90)).append('\n');
		} else if (endRank == null) {
			sb.append("Minimum: ").append(getPercentileRank(1.00)).append('\n');
			sb.append("Maximum: ").append(getPercentileRank(0.00)).append('\n');
			sb.append("10%: ").append(getPercentileRank(0.10)).append('\n');
			sb.append("25%: ").append(getPercentileRank(0.25)).append('\n');
			sb.append("50%: ").append(getPercentileRank(0.50)).append('\n');
			sb.append("75%: ").append(getPercentileRank(0.75)).append('\n');
			sb.append("90%: ").append(getPercentileRank(0.90)).append('\n');
		} else if (winRate == -1) {
			sb.append("Average: ").append(getAverageWinRate()).append('\n');
			sb.append("Minimum: ").append(getPercentileWinRate(0.00)).append('\n');
			sb.append("Maximum: ").append(getPercentileWinRate(1.00)).append('\n');
			sb.append("10%: ").append(getPercentileWinRate(0.10)).append('\n');
			sb.append("25%: ").append(getPercentileWinRate(0.25)).append('\n');
			sb.append("50%: ").append(getPercentileWinRate(0.50)).append('\n');
			sb.append("75%: ").append(getPercentileWinRate(0.75)).append('\n');
			sb.append("90%: ").append(getPercentileWinRate(0.90)).append('\n');
		}

		return sb.toString();
	}

	public String getCSVOutput() {
		StringBuilder sb = new StringBuilder();

		if (games == -1) {
			sb.append("Result,# of Games").append('\n');
			sb.append("Average,").append(getAverageGames()).append('\n');
			sb.append("Minimum,").append(getPercentileGames(0.00)).append('\n');
			sb.append("Maximum,").append(getPercentileGames(1.00)).append('\n');
			sb.append("10%,").append(getPercentileGames(0.10)).append('\n');
			sb.append("25%,").append(getPercentileGames(0.25)).append('\n');
			sb.append("50%,").append(getPercentileGames(0.50)).append('\n');
			sb.append("75%,").append(getPercentileGames(0.75)).append('\n');
			sb.append("90%,").append(getPercentileGames(0.90)).append('\n');
		} else if (endRank == null) {
			sb.append("Result,End Rank").append('\n');
			sb.append("Minimum,").append(getPercentileRank(0.00)).append('\n');
			sb.append("Maximum,").append(getPercentileRank(1.00)).append('\n');
			sb.append("10%,").append(getPercentileRank(0.10)).append('\n');
			sb.append("25%,").append(getPercentileRank(0.25)).append('\n');
			sb.append("50%,").append(getPercentileRank(0.50)).append('\n');
			sb.append("75%,").append(getPercentileRank(0.75)).append('\n');
			sb.append("90%,").append(getPercentileRank(0.90)).append('\n');
		} else if (winRate == -1) {
			sb.append("Result,Win Rate %").append('\n');
			sb.append("Average,").append(getAverageWinRate()).append('\n');
			sb.append("Minimum,").append(getPercentileWinRate(0.00)).append('\n');
			sb.append("Maximum,").append(getPercentileWinRate(1.00)).append('\n');
			sb.append("10%,").append(getPercentileWinRate(0.10)).append('\n');
			sb.append("25%,").append(getPercentileWinRate(0.25)).append('\n');
			sb.append("50%,").append(getPercentileWinRate(0.50)).append('\n');
			sb.append("75%,").append(getPercentileWinRate(0.75)).append('\n');
			sb.append("90%,").append(getPercentileWinRate(0.90)).append('\n');
		}

		return sb.toString();
	}

	@Override
	public void run() {
		ExecutorService executor = Executors.newCachedThreadPool();

		simulations = new Simulation[runs];

		for (int i = 0; i < runs; i++) {
			Simulation s = new Simulation(startRank, endRank, winRate, games);

			executor.execute(s);

			simulations[i] = s;
		}

		executor.shutdown();
	}
}