package com.darrenswhite.permutation;

import com.darrenswhite.io.FixedArrayList;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author Darren White
 */
public class ParallelPermutationGenerator<T, R> extends PermutationGenerator<T, R> {

	private final FixedArrayList<Future<R>> queue;
	private final ExecutorService executor;
	private R next = null;

	public ParallelPermutationGenerator(Iterator<T[]> it, Function<T[], R> f, PermutationConsumer<R> consumer, ExecutorService executor, int capacity) {
		super(it, f, consumer);
		this.executor = executor;
		queue = new FixedArrayList<>(capacity);
	}

	private void consumeQueue() {
		Future<R> f;

		Iterator<Future<R>> i = queue.iterator();
		while (i.hasNext()) {
			f = i.next();

			if (f.isDone()) {
				try {
					consumer.accept(f.get());
				} catch (ExecutionException | InterruptedException e) {
					e.printStackTrace();
				}

				i.remove();
			}
		}
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
			if (!queue.isFull()) {
				T[] next = it.next();
				queue.add(executor.submit(() -> f.apply(next)));
			}

			consumeQueue();
		}

		while (!queue.isEmpty()) {
			consumeQueue();
		}

		System.out.println("Iteration completed successfully.");

		System.out.println("Shutting down executor service...");

		executor.shutdown();

		while (!executor.isTerminated()) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Stopping consumer...");

		consumer.stop();

		System.out.println("Consumer stopped successfully.");
	}
}