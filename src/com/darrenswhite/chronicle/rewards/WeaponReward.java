package com.darrenswhite.chronicle.rewards;

import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.player.Player;

/**
 * @author Darren White
 */
public class WeaponReward implements Reward {

	private final Weapon weapon;

	public WeaponReward(Weapon weapon) {
		this.weapon = weapon;
	}

	@Override
	public void apply(Player p) {
		p.weapon = weapon == null ? null : weapon.copy();
	}

	@Override
	public Type getType() {
		return Type.WEAPON;
	}
}