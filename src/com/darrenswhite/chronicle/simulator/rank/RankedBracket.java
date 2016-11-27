package com.darrenswhite.chronicle.simulator.rank;

public class RankedBracket {

	public static final RankedBracket BRONZE = new RankedBracket(0, 9, "Bronze");
	public static final RankedBracket SILVER = new RankedBracket(10, 34, "Silver");
	public static final RankedBracket GOLD = new RankedBracket(35, 74, "Gold");
	public static final RankedBracket PLATINUM = new RankedBracket(75, 124, "Platinum");
	public static final RankedBracket DIAMOND = new RankedBracket(125, 125, "Diamond");
	public final int max;
	public final String internalName;
	public final int min;

	private RankedBracket(int min, int max, String internalName) {
		this.min = min;
		this.max = max;
		this.internalName = internalName;
	}

	public static int clientBracketPoints(int rating) {
		if (rating > RankedBracket.DIAMOND.max) {
			rating = RankedBracket.DIAMOND.max;
		}

		if (rating < RankedBracket.BRONZE.min) {
			rating = RankedBracket.BRONZE.min;
		}

		return fromAbsoluteRating(rating).max + 1 - rating;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RankedBracket that = (RankedBracket) o;
		return max == that.max && min == that.min && internalName.equals(that.internalName);
	}

	public static RankedBracket fromAbsoluteRating(int rating) {
		if (rating <= RankedBracket.BRONZE.max) {
			return RankedBracket.BRONZE;
		}
		if (rating <= RankedBracket.SILVER.max) {
			return RankedBracket.SILVER;
		}
		if (rating <= RankedBracket.GOLD.max) {
			return RankedBracket.GOLD;
		}
		if (rating <= RankedBracket.PLATINUM.max) {
			return RankedBracket.PLATINUM;
		}
		return RankedBracket.DIAMOND;
	}

	public static RankedBracket fromInternalName(String name) {
		if (name.equalsIgnoreCase(RankedBracket.BRONZE.internalName)) {
			return RankedBracket.BRONZE;
		}
		if (name.equalsIgnoreCase(RankedBracket.SILVER.internalName)) {
			return RankedBracket.SILVER;
		}
		if (name.equalsIgnoreCase(RankedBracket.GOLD.internalName)) {
			return RankedBracket.GOLD;
		}
		if (name.equalsIgnoreCase(RankedBracket.PLATINUM.internalName)) {
			return RankedBracket.PLATINUM;
		}
		if (!name.equalsIgnoreCase(RankedBracket.DIAMOND.internalName)) {
			return RankedBracket.BRONZE;
		}
		return RankedBracket.DIAMOND;
	}

	public static RankedBracket[] getAllBrackets() {
		return new RankedBracket[]{RankedBracket.BRONZE, RankedBracket.SILVER, RankedBracket.GOLD, RankedBracket.PLATINUM, RankedBracket.DIAMOND};
	}

	@Override
	public int hashCode() {
		return internalName.hashCode();
	}

	@Override
	public String toString() {
		return internalName;
	}
}