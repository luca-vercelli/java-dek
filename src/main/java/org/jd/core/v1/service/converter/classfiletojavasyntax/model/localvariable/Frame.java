/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.model.localvariable;

import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_BOOLEAN;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_BYTE;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_CHAR;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_DOUBLE;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_FLOAT;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_INT;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_LONG;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.FLAG_SHORT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.jd.core.v1.model.javasyntax.declaration.ExpressionVariableInitializer;
import org.jd.core.v1.model.javasyntax.declaration.LocalVariableDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.LocalVariableDeclarator;
import org.jd.core.v1.model.javasyntax.declaration.LocalVariableDeclarators;
import org.jd.core.v1.model.javasyntax.declaration.VariableInitializer;
import org.jd.core.v1.model.javasyntax.expression.BaseExpression;
import org.jd.core.v1.model.javasyntax.expression.BinaryOperatorExpression;
import org.jd.core.v1.model.javasyntax.expression.Expression;
import org.jd.core.v1.model.javasyntax.expression.Expressions;
import org.jd.core.v1.model.javasyntax.expression.NewExpression;
import org.jd.core.v1.model.javasyntax.expression.NewInitializedArray;
import org.jd.core.v1.model.javasyntax.statement.ExpressionStatement;
import org.jd.core.v1.model.javasyntax.statement.LocalVariableDeclarationStatement;
import org.jd.core.v1.model.javasyntax.statement.Statement;
import org.jd.core.v1.model.javasyntax.statement.Statements;
import org.jd.core.v1.model.javasyntax.type.BaseType;
import org.jd.core.v1.model.javasyntax.type.DiamondTypeArgument;
import org.jd.core.v1.model.javasyntax.type.GenericType;
import org.jd.core.v1.model.javasyntax.type.InnerObjectType;
import org.jd.core.v1.model.javasyntax.type.ObjectType;
import org.jd.core.v1.model.javasyntax.type.PrimitiveType;
import org.jd.core.v1.model.javasyntax.type.Type;
import org.jd.core.v1.model.javasyntax.type.TypeArgumentVisitor;
import org.jd.core.v1.model.javasyntax.type.TypeArguments;
import org.jd.core.v1.model.javasyntax.type.WildcardExtendsTypeArgument;
import org.jd.core.v1.model.javasyntax.type.WildcardSuperTypeArgument;
import org.jd.core.v1.model.javasyntax.type.WildcardTypeArgument;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileLocalVariableDeclarator;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileLocalVariableReferenceExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.statement.ClassFileForStatement;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.LocalVariableMaker;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.PrimitiveTypeUtil;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;
import org.jd.core.v1.service.converter.classfiletojavasyntax.visitor.CreateLocalVariableVisitor;
import org.jd.core.v1.service.converter.classfiletojavasyntax.visitor.SearchLocalVariableVisitor;
import org.jd.core.v1.service.converter.classfiletojavasyntax.visitor.SearchUndeclaredLocalVariableVisitor;
import org.jd.core.v1.util.DefaultList;

public class Frame {
    protected static final AbstractLocalVariableComparator ABSTRACT_LOCAL_VARIABLE_COMPARATOR = new AbstractLocalVariableComparator();
    protected static final Set<String> CAPITALIZED_JAVA_LANGUAGE_KEYWORDS = new HashSet<>(Arrays.asList("Abstract",
            "Continue", "For", "New", "Switch", "Assert", "Default", "Goto", "Package", "Synchronized", "Boolean", "Do",
            "If", "Private", "This", "Break", "Double", "Implements", "Protected", "Throw", "Byte", "Else", "Import",
            "Public", "Throws", "Case", "Enum", "Instanceof", "Return", "Transient", "Catch", "Extends", "Int", "Short",
            "Try", "Char", "Final", "Interface", "Static", "Void", "Class", "Finally", "Long", "Strictfp", "Volatile",
            "Const", "Float", "Native", "Super", "While"));

    static final int INITIAL_CAPACITY = 10;
    protected AbstractLocalVariable[] localVariableArray = new AbstractLocalVariable[INITIAL_CAPACITY];
    protected Map<NewExpression, AbstractLocalVariable> newExpressions = null;
    protected DefaultList<Frame> children = null;
    protected Frame parent;
    protected Statements statements;
    protected AbstractLocalVariable exceptionLocalVariable = null;

    public Frame(Frame parent, Statements statements) {
        this.parent = parent;
        this.statements = statements;
    }

    public void addLocalVariable(AbstractLocalVariable lv) {
        assert lv.getNext() == null : "Frame.addLocalVariable(lv) : add local variable failed";

        int index = lv.getIndex();

        if (index >= localVariableArray.length) {
            AbstractLocalVariable[] tmp = localVariableArray;
            localVariableArray = new AbstractLocalVariable[index * 2];
            System.arraycopy(tmp, 0, localVariableArray, 0, tmp.length);
        }

        AbstractLocalVariable next = localVariableArray[index];

        if (next != lv) {
            localVariableArray[index] = lv;
            lv.setNext(next);
            lv.setFrame(this);
        }
    }

