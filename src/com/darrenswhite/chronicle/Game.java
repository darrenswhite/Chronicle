package com.darrenswhite.chronicle;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.player.Player;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Darren White
 */
public class Game {

	private final List<Card> cards = new LinkedList<>();
	private final int base;
	private final int gold;
	private final int health;
	private final int armour;
	private Player player;
	private Player rival;
	private int index;

	public Game() {
		this(2, 0, 30, 0);
	}

	public Game(int base, int gold, int health, int armour) {
		this.base = base;
		this.gold = gold;
		this.health = health;
		this.armour = armour;

		reset();
	}

	public void addCard(Card c) {
		cards.add(c.copy());
	}

	public void addCards(Card[] cards) {
		for (Card c : cards) {
			addCard(c);
		}
	}

	public void addCards(Collection<Card> cards) {
		cards.forEach(this::addCard);
	}

	public int getArmour() {
		return armour;
	}

	public int getBase() {
		return base;
	}

	public List<Card> getCards() {
		return cards;
	}

	public int getGold() {
		return gold;
	}

	public int getHealth() {
		return health;
	}

	public <T extends Card> Optional<T> getNextCard() {
		return getNextCard(card -> true);
	}

	@SuppressWarnings("unchecked")
	public <T extends Card> Optional<T> getNextCard(Predicate<Card> predicate) {
		Iterator<Card> it = cards.iterator();
		int i = 0;

		while (it.hasNext()) {
			Card next = it.next();

			if (i++ > index && predicate.test(next)) {
				return Optional.of((T) next);
			}
		}

		return Optional.empty();
	}

	public Player getPlayer() {
		return player;
	}

	public Player getRival() {
		return rival;
	}

	public void reset() {
		player = new Player(base, gold, health, armour);
		rival = new Player(base, gold, health, armour);
		cards.clear();
	}

	public void start() {
		Iterator<Card> it = cards.iterator();

		index = 0;

		while (player.getHealth() > 0 && it.hasNext()) {
			Card c = it.next();

			if (c == null) {
				return;
			}

			c.encounter(this);

			if (player.temporaryAttack > 0) {
				player.temporaryAttack = 0;
			}

			if (player.getHealth() <= 0 || rival.getHealth() <= 0) {
				break;
			}

			index++;
		}
	}
}