/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javafragment;

import org.jd.core.v1.model.fragment.EndFlexibleBlockFragment;

public class EndBlockFragment extends EndFlexibleBlockFragment {
    protected StartBlockFragment start;

    public EndBlockFragment(int minimalLineCount, int lineCount, int maximalLineCount, int weight, String label,
            StartBlockFragment start) {
        super(minimalLineCount, lineCount, maximalLineCount, weight, label);
        this.start = start;
        start.setEndArrayInitializerBlockFragment(this);
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public StartBlockFragment getStartArrayInitializerBlockFragment() {
        return start;
    }

    @Override
    public boolean incLineCount(boolean force) {
        if (lineCount < maximalLineCount) {
            lineCount++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean decLineCount(boolean force) {
        if (lineCount > minimalLineCount) {
            lineCount--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void accept(JavaFragmentVisitor visitor) {
        visitor.visit(this);
    }
}
