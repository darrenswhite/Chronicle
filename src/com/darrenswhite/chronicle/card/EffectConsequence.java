package com.darrenswhite.chronicle.card;

import org.json.simple.JSONArray;

/**
 * @author Darren White
 */
public class EffectConsequence {

	public int id;
	public int action;
	public int target;
	public int targetproperty;
	public int source;
	public int sourceproperty;
	public double multiplier;
	public int addend;
	public int targetSlot;
	public int sourceSlot;
	public int subTarget;
	public int subSource;
	public JSONArray targetPredicates;
	public JSONArray sourcePredicates;
	public int targetSlotCount;
	public int sourceSlotCount;

	@Override
	public String toString() {
		return "EffectConsequence{" +
				"id=" + id +
				", action=" + action +
				", target=" + target +
				", targetproperty=" + targetproperty +
				", source=" + source +
				", sourceproperty=" + sourceproperty +
				", multiplier=" + multiplier +
				", addend=" + addend +
				", targetSlot=" + targetSlot +
				", sourceSlot=" + sourceSlot +
				", subTarget=" + subTarget +
				", subSource=" + subSource +
				", targetPredicates=" + targetPredicates +
				", sourcePredicates=" + sourcePredicates +
				", targetSlotCount=" + targetSlotCount +
				", sourceSlotCount=" + sourceSlotCount +
				'}';
	}
}