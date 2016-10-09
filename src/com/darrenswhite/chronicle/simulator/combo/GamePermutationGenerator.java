package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.Legend;
import com.darrenswhite.chronicle.card.Rarity;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.permutation.LexicographicPermutation;
import com.darrenswhite.chronicle.permutation.ParallelPermutationGenerator;
import com.darrenswhite.chronicle.permutation.PermutationConsumer;
import com.darrenswhite.chronicle.permutation.PermutationGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Darren White
 */
public class GamePermutationGenerator implements Runnable {

	private final int numCards;
	private final int parallelism;
	private final PermutationConsumer<Game> consumer;

	public GamePermutationGenerator(int numCards, int parallelism, PermutationConsumer<Game> consumer) {
		this.numCards = numCards;
		this.parallelism = parallelism;
		this.consumer = consumer;
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

		allCards.sort(Card::compareTo);

		return allCards.toArray(new Card[allCards.size()]);
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
			System.err.println("No number of cards parameter specified.");
			return;
		}

		long t = System.currentTimeMillis();
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

		System.out.println("Finished in: " + (System.currentTimeMillis() - t) + "ms.");
	}

	private Game process(Card[] permutation) {
		if (!isPermutationValid(permutation)) {
			return null;
		}

		Game g = new Game();

		g.addCards(permutation);
		g.start();

		if (g.getPlayer().getHealth() <= 0) {
			return null;
		}

		return g;
	}

	@Override
	public void run() {
		Iterator<Card[]> it = new LexicographicPermutation<>(getAllCards(), numCards).iterator();
		PermutationGenerator<Card, Game> generator;

		if (parallelism > 1) {
			ExecutorService executor = Executors.newWorkStealingPool(parallelism);
			generator = new ParallelPermutationGenerator<>(it, this::process, executor, parallelism);
		} else {
			generator = new PermutationGenerator<>(it, this::process);
		}

		System.out.println("Starting consumer...");

		if (!consumer.start()) {
			System.err.println("Consumer failed to start.");
			return;
		}

		System.out.println("Consumer started successfully.");

		System.out.println("Iterating permutations...");

		while (generator.hasNext()) {
			consumer.accept(generator.next());
		}

		System.out.println("Iteration completed successfully.");

		System.out.println("Stopping consumer...");

		consumer.stop();

		System.out.println("Consumer stopped successfully.");
	}
}