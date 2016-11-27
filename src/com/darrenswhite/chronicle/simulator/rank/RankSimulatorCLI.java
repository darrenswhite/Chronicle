package com.darrenswhite.chronicle.simulator.rank;

/**
 * @author Darren White
 */
public class RankSimulatorCLI {

	public static void main(String[] args) {
		if (args.length == 0) {
			return;
		}

		PlayerRank startRank = null, endRank = null;
		double winRate = -1;
		int games = -1;
		int runs = 100;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String value = i < args.length - 1 ? args[i + 1] : null;
			String value2 = i < args.length - 2 ? args[i + 2] : null;

			switch (arg) {
				case "-s":
				case "--start-rank":
					if (value == null) {
						throw new IllegalArgumentException("Start rank must have a league!");
					}

					startRank = new PlayerRank(RankedBracket.fromInternalName(value), parseInteger(value2) - 1);
					break;
				case "-d":
				case "--desired-rank":
					if (value == null) {
						throw new IllegalArgumentException("Desired rank must have a league!");
					}

					endRank = new PlayerRank(RankedBracket.fromInternalName(value), parseInteger(value2) - 1);
					break;
				case "-w":
				case "--win-rate":
					winRate = value == null ? -1 : Double.parseDouble(value) / 100D;
					break;
				case "-g":
				case "--games-played":
					games = value == null ? -1 : Integer.parseInt(value);
					break;
				case "-n":
				case "--simulations":
					runs = value == null ? 100 : Integer.parseInt(value);
					break;
			}
		}

		Simulator sim = new Simulator(startRank, endRank, winRate, games, runs);

		sim.run();

		System.out.println(sim.getCSVOutput());
	}

	private static int parseInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}