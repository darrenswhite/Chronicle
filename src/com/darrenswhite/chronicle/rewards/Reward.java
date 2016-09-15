package com.darrenswhite.chronicle.rewards;

import com.darrenswhite.chronicle.player.Player;

/**
 * @author Darren White
 */
public interface Reward {

	void apply(Player p);

	Type getType();

	enum Type {

		NONE,
		TRAIT,
		GOLD,
		HEALTH,
		ATTACK,
		ARMOUR,
		WEAPON
	}
}