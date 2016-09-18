package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.game.Game;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public class EffectConditionInt extends EffectCondition {

	public EffectConditionInt(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
	}

	@Override
	public boolean assess(Game g) {
		List<IEffectTarget> targets = getTargets(g, target, subTarget, targetSlot, targetSlotCount, targetPredicates);
		List<IEffectTarget> effectTargetList = getTargets(g, source, subSource, sourceSlot, sourceSlotCount, sourcePredicates);

		if (source == EffectTarget.SELF) {
			effectTargetList = targets;
		}

		int sourceValue = 0;
		int targetValue = 0;

		if (effectTargetList.size() > 0) {
			for (IEffectTarget effectTarget : effectTargetList) {
				sourceValue += effectTarget.getPropertyValue(sourceProp, source == EffectTarget.SELF ? targetPredicates : sourcePredicates, value);
			}
		} else {
			sourceValue = value;
		}

		sourceValue = (int) Math.round(multiplier * (double) sourceValue) + addend;

		if (targets.size() > 0) {
			for (IEffectTarget effectTarget : targets) {
				targetValue += effectTarget.getPropertyValue(targetProp, targetPredicates, 0);
			}
		}

		return Effect.intAssessment(targetValue, eval, sourceValue);
	}

	@Override
	public EffectConditionInt copy() {
		return new EffectConditionInt(getHeaders(), getRecord());
	}

	@Override
	public void setValue(int value) {
		this.value = value;
	}
}