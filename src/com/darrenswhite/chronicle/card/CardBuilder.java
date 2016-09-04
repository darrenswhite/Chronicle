package com.darrenswhite.chronicle.card;

/**
 * @author Darren White
 */
public interface CardBuilder {

	Card create();

	CardBuilder effect(Effect effect);

	CardBuilder legend(Card.Legend legend);

	CardBuilder name(String name);

	CardBuilder rarity(Card.Rarity rarity);

	CardBuilder reward(Reward reward);

	CardBuilder type(Card.Type type);
}