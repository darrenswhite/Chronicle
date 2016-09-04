package com.darrenswhite.chronicle.simulator.combo;

import java.util.*;

/**
 * @author Darren White
 */
public class Permutation<T> implements Iterator<List<T>> {

	private final List<T> elements;
	private final List<T> result;
	private final int n;
	private final int r;
	private Comparator<? super T> cmp;
	private boolean hasNext = true;

	public Permutation(List<T> elements, int r) {
		this(elements, r, null);
	}

	public Permutation(List<T> elements, int r, Comparator<? super T> cmp) {
		Collections.sort(elements, cmp);

		this.elements = elements;
		this.n = elements.size();
		this.r = r;
		this.cmp = cmp;
		result = new LinkedList<>();

		if (n < 1) {
			throw new IllegalArgumentException("Need at least 1 element!");
		}

		if (r < 0 || r > n) {
			throw new IllegalArgumentException("0 < r <= n!");
		}
	}

	private int compare(T t1, T t2) {
		if (cmp != null) {
			return cmp.compare(t1, t2);
		} else {
			@SuppressWarnings("unchecked")
			Comparable<? super T> t = (Comparable<? super T>) t1;
			return t.compareTo(t2);
		}
	}

	private void computeNext() {
		int i = r - 1;
		int j = r;

		while (j < n && compare(elements.get(i), elements.get(j)) >= 0) {
			++j;
		}

		if (j < n) {
			swap(i, j);
		} else {
			reverseRightOf(i);
			--i;

			while (i >= 0 && compare(elements.get(i), elements.get(i + 1)) >= 0) {
				--i;
			}

			if (i < 0) {
				hasNext = false;
				return;
			}

			--j;

			while (j > i && compare(elements.get(i), elements.get(j)) >= 0) {
				--j;
			}

			swap(i, j);
			reverseRightOf(i);
		}
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public List<T> next() {
		result.clear();
		result.addAll(0, elements.subList(0, r));

		computeNext();

		return result;
	}

	private void reverseRightOf(final int start) {
		int i = start + 1;
		int j = n - 1;

		while (i < j) {
			swap(i, j);

			++i;
			--j;
		}
	}

	private void swap(final int x, final int y) {
		T t = elements.get(x);

		elements.set(x, elements.get(y));
		elements.set(y, t);
	}
}