package com.darrenswhite.permutation;

import java.util.function.Consumer;

/**
 * @author Darren White
 */
public abstract class PermutationConsumer<T> implements Consumer<T> {

	public boolean start() {
		return true;
	}

	public void stop() {
	}
}