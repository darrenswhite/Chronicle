package com.darrenswhite.chronicle.permutation;

import java.util.Comparator;
import java.util.concurrent.*;

/**
 * @author Darren White
 */
public abstract class PermutationGenerator<T, R> implements Runnable {

	public static final int QUEUE_CAPACITY = (int) Math.pow(2, 15);

	private final BlockingQueue<Future<R>> queue = new ArrayBlockingQueue<>(1024);

	private Runnable createConsumerRunnable(PermutationConsumer<R> consumer) {
		return () -> {
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

		if (!consumer.start()) {
			System.err.println("Consumer failed to start!");
			return;
		}

		Thread t = new Thread(createConsumerRunnable(consumer));

		t.setPriority(Thread.MAX_PRIORITY);
		t.start();

		for (T[] perm : permutations) {
			try {
				queue.put(executor.submit(() -> process(perm)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		consumer.setRunning(false);
		executor.shutdown();

		while (consumer.isRunning() || !executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ignored) {
			}
		}

		consumer.stop();
	}
}