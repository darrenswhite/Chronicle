package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.player.Player;

import java.util.Comparator;

/**
 * @author Darren White
 */
public class ComboComparator implements Comparator<Game> {

	public static final int ARMOUR = 0;
	public static final int BASE = 1;
	public static final int DAMAGE = 2;
	public static final int GOLD = 3;

	private final int priority;
	private final int minHealth;

	public ComboComparator(int priority, int minHealth) {
		this.priority = priority;
		this.minHealth = minHealth;
	}

	@Override
	public int compare(Game g1, Game g2) {
		Player p1 = g1.getPlayer();
		Player p2 = g2.getPlayer();
		int compare;

		if (p1.health < minHealth) {
			if (p2.health < minHealth) {
				return 0;
			} else {
				return 1;
			}
		} else if (p2.health < minHealth) {
			return -1;
		}

		switch (priority) {
			case ARMOUR:
				compare = Integer.compare(p2.armour, p1.armour);
				break;
			case BASE:
				compare = Integer.compare(p2.base, p1.base);
				break;
			case DAMAGE:
				compare = Integer.compare(g1.getRival().health, g2.getRival().health);
				break;
			case GOLD:
				compare = Integer.compare(p2.gold, p1.gold);
				break;
			default:
				throw new IllegalArgumentException("Unknown priority value: " + priority);
		}

		if (compare != 0) {
			return compare;
		}

		compare = Integer.compare(g1.getCards().size(), g2.getCards().size());
		if (compare != 0) {
			return compare;
		}

		compare = Integer.compare(p2.health, p1.health);
		if (compare != 0) {
			return compare;
		}

		return 0;
	}
}