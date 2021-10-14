/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.util;

import java.util.Stack;

/**
 * A stack
 */
public class DefaultStack<E> extends Stack<E> {

	private static final long serialVersionUID = -5876733783687054972L;

	public DefaultStack() {
	}

	/**
	 * Create a stack with same elements of <code>other</code> and in same order.
	 * 
	 * Similar to <code>other.clone()</code>
	 * @param other
	 */
	public DefaultStack(DefaultStack<E> other) {
		elementData = other.elementData.clone();
		elementCount = other.elementCount;
	}

	/**
	 * Replace content of this queue with content of other queue
	 * 
	 * @param other
	 */
	@SuppressWarnings("unchecked")
	public void copy(DefaultStack<E> other) {
		if (elementData.length < other.elementCount) {
			elementData = (E[]) new Object[other.elementCount];
		}

		System.arraycopy(other.elementData, 0, elementData, 0, other.elementCount);
		elementCount = other.elementCount;
	}

	/**
	 * Replace all occurrences of <code>old</code> in the stack with
	 * <code>nevv</code>
	 * 
	 * @param old
	 * @param nevv
	 */
	public void replace(E old, E nevv) {
		int i = elementCount - 1;

		// WARNING using == not .equals()
		while ((i >= 0) && (elementData[i] == old)) {
			elementData[i--] = nevv;
		}
	}
}
