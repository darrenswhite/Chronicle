package com.darrenswhite.chronicle.card;

import java.util.function.Predicate;

/**
 * @author Darren White
 */
public class CardPredicate {

	private final CardPredicateType type;
	private final Predicate<Card> predicate;

	public CardPredicate(CardPredicateType type, Predicate<Card> predicate) {
		this.predicate = predicate;
		this.type = type;
	}

	public Predicate<Card> getPredicate() {
		return predicate;
	}

	public CardPredicateType getType() {
		return type;
	}

	public boolean test(Card c) {
		return predicate.test(c);
	}
}