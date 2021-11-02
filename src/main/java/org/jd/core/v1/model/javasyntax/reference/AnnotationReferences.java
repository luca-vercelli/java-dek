/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javasyntax.reference;

import org.jd.core.v1.util.DefaultList;

import java.util.Collection;

public class AnnotationReferences extends DefaultList<AnnotationReference>
        implements BaseAnnotationReference {

    private static final long serialVersionUID = 9031417743809162767L;

    public AnnotationReferences() {
    }

    public AnnotationReferences(int capacity) {
        super(capacity);
    }

    public AnnotationReferences(Collection<AnnotationReference> collection) {
        super(collection);
    }

    @Override
    public void accept(ReferenceVisitor visitor) {
        visitor.visit(this);
    }
}
