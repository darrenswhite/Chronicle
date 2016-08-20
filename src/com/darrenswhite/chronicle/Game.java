package com.darrenswhite.chronicle;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.Weapon;
import com.darrenswhite.chronicle.player.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Darren White
 */
public class Game {

	private final Set<Card> cards = new LinkedHashSet<>();
	private int base;
	private int gold;
	private int health;
	private int armour;
	private Weapon weapon;
	private Player player;
	private Player rival;

	public Game(int base, int gold, int health, int armour, Weapon weapon) {
		this.base = base;
		this.gold = gold;
		this.health = health;
		this.armour = armour;
		this.weapon = weapon;

		reset();
	}

	public void addCard(Card c) {
		cards.add(c.getBuilder().create());
	}

	public void addCards(Collection<Card> cards) {
		this.cards.addAll(cards);
	}

	public int getArmour() {
		return armour;
	}

	public int getBase() {
		return base;
	}

	public Set<Card> getCards() {
		return cards;
	}

	public int getGold() {
		return gold;
	}

	public int getHealth() {
		return health;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getRival() {
		return rival;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void reset() {
		player = new Player(base, gold, health, armour, weapon);
		rival = new Player(base, gold, health, armour, weapon);
		cards.clear();
	}

	public void setArmour(int armour) {
		this.armour = armour;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public void start() {
		Iterator<Card> it = cards.iterator();

		while (player.getHealth() > 0 && it.hasNext()) {
			Card c = it.next();

			if (c == null) {
				return;
			}

			c.encounter(Game.this);

			if (player.getWeapon() != null && player.getWeapon().getDurability() == 0) {
				player.setWeapon(null);
			}
		}
	}
}