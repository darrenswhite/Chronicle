package com.darrenswhite.chronicle.rewards;

import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.player.Player;

/**
 * @author Darren White
 */
public interface Reward {

	void apply(Player p);

	int getStat();

	Type getType();

	Weapon getWeapon();

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