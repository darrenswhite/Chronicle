package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.CardCollection;
import com.darrenswhite.chronicle.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Darren White
 */
public class ComboMaker implements Runnable {

	private Game executeCombo(List<Card> combo) {
		if (!isComboValid(combo)) {
			return null;
		}

		Game game = new Game();
		Player p = game.getPlayer();
		Player rival = game.getRival();

		game.addCards(combo);
		game.start();

		return game;
	}

	private List<Card> getAllCards() {
		List<Card> allCards = new ArrayList<>();
		List<Card> cards = CardCollection.findAll(c -> true);

		cards.forEach(c -> {
			allCards.add(c);

			if (c.getRarity() != Card.Rarity.DIAMOND) {
				allCards.add(c);
			}
		});

		return allCards;
	}

	private String getCardsString(Collection<Card> cards) {
		StringBuilder sb = new StringBuilder();

		for (Card c : cards) {
			if (sb.length() > 0) {
				sb.append(" -> ");
			}

			if (c != null) {
				sb.append(c.getName());
			} else {
				sb.append('_');
			}
		}

		return sb.toString();
	}

	private static boolean isComboValid(List<Card> combo) {
		Card.Legend legend = null;

		for (Card c : combo) {
			if (c.getLegend() == Card.Legend.ALL) {
				continue;
			}

			if (legend == null) {
				legend = c.getLegend();
			} else if (c.getLegend() != legend) {
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		ComboMaker combos = new ComboMaker();

		combos.run();
	}

	@Override
	public void run() {
		int priority = ComboComparator.ARMOUR;
		int minHealth = 1;
		int limit = 5;
		int numCards = 4;

		TreeSet<Game> games = new TreeSet<>(new ComboComparator(priority, minHealth));
		List<Card> cards = getAllCards();
		Permutation<Card> it = new Permutation<>(cards, numCards, (o1, o2) -> o1.getName().compareTo(o2.getName()));

		while (it.hasNext()) {
			List<Card> combo = it.next();
			Game game = executeCombo(combo);

			if (game == null) {
				continue;
			}

			games.add(game);

			if (games.size() > limit) {
				games.pollLast();
			}
		}

		games.forEach(g -> {
			System.out.println(getCardsString(g.getCards()));
			System.out.println("Player: " + g.getPlayer());
			System.out.println("Rival: " + g.getRival());
			System.out.println();
		});
	}
}