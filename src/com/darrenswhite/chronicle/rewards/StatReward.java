package com.darrenswhite.chronicle.rewards;

import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.player.Player;

/**
 * @author Darren White
 */
public class StatReward implements Reward {

	private final Type type;
	private final int amount;

	public StatReward(Type type, int amount) {
		this.type = type;
		this.amount = amount;
	}

	@Override
	public void apply(Player p) {
		switch (type) {
			case ATTACK:
				p.addAttack(amount);
				break;
			case GOLD:
				p.addGold(amount);
				break;
			case HEALTH:
				p.addHealth(amount);
				break;
			case ARMOUR:
				p.addArmour(amount);
				break;
		}
	}

	@Override
	public int getStat() {
		return amount;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Weapon getWeapon() {
		return null;
	}
}