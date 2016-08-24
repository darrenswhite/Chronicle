package com.darrenswhite.chronicle.simulator.rank;

/**
 * @author Darren White
 */
public class Rank extends Number implements Comparable<Rank>, Cloneable {

	private League league;
	private int position;

	public Rank() {
		this(null, 0);
	}

	public Rank(League league, int position) {
		this.league = league;
		this.position = position;
	}

	@Override
	public int compareTo(Rank o) {
		int leagueCmp = league.compareTo(o.league);

		if (leagueCmp == 0) {
			return Integer.compare(o.position, position);
		}

		return leagueCmp;
	}

	public Rank copy() {
		return new Rank(getLeague(), getPosition());
	}

	public void demote() {
		if (getPosition() < getLeague().getStartPosition()) {
			setPosition(getPosition() + 1);
		}
	}

	@Override
	public double doubleValue() {
		return hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Rank rank = (Rank) o;

		return position == rank.position && league == rank.league;
	}

	@Override
	public float floatValue() {
		return hashCode();
	}

	public League getLeague() {
		return league;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public int hashCode() {
		int result = league != null ? league.ordinal() : -1;
		result = 100 * result + position;
		return result;
	}

	@Override
	public int intValue() {
		return hashCode();
	}

	@Override
	public long longValue() {
		return hashCode();
	}

	public void promote(long streak) {
		if (streak >= 3) {
			setPosition(getPosition() - 2);
		} else {
			setPosition(getPosition() - 1);
		}
	}

	public void promoteLeague() {
		if (league != League.DIAMOND && league != League.NONE) {
			league = League.values()[league.ordinal() + 1];
			position = league.getStartPosition();
		}
	}

	public void setLeague(League league) {
		this.league = league;
	}

	public void setPosition(int position) {
		this.position = Math.max(1, position);
	}

	@Override
	public String toString() {
		return league.toString() + (position != -1 ? " " + position : "");
	}

	public enum League {

		BRONZE(10), SILVER(25), GOLD(40), PLATINUM(50), DIAMOND(-1), NONE(-1);

		private final int startPosition;

		League(int startPosition) {
			this.startPosition = startPosition;
		}

		public int getStartPosition() {
			return startPosition;
		}

		@Override
		public String toString() {
			return name().toUpperCase().charAt(0) + name().substring(1).toLowerCase();
		}
	}
}