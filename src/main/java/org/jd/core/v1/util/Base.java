/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Either a single object of type T or a list of objects of type T. If the
 * object is not a list, it shall be considered a 1-element long list.
 * 
 * WARNING type hierarchy is wrong: T is confused with Base<T>
 *
 * @param <T>
 */
public interface Base<T> extends Iterable<T> {

	/**
	 * True if this is a List type.
	 * 
	 * List subtypes should redeclare this.
	 */
	default boolean isList() {
		return false;
	}

	/**
	 * Get first element
	 */
	@SuppressWarnings("unchecked")
	default T getFirst() {
		return (T) this;
	}

	/**
	 * Get last element
	 */
	@SuppressWarnings("unchecked")
	default T getLast() {
		return (T) this;
	}

	/**
	 * Convert to DefaultList
	 */
	default DefaultList<T> getList() {
		throw new UnsupportedOperationException();
	}

	default int size() {
		return 1;
	}

	@Override
	default Iterator<T> iterator() {
		return new Iterator<T>() {
			private boolean hasNext = true;

			public boolean hasNext() {
				return hasNext;
			}

			@SuppressWarnings("unchecked")
			public T next() {
				if (hasNext) {
					hasNext = false;
					return (T) Base.this;
				}
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
