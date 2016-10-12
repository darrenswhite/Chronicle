package com.darrenswhite.chronicle.player;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.CardPredicate;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.effect.EffectAction;
import com.darrenswhite.chronicle.effect.EffectProperty;
import com.darrenswhite.chronicle.effect.IEffectTarget;
import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.game.Game;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class Player implements IEffectTarget {

	private static final int MAX_HAND_SIZE = 10;

	private final List<Card> hand = new ArrayList<>();
	private final Queue<Integer> temporaryAttacks = new LinkedList<>();
	private final Random rnd = new Random();
	private int attack;
	private int gold;
	private int armour;
	private Weapon weapon;
	private int maxHealth;
	private int health;
	private int pvpAttacks;

	public Player(int attack, int gold, int health, int maxHealth, int armour) {
		this.attack = attack;
		this.gold = gold;
		this.health = health;
		this.maxHealth = maxHealth;
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
		temporaryAttacks.offer(amount);
	}

	@Override
	public void applyToProperty(Game g, EffectProperty property, EffectAction action, List<CardPredicate> predicates, int value, int value2) {
		switch (property) {
			case RANDOM_CARD_HAND:
				if (action == EffectAction.ADD) {
					List<Card> all = ConfigProvider.getInstance().getCards(c -> {
						for (CardPredicate cardPredicate : predicates) {
							if (!cardPredicate.test(c)) {
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
								hand.add(ConfigProvider.getInstance().getCard(cardIDs[index]));
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
				}
				break;
			case EXHAUST:
				addTemporaryAttack(-attack + 1);
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
				if (weapon.durability <= 0) {
					weapon = null;
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
			case STRIKE:
				Player other;
				if (g.getPlayer() == this) {
					other = g.getRival();
				} else {
					other = g.getPlayer();
				}
				other.dealDamage(getTotalAttack());
				updateWeapon();
				break;
		}
	}

	@Override
	public void applyToProperty(Game g, EffectProperty property, EffectAction action, List<CardPredicate> predicates, Weapon weapon) {
		if (property != EffectProperty.WEAPON) {
			return;
		}

		switch (action) {
			case SET:
			case ADD:
				this.weapon = weapon.copy();
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

	public int getBaseAttack() {
		int base = attack;

		for (int i : temporaryAttacks) {
			base += i;
		}

		return base;
	}

	public int getGold() {
		return gold;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	@Override
	public int getPropertyValue(EffectProperty property, List<CardPredicate> predicates, int maxValue) {
		int value = 0;

		switch (property) {
			case ROUND_TIMER:
			case INSTANT_CARD_DRAW:
			case SPECIFIC_CARD_HAND:
			case SPECIFIC_CARD_DECK:
				value = maxValue;
				break;
			case DECK_SIZE:
				value = maxValue;
				break;
			case HAND_SIZE:
			case RANDOM_CARD_HAND:
				if (predicates != null && predicates.size() > 0) {
					List<Card> all = hand.stream().filter(c -> {
						for (CardPredicate cardPredicate : predicates) {
							if (!cardPredicate.test(c)) {
								return false;
							}
						}
						return true;
					}).collect(Collectors.toList());
					value = all == null ? 0 : all.size();
				} else {
					value = hand.size();
				}
				break;
			case PVP_ATTACK:
				value = pvpAttacks;
				break;
			case RANDOM_CARD_DECK:
				value = maxValue;
				break;
			case DAMAGE:
				value = maxValue;
				break;
			case OVERALL_ATTACK:
				value = attack;

				if (weapon != null) {
					value += weapon.attack;
					break;
				}
				break;
			case HEALTH:
				value = health;
				break;
			case MAX_HEALTH:
				value = maxHealth;
				break;
			case ATTACK:
				value = attack;
				break;
			case GOLD:
				value = gold;
				break;
			case ARMOUR:
				value = armour;
				break;
			case WEAPON_DURABILITY:
				if (weapon != null) {
					value = weapon.durability;
				}
				break;
			case WEAPON_ATTACK:
				if (weapon != null) {
					value = weapon.attack;
				}
				break;
			case WEAPON_COUNT:
				value = weapon == null ? 0 : 1;
				break;
		}

		if (maxValue <= 0) {
			return value;
		}

		return Math.min(maxValue, value);
	}

	public Queue<Integer> getTemporaryAttacks() {
		return temporaryAttacks;
	}

	public int getTotalAttack() {
		int total = getBaseAttack();

		if (weapon != null && weapon.durability > 0) {
			total += weapon.attack;
		}

		return total;
	}

	@Override
	public Weapon getWeapon() {
		return weapon;
	}

	private void removeHealth(int amount) {
		if (amount > 0) {
			health -= amount;
		}
	}

	public void setHealth(int health) {
		this.health = Math.min(maxHealth, health);
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
		if (weapon != null) {
			weapon.durability--;

			if (weapon.durability <= 0) {
				weapon = null;
			}
		}
	}
}