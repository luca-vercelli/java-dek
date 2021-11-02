/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import java.util.Map;

import org.jd.core.v1.model.javasyntax.AbstractJavaSyntaxVisitor;
import org.jd.core.v1.model.javasyntax.expression.ConstructorReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.DoubleConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.EnumConstantReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.FloatConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.IntegerConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.LocalVariableReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.LongConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.NullExpression;
import org.jd.core.v1.model.javasyntax.expression.ObjectTypeReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.SuperExpression;
import org.jd.core.v1.model.javasyntax.expression.ThisExpression;
import org.jd.core.v1.model.javasyntax.expression.TypeReferenceDotClassExpression;
import org.jd.core.v1.model.javasyntax.reference.InnerObjectReference;
import org.jd.core.v1.model.javasyntax.reference.ObjectReference;
import org.jd.core.v1.model.javasyntax.statement.BreakStatement;
import org.jd.core.v1.model.javasyntax.statement.ByteCodeStatement;
import org.jd.core.v1.model.javasyntax.statement.ContinueStatement;
import org.jd.core.v1.model.javasyntax.type.InnerObjectType;
import org.jd.core.v1.model.javasyntax.type.ObjectType;
import org.jd.core.v1.model.javasyntax.type.TypeArguments;
import org.jd.core.v1.model.javasyntax.type.TypeParameterWithTypeBounds;
import org.jd.core.v1.model.javasyntax.type.Types;
import org.jd.core.v1.model.javasyntax.type.WildcardExtendsTypeArgument;
import org.jd.core.v1.model.javasyntax.type.WildcardSuperTypeArgument;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileLocalVariableReferenceExpression;

/**
 * Replace all local variables according to given mapping.
 */
public class RenameLocalVariablesVisitor extends AbstractJavaSyntaxVisitor {
    protected Map<String, String> nameMapping;

    public void init(Map<String, String> nameMapping) {
        this.nameMapping = nameMapping;
    }

    /**
     * Replace all local variables according to given mapping.
     */
    @Override
    public void visit(LocalVariableReferenceExpression expression) {
        ClassFileLocalVariableReferenceExpression lvre = (ClassFileLocalVariableReferenceExpression) expression;
        String newName = nameMapping.get(lvre.getName());

        if (newName != null) {
            lvre.getLocalVariable().setName(newName);
        }
    }

    @Override
    public void visit(FloatConstantExpression expression) {
    }

    @Override
    public void visit(IntegerConstantExpression expression) {
    }

    @Override
    public void visit(ConstructorReferenceExpression expression) {
    }

    @Override
    public void visit(DoubleConstantExpression expression) {
    }

    @Override
    public void visit(EnumConstantReferenceExpression expression) {
    }

    @Override
    public void visit(LongConstantExpression expression) {
    }

    @Override
    public void visit(BreakStatement statement) {
    }

    @Override
    public void visit(ByteCodeStatement statement) {
    }

    @Override
    public void visit(ContinueStatement statement) {
    }

    @Override
    public void visit(NullExpression expression) {
    }

    @Override
    public void visit(ObjectTypeReferenceExpression expression) {
    }

    @Override
    public void visit(SuperExpression expression) {
    }

    @Override
    public void visit(ThisExpression expression) {
    }

    @Override
    public void visit(TypeReferenceDotClassExpression expression) {
    }

    @Override
    public void visit(ObjectReference reference) {
    }

    @Override
    public void visit(InnerObjectReference reference) {
    }

    @Override
    public void visit(TypeArguments type) {
    }

    @Override
    public void visit(WildcardExtendsTypeArgument type) {
    }

    @Override
    public void visit(ObjectType type) {
    }

    @Override
    public void visit(InnerObjectType type) {
    }

    @Override
    public void visit(WildcardSuperTypeArgument type) {
    }

    @Override
    public void visit(Types types) {
    }

    @Override
    public void visit(TypeParameterWithTypeBounds type) {
    }
}
