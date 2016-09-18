package com.darrenswhite.chronicle.game;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.player.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class Game {

	private final List<Card> cards = new LinkedList<>();
	private final List<Card> cardHistory = new LinkedList<>();
	private final int base;
	private final int gold;
	private final int health;
	private final int maxHealth;
	private final int armour;
	private Player p;
	private Player rival;
	private int currentSlot;
	private int decay;

	public Game() {
		this(2, 0, 30, 30, 0);
	}

	public Game(int base, int gold, int health, int maxHealth, int armour) {
		this.base = base;
		this.gold = gold;
		this.health = health;
		this.maxHealth = maxHealth;
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

	public List<Card> getCardHistory() {
		return cardHistory;
	}

	public List<Card> getCardHistory(Predicate<Card> filter) {
		return cardHistory.stream().filter(filter).collect(Collectors.toList());
	}

	public List<Card> getCards() {
		return cards;
	}

	public Card getCurrentCard() {
		return cards.get(currentSlot);
	}

	public List<Card> getNextCards(Predicate<Card> predicate) {
		List<Card> matches = new LinkedList<>();
		Iterator<Card> it = cards.iterator();
		int i = 0;

		while (it.hasNext()) {
			Card next = it.next();

			if (i++ > currentSlot && predicate.test(next)) {
				matches.add(next);
			}
		}

		return matches;
	}

	public Player getPlayer() {
		return p;
	}

	public Player getRival() {
		return rival;
	}

	public void reset() {
		p = new Player(base, gold, health, maxHealth, armour);
		rival = new Player(base, gold, health, maxHealth, armour);
		cards.clear();
		cardHistory.clear();
	}

	public void start() {
		Iterator<Card> it = cards.iterator();

		decay = 0;
		currentSlot = 0;

		while (p.getHealth() > 0 && it.hasNext()) {
			Card c = it.next();

			if (c == null) {
				return;
			}

			c.encounter(this);

			updateTemporaryAttack();

			cardHistory.add(c);

			if (p.getHealth() <= 0 || rival.getHealth() <= 0) {
				break;
			}

			currentSlot++;
		}
	}

	private void updateTemporaryAttack() {
		if (p.getTemporaryAttacks().size() > 0) {
			decay += p.getTemporaryAttacks().size();
		}

		if (decay != 0 && decay - 2 >= 0) {
			do {
				p.getTemporaryAttacks().poll();
			} while (p.getTemporaryAttacks().size() > 1);

			decay -= 2;
		}
	}
}