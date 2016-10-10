package com.darrenswhite.io;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Darren White
 */
public class FixedArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess {

	private static final int DEFAULT_CAPACITY = 10;
	private final transient Object[] elements;
	private final int capacity;
	private int size;

	public FixedArrayList(int capacity) {
		if (capacity > 0) {
			elements = new Object[this.capacity = capacity];
		} else {
			throw new IllegalArgumentException("Illegal capacity: " + capacity);
		}
	}

	@Override
	public boolean add(E e) {
		if (size >= capacity) {
			return false;
		}

		modCount++;
		elements[size++] = e;

		return true;
	}

	public int capacity() {
		return capacity;
	}

	@Override
	public void clear() {
		modCount++;

		for (int i = 0; i < size; i++) {
			elements[i] = null;
		}

		size = 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E get(int index) {
		return (E) elements[index];
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public boolean isFull() {
		return size == capacity;
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}

	@Override
	public E remove(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		modCount++;

		E oldValue = get(index);
		int numMoved = size - index - 1;

		if (numMoved > 0) {
			System.arraycopy(elements, index + 1, elements, index, numMoved);
		}

		elements[--size] = null;

		return oldValue;
	}

	@Override
	public int size() {
		return size;
	}

	private class Itr implements Iterator<E> {

		int cursor;
		int lastRet = -1;
		int expectedModCount = modCount;

		void checkForComodification() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void forEachRemaining(Consumer<? super E> consumer) {
			Objects.requireNonNull(consumer);

			int size = FixedArrayList.this.size;
			int i = cursor;

			if (i >= size) {
				return;
			}

			Object[] elements = FixedArrayList.this.elements;

			if (i >= elements.length) {
				throw new ConcurrentModificationException();
			}

			while (i != size && modCount == expectedModCount) {
				consumer.accept((E) elements[i++]);
			}

			cursor = i;
			lastRet = i - 1;

			checkForComodification();
		}

		@Override
		public boolean hasNext() {
			return cursor != size;
		}

		@Override
		@SuppressWarnings("unchecked")
		public E next() {
			checkForComodification();

			int i = cursor;

			if (i >= size) {
				throw new NoSuchElementException();
			}

			Object[] elements = FixedArrayList.this.elements;

			if (i >= elements.length) {
				throw new ConcurrentModificationException();
			}

			cursor = i + 1;

			return (E) elements[lastRet = i];
		}

		@Override
		public void remove() {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}

			checkForComodification();

			try {
				FixedArrayList.this.remove(lastRet);

				cursor = lastRet;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}
}