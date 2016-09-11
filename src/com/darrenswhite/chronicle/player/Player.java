package com.darrenswhite.chronicle.player;

import com.darrenswhite.chronicle.card.Card;

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
	public int base;
	public int gold;
	public int health;
	public int armour;
	public int weaponAttack;
	public int weaponDurability;
	public int maxHealth = 30;
	public int temporaryAttack;

	public Player(int base, int gold, int health, int armour) {
		this.base = base;
		this.gold = gold;
		this.health = health;
		this.armour = armour;
	}

	public void addCard(Card c) {
		if (cards.size() < 10) {
			cards.add(c);
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

	public Optional<Card> getCard(Predicate<Card> filter) {
		return cards.stream().filter(filter).findAny();
	}

	public List<Card> getCards() {
		return cards;
	}

	public int getTotalAttack() {
		int total = base + temporaryAttack;

		if (weaponDurability > 0) {
			total += weaponAttack;
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

	@Override
	public String toString() {
		return "Player{" +
				"base=" + base +
				", gold=" + gold +
				", health=" + health +
				", armour=" + armour +
				", weaponAttack=" + weaponAttack +
				", weaponDurability=" + weaponDurability +
				", maxHealth=" + maxHealth +
				", temporaryAttack=" + temporaryAttack +
				'}';
	}

	public void updateWeapon() {
		if (weaponDurability > 0) {
			weaponDurability--;

			if (weaponDurability == 0) {
				weaponAttack = 0;
			}
		}
	}
}