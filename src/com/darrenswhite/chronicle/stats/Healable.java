package com.darrenswhite.chronicle.stats;

/**
 * @author Darren White
 */
public interface Healable {

	int getHealth();

	int getMaxHealth();

	default void removeHealth(int amount) {
		if (amount > 0) {
			setHealth(getHealth() - amount);
		}
	}

	void setHealth(int health);

	default void stealHealth(Healable h, int amount) {
		if (h.getHealth() < amount) {
			amount = h.getHealth();
		}

		int startHealth = getHealth();
		int endHealth = Math.min(startHealth + amount, getMaxHealth());
		int steal = endHealth - startHealth;

		h.removeHealth(steal);
		setHealth(endHealth);
	}
}