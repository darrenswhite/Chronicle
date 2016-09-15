package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.config.ConfigProvider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public class Effect {

	private final List<EffectCondition> conditions;
	private final List<EffectConsequence> consequences;
	private final List<EffectConditionLink> conditionLinks;

	private Effect(List<EffectCondition> conditions,
	               List<EffectConsequence> consequences,
	               List<EffectConditionLink> conditionLinks) {
		this.conditions = conditions;
		this.consequences = consequences;
		this.conditionLinks = conditionLinks;
	}

	public Effect(Effect copy) {
		conditions = new LinkedList<>(copy.conditions);
		consequences = new LinkedList<>(copy.consequences);
		conditionLinks = new LinkedList<>(copy.conditionLinks);
	}

	public void apply(Game g) {
		Map<Integer, Boolean> conditionResults = new HashMap<>();
		int validConditions = 0;

		if (conditions.size() > 0) {
			for (int i = 0; i < conditions.size(); i++) {
				EffectCondition condition = conditions.get(i);

				if (condition.apply(g)) {
					conditionResults.put(i, true);
					validConditions++;
				} else {
					conditionResults.put(i, false);
				}
			}
		}

		if (conditionLinks.isEmpty()) {
			if (validConditions == conditions.size()) {
				if (consequences.size() > 0) {
					for (EffectConsequence consequence : consequences) {
						if (g.getPlayer().getHealth() <= 0 || g.getRival().getHealth() <= 0) {
							break;
						}

						consequence.accept(g);
					}
				}
			}
		} else if (consequences != null && consequences.size() > 0) {
			int num2 = 0;

			for (int i = 0; i < consequences.size(); i++) {
				EffectConsequence consequence = consequences.get(i);
				boolean valid = true;

				for (EffectConditionLink link : conditionLinks) {
					if (link.getConsequenceNum() - 1 == i) {
						valid = link.test(conditionResults);
					}
				}

				if (g.getPlayer().getHealth() <= 0 || g.getRival().getHealth() <= 0) {
					break;
				}

				if (valid) {
					consequence.accept(g);
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
		List<EffectConditionLink> links = ConfigProvider.getInstance().getLinks(cardId);

		return new Effect(conditions, consequences, links);
	}
}