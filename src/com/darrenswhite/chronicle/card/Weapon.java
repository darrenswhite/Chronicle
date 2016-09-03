package com.darrenswhite.chronicle.card;

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

	public int getAttack() {
		return attack;
	}

	public int getDurability() {
		return durability;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}

	@Override
	public String toString() {
		return attack + "/" + durability;
	}
}