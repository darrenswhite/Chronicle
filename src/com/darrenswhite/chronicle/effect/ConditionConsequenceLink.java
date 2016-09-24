package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.config.ConfigTemplate;
import org.apache.commons.csv.CSVRecord;

import java.util.Map;

/**
 * @author Darren White
 */
public class ConditionConsequenceLink extends ConfigTemplate {

	private final int cardId;
	private final int consequenceNum;
	private final int conditionId0;
	private final int conditionId1;
	private final int linkType;

	public ConditionConsequenceLink(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		cardId = parseInt(record.get(headers.get("card_id")));
		consequenceNum = parseInt(record.get(headers.get("consequence_num")));
		conditionId0 = parseInt(record.get(headers.get("condition_id_0")));
		conditionId1 = parseInt(record.get(headers.get("condition_id_1")));
		linkType = parseInt(record.get(headers.get("link_type")));
	}

	@Override
	public ConditionConsequenceLink copy() {
		try {
			return (ConditionConsequenceLink) clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public int getCardId() {
		return cardId;
	}

	public int getConsequenceNum() {
		return consequenceNum;
	}

	public boolean test(Map<Integer, Boolean> conditionResults) {
		boolean flag1 = conditionResults.get(conditionId0);
		boolean flag2 = conditionResults.get(conditionId1);
		boolean flag3 = conditionResults.containsKey(conditionId1);
		boolean result = false;

		switch (linkType) {
			case 0:
				if (flag3) {
					if (flag1 | flag2) {
						result = true;
						break;
					}
					break;
				}
				if (flag1) {
					result = true;
					break;
				}
				break;
			case 1:
				if (flag3) {
					if (flag1 & flag2) {
						result = true;
						break;
					}
					break;
				}
				if (flag1) {
					result = true;
					break;
				}
				break;
			case 2:
				if (flag3) {
					if (!flag1 || !flag2) {
						result = true;
						break;
					}
					break;
				}
				if (!flag1) {
					result = true;
					break;
				}
				break;
			case 3:
				if (flag3) {
					if (!flag1 && !flag2) {
						result = true;
						break;
					}
					break;
				}
				if (!flag1) {
					result = true;
					break;
				}
				break;
			default:
				result = false;
				break;
		}

		return result;
	}
}