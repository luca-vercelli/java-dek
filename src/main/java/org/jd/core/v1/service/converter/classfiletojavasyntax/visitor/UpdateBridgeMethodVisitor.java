/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import static org.jd.core.v1.model.classfile.AccessType.ACC_STATIC;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jd.core.v1.model.javasyntax.AbstractJavaSyntaxVisitor;
import org.jd.core.v1.model.javasyntax.declaration.AnnotationDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.BodyDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ClassDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ConstructorDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.EnumDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.InterfaceDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.MethodDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.StaticInitializerDeclaration;
import org.jd.core.v1.model.javasyntax.expression.BaseExpression;
import org.jd.core.v1.model.javasyntax.expression.BinaryOperatorExpression;
import org.jd.core.v1.model.javasyntax.expression.Expression;
import org.jd.core.v1.model.javasyntax.expression.Expressions;
import org.jd.core.v1.model.javasyntax.expression.FieldReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.MethodInvocationExpression;
import org.jd.core.v1.model.javasyntax.expression.ObjectTypeReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.PostOperatorExpression;
import org.jd.core.v1.model.javasyntax.expression.PreOperatorExpression;
import org.jd.core.v1.model.javasyntax.expression.SuperExpression;
import org.jd.core.v1.model.javasyntax.statement.BaseStatement;
import org.jd.core.v1.model.javasyntax.statement.Statement;
import org.jd.core.v1.model.javasyntax.type.BaseType;
import org.jd.core.v1.model.javasyntax.type.ObjectType;
import org.jd.core.v1.model.javasyntax.type.PrimitiveType;
import org.jd.core.v1.model.javasyntax.type.Type;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileBodyDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileConstructorOrMethodDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileMethodDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileLocalVariableReferenceExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileMethodInvocationExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;
import org.jd.core.v1.util.DefaultList;

/**
 * Bridge methods are required by generics type erasure
 * 
 * However, this class does a bit of confusion between synthetic and bridge
 * methods.
 * 
 * @see https://docs.oracle.com/javase/tutorial/java/generics/bridgeMethods.html
 * 
 * 
 */
public class UpdateBridgeMethodVisitor extends AbstractUpdateExpressionVisitor {
    protected BodyDeclarationsVisitor bodyDeclarationsVisitor = new BodyDeclarationsVisitor();
    protected Map<String, Map<String, ClassFileMethodDeclaration>> bridgeMethodDeclarations = new HashMap<>();
    protected TypeMaker typeMaker;
    protected Stack<ClassFileMethodDeclaration> context = new Stack<>();

    public UpdateBridgeMethodVisitor(TypeMaker typeMaker) {
        this.typeMaker = typeMaker;
    }

    public boolean init(ClassFileBodyDeclaration bodyDeclaration) {
        bridgeMethodDeclarations.clear();
        bodyDeclarationsVisitor.visit(bodyDeclaration);
        return !bridgeMethodDeclarations.isEmpty();
    }

    @Override
    public void visit(MethodDeclaration declaration) {
        context.push((ClassFileMethodDeclaration) declaration);
        super.visit(declaration);
        context.pop();
    }

