package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;

/**
 * @author Darren White
 */
public interface Card {

	void encounter(Game g);

	CardBuilder getBuilder();

	Effect getEffect();

	String getName();

	Reward getReward();

	Type getType();

	enum Type {
		ALLY, BEAST, DEMON, EQUIPMENT, GOBLIN, KALPHITE, POTION, SPELL, TZHAAR, VAMPYRE, NONE
	}
}