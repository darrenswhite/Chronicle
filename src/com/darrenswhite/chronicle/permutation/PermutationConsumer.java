package com.darrenswhite.chronicle.permutation;

import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * @author Darren White
 */
public abstract class PermutationConsumer<T> implements Consumer<Future<T>> {

	private volatile boolean running = false;

	public final boolean isRunning() {
		return running;
	}

	public final void shutdown() {
		running = false;
	}

	public boolean start() {
		running = true;

		return true;
	}

	public void stop() {
	}
}