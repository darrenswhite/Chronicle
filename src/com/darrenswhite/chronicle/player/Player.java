package com.darrenswhite.chronicle.player;

import com.darrenswhite.chronicle.card.Weapon;

/**
 * @author Darren White
 */
public class Player {

	private int base;
	private int gold;
	private int health;
	private int armour;
	private Weapon weapon;
	private int maxHealth = 30;
	private int temporaryAttack;

	public Player(int base, int gold, int health, int armour, Weapon weapon) {
		this.base = base;
		this.gold = gold;
		this.health = health;
		this.armour = armour;
		this.weapon = weapon;
	}

	public void dealDamage(int amount) {
		int hit = amount;

		if (armour > 0) {
			hit -= armour;
			armour = Math.max(0, armour - amount);
		}

		removeHealth(hit);
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

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getTemporaryAttack() {
		return temporaryAttack;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public boolean isMortal() {
		return health <= 15 && health > 0;
	}

	public void removeHealth(int amount) {
		health = Math.max(0, health - amount);
	}

	public void setArmour(int armour) {
		this.armour = armour;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void setHealth(int health) {
		this.health = Math.min(maxHealth, health);
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setTemporaryAttack(int temporaryAttack) {
		this.temporaryAttack = temporaryAttack;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	@Override
	public String toString() {
		return "Player{base=" + base + ", gold=" + gold + ", health=" + health + ", armour=" + armour + ", weapon=" + weapon + ", maxHealth=" + maxHealth + '}';
	}
}