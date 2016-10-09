package com.darrenswhite.chronicle.permutation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @author Darren White
 */
public class ParallelPermutationGenerator<T, R> extends PermutationGenerator<T, R> {

	private final ExecutorService executor;
	private final BlockingQueue<Future<R>> queue;
	private Future<R> next = null;

	public ParallelPermutationGenerator(Iterator<T[]> it, Function<T[], R> f, ExecutorService executor, int capacity) {
		super(it, f);
		this.executor = executor;
		queue = new ArrayBlockingQueue<>(capacity);
	}

	@Override
	public boolean hasNext() {
		while (next == null && (it.hasNext() || !queue.isEmpty() || !executor.isTerminated())) {
			next = queue.poll();

			if (next == null) {
				if (it.hasNext()) {
					try {
						queue.put(executor.submit(() -> f.apply(it.next())));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (queue.isEmpty() && !it.hasNext() && !executor.isShutdown()) {
				System.out.println("Shutting down executor...");
				executor.shutdown();
			}
		}

		return next != null;
	}

	@Override
	public R next() {
		Future<R> n = next;

		if (n != null) {
			next = null;
		} else {
			throw new NoSuchElementException();
		}

		try {
			return n.get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}