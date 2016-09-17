package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.card.CardPredicate;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.player.Player;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Map;

/**
 * // TODO Equipment consequence
 *
 * @author Darren White
 */
public class EffectConsequence extends EffectComponent {

	private static final int TEMP_PROPERTY_DURABILITY_PLAYER = 2;
	private static final int TEMP_PROPERTY_DURABILITY_OPPONENT = 1;

	private final int id;
	private final EffectAction action;
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
	private int value0;
	private int value1;

	public EffectConsequence(Map<String, Integer> headers, CSVRecord record) {
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

	public void apply(Game g) {
		List<IEffectTarget> targets1 = getTargets(g, target, subTarget, targetSlot, targetSlotCount, targetPredicates);
		int num1 = 0;

		if (targets1.size() == 0) {
			return;
		}

		if (targetProp == EffectProperty.TEMP_ATTACK || targetProp == EffectProperty.EXHAUST) {
			value1 = target == EffectTarget.PLAYER ? TEMP_PROPERTY_DURABILITY_PLAYER : TEMP_PROPERTY_DURABILITY_OPPONENT;
		}

		if (source == EffectTarget.NONE) {
			if (action == EffectAction.STEAL || action == EffectAction.SWAP) {
				return;
			}

			for (IEffectTarget effectTarget : targets1) {
				int num4 = action != EffectAction.REMOVE ? value0 : effectTarget.getPropertyValue(targetProp, targetPredicates, value0);

				if (num4 != 0 || action == EffectAction.SET) {
					effectTarget.applyToProperty(targetProp, action, targetPredicates, num4, value1);
				}
			}
		} else {
			List<IEffectTarget> targets2 = getTargets(g, source, subSource, sourceSlot, sourceSlotCount, sourcePredicates);
			if (source != EffectTarget.SELF && targets2.size() == 0) {
				return;
			}
			if (targets2.size() > 1 && targets1.size() > 1) {
				return;
			}
			if (action == EffectAction.SWAP && source != EffectTarget.SELF && (targets2.size() > 1 || targets1.size() > 1)) {
				return;
			}

			if (source != EffectTarget.SELF && action != EffectAction.STEAL && action != EffectAction.SWAP) {
				for (IEffectTarget effectTarget : targets2) {
					num1 += effectTarget.getPropertyValue(sourceProp, sourcePredicates, value0);
				}
			}
			for (IEffectTarget effectTarget1 : targets1) {
				if (source == EffectTarget.SELF) {
					targets2.clear();
					targets2.add(effectTarget1);
				}

				switch (action) {
					case STEAL:
						for (IEffectTarget effectTarget2 : targets2) {
							int val1 = effectTarget2.getPropertyValue(sourceProp, sourcePredicates, value0);

							if (effectTarget2 instanceof Player && sourceProp == EffectProperty.ATTACK) {
								int propertyValue = effectTarget2.getPropertyValue(sourceProp, sourcePredicates, 0);
								val1 = Math.min(val1, propertyValue - 1);
							}

							switch (targetProp) {
								case WEAPON_ATTACK:
								case WEAPON_DURABILITY:
									if (effectTarget1.getPropertyValueWeapon() == null) {
										continue;
									}
									break;
								case INSTANT_CARD_DRAW:
									val1 = Math.min(val1, 10 - effectTarget1.getPropertyValue(EffectProperty.HAND_SIZE, targetPredicates, 0));
									break;
								case HEALTH:
									if (val1 > 0 && effectTarget1.getPropertyValue(EffectProperty.MAX_HEALTH, targetPredicates, 0) > 0) {
										val1 = Math.min(val1, effectTarget1.getPropertyValue(EffectProperty.MAX_HEALTH, targetPredicates, 0) - effectTarget1.getPropertyValue(EffectProperty.HEALTH, targetPredicates, 0));

										if (val1 == 0) {
											continue;
										}
									}
									break;
							}

							if (val1 != 0) {
								effectTarget2.applyToProperty(sourceProp, EffectAction.REMOVE, sourcePredicates, val1, 1);
								effectTarget1.applyToProperty(targetProp, EffectAction.ADD, targetPredicates, val1, 1);
							}
						}
						continue;
					case SWAP:
						for (IEffectTarget current : targets2) {
							int propertyValue = effectTarget1.getPropertyValue(targetProp, targetPredicates, value0);
							num1 = current.getPropertyValue(sourceProp, sourcePredicates, value0);
							if (num1 != propertyValue) {
								int num2 = propertyValue - num1;
								if (num2 > 0) {
									effectTarget1.applyToProperty(targetProp, EffectAction.REMOVE, targetPredicates, num2, 0);
									current.applyToProperty(sourceProp, EffectAction.ADD, sourcePredicates, num2, 0);
								} else {
									effectTarget1.applyToProperty(targetProp, EffectAction.ADD, targetPredicates, -num2, 0);
									current.applyToProperty(sourceProp, EffectAction.REMOVE, sourcePredicates, -num2, 0);
								}
							}
						}
						continue;
					default:
						num1 = Math.max(0, (int) Math.ceil(multiplier * (double) num1) + addend);
						if (num1 == 0 && action != EffectAction.SET) {
							return;
						}
						effectTarget1.applyToProperty(targetProp, action, targetPredicates, num1, value1);
				}
			}
		}
	}

	public EffectConsequence copy() {
		EffectConsequence consequence = new EffectConsequence(getHeaders(), getRecord());
		consequence.value0 = value0;
		consequence.value1 = value1;
		return consequence;
	}

	public int getId() {
		return id;
	}

	public EffectProperty getTargetProperty() {
		return targetProp;
	}

	public void setValue0(int value0) {
		this.value0 = value0;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}
}