/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javasyntax.type;

public interface Type extends BaseType, TypeArgument {
    String getName();

    String getDescriptor();

    int getDimension();

    /**
     * Return a type with same descriptor of this and given dimension. May return
     * this.
     * 
     * @param dimension
     * @return
     */
    Type createType(int dimension);
}
