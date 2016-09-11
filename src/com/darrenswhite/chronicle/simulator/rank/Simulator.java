package com.darrenswhite.chronicle.simulator.rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Darren White
 */
public class Simulator implements Runnable {

	private final Number[] predictions;
	private final PlayerRank startRank;
	private final int runs;
	private final Prediction prediction;
	private final AtomicInteger index = new AtomicInteger();
	private PlayerRank endRank;
	private double winRate;
	private int games;
	private int averageGames;
	private int averageRank;
	private double averageWinRate;

	public Simulator(PlayerRank startRank, PlayerRank endRank, double winRate, int games, int runs) {
		this.startRank = startRank;
		this.endRank = endRank;
		this.winRate = winRate;
		this.runs = runs;
		this.games = games;

		if (games == -1) {
			prediction = Prediction.TOTAL_GAMES;
		} else if (endRank == null) {
			prediction = Prediction.END_RANK;
		} else if (winRate == -1) {
			prediction = Prediction.WIN_RATE;
		} else {
			throw new IllegalArgumentException("We have nothing to predict!");
		}

		predictions = new Number[runs];
	}

	public long getAverageGames() {
		return averageGames;
	}

	public int getAverageRank() {
		return averageRank;
	}

	public double getAverageWinRate() {
		return averageWinRate;
	}

	public String getCSVOutput() {
		StringBuilder sb = new StringBuilder();

		switch (prediction) {
			case TOTAL_GAMES:
				sb.append("Result,# of Games").append('\n');
				sb.append("Average,").append(getAverageGames()).append('\n');
				sb.append("Minimum,").append(getPercentileGames(0.00)).append('\n');
				sb.append("Maximum,").append(getPercentileGames(1.00)).append('\n');
				sb.append("10%,").append(getPercentileGames(0.10)).append('\n');
				sb.append("25%,").append(getPercentileGames(0.25)).append('\n');
				sb.append("50%,").append(getPercentileGames(0.50)).append('\n');
				sb.append("75%,").append(getPercentileGames(0.75)).append('\n');
				sb.append("90%,").append(getPercentileGames(0.90)).append('\n');
				break;
			case END_RANK:
				sb.append("Result,End Rank").append('\n');
				sb.append("Minimum,").append(getPercentileRank(0.00)).append('\n');
				sb.append("Maximum,").append(getPercentileRank(1.00)).append('\n');
				sb.append("10%,").append(getPercentileRank(0.10)).append('\n');
				sb.append("25%,").append(getPercentileRank(0.25)).append('\n');
				sb.append("50%,").append(getPercentileRank(0.50)).append('\n');
				sb.append("75%,").append(getPercentileRank(0.75)).append('\n');
				sb.append("90%,").append(getPercentileRank(0.90)).append('\n');
				break;
			case WIN_RATE:
				sb.append("Result,Win Rate %").append('\n');
				sb.append("Average,").append(getAverageWinRate()).append('\n');
				sb.append("Minimum,").append(getPercentileWinRate(0.00)).append('\n');
				sb.append("Maximum,").append(getPercentileWinRate(1.00)).append('\n');
				sb.append("10%,").append(getPercentileWinRate(0.10)).append('\n');
				sb.append("25%,").append(getPercentileWinRate(0.25)).append('\n');
				sb.append("50%,").append(getPercentileWinRate(0.50)).append('\n');
				sb.append("75%,").append(getPercentileWinRate(0.75)).append('\n');
				sb.append("90%,").append(getPercentileWinRate(0.90)).append('\n');
				break;
		}

		return sb.toString();
	}

