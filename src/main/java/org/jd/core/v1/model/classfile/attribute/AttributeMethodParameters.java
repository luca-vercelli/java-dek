/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.classfile.attribute;

import java.util.Arrays;

public class AttributeMethodParameters implements Attribute {
    protected MethodParameter[] parameters;

    public AttributeMethodParameters(MethodParameter[] parameters) {
        this.parameters = parameters;
    }

    public MethodParameter[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + Arrays.asList(parameters);
    }
}
