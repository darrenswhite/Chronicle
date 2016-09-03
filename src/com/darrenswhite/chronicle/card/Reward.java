package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.player.Player;

/**
 * @author Darren White
 */
public class Reward {

	public static final Reward NONE = new Builder().create();

	private final int health;
	private final int armour;
	private final int base;
	private final int gold;
	private final Weapon weapon;

	private Reward(int health, int armour, int base, int gold, Weapon weapon) {
		this.health = health;
		this.armour = armour;
		this.base = base;
		this.gold = gold;
		this.weapon = weapon;
	}

	public void apply(Game g) {
		Player p = g.getPlayer();

		p.setBase(p.getBase() + base);
		p.setGold(p.getGold() + gold);
		p.setHealth(p.getHealth() + health);
		p.setArmour(p.getArmour() + armour);

		if (weapon != null) {
			p.setWeapon(new Weapon(weapon.getAttack(), weapon.getDurability()));
		}
	}

	public int getArmour() {
		return armour;
	}

	public int getBase() {
		return base;
	}

	public int getGold() {
		return gold;
	}

	public int getHealth() {
		return health;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (base > 0) {
			sb.append("base{").append(base).append('}');
		}

		if (gold > 0) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append("gold{").append(gold).append('}');
		}

		if (health > 0) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append("health{").append(health).append('}');
		}

		if (armour > 0) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append("armour{").append(armour).append('}');
		}

		if (weapon != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append("weapon{").append(weapon).append('}');
		}

		sb.insert(0, "Reward: ");

		return sb.toString();
	}

	public static class Builder {

		private int health = 0;
		private int armour = 0;
		private int base = 0;
		private int gold = 0;
		private Weapon weapon = null;

		public Builder armour(int armour) {
			this.armour = armour;
			return this;
		}

		public Builder base(int base) {
			this.base = base;
			return this;
		}

		public Reward create() {
			return new Reward(health, armour, base, gold, weapon);
		}

		public Builder gold(int gold) {
			this.gold = gold;
			return this;
		}

		public Builder health(int health) {
			this.health = health;
			return this;
		}

		public Builder weapon(int attack, int durability) {
			this.weapon = new Weapon(attack, durability);
			return this;
		}
	}
}