    public AbstractLocalVariable getLocalVariable(int index) {
        if (index < localVariableArray.length) {
            AbstractLocalVariable lv = localVariableArray[index];
            if (lv != null) {
                return lv;
            }
        }
        return parent.getLocalVariable(index);
    }

    public Frame getParent() {
        return parent;
    }

    public void setExceptionLocalVariable(AbstractLocalVariable exceptionLocalVariable) {
        this.exceptionLocalVariable = exceptionLocalVariable;
    }

    public void mergeLocalVariable(Map<String, BaseType> typeBounds, LocalVariableMaker localVariableMaker,
            AbstractLocalVariable lv) {
        int index = lv.getIndex();
        AbstractLocalVariable alvToMerge;

        if (index < localVariableArray.length) {
            alvToMerge = localVariableArray[index];
        } else {
            alvToMerge = null;
        }

        if (alvToMerge != null) {
            if (!lv.isAssignableFrom(typeBounds, alvToMerge) && !alvToMerge.isAssignableFrom(typeBounds, lv)) {
                alvToMerge = null;
            } else if ((lv.getName() != null) && (alvToMerge.getName() != null)
                    && !lv.getName().equals(alvToMerge.getName())) {
                alvToMerge = null;
            }
        }

        if (alvToMerge == null) {
            if (children != null) {
                for (Frame frame : children) {
                    frame.mergeLocalVariable(typeBounds, localVariableMaker, lv);
                }
            }
        } else if (lv != alvToMerge) {
            for (LocalVariableReference reference : alvToMerge.getReferences()) {
                reference.setLocalVariable(lv);
            }

            lv.getReferences().addAll(alvToMerge.getReferences());
            lv.setFromOffset(alvToMerge.getFromOffset());

            Type type = lv.getType();
            Type alvToMergeType = alvToMerge.getType();

            if (lv.isAssignableFrom(typeBounds, alvToMerge)
                    || localVariableMaker.isCompatible(lv, alvToMerge.getType())) {
                if (type.isPrimitiveType()) {
                    PrimitiveLocalVariable plv = (PrimitiveLocalVariable) lv;
                    PrimitiveLocalVariable plvToMergeType = (PrimitiveLocalVariable) alvToMerge;
                    Type t = PrimitiveTypeUtil.getCommonPrimitiveType((PrimitiveType) plv.getType(),
                            (PrimitiveType) plvToMergeType.getType());

                    if (t == null) {
                        t = PrimitiveType.TYPE_INT;
                    }

                    plv.setType((PrimitiveType) t.createType(type.getDimension()));
                }
            } else {
                assert (type.isPrimitiveType() == alvToMergeType.isPrimitiveType())
                        && (type.isObjectType() == alvToMergeType.isObjectType())
                        && (type.isGenericType() == alvToMergeType.isGenericType())
                        : "Frame.mergeLocalVariable(lv) : merge local variable failed";

                if (type.isPrimitiveType()) {
                    PrimitiveLocalVariable plv = (PrimitiveLocalVariable) lv;

                    if (alvToMerge.isAssignableFrom(typeBounds, lv)
                            || localVariableMaker.isCompatible(alvToMerge, lv.getType())) {
                        plv.setType((PrimitiveType) alvToMergeType);
                    } else {
                        plv.setType(PrimitiveType.TYPE_INT);
                    }
                } else if (type.isObjectType()) {
                    ObjectLocalVariable olv = (ObjectLocalVariable) lv;

                    if (alvToMerge.isAssignableFrom(typeBounds, lv)
                            || localVariableMaker.isCompatible(alvToMerge, lv.getType())) {
                        olv.setType(typeBounds, alvToMergeType);
                    } else {
                        int dimension = Math.max(lv.getDimension(), alvToMerge.getDimension());
                        olv.setType(typeBounds, ObjectType.TYPE_OBJECT.createType(dimension));
                    }
                }
            }

            localVariableArray[index] = alvToMerge.getNext();
        }
    }

    public void removeLocalVariable(AbstractLocalVariable lv) {
        int index = lv.getIndex();
        AbstractLocalVariable alvToRemove;

        if ((index < localVariableArray.length) && (localVariableArray[index] == lv)) {
            alvToRemove = lv;
        } else {
            alvToRemove = null;
        }

        if (alvToRemove == null) {
            if (children != null) {
                for (Frame frame : children) {
                    frame.removeLocalVariable(lv);
                }
            }
        } else {
            localVariableArray[index] = alvToRemove.getNext();
            alvToRemove.setNext(null);
        }
    }

    public void addChild(Frame child) {
        if (children == null) {
            children = new DefaultList<>();
        }
        children.add(child);
    }

