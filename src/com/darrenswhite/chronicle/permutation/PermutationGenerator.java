package com.darrenswhite.chronicle.permutation;

import java.util.Comparator;
import java.util.concurrent.*;

/**
 * @author Darren White
 */
public abstract class PermutationGenerator<T, R> implements Runnable {

	public static final int QUEUE_CAPACITY = (int) Math.pow(2, 15);

	private final BlockingQueue<Future<R>> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

	private Runnable createConsumerRunnable(PermutationConsumer<R> consumer) {
		return () -> {
			System.out.println("Consumer has been started and is accepting items.");

			while (consumer.isRunning() || !queue.isEmpty()) {
				try {
					Future<R> f = queue.take();

					if (f != null) {
						consumer.accept(f.get());
					}
				} catch (ExecutionException | InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("Consumer stopped and queue empty.");
		};
	}

	public abstract Comparator<? super T> getComparator();

	public abstract PermutationConsumer<R> getConsumer();

	public abstract T[] getElements();

	public abstract ExecutorService getExecutor();

	public abstract int getSamples();

	public abstract R process(T[] t);

	@Override
	public void run() {
		T[] elements = getElements();
		int k = getSamples();
		Comparator<? super T> cmp = getComparator();
		ExecutorService executor = getExecutor();
		LexicographicPermutation<T> permutations = new LexicographicPermutation<>(elements, k, cmp);
		PermutationConsumer<R> consumer = getConsumer();

		System.out.println("Starting consumer...");

		if (!consumer.start()) {
			System.err.println("Consumer failed to start.");
			return;
		}

		System.out.println("Consumer started successfully.");

		Thread t = new Thread(createConsumerRunnable(consumer));

		System.out.println("Starting consumer thread...");

		t.setPriority(Thread.MAX_PRIORITY);
		t.start();

		System.out.println("Iterating permutations...");

		for (T[] perm : permutations) {
			try {
				queue.put(executor.submit(() -> process(perm)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Shutting down consumer...");

		consumer.shutdown();

		System.out.println("Shutting down executor...");

		executor.shutdown();

		while (consumer.isRunning() || !executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ignored) {
			}
		}

		System.out.println("Stopping consumer...");

		consumer.stop();

		System.out.println("Consumer stopped successfully.");
	}
}