	public String getOutput() {
		StringBuilder sb = new StringBuilder();

		switch (prediction) {
			case TOTAL_GAMES:
				sb.append("Average: ").append(getAverageGames()).append('\n');
				sb.append("Minimum: ").append(getPercentileGames(0.00)).append('\n');
				sb.append("Maximum: ").append(getPercentileGames(1.00)).append('\n');
				sb.append("10%: ").append(getPercentileGames(0.10)).append('\n');
				sb.append("25%: ").append(getPercentileGames(0.25)).append('\n');
				sb.append("50%: ").append(getPercentileGames(0.50)).append('\n');
				sb.append("75%: ").append(getPercentileGames(0.75)).append('\n');
				sb.append("90%: ").append(getPercentileGames(0.90)).append('\n');
				break;
			case END_RANK:
				sb.append("Minimum: ").append(getPercentileRank(0.00)).append('\n');
				sb.append("Maximum: ").append(getPercentileRank(1.00)).append('\n');
				sb.append("10%: ").append(getPercentileRank(0.90)).append('\n');
				sb.append("25%: ").append(getPercentileRank(0.75)).append('\n');
				sb.append("50%: ").append(getPercentileRank(0.50)).append('\n');
				sb.append("75%: ").append(getPercentileRank(0.25)).append('\n');
				sb.append("90%: ").append(getPercentileRank(0.10)).append('\n');
				break;
			case WIN_RATE:
				sb.append("Average: ").append(getAverageWinRate()).append('%').append('\n');
				sb.append("Minimum: ").append(getPercentileWinRate(0.00)).append('%').append('\n');
				sb.append("Maximum: ").append(getPercentileWinRate(1.00)).append('%').append('\n');
				sb.append("10%: ").append(getPercentileWinRate(0.10)).append('%').append('\n');
				sb.append("25%: ").append(getPercentileWinRate(0.25)).append('%').append('\n');
				sb.append("50%: ").append(getPercentileWinRate(0.50)).append('%').append('\n');
				sb.append("75%: ").append(getPercentileWinRate(0.75)).append('%').append('\n');
				sb.append("90%: ").append(getPercentileWinRate(0.90)).append('%').append('\n');
				break;
		}

		return sb.toString();
	}

	public Number getPercentile(double percent) {
		int i, n = predictions.length - 1;
		long sum, total;

		Arrays.parallelSort(predictions, (p, q) -> Long.compare(p.longValue(), q.longValue()));

		for (i = 0, total = 0; i < n; i++) {
			total += predictions[i].longValue();
		}

		for (i = 0, sum = 0; i < n && sum < percent * total; i++) {
			sum += predictions[i].longValue();
		}

		return predictions[i];
	}

	public int getPercentileGames(double percent) {
		return (int) getPercentile(percent);
	}

	public PlayerRank getPercentileRank(double percent) {
		return new PlayerRank(new ArrayList<>(), (int) getPercentile(percent), 0, 0, 0);
	}

	public double getPercentileWinRate(double percent) {
		return (double) getPercentile(percent);
	}

	@Override
	public void run() {
		ExecutorService executor = Executors.newWorkStealingPool();
		AtomicInteger totalGames = new AtomicInteger();
		AtomicInteger totalRank = new AtomicInteger();
		AtomicReference<Double> totalWinRate = new AtomicReference<>(0D);
		ReentrantLock lock = new ReentrantLock();

		for (int i = 0; i < runs; i++) {
			Simulation s = new Simulation(startRank, endRank, winRate, games);

			executor.execute(() -> {
				s.run();

				totalGames.accumulateAndGet(s.getTotalGames(), (l, r) -> l + r);
				totalRank.accumulateAndGet(s.getCurrentRank().getAbsoluteRating(), (l, r) -> l + r);
				totalWinRate.accumulateAndGet(s.getWinRate(), (l, r) -> l + r);

				switch (prediction) {
					case TOTAL_GAMES:
						predictions[index.getAndIncrement()] = s.getTotalGames();
						break;
					case END_RANK:
						predictions[index.getAndIncrement()] = s.getCurrentRank().getAbsoluteRating();
						break;
					case WIN_RATE:
						predictions[index.getAndIncrement()] = s.getWinRate() * 100D;
						break;
				}
			});
		}

		executor.shutdown();

		while (!executor.isTerminated()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ignored) {
			}
		}

		averageGames = totalGames.get() / runs;
		averageRank = totalRank.get() / runs;
		averageWinRate = (totalWinRate.get() / runs) * 100D;
	}

	private enum Prediction {
		TOTAL_GAMES, END_RANK, WIN_RATE
	}
}