    @Override
    protected Expression updateExpression(Expression expression) {
        if (!expression.isMethodInvocationExpression()) {
            return expression;
        }

        ClassFileMethodInvocationExpression mie1 = (ClassFileMethodInvocationExpression) expression;
        Map<String, ClassFileMethodDeclaration> map = bridgeMethodDeclarations
                .get(mie1.getExpression().getType().getDescriptor());

        if (map == null) {
            return expression;
        }

        ClassFileMethodDeclaration bridgeMethodDeclaration = map.get(mie1.getName() + mie1.getDescriptor());

        if (bridgeMethodDeclaration == null) {
            return expression;
        }

        // here, expression is an invocation of a bridge method
        Statement statement = bridgeMethodDeclaration.getStatements().getFirst();
        Expression exp;

        if (statement.isReturnExpressionStatement()) {
            exp = statement.getExpression();
        } else if (statement.isExpressionStatement()) {
            exp = statement.getExpression();
        } else {
            return expression;
        }

        BaseType parameterTypes = bridgeMethodDeclaration.getParameterTypes();
        int parameterTypesCount = (parameterTypes == null) ? 0 : parameterTypes.size();

        if (exp.isFieldReferenceExpression()) {
            FieldReferenceExpression fre = getFieldReferenceExpression(exp);

            expression = (parameterTypesCount == 0) ? fre.getExpression() : mie1.getParameters().getFirst();

            FieldReferenceExpression returnExpression = new FieldReferenceExpression(mie1.getLineNumber(),
                    fre.getType(), expression, fre.getInternalTypeName(), fre.getName(), fre.getDescriptor());

            // LV 2021 now check if SuperExpression required
            Expression superExpression = getSuperExpressionIfRequired(bridgeMethodDeclaration);
            if (superExpression != null) {
                returnExpression.setExpression(superExpression);
            }
            return returnExpression;

        } else if (exp.isMethodInvocationExpression()) {
            MethodInvocationExpression mie2 = (MethodInvocationExpression) exp;
            TypeMaker.MethodTypes methodTypes = typeMaker.makeMethodTypes(mie2.getInternalTypeName(), mie2.getName(),
                    mie2.getDescriptor());

            if (methodTypes != null) {
                if (mie2.getExpression().isObjectTypeReferenceExpression()) {
                    // Static method invocation
                    return new ClassFileMethodInvocationExpression(mie1.getLineNumber(), null, methodTypes.returnedType,
                            mie2.getExpression(), mie2.getInternalTypeName(), mie2.getName(), mie2.getDescriptor(),
                            methodTypes.parameterTypes, mie1.getParameters());
                } else {
                    BaseExpression mie1Parameters = mie1.getParameters();
                    BaseExpression newParameters = null;

                    switch (mie1Parameters.size()) {
                    case 0:
                    case 1:
                        break;
                    case 2:
                        newParameters = mie1Parameters.getList().get(1);
                        break;
                    default:
                        DefaultList<Expression> p = mie1Parameters.getList();
                        newParameters = new Expressions(p.subList(1, p.size()));
                        break;
                    }

                    ClassFileMethodInvocationExpression returnExpression = new ClassFileMethodInvocationExpression(
                            mie1.getLineNumber(), null, methodTypes.returnedType, mie1Parameters.getFirst(),
                            mie2.getInternalTypeName(), mie2.getName(), mie2.getDescriptor(),
                            methodTypes.parameterTypes, newParameters);
                    // LV 2021 now check if SuperExpression required
                    Expression superExpression = getSuperExpressionIfRequired(bridgeMethodDeclaration);
                    if (superExpression != null) {
                        returnExpression.setExpression(superExpression);
                    }
                    return returnExpression;
                }
            }
        } else if (exp.isBinaryOperatorExpression()) {
            FieldReferenceExpression fre = getFieldReferenceExpression(exp.getLeftExpression());

            if (parameterTypesCount == 1) {
                return new BinaryOperatorExpression(mie1.getLineNumber(), mie1.getType(),
                        new FieldReferenceExpression(fre.getType(), fre.getExpression(), fre.getInternalTypeName(),
                                fre.getName(), fre.getDescriptor()),
                        exp.getOperator(), mie1.getParameters().getFirst(), exp.getPriority());
            } else if (parameterTypesCount == 2) {
                DefaultList<Expression> parameters = mie1.getParameters().getList();

                return new BinaryOperatorExpression(mie1.getLineNumber(), mie1.getType(),
                        new FieldReferenceExpression(fre.getType(), parameters.get(0), fre.getInternalTypeName(),
                                fre.getName(), fre.getDescriptor()),
                        exp.getOperator(), parameters.get(1), exp.getPriority());
            }
        } else if (exp.isPostOperatorExpression()) {
            FieldReferenceExpression fre = getFieldReferenceExpression(exp.getExpression());

            expression = (parameterTypesCount == 0) ? fre.getExpression() : mie1.getParameters().getFirst();

            return new PostOperatorExpression(mie1.getLineNumber(), new FieldReferenceExpression(fre.getType(),
                    expression, fre.getInternalTypeName(), fre.getName(), fre.getDescriptor()), exp.getOperator());
        } else if (exp.isPreOperatorExpression()) {
            FieldReferenceExpression fre = getFieldReferenceExpression(exp.getExpression());

            expression = (parameterTypesCount == 0) ? fre.getExpression() : mie1.getParameters().getFirst();

            return new PreOperatorExpression(mie1.getLineNumber(), exp.getOperator(), new FieldReferenceExpression(
                    fre.getType(), expression, fre.getInternalTypeName(), fre.getName(), fre.getDescriptor()));
        } else if (exp.isIntegerConstantExpression()) {
            return exp;
        }

        return expression;
    }

