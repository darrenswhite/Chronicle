package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;

/**
 * @author Darren White
 */
public interface Card {

	void encounter(Game g);

	CardBuilder getBuilder();

	Effect getEffect();

	Legend getLegend();

	String getName();

	Rarity getRarity();

	Reward getReward();

	Type getType();

	enum Legend {
		ALL, ARIANE, LINZA, MORVRAN, OZAN, RAPTOR, VANESCULA
	}

	enum Rarity {
		BASIC, SAPPHIRE, EMERALD, RUBY, DIAMOND
	}

	enum Type {
		ALLY, BEAST, DEMON, EQUIPMENT, FAMILIAR, GOBLIN, LOCATION, KALPHITE,
		PIRATE, POTION, SPELL, TZHAAR, VAMPYRE, NONE
	}
}