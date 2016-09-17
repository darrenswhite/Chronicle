package com.darrenswhite.chronicle.game;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.player.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Darren White
 */
public class Game {

	private final List<Card> cards = new LinkedList<>();
	private final List<Card> cardHistory = new LinkedList<>();
	private final Stack<Integer> temporaryAttack = new Stack<>();
	private final int base;
	private final int gold;
	private final int health;
	private final int armour;
	private Player p;
	private Player rival;
	private int currentSlot;
	private int decay;

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

	public Stream<Card> getCardHistory(Predicate<Card> filter) {
		return cardHistory.stream().filter(filter);
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
		p = new Player(base, gold, health, armour);
		rival = new Player(base, gold, health, armour);
		cards.clear();
		cardHistory.clear();
		temporaryAttack.clear();
	}

	public void start() {
		Iterator<Card> it = cards.iterator();

		temporaryAttack.add(0);
		decay = 0;
		currentSlot = 0;

		while (p.getHealth() > 0 && it.hasNext()) {
			Card c = it.next();

			if (c == null) {
				return;
			}

			c.encounter(this);

			if (p.getHealth() <= 0 || rival.getHealth() <= 0) {
				break;
			}

			updateTemporaryAttack();

			cardHistory.add(c);
			currentSlot++;
		}
	}

	private void updateTemporaryAttack() {
		if (p.getTemporaryAttack() != 0) {
			decay++;
		}

		if (decay > 0 && decay % 2 == 0) {
			p.removeTemporaryAttack(temporaryAttack.pop());
			decay -= 2;
		}

		if (p.getTemporaryAttack() != temporaryAttack.lastElement()) {
			temporaryAttack.push(p.getTemporaryAttack() - temporaryAttack.lastElement());
		}
	}
}