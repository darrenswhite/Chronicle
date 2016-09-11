package com.darrenswhite.chronicle.simulator.rank;

import java.util.Random;

/**
 * @author Darren White
 */
public class Simulation implements Runnable {

	private static final double OPPONENT_RISING_STAR = 0.1;
	private final Random rnd = new Random();

	private PlayerRank startRank;
	private PlayerRank currRank;
	private PlayerRank endRank;
	private double winRate;
	private int games;

	private int total = 0;
	private int wins = 0;
	private int losses = 0;

	public Simulation(PlayerRank startRank, PlayerRank endRank, double winRate, int games) {
		this.startRank = startRank;
		this.endRank = endRank;
		this.winRate = winRate;
		this.games = games;
	}

	public PlayerRank getCurrentRank() {
		return currRank;
	}

	public int getLosses() {
		return losses;
	}

	public int getRandomELO() {
		return rnd.nextInt(100);
	}

	public int getTotalGames() {
		return total;
	}

	public double getWinRate() {
		return winRate;
	}

	public int getWins() {
		return wins;
	}

	private void reset() {
		currRank = startRank.createClone();
		winRate = rnd.nextDouble();
		total = 0;
		wins = 0;
		losses = 0;
	}

	@Override
	public void run() {
		if (winRate == -1) {
			winRate = rnd.nextDouble();
		}

		currRank = startRank.createClone();

		while (shouldRun()) {
			MatchOutcome outcome = rnd.nextDouble() <= winRate ? MatchOutcome.WIN : MatchOutcome.LOSS;
			boolean risingStar = rnd.nextDouble() <= OPPONENT_RISING_STAR;

			currRank = currRank.newRankFromMatchResult(outcome, risingStar, getRandomELO());
			total++;

			switch (outcome) {
				case WIN:
					wins++;
					break;
				case LOSS:
					losses++;
					break;
			}
		}
	}

	public void setCurrentRank(PlayerRank currRank) {
		this.currRank = currRank;
	}

	private boolean shouldRun() {
		if (games != -1) {
			if (total < games) {
				if (endRank != null && currRank.getAbsoluteRating() >= endRank.getAbsoluteRating()) {
					reset();
				}

				return true;
			}

			if (endRank != null && currRank.getAbsoluteRating() < endRank.getAbsoluteRating()) {
				reset();
				return true;
			}

			return false;
		}

		return endRank != null && currRank.getAbsoluteRating() < endRank.getAbsoluteRating();
	}
}