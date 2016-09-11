package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;

/**
 * @author Darren White
 */
public class ConditionLink {

	public int card_id;
	public int condition_id;
	public int condition_value;

	public boolean apply(Game g) {
		switch (condition_id) {
			case 5:
				return g.getPlayer().health <= 15 && g.getPlayer().health > 0;
			case 18:
				return g.getPlayer().removeCard(c -> c.name.equals("Cannonball"));
		}

		return true;
	}

	@Override
	public String toString() {
		return "ConditionLink{" +
				"cardId=" + card_id +
				", conditionId=" + condition_id +
				", conditionValue=" + condition_value +
				'}';
	}
}