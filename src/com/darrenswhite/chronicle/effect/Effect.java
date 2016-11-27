package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.game.Game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public class Effect {

	private final List<EffectCondition> conditions;
	private final List<EffectConsequence> consequences;
	private final List<ConditionConsequenceLink> conditionConsequenceLinks;

	private Effect(List<EffectCondition> conditions,
	               List<EffectConsequence> consequences,
	               List<ConditionConsequenceLink> conditionConsequenceLinks) {
		this.conditions = conditions;
		this.consequences = consequences;
		this.conditionConsequenceLinks = conditionConsequenceLinks;
	}

	public void apply(Game g) {
		Map<Integer, Boolean> conditionResults = new HashMap<>();
		int validConditions = 0;

		if (conditions.size() > 0) {
			for (int i = 0; i < conditions.size(); i++) {
				EffectCondition condition = conditions.get(i);

				if (condition.assess(g)) {
					conditionResults.put(i, true);
					validConditions++;
				} else {
					conditionResults.put(i, false);
				}
			}
		}

		if (conditionConsequenceLinks.isEmpty()) {
			if (validConditions == conditions.size()) {
				if (consequences.size() > 0) {
					for (EffectConsequence consequence : consequences) {
						if (g.getPlayer().getHealth() <= 0 || g.getRival().getHealth() <= 0) {
							break;
						}

						consequence.apply(g);
					}
				}
			}
		} else if (consequences != null && consequences.size() > 0) {
			for (int i = 0; i < consequences.size(); i++) {
				EffectConsequence consequence = consequences.get(i);
				boolean valid = true;

				for (ConditionConsequenceLink link : conditionConsequenceLinks) {
					if (link.getConsequenceNum() - 1 == i) {
						valid = link.test(conditionResults);
					}
				}

				if (g.getPlayer().getHealth() <= 0 || g.getRival().getHealth() <= 0) {
					break;
				}

				if (valid) {
					consequence.apply(g);
				}
			}
		}
	}

	public static Effect create(int cardId) {
		List<EffectConsequence> consequences = ConfigProvider.getInstance().getConsequences(cardId);

		if (consequences == null || consequences.isEmpty()) {
			return null;
		}

		List<EffectCondition> conditions = ConfigProvider.getInstance().getConditions(cardId);
		List<ConditionConsequenceLink> links = ConfigProvider.getInstance().getConditionConsequenceLinks(cardId);

		return new Effect(conditions, consequences, links);
	}

	public List<EffectConsequence> getConsequences() {
		return consequences;
	}

	public static boolean intAssessment(int target, EffectEvalInt eval, int value) {
		switch (eval) {
			case EQUALS:
				return target == value;
			case NOT_EQUALS:
				return target != value;
			case GREATER_THAN:
				return target > value;
			case LESS_THAN:
				return target < value;
			case GREATER_OR_EQUAL:
				return target >= value;
			case SMALLER_OR_EQUAL:
				return target <= value;
			case NONE:
				return true;
			default:
				return false;
		}
	}

	public void removeProperty(EffectProperty property) {
		if (consequences == null) {
			return;
		}

		Iterator<EffectConsequence> it = consequences.iterator();

		while (it.hasNext()) {
			if (it.next().getTargetProperty() == property) {
				it.remove();
			}
		}
	}
}