package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.config.ConfigTemplate;
import org.apache.commons.csv.CSVRecord;

import java.util.Map;

/**
 * @author Darren White
 */
public class ConditionLink extends ConfigTemplate {

	private final int cardId;
	private final int conditionId;
	private final int conditionValue;

	public ConditionLink(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		cardId = parseInt(record.get(headers.get("card_id")));
		conditionId = parseInt(record.get(headers.get("condition_id")));
		conditionValue = parseInt(record.get(headers.get("condition_value")));
	}

	public int getCardId() {
		return cardId;
	}

	public int getConditionId() {
		return conditionId;
	}

	public int getConditionValue() {
		return conditionValue;
	}
}