/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.classfile.constant;

public abstract class Constant {

    protected ConstantPoolTag tag;

    public Constant(ConstantPoolTag tag) {
        this.tag = tag;
    }

    public ConstantPoolTag getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + tag;
    }
}
