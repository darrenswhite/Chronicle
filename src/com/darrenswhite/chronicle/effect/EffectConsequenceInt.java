package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.player.Player;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public class EffectConsequenceInt extends EffectConsequence {

	private int value0;
	private int value1;

	public EffectConsequenceInt(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
	}

	@Override
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
					effectTarget.applyToProperty(g, targetProp, action, targetPredicates, num4, value1);
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
								effectTarget2.applyToProperty(g, sourceProp, EffectAction.REMOVE, sourcePredicates, val1, 1);
								effectTarget1.applyToProperty(g, targetProp, EffectAction.ADD, targetPredicates, val1, 1);
							}
						}
						break;
					case SWAP:
						for (IEffectTarget current : targets2) {
							int propertyValue = effectTarget1.getPropertyValue(targetProp, targetPredicates, value0);
							num1 = current.getPropertyValue(sourceProp, sourcePredicates, value0);
							if (num1 != propertyValue) {
								int num2 = propertyValue - num1;
								if (num2 > 0) {
									effectTarget1.applyToProperty(g, targetProp, EffectAction.REMOVE, targetPredicates, num2, 0);
									current.applyToProperty(g, sourceProp, EffectAction.ADD, sourcePredicates, num2, 0);
								} else {
									effectTarget1.applyToProperty(g, targetProp, EffectAction.ADD, targetPredicates, -num2, 0);
									current.applyToProperty(g, sourceProp, EffectAction.REMOVE, sourcePredicates, -num2, 0);
								}
							}
						}
						break;
					default:
						num1 = Math.max(0, (int) Math.ceil(multiplier * (double) num1) + addend);
						if (num1 == 0 && action != EffectAction.SET) {
							return;
						}
						effectTarget1.applyToProperty(g, targetProp, action, targetPredicates, num1, value1);
				}
			}
		}
	}

	@Override
	public EffectConsequenceInt copy() {
		return new EffectConsequenceInt(getHeaders(), getRecord());
	}

	@Override
	public void setValue(int value0, int value1) {
		this.value0 = value0;
		this.value1 = value1;
	}
}