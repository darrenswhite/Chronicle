package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.CardPredicate;
import com.darrenswhite.chronicle.card.CardPredicateType;
import com.darrenswhite.chronicle.config.ConfigTemplate;
import com.darrenswhite.chronicle.game.Game;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public abstract class EffectComponent extends ConfigTemplate {

	public EffectComponent(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
	}

	private List<IEffectTarget> getCards(Game g, EffectSlot slot, int numberOfCards, List<CardPredicate> predicates) {
		List<IEffectTarget> effectTargetList = new LinkedList<>();
		Predicate<Card> all = predicates.stream().map(CardPredicate::getPredicate).reduce(Predicate::and).get();

		switch (slot) {
			case CURRENT:
				Card current = g.getCurrentCard();

				for (CardPredicate cp : predicates) {
					cp.predicate(current);
				}

				effectTargetList.add(current);
				break;
			case NEXT:
				List<Card> cardInstanceList = g.getNextCards(all);

				for (int i = 0; i < cardInstanceList.size(); i++) {
					Card c = cardInstanceList.get(i);

					if (i < numberOfCards || numberOfCards == 0) {
						effectTargetList.add(c);
					}
				}
				break;
			case PREVIOUS:
			case PREVIOUS_GAME:
				List<Card> previous = g.getCardHistory(all).collect(Collectors.toList());

				for (int i = 0; i < previous.size(); i++) {
					Card c = previous.get(i);

					if (i < numberOfCards || numberOfCards == 0) {
						effectTargetList.add(c);
					}
				}
				break;
		}

		return effectTargetList;
	}

	protected List<CardPredicate> getPredicates(String s) {
		List<CardPredicate> predicates = new LinkedList<>();

		try {
			JSONArray array = (JSONArray) new JSONParser().parse(s);

			if (array != null) {
				for (Object o : array) {
					JSONObject jobject = (JSONObject) o;
					CardPredicateType type = parseEnum(CardPredicateType.class, String.valueOf(jobject.get("predicate")));
					EffectEvalInt operand = jobject.containsKey("eval") ? parseEnum(EffectEvalInt.class, String.valueOf(jobject.get("eval"))) : EffectEvalInt.NONE;
					int num = parseInt(String.valueOf(jobject.get("value")));

					predicates.add(CardPredicate.generate(type, operand, num));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return predicates;
	}

	protected List<IEffectTarget> getTargets(Game g, EffectTarget target, EffectSubTarget subTarget, EffectSlot slot, int numberOfCards, List<CardPredicate> predicates) {
		List<IEffectTarget> effectTargetList = new LinkedList<>();

		switch (target) {
			case BOARD:
				return getCards(g, slot, numberOfCards, predicates);
			case PLAYER:
				if (subTarget != EffectSubTarget.SELF) {
					if (subTarget != EffectSubTarget.CARD) {
						return effectTargetList;
					}

					return getCards(g, slot, numberOfCards, predicates);
				}

				effectTargetList.add(g.getPlayer());
				break;
			case OPPONENT:
				if (subTarget != EffectSubTarget.SELF) {
					if (subTarget != EffectSubTarget.CARD) {
						return effectTargetList;
					}

					return getCards(g, slot, numberOfCards, predicates);
				}

				effectTargetList.add(g.getRival());
				break;
		}

		return effectTargetList;
	}
}