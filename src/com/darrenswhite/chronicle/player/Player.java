package com.darrenswhite.chronicle.player;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.CardPredicate;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.effect.EffectAction;
import com.darrenswhite.chronicle.effect.EffectProperty;
import com.darrenswhite.chronicle.effect.IEffectTarget;
import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.stats.Healable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class Player implements Healable, IEffectTarget {

	private static final int MAX_HAND_SIZE = 10;
	private final List<Card> hand = new ArrayList<>();
	private int attack;
	private int gold;
	private int armour;
	private Weapon weapon;
	private int maxHealth = 30;
	private int temporaryAttack;
	private int health;
	private int pvpAttacks;
	private Random rnd = new Random();

	public Player(int attack, int gold, int health, int armour) {
		this.attack = attack;
		this.gold = gold;
		this.health = health;
		this.armour = armour;
	}

	public void addArmour(int amount) {
		if (amount > 0) {
			armour += amount;
		}
	}

	public void addAttack(int amount) {
		if (amount > 0) {
			attack += amount;
		}
	}

	public void addGold(int amount) {
		if (amount > 0) {
			gold += amount;
		}
	}

	public void addHealth(int amount) {
		health = Math.min(maxHealth, health + amount);
	}

	public void addTemporaryAttack(int amount) {
		temporaryAttack += amount;
	}

	@Override
	public void applyToProperty(EffectProperty property, EffectAction action, List<CardPredicate> predicates, int value, int value2) {
		switch (property) {
			case RANDOM_CARD_HAND:
				if (action == EffectAction.ADD) {
					List<Card> all = ConfigProvider.getInstance().getAll(c -> {
						for (CardPredicate cardPredicate : predicates) {
							if (!cardPredicate.predicate(c)) {
								return false;
							}
						}
						return c.getSource() != Source.NONE;
					});

					if (all.size() > 0) {
						int[] cardIDs = new int[value];
						boolean[] success = new boolean[value];
						int num = 0;

						for (int index = 0; index < value; index++) {
							Card cardInstance = all.get(rnd.nextInt(all.size()));
							if (cardInstance != null) {
								cardIDs[index] = cardInstance.getId();
								if (hand.size() + num < MAX_HAND_SIZE) {
									num++;
									success[index] = true;
								} else {
									success[index] = false;
								}
							} else {
								cardIDs[index] = -1;
								success[index] = false;
							}
						}

						for (int index = 0; index < cardIDs.length; index++) {
							if (success[index]) {
								ConfigProvider.getInstance().get(cardIDs[index]).ifPresent(hand::add);
							}
						}
					}
				}
				break;
			case PVP_ATTACK:
				if (action == EffectAction.ADD) {
					pvpAttacks = pvpAttacks + value;
				} else if (action == EffectAction.REMOVE) {
					pvpAttacks = pvpAttacks - value;
				}
				break;
			case DAMAGE:
				if (action == EffectAction.ADD) {
					dealDamage(value);
					break;
				}
				break;
			case TEMP_ATTACK:
				if (action == EffectAction.ADD) {
					addTemporaryAttack(value);
					break;
				}
				break;
			case EXHAUST:
				setTemporaryAttack(-getAttack() + 1);
				break;
			case HEALTH:
				switch (action) {
					case SET:
						setHealth(value);
						break;
					case ADD:
						addHealth(value);
						break;
					case REMOVE:
						removeHealth(value);
						break;
				}
				break;
			case MAX_HEALTH:
				switch (action) {
					case SET:
						maxHealth = value;
						break;
					case ADD:
						maxHealth += value;
						break;
					case REMOVE:
						maxHealth -= value;
						break;
				}
				break;
			case ATTACK:
				switch (action) {
					case SET:
						attack = value;
						break;
					case ADD:
						attack += value;
						break;
					case REMOVE:
						attack -= value;
						break;
				}
				break;
			case GOLD:
				switch (action) {
					case SET:
						gold = value;
						break;
					case ADD:
						gold += value;
						break;
					case REMOVE:
						gold -= value;
						break;
				}
				break;
			case ARMOUR:
				switch (action) {
					case SET:
						armour = value;
						break;
					case ADD:
						armour += value;
						break;
					case REMOVE:
						armour -= value;
						break;
				}
				break;
			case WEAPON_DURABILITY:
				if (weapon == null) {
					break;
				}
				switch (action) {
					case SET:
						weapon.durability = value;
						break;
					case ADD:
						weapon.durability += value;
						break;
					case REMOVE:
						weapon.durability -= value;
						break;
				}
				break;
			case WEAPON_ATTACK:
				if (weapon == null) {
					break;
				}
				switch (action) {
					case SET:
						weapon.attack = value;
						break;
					case ADD:
						weapon.attack += value;
						break;
					case REMOVE:
						weapon.attack -= value;
						break;
				}
				break;
		}
	}

	@Override
	public void applyToProperty(EffectProperty property, EffectAction action, List<CardPredicate> predicates, Weapon weapon) {
		if (property != EffectProperty.WEAPON) {
			return;
		}

		switch (action) {
			case SET:
			case ADD:
				this.weapon = weapon;
				break;
			case REMOVE:
				this.weapon = null;
				break;
		}
	}

	public void dealDamage(int amount) {
		int hit = amount;

		if (armour > 0) {
			hit -= armour;
			armour = Math.max(0, armour - amount);
		}

		if (hit > 0) {
			removeHealth(hit);
		}
	}

	public int getArmour() {
		return armour;
	}

	public int getAttack() {
		return attack + temporaryAttack;
	}

	public int getBaseAttack() {
		return attack;
	}

	public int getGold() {
		return gold;
	}

	@Override
	public int getHealth() {
		return health;
	}

	@Override
	public int getPropertyValue(EffectProperty property, List<CardPredicate> predicates, int maxValue) {
		int val1 = 0;

		switch (property) {
			case ROUND_TIMER:
			case INSTANT_CARD_DRAW:
			case SPECIFIC_CARD_HAND:
			case SPECIFIC_CARD_DECK:
				val1 = maxValue;
				break;
			case DECK_SIZE:
				val1 = maxValue;
				break;
			case HAND_SIZE:
			case RANDOM_CARD_HAND:
				if (predicates != null && predicates.size() > 0) {
					List<Card> all = hand.stream().filter(c -> {
						for (CardPredicate cardPredicate : predicates) {
							if (!cardPredicate.predicate(c)) {
								return false;
							}
						}
						return true;
					}).collect(Collectors.toList());
					val1 = all == null ? 0 : all.size();
				} else {
					val1 = hand.size();
				}
				break;
			case PVP_ATTACK:
				val1 = pvpAttacks;
				break;
			case RANDOM_CARD_DECK:
				val1 = maxValue;
				break;
			case DAMAGE:
				val1 = maxValue;
				break;
			case OVERALL_ATTACK:
				val1 = attack;

				if (weapon != null) {
					val1 += weapon.attack;
					break;
				}
				break;
			case HEALTH:
				val1 = health;
				break;
			case MAX_HEALTH:
				val1 = maxHealth;
				break;
			case ATTACK:
				val1 = attack;
				break;
			case GOLD:
				val1 = gold;
				break;
			case ARMOUR:
				val1 = armour;
				break;
			case WEAPON_DURABILITY:
				if (weapon != null) {
					val1 = weapon.durability;
					break;
				}
				break;
			case WEAPON_ATTACK:
				if (weapon != null) {
					val1 = weapon.attack;
					break;
				}
				break;
			case WEAPON_COUNT:
				val1 = weapon == null ? 0 : 1;
				break;
		}

		if (maxValue <= 0) {
			return val1;
		}

		return Math.min(maxValue, val1);
	}

	@Override
	public Weapon getPropertyValueWeapon() {
		return weapon;
	}

	public int getTemporaryAttack() {
		return temporaryAttack;
	}

	public int getTotalAttack() {
		int total = attack + temporaryAttack;

		if (weapon != null && weapon.durability > 0) {
			total += weapon.attack;
		}

		return total;
	}

	public void removeTemporaryAttack(int amount) {
		if (amount > 0) {
			temporaryAttack -= amount;
		}
	}

	@Override
	public void setHealth(int health) {
		this.health = Math.min(maxHealth, health);
	}

	public void setTemporaryAttack(int temporaryAttack) {
		this.temporaryAttack = temporaryAttack;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public boolean spendGold(int amount) {
		if (amount < 0 || amount > gold) {
			return false;
		}

		gold -= amount;
		return true;
	}

	@Override
	public String toString() {
		return "Player{" +
				"attack=" + attack +
				", gold=" + gold +
				", health=" + health +
				", armour=" + armour +
				", weapon=" + weapon +
				", maxHealth=" + maxHealth +
				", totalAttack=" + getTotalAttack() +
				'}';
	}

	public void updateWeapon() {
		if (weapon != null && weapon.durability > 0) {
			weapon.durability--;

			if (weapon.durability == 0) {
				weapon = null;
			}
		}
	}
}