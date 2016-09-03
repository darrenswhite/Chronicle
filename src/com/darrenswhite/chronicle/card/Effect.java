package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;

/**
 * @author Darren White
 */
@FunctionalInterface
public interface Effect {

	void apply(Game g);

	static Effect none() {
		return e -> {
		};
	}
}