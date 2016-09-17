package com.darrenswhite.chronicle.stats;

/**
 * @author Darren White
 */
public interface Healable {

	int getHealth();

	default void removeHealth(int amount) {
		if (amount > 0) {
			setHealth(getHealth() - amount);
		}
	}

	void setHealth(int health);
}