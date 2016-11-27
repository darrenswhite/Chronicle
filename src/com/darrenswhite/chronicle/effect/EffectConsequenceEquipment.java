package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.game.Game;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public class EffectConsequenceEquipment extends EffectConsequence {

	private Weapon value;

	public EffectConsequenceEquipment(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
	}

	@Override
	public void apply(Game g) {
		List<IEffectTarget> targets1 = getTargets(g, target, subTarget, EffectSlot.CURRENT, 0, targetPredicates);

		if (targets1.size() == 0) {
			return;
		}

		if (source == EffectTarget.NONE) {
			for (IEffectTarget effectTarget : targets1) {
				Weapon weapon2 = action != EffectAction.REMOVE ? value : effectTarget.getWeapon();
				effectTarget.applyToProperty(g, targetProp, action, targetPredicates, weapon2);
			}
		} else {
			List<IEffectTarget> targets2 = getTargets(g, source, subSource, EffectSlot.CURRENT, 0, sourcePredicates);

			if (targets2.size() == 0) {
				return;
			}
			if (targets2.size() > 1) {
				return;
			}

			for (IEffectTarget effectTarget1 : targets1) {
				for (IEffectTarget effectTarget2 : targets2) {
					Weapon weapon = value != null ? value : effectTarget2.getWeapon();
					switch (action) {
						case ADD:
						case REMOVE:
							effectTarget1.applyToProperty(g, targetProp, action, targetPredicates, weapon);
							break;
						case STEAL:
							if (weapon != null) {
								effectTarget2.applyToProperty(g, sourceProp, EffectAction.REMOVE, sourcePredicates, weapon);
								effectTarget1.applyToProperty(g, targetProp, EffectAction.ADD, targetPredicates, weapon);
							}
							break;
						case SWAP:
							Weapon propertyValueWeapon = effectTarget1.getWeapon();
							if (propertyValueWeapon != null) {
								effectTarget2.applyToProperty(g, sourceProp, EffectAction.REMOVE, sourcePredicates, propertyValueWeapon);
								effectTarget1.applyToProperty(g, targetProp, EffectAction.ADD, targetPredicates, propertyValueWeapon);
								effectTarget1.applyToProperty(g, targetProp, EffectAction.REMOVE, targetPredicates, null);
								effectTarget2.applyToProperty(g, sourceProp, EffectAction.ADD, sourcePredicates, null);
							}
							break;
					}
				}
			}
		}
	}

	@Override
	public EffectConsequenceEquipment copy() {
		try {
			return (EffectConsequenceEquipment) clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public void setValue(int value0, int value1) {
		if (value0 > 0) {
			value = new Weapon(value0, value1);
		}
	}
}