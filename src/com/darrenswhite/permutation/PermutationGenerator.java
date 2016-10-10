package com.darrenswhite.permutation;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Darren White
 */
public class PermutationGenerator<T, R> implements Runnable {

	protected final Iterator<T[]> it;
	protected final Function<T[], R> f;
	protected final PermutationConsumer<R> consumer;

	public PermutationGenerator(Iterator<T[]> it, Function<T[], R> f, PermutationConsumer<R> consumer) {
		this.it = it;
		this.f = f;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		System.out.println("Starting consumer...");

		if (!consumer.start()) {
			System.err.println("Consumer failed to start.");
			return;
		}

		System.out.println("Consumer started successfully.");

		System.out.println("Iterating permutations...");

		while (it.hasNext()) {
			consumer.accept(f.apply(it.next()));
		}

		System.out.println("Iteration completed successfully.");

		System.out.println("Stopping consumer...");

		consumer.stop();

		System.out.println("Consumer stopped successfully.");
	}
}