package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.config.ConfigTemplate;
import org.apache.commons.csv.CSVRecord;

import java.util.Map;

/**
 * @author Darren White
 */
public class EffectCondition extends ConfigTemplate {

	private final int cardId;
	private final int conditionId;
	private final int conditionValue;

	public EffectCondition(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		cardId = parseInt(record.get(headers.get("card_id")));
		conditionId = parseInt(record.get(headers.get("condition_id")));
		conditionValue = parseInt(record.get(headers.get("condition_value")));
	}

	public boolean apply(Game g) {
		switch (conditionId) {
			case 5: // Mortal
				return g.getPlayer().getHealth() <= conditionValue;
			case 18: // Spend cannonball
				return g.getPlayer().removeCard(c -> c.getName().equals("Cannonball"));
			case 19: // Next creature has x or more health
				return g.getNextCard(c -> c.getType() == Card.Type.COMBAT).filter(c -> c.getHealth() >= conditionValue).isPresent();
			default:
				// System.err.println("Unknown condition id: " + condition_id);
				return false;
		}
	}

	public int getCardId() {
		return cardId;
	}
}