    /**
     * If bridgeMethodDeclaration is in a class different from context, add "super"
     * expression
     */
    private Expression getSuperExpressionIfRequired(ClassFileMethodDeclaration bridgeMethodDeclaration) {
        Expression superExpression = null;
        try {
            ClassFileMethodDeclaration ctx = context.peek();
            if (!ctx.getBodyDeclaration().getInternalTypeName()
                    .equals(bridgeMethodDeclaration.getBodyDeclaration().getInternalTypeName())) {

                ObjectType typeCtx = typeMaker.makeFromInternalTypeName(ctx.getBodyDeclaration().getInternalTypeName());
                ObjectType typeBridge = typeMaker
                        .makeFromInternalTypeName(bridgeMethodDeclaration.getBodyDeclaration().getInternalTypeName());
                if (typeMaker.isAssignable(Collections.emptyMap(), typeBridge, typeCtx)) {

                    // I guess it's the supertype
                    // could it be the supertype of the supertype?! FIXME
                    superExpression = new SuperExpression(typeCtx);
                }
            }
        } catch (EmptyStackException exc) {
            // do nothing
        }
        return superExpression;
    }

    protected static FieldReferenceExpression getFieldReferenceExpression(Expression expression) {
        FieldReferenceExpression fre = (FieldReferenceExpression) expression;
        Expression freExpression = fre.getExpression();

        if ((freExpression != null) && freExpression.isObjectTypeReferenceExpression()) {
            ((ObjectTypeReferenceExpression) freExpression).setExplicit(true);
        }

        return fre;
    }

    /**
     * Load bridge (or synthetic?!) methods into
     * <code>bridgeMethodDeclarations</code> during visit.
     */
    protected class BodyDeclarationsVisitor extends AbstractJavaSyntaxVisitor {
        protected Map<String, ClassFileMethodDeclaration> map = null;

        @Override
        public void visit(ClassDeclaration declaration) {
            safeAccept(declaration.getBodyDeclaration());
        }

        @Override
        public void visit(EnumDeclaration declaration) {
            safeAccept(declaration.getBodyDeclaration());
        }

        @Override
        public void visit(InterfaceDeclaration declaration) {
        }

        @Override
        public void visit(AnnotationDeclaration declaration) {
        }

        /**
         * Store bridgeMethodDeclarations during visit, if any
         */
        @Override
        public void visit(BodyDeclaration declaration) {
            ClassFileBodyDeclaration bodyDeclaration = (ClassFileBodyDeclaration) declaration;
            List<ClassFileConstructorOrMethodDeclaration> methodDeclarations = bodyDeclaration.getMethodDeclarations();

            if ((methodDeclarations != null) && !methodDeclarations.isEmpty()) {
                final Map<String, ClassFileMethodDeclaration> backup = map;

                map = new HashMap<>();

                acceptListDeclaration(methodDeclarations);

                if (!map.isEmpty()) {
                    bridgeMethodDeclarations.put('L' + bodyDeclaration.getInternalTypeName() + ';', map);
                }

                map = backup;
            }

            safeAcceptListDeclaration(bodyDeclaration.getInnerTypeDeclarations());
        }

        @Override
        public void visit(StaticInitializerDeclaration declaration) {
        }

        @Override
        public void visit(ConstructorDeclaration declaration) {
        }

        @Override
        public void visit(MethodDeclaration declaration) {
            // consider static methods only
            if ((declaration.getFlags() & ACC_STATIC) == 0) {
                return;
            }

            BaseStatement statements = declaration.getStatements();

            if ((statements == null) || (statements.size() != 1)) {
                return;
            }

            String name = declaration.getName();

            // consider methods starting with access$ only
            if (!name.startsWith("access$")) {
                return;
            }

            ClassFileMethodDeclaration bridgeMethodDeclaration = (ClassFileMethodDeclaration) declaration;

            if (!checkBridgeMethodDeclaration(bridgeMethodDeclaration)) {
                return;
            }

            map.put(name + declaration.getDescriptor(), bridgeMethodDeclaration);
        }

