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
		p.setWeapon(weapon == null ? null : weapon.copy());
	}

	@Override
	public int getStat() {
		return -1;
	}

	@Override
	public Type getType() {
		return Type.WEAPON;
	}

	@Override
	public Weapon getWeapon() {
		return weapon;
	}
}