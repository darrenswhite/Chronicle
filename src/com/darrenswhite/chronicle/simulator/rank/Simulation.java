package com.darrenswhite.chronicle.simulator.rank;

import java.util.Random;

/**
 * @author Darren White
 */
public class Simulation implements Runnable {

	private final Random rnd = new Random();

	private Rank startRank;
	private Rank currRank;
	private Rank endRank;
	private double winRate;
	private long games;

	private long total = 0;
	private long wins = 0;
	private long losses = 0;

	public Simulation(Rank startRank, Rank endRank, double winRate, long games) {
		this.startRank = startRank;
		this.endRank = endRank;
		this.winRate = winRate;
		this.games = games;
	}

	public Rank getCurrentRank() {
		return currRank;
	}

	public long getLosses() {
		return losses;
	}

	public long getTotalGames() {
		return total;
	}

	public double getWinRate() {
		return (double) getWins() / (double) getTotalGames();
	}

	public long getWins() {
		return wins;
	}

	@Override
	public void run() {
		long streak = 0;

		if (winRate == -1) {
			winRate = rnd.nextDouble();
		}

		currRank = startRank.copy();

		while (shouldRun()) {
			boolean win = rnd.nextDouble() <= winRate;

			total++;

			if (win) {
				wins++;
				streak++;
				currRank.promote(streak);
			} else {
				losses++;
				streak = 0;
				currRank.demote();
				continue;
			}

			if (currRank.getPosition() == 1 && streak >= 3) {
				currRank.promoteLeague();
			}
		}
	}

	public void setCurrentRank(Rank currRank) {
		this.currRank = currRank;
	}

	private void reset() {
		currRank = startRank.copy();
		winRate = rnd.nextDouble();
		total = 0;
		wins = 0;
		losses = 0;
	}

	private boolean shouldRun() {
		if (games != -1) {
			if (total < games) {
				if (endRank != null && currRank.compareTo(endRank) >= 0) {
					reset();
				}

				return true;
			}

			if (endRank != null && currRank.compareTo(endRank) < 0) {
				reset();
				return true;
			}

			return false;
		}

		return endRank != null && currRank.compareTo(endRank) < 0;
	}

	public void setLosses(long losses) {
		this.losses = losses;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public void setWins(long wins) {
		this.wins = wins;
	}
}