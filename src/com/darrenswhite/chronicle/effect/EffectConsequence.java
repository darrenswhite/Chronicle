package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.config.ConfigTemplate;
import org.apache.commons.csv.CSVRecord;

import java.util.Map;
import java.util.Optional;

/**
 * @author Darren White
 */
public class EffectConsequence extends ConfigTemplate {

	private final int cardId;
	private final int consequenceId;
	private final int consequenceValue0;
	private final int consequenceValue1;

	public EffectConsequence(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		cardId = parseInt(record.get(headers.get("card_id")));
		consequenceId = parseInt(record.get(headers.get("consequence_id")));
		consequenceValue0 = parseInt(record.get(headers.get("consequence_value_0")));
		consequenceValue1 = parseInt(record.get(headers.get("consequence_value_1")));
	}

	public void accept(Game g) {
		switch (consequenceId) {
			case 3: // Steal health
				g.getPlayer().stealHealth(g.getRival(), consequenceValue0);
				break;
			case 5: // Deal damage to player
				g.getPlayer().dealDamage(consequenceValue0);
				break;
			case 8: // Deal damage to rival
				g.getRival().dealDamage(consequenceValue0);
				break;
			case 16: // Strike rival
				g.getRival().dealDamage(g.getPlayer().getTotalAttack());
				g.getPlayer().updateWeapon();
				break;
			case 17: // Strike player
				g.getPlayer().dealDamage(g.getRival().getTotalAttack());
				g.getRival().updateWeapon();
				break;
			case 22: // Remove health
				g.getPlayer().removeHealth(consequenceValue0);
				break;
			case 53: // AP gain
				g.getPlayer().attack += consequenceValue0;
				break;
			case 114: // Steal health from next creature
				Optional<Card> next = g.getNextCard(c -> c.getType() == Card.Type.COMBAT);

				next.ifPresent(c -> g.getPlayer().stealHealth(c, consequenceValue0));
				break;
			case 208: // Exhaust
				g.getPlayer().temporaryAttack = -g.getPlayer().attack + 1;
				break;
			case 222: // Add cannonball to hand
				g.getPlayer().addCard(ConfigProvider.getInstance().get(c -> c.getName().equals("Cannonball")).get());
				break;
			case 223: // Add cannonball to rival's hand
				g.getRival().addCard(ConfigProvider.getInstance().get(c -> c.getName().equals("Cannonball")).get());
				break;
			case 269: // Add AP to rival
				g.getRival().attack += consequenceValue0;
				break;
			default:
				// System.err.println("Unknown consequence id: " + consequence_id);
				break;
		}
	}

	public int getCardId() {
		return cardId;
	}
}