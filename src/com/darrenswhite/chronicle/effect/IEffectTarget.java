package com.darrenswhite.chronicle.effect;

import com.darrenswhite.chronicle.card.CardPredicate;
import com.darrenswhite.chronicle.equipment.Weapon;

import java.util.List;

/**
 * @author Darren White
 */
public interface IEffectTarget {

	void applyToProperty(EffectProperty property, EffectAction action, List<CardPredicate> predicates, int value, int value2);

	void applyToProperty(EffectProperty property, EffectAction action, List<CardPredicate> predicates, Weapon weapon);

	int getPropertyValue(EffectProperty property, List<CardPredicate> predicates, int maxValue);

	Weapon getPropertyValueWeapon();
}