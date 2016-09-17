package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.card.CardPredicate;
import com.darrenswhite.chronicle.game.Game;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public class EffectCondition extends EffectComponent {

	private final int id;
	private final EffectEvalInt eval;
	private final EffectTarget target;
	private final EffectProperty targetProp;
	private final EffectTarget source;
	private final EffectProperty sourceProp;
	private final double multiplier;
	private final int addend;
	private final EffectSlot targetSlot;
	private final EffectSlot sourceSlot;
	private final EffectSubTarget subTarget;
	private final EffectSubTarget subSource;
	private final List<CardPredicate> targetPredicates;
	private final List<CardPredicate> sourcePredicates;
	private final int targetSlotCount;
	private final int sourceSlotCount;
	private int value;

	public EffectCondition(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		id = parseInt(record.get(headers.get("id")));
		eval = parseEnum(EffectEvalInt.class, record.get(headers.get("evaluation")));
		target = parseEnum(EffectTarget.class, record.get(headers.get("target")));
		targetProp = parseEnum(EffectProperty.class, record.get(headers.get("targetproperty")));
		source = parseEnum(EffectTarget.class, record.get(headers.get("source")));
		sourceProp = parseEnum(EffectProperty.class, record.get(headers.get("sourceproperty")));
		multiplier = parseDoubleOr(record.get(headers.get("multiplier")), 1);
		addend = parseInt(record.get(headers.get("addend")));
		targetSlot = parseEnum(EffectSlot.class, record.get(headers.get("targetSlot")));
		sourceSlot = parseEnum(EffectSlot.class, record.get(headers.get("sourceSlot")));
		subTarget = parseEnum(EffectSubTarget.class, record.get(headers.get("subTarget")));
		subSource = parseEnum(EffectSubTarget.class, record.get(headers.get("subSource")));
		targetPredicates = getPredicates(record.get(headers.get("targetPredicates")));
		sourcePredicates = getPredicates(record.get(headers.get("sourcePredicates")));
		targetSlotCount = parseInt(record.get(headers.get("targetSlotCount")));
		sourceSlotCount = parseInt(record.get(headers.get("sourceSlotCount")));
	}

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

	public EffectCondition copy() {
		EffectCondition condition = new EffectCondition(getHeaders(), getRecord());
		condition.value = value;
		return condition;
	}

	public int getId() {
		return id;
	}

	public void setValue(int value) {
		this.value = value;
	}
}