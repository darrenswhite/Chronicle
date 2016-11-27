package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.Legend;
import com.darrenswhite.chronicle.card.Rarity;
import com.darrenswhite.chronicle.card.Source;
import com.darrenswhite.chronicle.config.ConfigProvider;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.permutation.LexicographicPermutation;
import com.darrenswhite.permutation.ParallelPermutationGenerator;
import com.darrenswhite.permutation.PermutationConsumer;
import com.darrenswhite.permutation.PermutationGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/**
 * @author Darren White
 */
public class GamePermutationGenerator implements Runnable {

	private final int numCards;
	private final int parallelism;
	private final int capacity;
	private final PermutationConsumer<Game> consumer;
	private final String[] names;

	public GamePermutationGenerator(int numCards, int parallelism, int capacity, PermutationConsumer<Game> consumer, String[] names) {
		this.numCards = numCards;
		this.parallelism = parallelism;
		this.capacity = capacity;
		this.consumer = consumer;
		this.names = names;
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
		int capacity = -1;
		String[] names = null;
		Path out = Paths.get("permutations", numCards + ".csv.bz2");

		for (int i = 0; i < args.length - 1; i++) {
			String arg = args[i];
			String value = i < args.length - 2 ? args[i + 1] : null;
			String value2 = i < args.length - 3 ? args[i + 2] : null;

			switch (arg) {
				case "-c":
				case "--capacity":
					if (value != null) {
						capacity = Integer.parseInt(value);
						i++;
					} else {
						throw new IllegalArgumentException(noValueMessage(arg));
					}
					break;
				case "-n":
				case "--card-names":
					if (value != null) {
						names = value.split(",");
						i++;
					} else {
						throw new IllegalArgumentException(noValueMessage(arg));
					}
					break;
				case "-o":
				case "--output-file":
					if (value != null) {
						out = Paths.get(value);
						i++;
					} else {
						throw new IllegalArgumentException(noValueMessage(arg));
					}
					break;
				case "-p":
				case "--parallelism":
					if (value != null) {
						parallelism = Integer.parseInt(value);
						i++;
					} else {
						throw new IllegalArgumentException(noValueMessage(arg));
					}
					break;
				default:
					throw new IllegalArgumentException("Unknown option: " + arg);
			}
		}

		if (capacity == -1) {
			capacity = parallelism;
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
				parallelism, capacity, consumer, names);

		combos.run();

		System.out.println("Finished in: " + (System.currentTimeMillis() - t) + "ms.");
	}

	private static String noValueMessage(String arg) {
		return arg + " option specified with no value!";
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
		Iterator<Card[]> it = new LexicographicPermutation<>(getAllCards(), numCards);
		PermutationGenerator<Card, Game> generator;

		if (names != null) {
			it = new FilteredIterator<>(it, cards -> {
				for (Card c : cards) {
					for (String name : names) {
						if (c.getName().equalsIgnoreCase(name)) {
							return true;
						}
					}
				}

				return false;
			});
		}

		if (parallelism > 1) {
			ExecutorService executor = Executors.newWorkStealingPool(parallelism);
			generator = new ParallelPermutationGenerator<>(it, this::process, consumer, executor, capacity);
		} else {
			generator = new PermutationGenerator<>(it, this::process, consumer);
		}

		generator.run();
	}

	private class FilteredIterator<E> implements Iterator<E> {

		private final Iterator<E> it;
		private final Predicate<E> predicate;
		private E next;

		public FilteredIterator(Iterator<E> it, Predicate<E> predicate) {
			Objects.requireNonNull(it);
			Objects.requireNonNull(predicate);
			this.it = it;
			this.predicate = predicate;
		}

		@Override
		public boolean hasNext() {
			if (next == null && !it.hasNext()) {
				throw new NoSuchElementException();
			}

			while (next == null && it.hasNext()) {
				E e = it.next();

				if (predicate.test(e)) {
					next = e;
				}
			}

			return next != null;
		}

		@Override
		public E next() {
			E e = next;
			next = null;
			return e;
		}
	}
}