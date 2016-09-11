package com.darrenswhite.chronicle.simulator.rank;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Darren White
 */
public class PlayerRank implements Cloneable {

	public static final int BRONZE_PROMOTION_WINS = 2;
	public static final int SILVER_PROMOTION_WINS = 3;
	public static final int GOLD_PROMOTION_WINS = 3;
	public static final int PLATINUM_PROMOTION_WINS = 3;
	public static final int K_VALUE = 20;
	public final RankedBracket bracket;
	public final int bracketPoints;
	public final List<Boolean> matchHistory;
	public final int elo;
	public final int promotionWins;
	public final int winStreak;

	public PlayerRank(List<Boolean> matchHistory, int absoluteRating, int elo, int promotionWins, int winStreak) {
		this.matchHistory = matchHistory;
		this.bracket = RankedBracket.FromAbsoluteRating(absoluteRating);
		this.bracketPoints = absoluteRating - bracket.min;
		this.elo = elo;
		this.promotionWins = promotionWins;
		this.winStreak = winStreak;
	}

	public PlayerRank(RankedBracket bracket, int bracketPoints) {
		matchHistory = new ArrayList<>();
		this.bracket = bracket;
		this.bracketPoints = (bracket.max - bracket.min) - bracketPoints;
		elo = 0;
		promotionWins = 0;
		winStreak = 0;
	}

	public int clientBracketPoints() {
		return RankedBracket.ClientBracketPoints(bracketPoints + bracket.min);
	}

	public PlayerRank createClone() {
		try {
			return (PlayerRank) clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public int getAbsoluteRating() {
		return bracket.min + bracketPoints;
	}

	public static int getNewELO(int myRating, int opponentRating, MatchOutcome outcome, int kValue) {
		double outcomeValue = 0.0;

		switch (outcome) {
			case WIN:
				outcomeValue = 1.0;
				break;
			case LOSS:
				outcomeValue = 0.0;
				break;
			case DRAW:
				outcomeValue = 0.5;
				break;
		}

		double calc = 1.0 / (1.0 + Math.pow(10.0, ((double) opponentRating - (double) myRating) / 400.0));
		myRating = (int) ((double) myRating + (double) kValue * (outcomeValue - calc));
		return myRating;
	}

	public boolean isRisingStar() {
		return winStreak >= 3;
	}

	public PlayerRank newRankFromMatchResult(MatchOutcome outcome, boolean opponentRisingStar, int opponentElo) {
		int len = matchHistory.size();
		List<Boolean> history = new ArrayList<>(matchHistory).subList(Math.max(0, len - 10), len);

		history.add(outcome == MatchOutcome.WIN);

		int elo = this.elo;
		int absoluteRating = bracketPoints + bracket.min;
		int promotionWins = this.promotionWins;

		if (bracket == RankedBracket.PLATINUM || bracket == RankedBracket.DIAMOND) {
			elo = getNewELO(elo, opponentElo, outcome, 20);
		}

		if (outcome != MatchOutcome.WIN) {
			if (outcome != MatchOutcome.LOSS) {
				return new PlayerRank(history, absoluteRating, elo, promotionWins, winStreak);
			}

			if (bracketPoints > 0 && absoluteRating > 10) {
				promotionWins = 0;
				absoluteRating--;
			}

			return new PlayerRank(history, absoluteRating, elo, promotionWins, 0);
		}

		int winStreak = this.winStreak + 1;
		if (bracket != RankedBracket.DIAMOND) {
			if (absoluteRating == bracket.max) {
				++promotionWins;
				int winsNeeded = 0;

				if (bracket == RankedBracket.BRONZE) {
					winsNeeded = BRONZE_PROMOTION_WINS;
				} else if (bracket == RankedBracket.SILVER) {
					winsNeeded = SILVER_PROMOTION_WINS;
				} else if (bracket == RankedBracket.GOLD) {
					winsNeeded = GOLD_PROMOTION_WINS;
				} else if (bracket == RankedBracket.PLATINUM) {
					winsNeeded = PLATINUM_PROMOTION_WINS;
				}

				if (promotionWins >= winsNeeded) {
					++absoluteRating;
					promotionWins = 0;
					winStreak = 0;
				}
			} else {
				int points = 1 + (this.winStreak + 1 >= 3 ? 1 : 0) + (opponentRisingStar ? 1 : 0);
				absoluteRating += points;

				if (absoluteRating > bracket.max) {
					int num2 = absoluteRating;
					int max = bracket.max;
					int num3 = num2 - max;

					absoluteRating = num2 - num3;
				}

				if (absoluteRating == bracket.max) {
					promotionWins = 1;
				}
			}
		}

		return new PlayerRank(history, absoluteRating, elo, promotionWins, winStreak);
	}

	@Override
	public String toString() {
		return bracket + " " + clientBracketPoints();
	}
}