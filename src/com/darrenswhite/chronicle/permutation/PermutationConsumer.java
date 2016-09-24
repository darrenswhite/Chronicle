package com.darrenswhite.chronicle.permutation;

import java.util.function.Consumer;

/**
 * @author Darren White
 */
public interface PermutationConsumer<T> extends Consumer<T> {

	boolean start();

	void stop();
}