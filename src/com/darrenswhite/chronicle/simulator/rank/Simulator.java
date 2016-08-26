package com.darrenswhite.chronicle.simulator.rank;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author Darren White
 */
public class Simulator implements Runnable {

	private final Rank startRank;
	private final int runs;

	private Rank endRank;
	private double winRate;
	private int games;

	private Simulation[] simulations;

	private Path tmp;

	private int averageGames;
	private double averageWinRate;

	public Simulator(Rank startRank, Rank endRank, double winRate, int games, int runs) {
		this.startRank = startRank;
		this.endRank = endRank;
		this.winRate = winRate;
		this.runs = runs;
		this.games = games;
	}

	private void checkSimulations() {
		if (simulations == null) {
			throw new IllegalStateException("Must run simulator first!");
		}
	}

	public long getAverageGames() {
		return averageGames;
	}

	public double getAverageWinRate() {
		return averageWinRate;
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

	public int getPercentileGames(double percent) {
		return getPercentile(percent, (s1, s2) -> Long.compare(s1.getWins(), s2.getWins()), Simulation::getTotalGames);
	}

	public Rank getPercentileRank(double percent) {
		return getPercentile(percent, (s1, s2) -> (s2.getCurrentRank().compareTo(s1.getCurrentRank())), Simulation::getCurrentRank);
	}

	public double getPercentileWinRate(double percent) {
		return getPercentile(percent, (s1, s2) -> (Double.compare(s1.getWinRate(), s2.getWinRate())), s -> s.getWinRate() * 100D);
	}

	@Override
	public void run() {
		ExecutorService executor = Executors.newCachedThreadPool();
		AtomicInteger totalGames = new AtomicInteger();
		AtomicReference<Double> totalWinRate = new AtomicReference<>(0D);

		simulations = new Simulation[runs];

		for (int i = 0; i < runs; i++) {
			Simulation s = new Simulation(startRank, endRank, winRate, games);

			executor.execute(() -> {
				s.run();
				totalGames.accumulateAndGet(s.getTotalGames(), (l, r) -> l + r);
				totalWinRate.accumulateAndGet(s.getWinRate(), (l, r) -> l + r);
			});

			simulations[i] = s;
		}

		executor.shutdown();

		while (!executor.isTerminated()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ignored) {
			}
		}

		averageGames = totalGames.get() / runs;
		averageWinRate = (totalWinRate.get() / runs) * 100D;
	}
}