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
		List<Card> cards = CardCollection.getInstance().findAll(c -> true);

		cards.forEach(c -> {
			allCards.add(c);

			if (c.rarity != Card.RARITY_DIAMOND) {
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
				sb.append(c.name);
			} else {
				sb.append('_');
			}
		}

		return sb.toString();
	}

	private static boolean isComboValid(List<Card> combo) {
		int archetype = -1;

		for (Card c : combo) {
			if (c.archetype == Card.ARCHETYPE_ALL) {
				continue;
			}

			if (archetype == -1) {
				archetype = c.archetype;
			} else if (c.archetype != archetype) {
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
		int priority = ComboComparator.DAMAGE;
		int minHealth = 1;
		int limit = 10;
		int numCards = 4;

		TreeSet<Game> games = new TreeSet<>(new ComboComparator(priority, minHealth));
		List<Card> cards = getAllCards();
		Permutation<Card> permutation = new Permutation<>(cards, numCards, (o1, o2) -> o1.name.compareTo(o2.name));

		for (List<Card> combo : permutation) {
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