        private boolean checkBridgeMethodDeclaration(ClassFileMethodDeclaration bridgeMethodDeclaration) {
            Statement statement = bridgeMethodDeclaration.getStatements().getFirst();
            Expression exp;

            if (statement.isReturnExpressionStatement()) {
                exp = statement.getExpression();
            } else if (statement.isExpressionStatement()) {
                exp = statement.getExpression();
            } else {
                return false;
            }

            BaseType parameterTypes = bridgeMethodDeclaration.getParameterTypes();
            int parameterTypesCount = (parameterTypes == null) ? 0 : parameterTypes.size();

            if (exp.isFieldReferenceExpression()) {
                FieldReferenceExpression fre = (FieldReferenceExpression) exp;

                if (parameterTypesCount == 0) {
                    return (fre.getExpression() != null) && fre.getExpression().isObjectTypeReferenceExpression();
                } else if (parameterTypesCount == 1) {
                    return (fre.getExpression() == null) || checkLocalVariableReference(fre.getExpression(), 0);
                }
            } else if (exp.isMethodInvocationExpression()) {
                MethodInvocationExpression mie2 = (MethodInvocationExpression) exp;

                if (mie2.getExpression().isObjectTypeReferenceExpression()) {
                    BaseExpression mie2Parameters = mie2.getParameters();

                    if ((mie2Parameters == null) || (mie2Parameters.size() == 0)) {
                        return true;
                    }

                    if (mie2Parameters.isList()) {
                        int i = 0;
                        for (Expression parameter : mie2Parameters) {
                            if (!checkLocalVariableReference(parameter, i++)) {
                                return false;
                            }
                            Type type = parameter.getType();
                            if (type.equals(PrimitiveType.TYPE_LONG) || type.equals(PrimitiveType.TYPE_DOUBLE)) {
                                i++;
                            }
                        }
                        return true;
                    }

                    return checkLocalVariableReference(mie2Parameters, 0);
                } else if ((parameterTypesCount > 0) && checkLocalVariableReference(mie2.getExpression(), 0)) {
                    BaseExpression mie2Parameters = mie2.getParameters();

                    if ((mie2Parameters == null) || (mie2Parameters.size() == 0)) {
                        return true;
                    }

                    if (mie2Parameters.isList()) {
                        int i = 1;
                        for (Expression parameter : mie2Parameters) {
                            if (!checkLocalVariableReference(parameter, i++)) {
                                return false;
                            }
                            Type type = parameter.getType();
                            if (type.equals(PrimitiveType.TYPE_LONG) || type.equals(PrimitiveType.TYPE_DOUBLE)) {
                                i++;
                            }
                        }
                        return true;
                    }

                    return checkLocalVariableReference(mie2Parameters, 1);
                }
            } else if (exp.isBinaryOperatorExpression()) {
                if (parameterTypesCount == 1) {
                    if (exp.getLeftExpression().isFieldReferenceExpression()
                            && checkLocalVariableReference(exp.getRightExpression(), 0)) {
                        FieldReferenceExpression fre = (FieldReferenceExpression) exp.getLeftExpression();
                        return fre.getExpression().isObjectTypeReferenceExpression();
                    }
                } else if (parameterTypesCount == 2) {
                    if (exp.getLeftExpression().isFieldReferenceExpression()
                            && checkLocalVariableReference(exp.getRightExpression(), 1)) {
                        FieldReferenceExpression fre = (FieldReferenceExpression) exp.getLeftExpression();
                        return checkLocalVariableReference(fre.getExpression(), 0);
                    }
                }
            } else if (exp.isPostOperatorExpression()) {
                exp = exp.getExpression();

                if (exp.isFieldReferenceExpression()) {
                    if ((parameterTypesCount == 0) && exp.getExpression().isObjectTypeReferenceExpression()) {
                        return true;
                    } else if ((parameterTypesCount == 1) && (exp.getExpression() != null)
                            && checkLocalVariableReference(exp.getExpression(), 0)) {
                        return true;
                    }
                }
            } else if ((parameterTypesCount == 1) && exp.isPreOperatorExpression()) {
                exp = exp.getExpression();

                if (exp.isFieldReferenceExpression()) {
                    if ((parameterTypesCount == 0) && exp.getExpression().isObjectTypeReferenceExpression()) {
                        return true;
                    } else if ((parameterTypesCount == 1) && (exp.getExpression() != null)
                            && checkLocalVariableReference(exp.getExpression(), 0)) {
                        return true;
                    }
                }
            } else if ((parameterTypesCount == 0) && exp.isIntegerConstantExpression()) {
                return true;
            }

            return false;
        }

        private boolean checkLocalVariableReference(BaseExpression expression, int index) {
            if (expression.isLocalVariableReferenceExpression()) {
                ClassFileLocalVariableReferenceExpression var = (ClassFileLocalVariableReferenceExpression) expression;
                return (var.getLocalVariable().getIndex() == index);
            }

            return false;
        }
    }
}
