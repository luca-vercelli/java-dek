/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An ArrayList with some more methods
 * 
 * @param <E>
 */
public class DefaultList<E> extends ArrayList<E> implements Base<E> {

	private static final long serialVersionUID = 6867881864799620874L;

	protected static final EmptyList<?> EMPTY_LIST = new EmptyList<>();

	public DefaultList() {
	}

	public DefaultList(int capacity) {
		super(capacity);
	}

	public DefaultList(Collection<E> collection) {
		super(collection);
	}

	@SafeVarargs
	public DefaultList(E element, E... elements) {
		ensureCapacity(elements.length + 1);

		add(element);

		for (E e : elements) {
			add(e);
		}
	}

	public DefaultList(E[] elements) {
		if ((elements != null) && (elements.length > 0)) {
			ensureCapacity(elements.length);

			for (E e : elements) {
				add(e);
			}
		}
	}

	@Override
	public E getFirst() {
		return (E) get(0);
	}

	@Override
	public E getLast() {
		return (E) get(size() - 1);
	}

	public E removeFirst() {
		return (E) remove(0);
	}

	public E removeLast() {
		return (E) remove(size() - 1);
	}

	@Override
	public boolean isList() {
		return true;
	}

	@Override
	public DefaultList<E> getList() {
		return this;
	}

	@SuppressWarnings("unchecked")
	public static <T> DefaultList<T> emptyList() {
		return (DefaultList<T>) EMPTY_LIST;
	}

	protected static class EmptyList<E> extends DefaultList<E> implements Iterator<E> {
		private static final long serialVersionUID = -1611271037753076432L;

		public EmptyList() {
			super(0);
		}

		public E set(int index, E e) {
			throw new UnsupportedOperationException();
		}

		public void add(int index, E e) {
			throw new UnsupportedOperationException();
		}

		public E remove(int index) {
			throw new UnsupportedOperationException();
		}

		public Iterator<E> iterator() {
			return this;
		}

		public boolean hasNext() {
			return false;
		}

		public E next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
