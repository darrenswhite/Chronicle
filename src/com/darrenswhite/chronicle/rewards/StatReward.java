package com.darrenswhite.chronicle.rewards;

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
				p.attack += amount;
				break;
			case GOLD:
				p.gold += amount;
				break;
			case HEALTH:
				p.addHealth(amount);
				break;
			case ARMOUR:
				p.armour += amount;
				break;
		}
	}

	@Override
	public Type getType() {
		return type;
	}
}