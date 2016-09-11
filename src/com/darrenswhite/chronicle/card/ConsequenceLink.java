package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;

/**
 * @author Darren White
 */
public class ConsequenceLink {

	public int card_id;
	public int consequence_id;
	public int consequence_value_0;
	public int consequence_value_1;

	public void accept(Game g) {
		switch (consequence_id) {
			case 8: // Deal damage to rival
				g.getRival().dealDamage(consequence_value_0);
				break;
			case 16: // Strike rival
				g.getRival().dealDamage(g.getPlayer().getTotalAttack());
				g.getPlayer().updateWeapon();
				break;
			case 17: // Strike player
				g.getPlayer().dealDamage(g.getRival().getTotalAttack());
				g.getRival().updateWeapon();
				break;
			case 208: // Exhaust

			case 222: // Add cannonball to hand
				g.getPlayer().addCard(CardCollection.getInstance().findAny(c -> c.name.equals("Cannonball")).get());
				break;
			case 223: // Add cannonball to rival's hand
				g.getRival().addCard(CardCollection.getInstance().findAny(c -> c.name.equals("Cannonball")).get());
				break;
		}
	}

	@Override
	public String toString() {
		return "ConsequenceLink{" +
				"cardId=" + card_id +
				", consequence_id=" + consequence_id +
				", consequence_value_0=" + consequence_value_0 +
				", consequence_value_1=" + consequence_value_1 +
				'}';
	}
}