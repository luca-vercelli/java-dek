/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.fragment;

import org.jd.core.v1.model.javafragment.JavaFragment;

/**
 * A Fragment that can be moved
 */
public abstract class FlexibleFragment implements Fragment, JavaFragment {
	protected final int minimalLineCount;
	protected int maximalLineCount;
	protected int initialLineCount;
	protected int lineCount;
	protected final int weight;
	protected final String label;

	public FlexibleFragment(int minimalLineCount, int lineCount, int maximalLineCount, int weight, String label) {
		this.minimalLineCount = minimalLineCount;
		this.maximalLineCount = maximalLineCount;
		this.initialLineCount = this.lineCount = lineCount;
		this.weight = weight;
		this.label = label;
	}

	public void resetLineCount() {
		lineCount = initialLineCount;
	}

	public int getMinimalLineCount() {
		return minimalLineCount;
	}

	public int getMaximalLineCount() {
		return maximalLineCount;
	}

	public int getInitialLineCount() {
		return initialLineCount;
	}

	public int getLineCount() {
		return lineCount;
	}

	public int getWeight() {
		return weight;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Increment line count, within maximalLineCount
	 * 
	 * @param force here it is ignored, it may be used in subclasses
	 */
	public boolean incLineCount(boolean force) {
		if (lineCount < maximalLineCount) {
			lineCount++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Decrement line count, if not below minimalLineCount
	 * 
	 * @param force here it is ignored, it may be used in subclasses
	 */
	public boolean decLineCount(boolean force) {
		if (lineCount > minimalLineCount) {
			lineCount--;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [lines:" + minimalLineCount + "-" + maximalLineCount + "]";
	}

	@Override
	public void accept(FragmentVisitor visitor) {
		visitor.visit(this);
	}
}
