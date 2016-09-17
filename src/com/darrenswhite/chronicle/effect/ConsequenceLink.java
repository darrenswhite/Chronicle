package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.config.ConfigTemplate;
import org.apache.commons.csv.CSVRecord;

import java.util.Map;

/**
 * @author Darren White
 */
public class ConsequenceLink extends ConfigTemplate {

	private final int cardId;
	private final int consequenceId;
	private final int consequenceValue0;
	private final int consequenceValue1;

	public ConsequenceLink(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		cardId = parseInt(record.get(headers.get("card_id")));
		consequenceId = parseInt(record.get(headers.get("consequence_id")));
		consequenceValue0 = parseInt(record.get(headers.get("consequence_value_0")));
		consequenceValue1 = parseInt(record.get(headers.get("consequence_value_1")));
	}

	public int getCardId() {
		return cardId;
	}

	public int getConsequenceId() {
		return consequenceId;
	}

	public int getConsequenceValue0() {
		return consequenceValue0;
	}

	public int getConsequenceValue1() {
		return consequenceValue1;
	}
}