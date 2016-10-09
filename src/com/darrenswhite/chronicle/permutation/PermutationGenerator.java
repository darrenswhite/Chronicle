package com.darrenswhite.chronicle.permutation;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Darren White
 */
public class PermutationGenerator<T, R> implements Iterator<R> {

	protected final Iterator<T[]> it;
	protected final Function<T[], R> f;

	public PermutationGenerator(Iterator<T[]> it, Function<T[], R> f) {
		this.it = it;
		this.f = f;
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public R next() {
		return f.apply(it.next());
	}
}