    public void close() {
        // Update type for 'new' expression
        if (newExpressions != null) {
            for (Map.Entry<NewExpression, AbstractLocalVariable> entry : newExpressions.entrySet()) {
                ObjectType ot1 = entry.getKey().getObjectType();
                ObjectType ot2 = (ObjectType) entry.getValue().getType();

                if ((ot1.getTypeArguments() == null) && (ot2.getTypeArguments() != null)) {
                    entry.getKey().setObjectType(ot1.createType(ot2.getTypeArguments()));
                }
            }
        }
    }

    public void createNames(Set<String> parentNames) {
        Set<String> names = new HashSet<>(parentNames);
        Map<Type, Boolean> types = new HashMap<>();
        int length = localVariableArray.length;

        for (int i = 0; i < length; i++) {
            AbstractLocalVariable lv = localVariableArray[i];

            while (lv != null) {
                if (types.containsKey(lv.getType())) {
                    // Non unique type
                    types.put(lv.getType(), Boolean.TRUE);
                } else {
                    // Unique type
                    types.put(lv.getType(), Boolean.FALSE);
                }
                if (lv.name != null) {
                    if (names.contains(lv.name)) {
                        lv.name = null;
                    } else {
                        names.add(lv.name);
                    }
                }
                assert lv != lv.getNext();
                lv = lv.getNext();
            }
        }

        if (exceptionLocalVariable != null) {
            if (types.containsKey(exceptionLocalVariable.getType())) {
                // Non unique type
                types.put(exceptionLocalVariable.getType(), Boolean.TRUE);
            } else {
                // Unique type
                types.put(exceptionLocalVariable.getType(), Boolean.FALSE);
            }
        }

        if (!types.isEmpty()) {
            GenerateLocalVariableNameVisitor visitor = new GenerateLocalVariableNameVisitor(names, types);

            for (int i = 0; i < length; i++) {
                AbstractLocalVariable lv = localVariableArray[i];

                while (lv != null) {
                    if (lv.name == null) {
                        lv.getType().accept(visitor);
                        lv.name = visitor.getName();
                    }
                    lv = lv.getNext();
                }
            }

            if (exceptionLocalVariable != null) {
                exceptionLocalVariable.getType().accept(visitor);
                exceptionLocalVariable.name = visitor.getName();
            }
        }

        // Recursive call
        if (children != null) {
            for (Frame child : children) {
                child.createNames(names);
            }
        }
    }

    public void updateLocalVariableInForStatements(TypeMaker typeMaker) {
        // Recursive call first
        if (children != null) {
            for (Frame child : children) {
                child.updateLocalVariableInForStatements(typeMaker);
            }
        }

        // Split local variable ranges in init 'for' statements
        SearchLocalVariableVisitor searchLocalVariableVisitor = new SearchLocalVariableVisitor();
        HashSet<AbstractLocalVariable> undeclaredInExpressionStatements = new HashSet<>();

        for (Statement statement : statements) {
            if (statement.isForStatement()) {
                if (statement.getInit() == null) {
                    if (statement.getCondition() != null) {
                        searchLocalVariableVisitor.init();
                        statement.getCondition().accept(searchLocalVariableVisitor);
                        undeclaredInExpressionStatements.addAll(searchLocalVariableVisitor.getVariables());
                    }
                    if (statement.getUpdate() != null) {
                        searchLocalVariableVisitor.init();
                        statement.getUpdate().accept(searchLocalVariableVisitor);
                        undeclaredInExpressionStatements.addAll(searchLocalVariableVisitor.getVariables());
                    }
                    if (statement.getStatements() != null) {
                        searchLocalVariableVisitor.init();
                        statement.getStatements().accept(searchLocalVariableVisitor);
                        undeclaredInExpressionStatements.addAll(searchLocalVariableVisitor.getVariables());
                    }
                }
            } else {
                searchLocalVariableVisitor.init();
                statement.accept(searchLocalVariableVisitor);
                undeclaredInExpressionStatements.addAll(searchLocalVariableVisitor.getVariables());
            }
        }

        SearchUndeclaredLocalVariableVisitor searchUndeclaredLocalVariableVisitor = new SearchUndeclaredLocalVariableVisitor();
        HashMap<AbstractLocalVariable, List<ClassFileForStatement>> undeclaredInForStatements = new HashMap<>();

        for (Statement statement : statements) {
            if (statement.isForStatement()) {
                ClassFileForStatement fs = (ClassFileForStatement) statement;

                if (fs.getInit() != null) {
                    searchUndeclaredLocalVariableVisitor.init();
                    fs.getInit().accept(searchUndeclaredLocalVariableVisitor);
                    searchUndeclaredLocalVariableVisitor.getVariables().removeAll(undeclaredInExpressionStatements);

                    for (AbstractLocalVariable lv : searchUndeclaredLocalVariableVisitor.getVariables()) {
                        List<ClassFileForStatement> list = undeclaredInForStatements.get(lv);
                        if (list == null) {
                            list = new ArrayList<>();
                            undeclaredInForStatements.put(lv, list);
                        }
                        list.add(fs);
                    }
                }
            }
        }

        if (!undeclaredInForStatements.isEmpty()) {
            CreateLocalVariableVisitor createLocalVariableVisitor = new CreateLocalVariableVisitor(typeMaker);

            for (Map.Entry<AbstractLocalVariable, List<ClassFileForStatement>> entry : undeclaredInForStatements
                    .entrySet()) {
                List<ClassFileForStatement> listFS = entry.getValue();

                // Split local variable range
                AbstractLocalVariable lv = entry.getKey();
                Iterator<ClassFileForStatement> iteratorFS = listFS.iterator();
                ClassFileForStatement firstFS = iteratorFS.next();

                while (iteratorFS.hasNext()) {
                    createNewLocalVariable(createLocalVariableVisitor, iteratorFS.next(), lv);
                }

                if (lv.getFrame() == this) {
                    lv.setFromOffset(firstFS.getFromOffset());
                    lv.setToOffset(firstFS.getToOffset(), true);
                } else {
                    createNewLocalVariable(createLocalVariableVisitor, firstFS, lv);

                    if (lv.getReferences().isEmpty()) {
                        lv.getFrame().removeLocalVariable(lv);
                    }
                }
            }
        }
    }

