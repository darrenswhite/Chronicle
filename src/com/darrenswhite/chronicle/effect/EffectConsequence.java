package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.card.CardPredicate;
import com.darrenswhite.chronicle.game.Game;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public abstract class EffectConsequence extends EffectComponent {

	protected static final int TEMP_PROPERTY_DURABILITY_PLAYER = 2;
	protected static final int TEMP_PROPERTY_DURABILITY_OPPONENT = 1;

	protected final int id;
	protected final EffectAction action;
	protected final EffectTarget target;
	protected final EffectProperty targetProp;
	protected final EffectTarget source;
	protected final EffectProperty sourceProp;
	protected final double multiplier;
	protected final int addend;
	protected final EffectSlot targetSlot;
	protected final EffectSlot sourceSlot;
	protected final EffectSubTarget subTarget;
	protected final EffectSubTarget subSource;
	protected final List<CardPredicate> targetPredicates;
	protected final List<CardPredicate> sourcePredicates;
	protected final int targetSlotCount;
	protected final int sourceSlotCount;

	protected EffectConsequence(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		id = parseInt(record.get(headers.get("id")));
		action = parseEnum(EffectAction.class, record.get(headers.get("action")));
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

	public abstract void apply(Game g);

	public abstract EffectConsequence copy();

	public static EffectConsequence create(Map<String, Integer> headers, CSVRecord record) {
		EffectProperty targetProp = EffectProperty.values()[Integer.parseInt(record.get(headers.get("targetproperty")))];

		switch (targetProp) {
			case WEAPON:
				return new EffectConsequenceEquipment(headers, record);
			default:
				return new EffectConsequenceInt(headers, record);
		}
	}

	public int getId() {
		return id;
	}

	public EffectProperty getTargetProperty() {
		return targetProp;
	}

	public abstract void setValue(int value0, int value1);
}