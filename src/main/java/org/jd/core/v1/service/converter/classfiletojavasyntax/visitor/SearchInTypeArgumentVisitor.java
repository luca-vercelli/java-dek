/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import org.jd.core.v1.model.javasyntax.type.AbstractTypeArgumentVisitor;
import org.jd.core.v1.model.javasyntax.type.GenericType;
import org.jd.core.v1.model.javasyntax.type.WildcardExtendsTypeArgument;
import org.jd.core.v1.model.javasyntax.type.WildcardSuperTypeArgument;
import org.jd.core.v1.model.javasyntax.type.WildcardTypeArgument;

public class SearchInTypeArgumentVisitor extends AbstractTypeArgumentVisitor {
    protected boolean wildcardFound;
    protected boolean wildcardSuperOrExtendsTypeFound;
    protected boolean genericFound;

    public SearchInTypeArgumentVisitor() {
        init();
    }

    public void init() {
        wildcardFound = false;
        wildcardSuperOrExtendsTypeFound = false;
        genericFound = false;
    }

    public boolean containsWildcard() {
        return wildcardFound;
    }

    public boolean containsWildcardSuperOrExtendsType() {
        return wildcardSuperOrExtendsTypeFound;
    }

    public boolean containsGeneric() {
        return genericFound;
    }

    @Override
    public void visit(WildcardTypeArgument type) {
        wildcardFound = true;
    }

    @Override
    public void visit(WildcardExtendsTypeArgument type) {
        wildcardSuperOrExtendsTypeFound = true;
        type.getType().accept(this);
    }

    @Override
    public void visit(WildcardSuperTypeArgument type) {
        wildcardSuperOrExtendsTypeFound = true;
        type.getType().accept(this);
    }

    @Override
    public void visit(GenericType type) {
        genericFound = true;
    }
}