    protected void createNewLocalVariable(CreateLocalVariableVisitor createLocalVariableVisitor,
            ClassFileForStatement fs, AbstractLocalVariable lv) {
        int fromOffset = fs.getFromOffset();
        int toOffset = fs.getToOffset();
        createLocalVariableVisitor.init(lv.getIndex(), fromOffset);
        lv.accept(createLocalVariableVisitor);
        AbstractLocalVariable newLV = createLocalVariableVisitor.getLocalVariable();

        newLV.setToOffset(toOffset, true);
        addLocalVariable(newLV);
        Iterator<LocalVariableReference> iteratorLVR = lv.getReferences().iterator();

        while (iteratorLVR.hasNext()) {
            LocalVariableReference lvr = iteratorLVR.next();
            int offset = ((ClassFileLocalVariableReferenceExpression) lvr).getOffset();

            if ((fromOffset <= offset) && (offset <= toOffset)) {
                lvr.setLocalVariable(newLV);
                newLV.addReference(lvr);
                iteratorLVR.remove();
            }
        }
    }

    public void createDeclarations(boolean containsLineNumber) {
        // Create inline declarations
        createInlineDeclarations();

        // Create start-block declarations
        createStartBlockDeclarations();

        // Merge declarations
        if (containsLineNumber) {
            mergeDeclarations();
        }

        // Recursive call
        if (children != null) {
            for (Frame child : children) {
                child.createDeclarations(containsLineNumber);
            }
        }
    }

