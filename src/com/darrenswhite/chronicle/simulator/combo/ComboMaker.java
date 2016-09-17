package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.Legend;
import com.darrenswhite.chronicle.card.Rarity;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.game.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Darren White
 */
public class ComboMaker implements Runnable {

	private Game executeCombo(Card[] combo) {
		if (!isComboValid(combo)) {
			return null;
		}

		Game game = new Game();

		game.addCards(combo);
		game.start();

		return game;
	}

	private Card[] getAllCards(Legend legend) {
		List<Card> allCards = new ArrayList<>();
		List<Card> cards = ConfigProvider.getInstance().getAll(c -> true);

		cards.forEach(c -> {
			Source s = c.getSource();
			Legend l = c.getLegend();

			if (s == Source.NONE ||
					s == Source.FROM_EFFECT ||
					s == Source.PAGE_CARD) {
				return;
			}

			if (legend != Legend.ALL && l != Legend.ALL && l != legend) {
				return;
			}

			allCards.add(c);

			if (c.getRarity() != Rarity.SUPER_RARE) {
				allCards.add(c);
			}
		});

		return allCards.toArray(new Card[allCards.size()]);
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

	private static boolean isComboValid(Card[] combo) {
		Legend legend = null;

		for (Card c : combo) {
			Legend l = c.getLegend();

			if (l == Legend.ALL) {
				continue;
			}

			if (legend == null) {
				legend = l;
			} else if (l != legend) {
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
		Legend legend = Legend.ALL;
		int priority = ComboComparator.DAMAGE;
		int minHealth = 1;
		int limit = 10;
		int numCards = 3;

		TreeSet<Game> games = new TreeSet<>(new ComboComparator(priority, minHealth));
		Card[] cards = getAllCards(legend);
		Permutation<Card> permutation = new Permutation<>(cards, numCards, (o1, o2) -> o1.getName().compareTo(o2.getName()));

		for (Card[] combo : permutation) {
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