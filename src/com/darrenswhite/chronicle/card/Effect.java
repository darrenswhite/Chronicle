package com.darrenswhite.chronicle.card;

/**
 * @author Darren White
 */
public class Effect {

	public int id;
	public String internalname;
	public int trigger;
	public int condition0;
	public int condition0value;
	public int condition1;
	public int condition1value;
	public int consequence0;
	public int consequence0value;
	public int consequence1;
	public int consequence1value;
	public int desc;

	@Override
	public String toString() {
		return "Effect{" +
				"id=" + id +
				", internalname='" + internalname + '\'' +
				", trigger=" + trigger +
				", condition0=" + condition0 +
				", condition0value=" + condition0value +
				", condition1=" + condition1 +
				", condition1value=" + condition1value +
				", consequence0=" + consequence0 +
				", consequence0value=" + consequence0value +
				", consequence1=" + consequence1 +
				", consequence1value=" + consequence1value +
				", desc=" + desc +
				'}';
	}
}