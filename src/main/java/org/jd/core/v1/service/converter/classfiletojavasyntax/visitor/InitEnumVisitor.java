/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import static org.jd.core.v1.model.classfile.AccessType.*;

import java.util.Comparator;

import org.jd.core.v1.model.javasyntax.AbstractJavaSyntaxVisitor;
import org.jd.core.v1.model.javasyntax.declaration.BodyDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ConstructorDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.EnumDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.FieldDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.FieldDeclarator;
import org.jd.core.v1.model.javasyntax.declaration.FormalParameters;
import org.jd.core.v1.model.javasyntax.declaration.MethodDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.StaticInitializerDeclaration;
import org.jd.core.v1.model.javasyntax.expression.BaseExpression;
import org.jd.core.v1.model.javasyntax.expression.Expression;
import org.jd.core.v1.model.javasyntax.expression.Expressions;
import org.jd.core.v1.model.javasyntax.expression.IntegerConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.NewExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileBodyDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileEnumDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileFieldDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileMethodDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileTypeDeclaration;
import org.jd.core.v1.util.DefaultList;

public class InitEnumVisitor extends AbstractJavaSyntaxVisitor {
    protected ClassFileBodyDeclaration bodyDeclaration = null;
    protected BodyDeclaration constantBodyDeclaration = null;
    protected DefaultList<ClassFileEnumDeclaration.ClassFileConstant> constants = new DefaultList<>();
    protected int lineNumber;
    protected int index;
    protected BaseExpression arguments;

    /**
     * Expose a sorted copy of internal <code>constants</code> field
     */
    public DefaultList<EnumDeclaration.Constant> getConstants() {
        if (!constants.isEmpty()) {
            constants.sort(new EnumConstantComparator());
        }

        return new DefaultList(constants);
    }

    /**
     * Accumulate <code>constants</code> during visit
     */
    @Override
    public void visit(BodyDeclaration declaration) {
        final ClassFileBodyDeclaration bd = bodyDeclaration;

        bodyDeclaration = (ClassFileBodyDeclaration) declaration;
        constants.clear();
        safeAcceptListDeclaration(bodyDeclaration.getFieldDeclarations());
        safeAcceptListDeclaration(bodyDeclaration.getMethodDeclarations());
        bodyDeclaration = bd;
    }

    /**
     * Mark some constructors as synthetic, and fix others
     */
    @Override
    public void visit(ConstructorDeclaration declaration) {
        if ((declaration.getFlags() & ACC_ANONYMOUS) != 0) {
            declaration.setFlags(ACC_SYNTHETIC);
        } else if (declaration.getStatements().size() <= 1) {
            declaration.setFlags(ACC_SYNTHETIC);
        } else {
            FormalParameters parameters = (FormalParameters) declaration.getFormalParameters();
            // Remove name & index parameterTypes
            parameters.subList(0, 2).clear();
            // Remove super constructor call
            declaration.getStatements().getList().remove(0);
            // Fix flags
            declaration.setFlags(0);
        }
    }

    @Override
    public void visit(StaticInitializerDeclaration declaration) {
    }

    /**
     * Mark methods <code>values</code> and <code>valueOf</code> as synthetic
     */
    @Override
    public void visit(MethodDeclaration declaration) {
        if ((declaration.getFlags() & (ACC_STATIC | ACC_PUBLIC)) != 0) {
            // LV 2021-10 check method signature
            if ((declaration.getName().equals("values") && declaration.getDescriptor().startsWith("()"))
                    || (declaration.getName().equals("valueOf")
                            && declaration.getDescriptor().startsWith("(Ljava/lang/String;)"))) {
                ClassFileMethodDeclaration cfmd = (ClassFileMethodDeclaration) declaration;
                cfmd.setFlags(cfmd.getFlags() | ACC_SYNTHETIC);
            }
        }
    }

    /**
     * Mark all non-enum fields as synthetic.
     */
    @Override
    public void visit(FieldDeclaration declaration) {
        if ((declaration.getFlags() & ACC_ENUM) != 0) {
            ClassFileFieldDeclaration cffd = (ClassFileFieldDeclaration) declaration;
            cffd.getFieldDeclarators().accept(this);
            cffd.setFlags(cffd.getFlags() | ACC_SYNTHETIC);
        }
    }

    @Override
    public void visit(FieldDeclarator declaration) {
        constantBodyDeclaration = null;
        safeAccept(declaration.getVariableInitializer());
        constants.add(new ClassFileEnumDeclaration.ClassFileConstant(lineNumber, declaration.getName(), index,
                arguments, constantBodyDeclaration));
    }

    @Override
    public void visit(NewExpression expression) {
        Expressions parameters = (Expressions) expression.getParameters();
        Expression exp = parameters.get(1);

        if (exp.isCastExpression()) {
            exp = exp.getExpression();
        }

        IntegerConstantExpression ice = (IntegerConstantExpression) exp;

        lineNumber = expression.getLineNumber();
        index = ice.getIntegerValue();

        // Remove name & index
        if (parameters.size() == 2) {
            arguments = null;
        } else {
            parameters.subList(0, 2).clear();
            arguments = parameters;
        }

        String enumInternalTypeName = expression.getObjectType().getInternalName();

        if (!enumInternalTypeName.equals(bodyDeclaration.getInternalTypeName())) {
            ClassFileTypeDeclaration typeDeclaration = bodyDeclaration.getInnerTypeDeclaration(enumInternalTypeName);
            if (typeDeclaration != null) {
                constantBodyDeclaration = typeDeclaration.getBodyDeclaration();
            }
        }
    }

    /**
     * Order ClassFileConstant's by index
     */
    protected static class EnumConstantComparator implements Comparator<ClassFileEnumDeclaration.ClassFileConstant> {
        public int compare(ClassFileEnumDeclaration.ClassFileConstant ec1,
                ClassFileEnumDeclaration.ClassFileConstant ec2) {
            return ec1.getIndex() - ec2.getIndex();
        }
    }
}
