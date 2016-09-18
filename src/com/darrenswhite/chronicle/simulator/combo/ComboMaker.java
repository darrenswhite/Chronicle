package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.Legend;
import com.darrenswhite.chronicle.card.Rarity;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.player.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
				allCards.add(c.copy());
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
		int numCards = 3;

		Card[] cards = getAllCards(legend);
		Permutation<Card> permutations = new Permutation<>(cards, numCards, (c1, c2) -> c1.getName().compareTo(c2.getName()));
		Path path = Paths.get("combos_" + numCards + ".csv");

		try (PrintWriter out = new PrintWriter(Files.newOutputStream(path))) {
			for (int i = 0; i < numCards; i++) {
				out.print("card" + i + "\t");
			}

			out.println("attack0\tgold0\thealth0\tarmour0\tweapon0\tmaxHealth0\tattack1\tgold1\thealth1\tarmour1\tweapon1\tmaxHealth1");
			out.flush();

			for (Card[] perm : permutations) {
				Card[] combo = Arrays.copyOf(perm, perm.length);

				Game g = executeCombo(combo);

				if (g != null) {
					writeGame(g, out);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeGame(Game g, PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		Player p = g.getPlayer();
		Player r = g.getRival();

		for (Card card : g.getCards()) {
			sb.append(card.getName()).append('\t');
		}

		sb.append(p.getAttack()).append('\t');
		sb.append(p.getGold()).append('\t');
		sb.append(p.getHealth()).append('\t');
		sb.append(p.getArmour()).append('\t');
		sb.append(p.getWeapon() != null ? p.getWeapon() : "").append('\t');
		sb.append(p.getMaxHealth()).append('\t');

		sb.append(r.getAttack()).append('\t');
		sb.append(r.getGold()).append('\t');
		sb.append(r.getHealth()).append('\t');
		sb.append(r.getArmour()).append('\t');
		sb.append(r.getWeapon() != null ? r.getWeapon() : "").append('\t');
		sb.append(r.getMaxHealth()).append('\t');

		out.println(sb.toString());
		out.flush();
	}
}