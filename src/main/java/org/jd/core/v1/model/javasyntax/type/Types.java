/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javasyntax.type;

import org.jd.core.v1.util.DefaultList;

import java.util.Collection;

/**
 * A list of Type that is itself a BaseType
 */
public class Types extends DefaultList<Type> implements BaseType {

    private static final long serialVersionUID = 1777952094308317187L;

    public Types() {
    }

    public Types(int capacity) {
        super(capacity);
    }

    public Types(Collection<Type> collection) {
        super(collection);
    }

    public Types(Type type, Type... types) {
        super(type, types);
        assert (types != null) && (types.length > 0) : "Uses 'Type' implementation instead";
    }

    @Override
    public boolean isTypes() {
        return true;
    }

    @Override
    public void accept(TypeVisitor visitor) {
        visitor.visit(this);
    }
}
