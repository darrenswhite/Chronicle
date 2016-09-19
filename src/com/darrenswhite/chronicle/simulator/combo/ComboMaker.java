package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.Legend;
import com.darrenswhite.chronicle.card.Rarity;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.player.Player;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Darren White
 */
public class ComboMaker implements Runnable {

	private final int numCards;
	private final int nThreads;

	public ComboMaker(int numCards, int nThreads) {
		this.numCards = numCards;
		this.nThreads = nThreads;
	}

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
		if (args.length == 0) {
			return;
		}

		int numCards = Integer.parseInt(args[args.length - 1]);
		int nThreads = 1;

		for (int i = 0; i < args.length - 1; i++) {
			String arg = args[i];
			String value = i < args.length - 1 ? args[i + 1] : null;
			String value2 = i < args.length - 2 ? args[i + 2] : null;

			switch (arg) {
				case "-t":
				case "--num-threads":
					if (value != null) {
						nThreads = Integer.parseInt(value);
						i++;
					}
					break;
				default:
					System.err.println("Unknown option: " + arg);
					return;
			}
		}

		ComboMaker combos = new ComboMaker(numCards, nThreads);

		combos.run();
	}

	@Override
	public void run() {
		Legend legend = Legend.ALL;

		Map<Integer, Future<Game>> queue = new HashMap<>();
		Card[] cards = getAllCards(legend);
		Permutation<Card> permutations = new Permutation<>(cards, numCards, (c1, c2) -> c1.getName().compareTo(c2.getName()));
		Path path = Paths.get("combos_" + numCards + ".csv.bz2");
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);

		try (PrintWriter out = new PrintWriter(new CompressorStreamFactory()
				.createCompressorOutputStream(CompressorStreamFactory.BZIP2, Files.newOutputStream(path)), true)) {
			new Thread(() -> {
				int index = 0;

				while (!executor.isTerminated() || !queue.isEmpty()) {
					synchronized (queue) {
						if (!queue.containsKey(index)) {
							continue;
						}

						try {
							Game g = queue.remove(index).get();

							if (g != null) {
								writeGame(g, out);
							}
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}

						index++;
					}
				}
			}).start();

			for (int i = 0; i < numCards; i++) {
				out.print("card" + i + "\t");
			}

			out.println("attack0\tgold0\thealth0\tarmour0\tweapon0\tmaxHealth0\tattack1\tgold1\thealth1\tarmour1\tweapon1\tmaxHealth1");

			int i = 0;

			for (Card[] combo : permutations) {
				synchronized (queue) {
					Future<Game> g = executor.submit(() -> executeCombo(combo));

					queue.put(i++, g);
				}
			}

			executor.shutdown();

			while (!executor.isTerminated()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException ignored) {
				}
			}
		} catch (CompressorException | IOException e) {
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
	}
}