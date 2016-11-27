package com.darrenswhite.chronicle.equipment;

/**
 * @author Darren White
 */
public class Weapon {

	public int attack;
	public int durability;

	public Weapon(int attack, int durability) {
		this.attack = attack;
		this.durability = durability;
	}

	public Weapon copy() {
		return new Weapon(attack, durability);
	}

	@Override
	public String toString() {
		return attack + "/" + durability;
	}
}