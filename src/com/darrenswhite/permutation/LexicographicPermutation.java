package com.darrenswhite.permutation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author Darren White
 */
public class LexicographicPermutation<T> implements Iterable<T[]> {

	private final T[] permutation;
	private final T[] elements;
	private final int n;
	private final int k;
	private final Comparator<? super T> cmp;
	private boolean hasNext = true;

	public LexicographicPermutation(T[] elements, int k) {
		this(elements, k, null);
	}

	public LexicographicPermutation(T[] elements, int k, Comparator<? super T> cmp) {
		this.elements = Arrays.copyOf(elements, elements.length);
		this.n = elements.length;
		this.k = k;
		this.cmp = cmp;
		permutation = Arrays.copyOf(elements, k);

		if (n < 1) {
			throw new IllegalArgumentException("Need at least 1 element!");
		}

		if (k < 0 || k > n) {
			throw new IllegalArgumentException("0 < k <= n!");
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
		int i = k - 1;
		int j = k;

		// Find the smallest j > k - 1 where a[j] > a[k - 1]
		while (j < n && compare(elements[i], elements[j]) >= 0) {
			j++;
		}

		if (j < n) {
			swap(i, j);
		} else {
			reverseRightOf(i);

			// i = (k - 1) - 1
			i--;

			// Find the largest index i such that a[i] < a[i + 1].
			while (i >= 0 && compare(elements[i], elements[i + 1]) >= 0) {
				i--;
			}

			// If no such index exists, the permutation is the
			// last permutation.
			if (i < 0) {
				hasNext = false;
				return;
			}

			// j = n - 1
			j--;

			// Find the largest index l greater than k such that
			// a[k] < a[l].
			while (j > i && compare(elements[i], elements[j]) >= 0) {
				j--;
			}

			// Swap the value of a[i] with that of a[j].
			swap(i, j);

			// Reverse the sequence from a[i + 1] up to and including
			// the final element a[n].
			reverseRightOf(i);
		}
	}

	@Override
	public Iterator<T[]> iterator() {
		return new Iterator<T[]>() {

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public T[] next() {
				return Arrays.copyOf(nextPermuation(), k);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private T[] nextPermuation() {
		System.arraycopy(elements, 0, permutation, 0, k);

		computeNext();

		return permutation;
	}

	private void reverseRightOf(int start) {
		int i = start + 1;
		int j = n - 1;

		while (i < j) {
			swap(i, j);

			i++;
			j--;
		}
	}

	private void swap(int x, int y) {
		T t = elements[x];

		elements[x] = elements[y];
		elements[y] = t;
	}
}