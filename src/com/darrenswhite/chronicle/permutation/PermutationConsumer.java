package com.darrenswhite.chronicle.permutation;

import java.util.function.Consumer;

/**
 * @author Darren White
 */
public abstract class PermutationConsumer<T> implements Consumer<T> {

	private volatile boolean running = false;

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean start() {
		running = true;

		return true;
	}

	public void stop() {
	}
}