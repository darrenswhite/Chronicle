package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.effect.Effect;
import com.darrenswhite.chronicle.effect.EffectEvalInt;

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

	public static CardPredicate generate(CardPredicateType type, EffectEvalInt operand, int value) {
		Predicate<Card> predicate;

		switch (type) {
			case SPECIFIC:
				predicate = ci -> ci.getType() != Type.OPEN_ROAD && (value <= 0 || ci.getId() == value);
				break;
			case TYPE:
				Type cardType = Type.values()[value];
				predicate = ci -> cardType == Type.ANY || ci.getType() == cardType;
				break;
			case FAMILY:
				Family family = Family.values()[value];
				predicate = ci -> ci.getType() != Type.OPEN_ROAD && (family == Family.NONE || ci.getFamily() == family);
				break;
			case ATTACK:
				predicate = ci -> ci.getType() == Type.COMBAT && Effect.intAssessment(ci.getAttack(), operand, value);
				break;
			case HEALTH:
				predicate = ci -> ci.getType() == Type.COMBAT && Effect.intAssessment(ci.getInitialHealth(), operand, value);
				break;
			case COST:
				predicate = ci -> ci.getType() == Type.NONCOMBAT && Effect.intAssessment(ci.getGoldCost(), operand, value);
				break;
			default:
				predicate = ci -> ci.getType() != Type.OPEN_ROAD;
				break;
		}

		return new CardPredicate(type, predicate);
	}

	public Predicate<Card> getPredicate() {
		return predicate;
	}

	public CardPredicateType getType() {
		return type;
	}

	public boolean predicate(Card c) {
		return predicate.test(c);
	}
}