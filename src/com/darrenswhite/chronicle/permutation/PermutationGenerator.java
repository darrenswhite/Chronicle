package com.darrenswhite.chronicle.permutation;

import java.util.Comparator;
import java.util.concurrent.*;

/**
 * @author Darren White
 */
public abstract class PermutationGenerator<T, R> implements Runnable {

	private final BlockingQueue<Future<R>> queue = new LinkedBlockingQueue<>();

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

		startConsumerThread();

		for (T[] perm : permutations) {
			synchronized (queue) {
				queue.offer(executor.submit(() -> process(perm)));
			}
		}

		executor.shutdown();

		while (!executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ignored) {
			}
		}
	}

	private void startConsumerThread() {
		new Thread(() -> {
			PermutationConsumer<R> consumer = getConsumer();
			ExecutorService executor = getExecutor();

			if (!consumer.start()) {
				System.err.println("Consumer start failed!");
				return;
			}

			while (!executor.isTerminated() || !queue.isEmpty()) {
				synchronized (queue) {
					try {
						Future<R> f = queue.poll(100, TimeUnit.MILLISECONDS);

						if (f != null) {
							consumer.accept(f.get());
						}
					} catch (ExecutionException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			consumer.stop();
		}).start();
	}
}