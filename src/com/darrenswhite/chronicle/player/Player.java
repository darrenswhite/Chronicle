package com.darrenswhite.chronicle.player;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.equipment.Weapon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Darren White
 */
public class Player {

	private final List<Card> cards = new ArrayList<>();
	public int attack;
	public int gold;
	public int armour;
	public Weapon weapon;
	public int maxHealth = 30;
	public int temporaryAttack;
	private int health;

	public Player(int attack, int gold, int health, int armour) {
		this.attack = attack;
		this.gold = gold;
		this.health = health;
		this.armour = armour;
	}

	public void addCard(Card c) {
		if (cards.size() < 10) {
			cards.add(c);
		}
	}

	public void addHealth(int amount) {
		health = Math.min(maxHealth, health + amount);
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

	public Optional<Card> getCard(Predicate<Card> filter) {
		return cards.stream().filter(filter).findAny();
	}

	public List<Card> getCards() {
		return cards;
	}

	public int getHealth() {
		return health;
	}

	public int getTotalAttack() {
		int total = attack + temporaryAttack;

		if (weapon != null && weapon.getDurability() > 0) {
			total += weapon.getAttack();
		}

		return total;
	}

	public boolean removeCard(Predicate<Card> filter) {
		Iterator<Card> it = cards.iterator();

		while (it.hasNext()) {
			if (filter.test(it.next())) {
				it.remove();
				return true;
			}
		}

		return false;
	}

	public void removeCard(Card c) {
		if (cards.contains(c)) {
			cards.remove(c);
		}
	}

	public void removeHealth(int amount) {
		if (amount > 0) {
			health = Math.max(0, health - amount);
		}
	}

	public void setHealth(int health) {
		this.health = Math.min(maxHealth, health);
	}

	public void stealHealth(Player p, int amount) {
		int startHealth = health;
		int endHealth = Math.min(startHealth + amount, maxHealth);
		int steal = endHealth - startHealth;

		p.removeHealth(steal);
		setHealth(endHealth);
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
		if (weapon != null && weapon.getDurability() > 0) {
			weapon.setDurability(weapon.getDurability() - 1);

			if (weapon.getDurability() == 0) {
				weapon = null;
			}
		}
	}
}