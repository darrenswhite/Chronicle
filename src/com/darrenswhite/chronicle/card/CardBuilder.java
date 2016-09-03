package com.darrenswhite.chronicle.card;

/**
 * @author Darren White
 */
public interface CardBuilder {

	Card create();

	CardBuilder effect(Effect effect);

	CardBuilder name(String name);

	CardBuilder reward(Reward reward);

	CardBuilder type(Card.Type type);
}