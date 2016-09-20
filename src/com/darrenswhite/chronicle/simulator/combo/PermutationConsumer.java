package com.darrenswhite.chronicle.simulator.combo;

import java.util.function.Consumer;

/**
 * @author Darren White
 */
public interface PermutationConsumer<T> extends Consumer<T> {

	boolean start();

	void stop();
}