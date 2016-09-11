package com.darrenswhite.chronicle.card;

/**
 * @author Darren White
 */
public class ConditionConsequenceLink {

	public int card_id;
	public int consequence_num;
	public int condition_id_0;
	public int condition_id_1;
	public int link_type;

	@Override
	public String toString() {
		return "ConditionConsequenceLink{" +
				"cardId=" + card_id +
				", consequence_num=" + consequence_num +
				", condition_id_0=" + condition_id_0 +
				", condition_id_1=" + condition_id_1 +
				", link_type=" + link_type +
				'}';
	}
}