    protected void createInlineDeclarations() {
        Map<Frame, Set<AbstractLocalVariable>> map = createMapForInlineDeclarations();

        if (!map.isEmpty()) {
            SearchUndeclaredLocalVariableVisitor searchUndeclaredLocalVariableVisitor = new SearchUndeclaredLocalVariableVisitor();

            for (Map.Entry<Frame, Set<AbstractLocalVariable>> entry : map.entrySet()) {
                Statements statements = entry.getKey().statements;
                ListIterator<Statement> iterator = statements.listIterator();
                Set<AbstractLocalVariable> undeclaredLocalVariables = entry.getValue();

                while (iterator.hasNext()) {
                    Statement statement = iterator.next();

                    searchUndeclaredLocalVariableVisitor.init();
                    statement.accept(searchUndeclaredLocalVariableVisitor);

                    Set<AbstractLocalVariable> undeclaredLocalVariablesInStatement = searchUndeclaredLocalVariableVisitor
                            .getVariables();
                    undeclaredLocalVariablesInStatement.retainAll(undeclaredLocalVariables);

                    if (!undeclaredLocalVariablesInStatement.isEmpty()) {
                        int index1 = iterator.nextIndex();

                        if (statement.isExpressionStatement()) {
                            createInlineDeclarations(undeclaredLocalVariables, undeclaredLocalVariablesInStatement,
                                    iterator, (ExpressionStatement) statement);
                        } else if (statement.isForStatement()) {
                            createInlineDeclarations(undeclaredLocalVariables, undeclaredLocalVariablesInStatement,
                                    (ClassFileForStatement) statement);
                        }

                        if (!undeclaredLocalVariablesInStatement.isEmpty()) {
                            // Set the cursor before current statement
                            int index2 = iterator.nextIndex() + undeclaredLocalVariablesInStatement.size();

                            while (iterator.nextIndex() >= index1) {
                                iterator.previous();
                            }

                            DefaultList<AbstractLocalVariable> sorted = new DefaultList<>(
                                    undeclaredLocalVariablesInStatement);
                            sorted.sort(ABSTRACT_LOCAL_VARIABLE_COMPARATOR);

                            for (AbstractLocalVariable lv : sorted) {
                                // Add declaration before current statement
                                iterator.add(new LocalVariableDeclarationStatement(lv.getType(),
                                        new ClassFileLocalVariableDeclarator(lv)));
                                lv.setDeclared(true);
                                undeclaredLocalVariables.remove(lv);
                            }

                            // Reset the cursor after current statement
                            while (iterator.nextIndex() < index2) {
                                iterator.next();
                            }
                        }
                    }

                    if (undeclaredLocalVariables.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    protected void createInlineDeclarations(Set<AbstractLocalVariable> undeclaredLocalVariables,
            Set<AbstractLocalVariable> undeclaredLocalVariablesInStatement, ListIterator<Statement> iterator,
            ExpressionStatement es) {

        if (es.getExpression().isBinaryOperatorExpression()) {
            Expression boe = es.getExpression();

            if (boe.getOperator().equals("=")) {
                Expressions expressions = new Expressions();

                splitMultiAssignment(Integer.MAX_VALUE, undeclaredLocalVariablesInStatement, expressions, boe);
                iterator.remove();

                for (Expression exp : expressions) {
                    iterator.add(newDeclarationStatement(undeclaredLocalVariables, undeclaredLocalVariablesInStatement,
                            exp));
                }

                if (expressions.isEmpty()) {
                    iterator.add(es);
                }
            }
        }
    }

    protected Map<Frame, Set<AbstractLocalVariable>> createMapForInlineDeclarations() {
        Map<Frame, Set<AbstractLocalVariable>> map = new HashMap<>();
        int i = localVariableArray.length;

        while (i-- > 0) {
            AbstractLocalVariable lv = localVariableArray[i];

            while (lv != null) {
                if ((this == lv.getFrame()) && !lv.isDeclared()) {
                    Set<AbstractLocalVariable> variablesToDeclare = map.get(this);
                    if (variablesToDeclare == null) {
                        variablesToDeclare = new HashSet<>();
                        map.put(this, variablesToDeclare);
                    }
                    variablesToDeclare.add(lv);
                }
                lv = lv.getNext();
            }
        }

        return map;
    }

    protected Expression splitMultiAssignment(int toOffset,
            Set<AbstractLocalVariable> undeclaredLocalVariablesInStatement, List<Expression> expressions,
            Expression expression) {

        if (expression.isBinaryOperatorExpression() && expression.getOperator().equals("=")) {
            Expression rightExpression = splitMultiAssignment(toOffset, undeclaredLocalVariablesInStatement,
                    expressions, expression.getRightExpression());

            if (expression.getLeftExpression().isLocalVariableReferenceExpression()) {
                ClassFileLocalVariableReferenceExpression lvre = (ClassFileLocalVariableReferenceExpression) expression
                        .getLeftExpression();
                AbstractLocalVariable localVariable = lvre.getLocalVariable();

                if (undeclaredLocalVariablesInStatement.contains(localVariable)
                        && (localVariable.getToOffset() <= toOffset)) {
                    // Split multi assignment
                    if (rightExpression == expression.getRightExpression()) {
                        expressions.add(expression);
                    } else {
                        expressions.add(new BinaryOperatorExpression(expression.getLineNumber(), expression.getType(),
                                lvre, "=", rightExpression, expression.getPriority()));
                    }
                    // Return local variable
                    return lvre;
                }
            }
        }

        return expression;
    }

    protected LocalVariableDeclarationStatement newDeclarationStatement(
            Set<AbstractLocalVariable> undeclaredLocalVariables,
            Set<AbstractLocalVariable> undeclaredLocalVariablesInStatement, Expression boe) {

        ClassFileLocalVariableReferenceExpression reference = (ClassFileLocalVariableReferenceExpression) boe
                .getLeftExpression();
        AbstractLocalVariable localVariable = reference.getLocalVariable();

        undeclaredLocalVariables.remove(localVariable);
        undeclaredLocalVariablesInStatement.remove(localVariable);
        localVariable.setDeclared(true);

        Type type = localVariable.getType();
        VariableInitializer variableInitializer;

        if (boe.getRightExpression().isNewInitializedArray()) {
            if (type.isObjectType() && (((ObjectType) type).getTypeArguments() != null)) {
                variableInitializer = new ExpressionVariableInitializer(boe.getRightExpression());
            } else {
                variableInitializer = ((NewInitializedArray) boe.getRightExpression()).getArrayInitializer();
            }
        } else {
            variableInitializer = new ExpressionVariableInitializer(boe.getRightExpression());
        }

        return new LocalVariableDeclarationStatement(type, new ClassFileLocalVariableDeclarator(boe.getLineNumber(),
                reference.getLocalVariable(), variableInitializer));
    }

    protected void createInlineDeclarations(Set<AbstractLocalVariable> undeclaredLocalVariables,
            Set<AbstractLocalVariable> undeclaredLocalVariablesInStatement, ClassFileForStatement fs) {

        BaseExpression init = fs.getInit();

        if (init != null) {
            Expressions expressions = new Expressions();
            int toOffset = fs.getToOffset();

            if (init.isList()) {
                for (Expression exp : init) {
                    splitMultiAssignment(toOffset, undeclaredLocalVariablesInStatement, expressions, exp);
                    if (expressions.isEmpty()) {
                        expressions.add(exp);
                    }
                }
            } else {
                splitMultiAssignment(toOffset, undeclaredLocalVariablesInStatement, expressions, init.getFirst());
                if (expressions.isEmpty()) {
                    expressions.add(init.getFirst());
                }
            }

            if (expressions.size() == 1) {
                updateForStatement(undeclaredLocalVariables, undeclaredLocalVariablesInStatement, fs,
                        expressions.getFirst());
            } else {
                updateForStatement(undeclaredLocalVariables, undeclaredLocalVariablesInStatement, fs, expressions);
            }
        }
    }

    protected void updateForStatement(Set<AbstractLocalVariable> undeclaredLocalVariables,
            Set<AbstractLocalVariable> undeclaredLocalVariablesInStatement, ClassFileForStatement forStatement,
            Expression init) {

        if (!init.isBinaryOperatorExpression()) {
            return;
        }

        if (!init.getLeftExpression().isLocalVariableReferenceExpression()) {
            return;
        }

        ClassFileLocalVariableReferenceExpression reference = (ClassFileLocalVariableReferenceExpression) init
                .getLeftExpression();
        AbstractLocalVariable localVariable = reference.getLocalVariable();

        if (localVariable.isDeclared() || (localVariable.getToOffset() > forStatement.getToOffset())) {
            return;
        }

        undeclaredLocalVariables.remove(localVariable);
        undeclaredLocalVariablesInStatement.remove(localVariable);
        localVariable.setDeclared(true);

        VariableInitializer variableInitializer = init.getRightExpression().isNewInitializedArray()
                ? ((NewInitializedArray) init.getRightExpression()).getArrayInitializer()
                : new ExpressionVariableInitializer(init.getRightExpression());

        forStatement.setDeclaration(
                new LocalVariableDeclaration(localVariable.getType(), new ClassFileLocalVariableDeclarator(
                        init.getLineNumber(), reference.getLocalVariable(), variableInitializer)));
        forStatement.setInit(null);
    }

    protected void updateForStatement(Set<AbstractLocalVariable> variablesToDeclare,
            Set<AbstractLocalVariable> foundVariables, ClassFileForStatement forStatement, Expressions init) {

        DefaultList<Expression> boes = new DefaultList<>();
        DefaultList<AbstractLocalVariable> localVariables = new DefaultList<>();
        Type type0 = null, type1 = null;
        int minDimension = 0, maxDimension = 0;

        for (Expression expression : init) {
            if (!expression.isBinaryOperatorExpression()) {
                return;
            }
            if (!expression.getLeftExpression().isLocalVariableReferenceExpression()) {
                return;
            }

            AbstractLocalVariable localVariable = ((ClassFileLocalVariableReferenceExpression) expression
                    .getLeftExpression()).getLocalVariable();

            if (localVariable.isDeclared() || (localVariable.getToOffset() > forStatement.getToOffset())) {
                return;
            }

            if (type1 == null) {
                type1 = localVariable.getType();
                type0 = type1.createType(0);
                minDimension = maxDimension = type1.getDimension();
            } else {
                Type type2 = localVariable.getType();

                if (type1.isPrimitiveType() && type2.isPrimitiveType()) {
                    Type type = PrimitiveTypeUtil.getCommonPrimitiveType((PrimitiveType) type1, (PrimitiveType) type2);

                    if (type == null) {
                        return;
                    }

                    type0 = type;
                    type1 = type.createType(type1.getDimension());
                    type2 = type.createType(type2.getDimension());
                } else if (!type1.equals(type2) && !type0.equals(type2.createType(0))) {
                    return;
                }

                int dimension = type2.getDimension();

                if (minDimension > dimension) {
                    minDimension = dimension;
                }
                if (maxDimension < dimension) {
                    maxDimension = dimension;
                }
            }

            localVariables.add(localVariable);
            boes.add(expression);
        }

        for (AbstractLocalVariable lv : localVariables) {
            variablesToDeclare.remove(lv);
            foundVariables.remove(lv);
            lv.setDeclared(true);
        }

        if (minDimension == maxDimension) {
            forStatement.setDeclaration(new LocalVariableDeclaration(type1, createDeclarators1(boes, false)));
        } else {
            forStatement.setDeclaration(new LocalVariableDeclaration(type0, createDeclarators1(boes, true)));
        }

        forStatement.setInit(null);
    }

    protected LocalVariableDeclarators createDeclarators1(List<Expression> boes, boolean setDimension) {
        LocalVariableDeclarators declarators = new LocalVariableDeclarators(boes.size());

        for (Expression boe : boes) {
            ClassFileLocalVariableReferenceExpression reference = (ClassFileLocalVariableReferenceExpression) boe
                    .getLeftExpression();
            VariableInitializer variableInitializer = boe.getRightExpression().isNewInitializedArray()
                    ? ((NewInitializedArray) boe.getRightExpression()).getArrayInitializer()
                    : new ExpressionVariableInitializer(boe.getRightExpression());
            LocalVariableDeclarator declarator = new ClassFileLocalVariableDeclarator(boe.getLineNumber(),
                    reference.getLocalVariable(), variableInitializer);

            if (setDimension) {
                declarator.setDimension(reference.getLocalVariable().getDimension());
            }

            declarators.add(declarator);
        }

        return declarators;
    }

    protected void createStartBlockDeclarations() {
        int addIndex = -1;
        int i = localVariableArray.length;

        while (i-- > 0) {
            AbstractLocalVariable lv = localVariableArray[i];

            while (lv != null) {
                if (!lv.isDeclared()) {
                    if (addIndex == -1) {
                        addIndex = getAddIndex();
                    }
                    statements.add(addIndex, new LocalVariableDeclarationStatement(lv.getType(),
                            new ClassFileLocalVariableDeclarator(lv)));
                    lv.setDeclared(true);
                }

                lv = lv.getNext();
            }
        }
    }

    protected int getAddIndex() {
        int addIndex = 0;

        if (parent.parent == null) {
            // Insert declarations after 'super' call invocation => Search index of
            // SuperConstructorInvocationExpression.
            int len = statements.size();

            while (addIndex < len) {
                Statement statement = statements.get(addIndex++);
                if (statement.isExpressionStatement()) {
                    Expression expression = statement.getExpression();
                    if (expression.isSuperConstructorInvocationExpression()
                            || expression.isConstructorInvocationExpression()) {
                        break;
                    }
                }
            }

            if (addIndex >= len) {
                addIndex = 0;
            }
        }

        return addIndex;
    }

    protected void mergeDeclarations() {
        int size = statements.size();

        if (size > 1) {
            DefaultList<LocalVariableDeclarationStatement> declarations = new DefaultList<>();
            ListIterator<Statement> iterator = statements.listIterator();

            while (iterator.hasNext()) {
                Statement previous;

                do {
                    previous = iterator.next();
                } while (!previous.isLocalVariableDeclarationStatement() && iterator.hasNext());

                if (previous.isLocalVariableDeclarationStatement()) {
                    LocalVariableDeclarationStatement lvds1 = (LocalVariableDeclarationStatement) previous;
                    Type type1 = lvds1.getType();
                    Type type0 = type1.createType(0);
                    int minDimension = type1.getDimension();
                    int maxDimension = minDimension;
                    int lineNumber1 = lvds1.getLocalVariableDeclarators().getLineNumber();

                    declarations.clear();
                    declarations.add(lvds1);

                    while (iterator.hasNext()) {
                        Statement statement = iterator.next();

                        if (!statement.isLocalVariableDeclarationStatement()) {
                            iterator.previous();
                            break;
                        }

                        LocalVariableDeclarationStatement lvds2 = (LocalVariableDeclarationStatement) statement;
                        int lineNumber2 = lvds2.getLocalVariableDeclarators().getLineNumber();

                        if (lineNumber1 != lineNumber2) {
                            iterator.previous();
                            break;
                        }

                        lineNumber1 = lineNumber2;

                        Type type2 = lvds2.getType();

                        if (type1.isPrimitiveType() && type2.isPrimitiveType()) {
                            Type type = PrimitiveTypeUtil.getCommonPrimitiveType((PrimitiveType) type1,
                                    (PrimitiveType) type2);

                            if (type == null) {
                                iterator.previous();
                                break;
                            }

                            type0 = type;
                            type1 = type.createType(type1.getDimension());
                            type2 = type.createType(type2.getDimension());
                        } else if (!type1.equals(type2) && !type0.equals(type2.createType(0))) {
                            iterator.previous();
                            break;
                        }

                        int dimension = type2.getDimension();

                        if (minDimension > dimension) {
                            minDimension = dimension;
                        }
                        if (maxDimension < dimension) {
                            maxDimension = dimension;
                        }

                        declarations.add(lvds2);
                    }

                    int declarationSize = declarations.size();

                    if (declarationSize > 1) {
                        while (--declarationSize > 0) {
                            iterator.previous();
                            iterator.remove();
                        }

                        iterator.previous();

                        if (minDimension == maxDimension) {
                            iterator.set(new LocalVariableDeclarationStatement(type1,
                                    createDeclarators2(declarations, false)));
                        } else {
                            iterator.set(new LocalVariableDeclarationStatement(type0,
                                    createDeclarators2(declarations, true)));
                        }

                        iterator.next();
                    }
                }
            }
        }
    }

    protected LocalVariableDeclarators createDeclarators2(List<LocalVariableDeclarationStatement> declarations,
            boolean setDimension) {
        LocalVariableDeclarators declarators = new LocalVariableDeclarators(declarations.size());

        for (LocalVariableDeclarationStatement declaration : declarations) {
            LocalVariableDeclarator declarator = (LocalVariableDeclarator) declaration.getLocalVariableDeclarators();

            if (setDimension) {
                declarator.setDimension(declaration.getType().getDimension());
            }

            declarators.add(declarator);
        }

        return declarators;
    }

    @Override
    public String toString() {
        return "Frame " + statements;
    }

    /**
     * Generate "reasonable" local variable names in <code>sb</code> during visit
     */
    protected static class GenerateLocalVariableNameVisitor implements TypeArgumentVisitor {
        protected static final String[] INTEGER_NAMES = { "i", "j", "k", "m", "n" };

        protected StringBuilder sb = new StringBuilder();
        protected Set<String> blackListNames;
        protected Map<Type, Boolean> types;
        protected String name;

        public GenerateLocalVariableNameVisitor(Set<String> blackListNames, Map<Type, Boolean> types) {
            this.blackListNames = blackListNames;
            this.types = types;
        }

        public String getName() {
            return name;
        }

        @Override
        public void visit(PrimitiveType type) {
            sb.setLength(0);

            switch (type.getJavaPrimitiveFlags()) {
            case FLAG_BYTE:
                sb.append("b");
                break;
            case FLAG_CHAR:
                sb.append("c");
                break;
            case FLAG_DOUBLE:
                sb.append("d");
                break;
            case FLAG_FLOAT:
                sb.append("f");
                break;
            case FLAG_INT:
                for (String in : INTEGER_NAMES) {
                    if (!blackListNames.contains(in)) {
                        blackListNames.add(name = in);
                        return;
                    }
                }
                sb.append("i");
                break;
            case FLAG_LONG:
                sb.append("l");
                break;
            case FLAG_SHORT:
                sb.append("s");
                break;
            case FLAG_BOOLEAN:
                sb.append("bool");
                break;
            }

            generate(type);
        }

        @Override
        public void visit(ObjectType type) {
            visit(type, type.getName());
        }

        @Override
        public void visit(InnerObjectType type) {
            visit(type, type.getName());
        }

        @Override
        public void visit(GenericType type) {
            visit(type, type.getName());
        }

        protected void visit(Type type, String str) {
            sb.setLength(0);

            switch (type.getDimension()) {
            case 0:
                if ("Class".equals(str)) {
                    sb.append("clazz");
                } else if ("String".equals(str)) {
                    sb.append("str");
                } else if ("Boolean".equals(str)) {
                    sb.append("bool");
                } else {
                    uncapitalize(str);
                    if (CAPITALIZED_JAVA_LANGUAGE_KEYWORDS.contains(str)) {
                        sb.append("_");
                    }
                }
                break;
            default:
                sb.append("arrayOf");
                capitalize(str);
                break;
            }

            generate(type);
        }

        /**
         * Given abc, append Abc to sb
         */
        protected void capitalize(String str) {
            if (str != null) {
                int length = str.length();

                if (length > 0) {
                    char firstChar = str.charAt(0);

                    if (Character.isUpperCase(firstChar)) {
                        sb.append(str);
                    } else {
                        sb.append(Character.toUpperCase(firstChar));
                        if (length > 1) {
                            sb.append(str.substring(1));
                        }
                    }
                }
            }
        }

        /**
         * Given Abc, append abc to sb
         */
        protected void uncapitalize(String str) {
            if (str != null) {
                int length = str.length();

                if (length > 0) {
                    char firstChar = str.charAt(0);

                    if (Character.isLowerCase(firstChar)) {
                        sb.append(str);
                    } else {
                        sb.append(Character.toLowerCase(firstChar));
                        if (length > 1) {
                            sb.append(str.substring(1));
                        }
                    }
                }
            }
        }

        protected void generate(Type type) {
            int length = sb.length();
            int counter = 1;

            if (types.get(type)) {
                sb.append(counter++);
            }

            name = sb.toString();

            while (blackListNames.contains(name)) {
                sb.setLength(length);
                sb.append(counter++);
                name = sb.toString();
            }

            blackListNames.add(name);
        }

        @Override
        public void visit(TypeArguments type) {
        }

        @Override
        public void visit(DiamondTypeArgument type) {
        }

        @Override
        public void visit(WildcardExtendsTypeArgument type) {
        }

        @Override
        public void visit(WildcardSuperTypeArgument type) {
        }

        @Override
        public void visit(WildcardTypeArgument type) {
        }
    }

    /**
     * Compare local variables by index
     */
    protected static class AbstractLocalVariableComparator implements Comparator<AbstractLocalVariable> {
        @Override
        public int compare(AbstractLocalVariable alv1, AbstractLocalVariable alv2) {
            return alv1.getIndex() - alv2.getIndex();
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
