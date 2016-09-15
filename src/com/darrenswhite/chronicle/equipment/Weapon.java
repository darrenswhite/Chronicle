package com.darrenswhite.chronicle.equipment;

/**
 * @author Darren White
 */
public class Weapon {

	private int attack;
	private int durability;

	public Weapon(int attack, int durability) {
		this.attack = attack;
		this.durability = durability;
	}

	public Weapon copy() {
		return new Weapon(attack, durability);
	}

	public int getAttack() {
		return attack;
	}

	public int getDurability() {
		return durability;
	}

	public void setAttack(int attack) {
		this.attack = Math.max(Math.min(this.attack, 0), attack);
	}

	public void setDurability(int durability) {
		this.durability = Math.max(0, durability);
	}
}