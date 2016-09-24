package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.Legend;
import com.darrenswhite.chronicle.card.Rarity;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.permutation.PermutationConsumer;
import com.darrenswhite.chronicle.permutation.PermutationGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Darren White
 */
public class GamePermutationGenerator extends PermutationGenerator<Card, Game> {

	private final BlockingQueue<Future<Game>> queue = new LinkedBlockingQueue<>();
	private final int numCards;
	private final PermutationConsumer<Game> consumer;
	private final ExecutorService executor;
	private final Card[] cards;

	public GamePermutationGenerator(int numCards, int parallelism, PermutationConsumer<Game> consumer) {
		this.numCards = numCards;
		this.consumer = consumer;
		executor = Executors.newWorkStealingPool(parallelism);
		cards = getAllCards();

		Arrays.sort(cards, getComparator());
	}

	private Card[] getAllCards() {
		List<Card> allCards = new ArrayList<>();
		List<Card> cards = ConfigProvider.getInstance().getCards(c -> {
			Source s = c.getSource();
			Legend l = c.getLegend();

			return s != Source.NONE &&
					s != Source.FROM_EFFECT &&
					s != Source.PAGE_CARD;
		});

		cards.forEach(c -> {
			allCards.add(c);

			if (c.getRarity() != Rarity.SUPER_RARE) {
				allCards.add(c.copy());
			}
		});

		return allCards.toArray(new Card[allCards.size()]);
	}

	@Override
	public Comparator<? super Card> getComparator() {
		return (c1, c2) -> Integer.compare(c1.getId(), c2.getId());
	}

	@Override
	public PermutationConsumer<Game> getConsumer() {
		return consumer;
	}

	@Override
	public Card[] getElements() {
		return cards;
	}

	@Override
	public ExecutorService getExecutor() {
		return executor;
	}

	@Override
	public int getSamples() {
		return numCards;
	}

	private boolean isPermutationValid(Card[] permutation) {
		Legend legend = null;

		for (Card c : permutation) {
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
		int parallelism = 1;
		Path out = Paths.get("permutations", numCards + ".csv.bz2");

		for (int i = 0; i < args.length - 1; i++) {
			String arg = args[i];
			String value = i < args.length - 1 ? args[i + 1] : null;
			String value2 = i < args.length - 2 ? args[i + 2] : null;

			switch (arg) {
				case "-o":
				case "--output-file":
					if (value != null) {
						out = Paths.get(value);
						i++;
					} else {
						throw new IllegalArgumentException(arg +
								" option specified with no value!");
					}
					break;
				case "-p":
				case "--parallelism":
					if (value != null) {
						parallelism = Integer.parseInt(value);
						i++;
					} else {
						throw new IllegalArgumentException(arg +
								" option specified with no value!");
					}
					break;
				default:
					System.err.println("Unknown option: " + arg);
					return;
			}
		}

		try {
			Files.createDirectories(out.getParent());
		} catch (IOException e) {
			System.err.println("Unable to create directories: " +
					e.getMessage());
			return;
		}

		PermutationConsumer<Game> consumer = new GamePermutationConsumer(out, numCards);
		GamePermutationGenerator combos = new GamePermutationGenerator(numCards,
				parallelism, consumer);

		combos.run();
	}

	@Override
	public Game process(Card[] permutation) {
		if (!isPermutationValid(permutation)) {
			return null;
		}

		Game game = new Game();

		game.addCards(permutation);
		game.start();

		if (game.getPlayer().getHealth() <= 0) {
			return null;
		}

		return game;
	}
}