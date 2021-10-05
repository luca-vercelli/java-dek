/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import java.util.HashMap;
import java.util.Map;

import org.jd.core.v1.model.javasyntax.AbstractJavaSyntaxVisitor;
import org.jd.core.v1.model.javasyntax.declaration.BodyDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ExpressionVariableInitializer;
import org.jd.core.v1.model.javasyntax.expression.Expression;
import org.jd.core.v1.model.javasyntax.expression.LambdaIdentifiersExpression;
import org.jd.core.v1.model.javasyntax.expression.MethodInvocationExpression;
import org.jd.core.v1.model.javasyntax.statement.LambdaExpressionStatement;
import org.jd.core.v1.model.javasyntax.statement.ReturnExpressionStatement;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileBodyDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.JavaVersion;

/**
 * Replace <code>Double.valueOf(x)</code> with <code>x</code> (autoboxing) and
 * <code>x.doubleValue()</code> with <code>x</code> (unboxing), if this is
 * allowed.
 * 
 * @see https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html
 */
public class AutoboxingVisitor extends AbstractJavaSyntaxVisitor {
	protected static final Map<String, String> VALUEOF_DESCRIPTOR_MAP = new HashMap<>();
	protected static final Map<String, String> VALUE_DESCRIPTOR_MAP = new HashMap<>();
	protected static final Map<String, String> VALUE_METHODNAME_MAP = new HashMap<>();

	static {
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Byte", "(B)Ljava/lang/Byte;");
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Character", "(C)Ljava/lang/Character;");
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Float", "(F)Ljava/lang/Float;");
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Integer", "(I)Ljava/lang/Integer;");
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Long", "(J)Ljava/lang/Long;");
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Short", "(S)Ljava/lang/Short;");
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Double", "(D)Ljava/lang/Double;");
		VALUEOF_DESCRIPTOR_MAP.put("java/lang/Boolean", "(Z)Ljava/lang/Boolean;");

		VALUE_DESCRIPTOR_MAP.put("java/lang/Byte", "()B");
		VALUE_DESCRIPTOR_MAP.put("java/lang/Character", "()C");
		VALUE_DESCRIPTOR_MAP.put("java/lang/Float", "()F");
		VALUE_DESCRIPTOR_MAP.put("java/lang/Integer", "()I");
		VALUE_DESCRIPTOR_MAP.put("java/lang/Long", "()J");
		VALUE_DESCRIPTOR_MAP.put("java/lang/Short", "()S");
		VALUE_DESCRIPTOR_MAP.put("java/lang/Double", "()D");
		VALUE_DESCRIPTOR_MAP.put("java/lang/Boolean", "()Z");

		VALUE_METHODNAME_MAP.put("java/lang/Byte", "byteValue");
		VALUE_METHODNAME_MAP.put("java/lang/Character", "charValue");
		VALUE_METHODNAME_MAP.put("java/lang/Float", "floatValue");
		VALUE_METHODNAME_MAP.put("java/lang/Integer", "intValue");
		VALUE_METHODNAME_MAP.put("java/lang/Long", "longValue");
		VALUE_METHODNAME_MAP.put("java/lang/Short", "shortValue");
		VALUE_METHODNAME_MAP.put("java/lang/Double", "doubleValue");
		VALUE_METHODNAME_MAP.put("java/lang/Boolean", "booleanValue");
	}

	/**
	 * Skip visit below Java 5
	 */
	@Override
	public void visit(BodyDeclaration declaration) {
		ClassFileBodyDeclaration cfbd = (ClassFileBodyDeclaration) declaration;
		boolean autoBoxingSupported = (cfbd.getClassFile().getMajorVersion() >= JavaVersion.JAVA5);

		if (autoBoxingSupported) {
			safeAccept(declaration.getMemberDeclarations());
		}
	}

	@Override
	public void visit(ExpressionVariableInitializer declaration) {
		if (declaration.getExpression() instanceof MethodInvocationExpression) {
			MethodInvocationExpression mie = (MethodInvocationExpression) declaration.getExpression();
			declaration.setExpression(updateExpression(mie));
		}
		declaration.getExpression().accept(this);
	}

	private Expression updateExpression(MethodInvocationExpression mie) {
		Expression newExpression = mie;
		int numParameters = (mie.getParameters() == null) ? 0 : mie.getParameters().size();
		if ("valueOf".equals(mie.getName())
				&& mie.getDescriptor().equals(VALUEOF_DESCRIPTOR_MAP.get(mie.getInternalTypeName()))
				&& numParameters == 1) {
			// this is an assignment x = Double.valueOf(y)
			newExpression = mie.getParameters().getFirst();
		} else if (mie.getName().equals(VALUE_METHODNAME_MAP.get(mie.getInternalTypeName()))
				&& mie.getDescriptor().equals(VALUE_DESCRIPTOR_MAP.get(mie.getInternalTypeName()))
				&& numParameters == 0) {
			// this is an assignment x = y.doubleValue()
			newExpression = mie.getExpression();
		}
		return newExpression;
	}

	@Override
	public void visit(ReturnExpressionStatement statement) {
		if (statement.getExpression() instanceof MethodInvocationExpression) {
			MethodInvocationExpression mie = (MethodInvocationExpression) statement.getExpression();
			statement.setExpression(updateExpression(mie));
		}
		statement.getExpression().accept(this);
	}

	@Override
	public void visit(LambdaIdentifiersExpression expression) {
		if (expression.getStatements() instanceof LambdaExpressionStatement) {
			// 1 statement only, isn't it?
			LambdaExpressionStatement le = (LambdaExpressionStatement) expression.getStatements();
			if (le.getExpression() instanceof MethodInvocationExpression) {
				MethodInvocationExpression mie = (MethodInvocationExpression) le.getExpression();
				le.setExpression(updateExpression(mie));
			}
		}
		safeAccept(expression.getStatements());
	}

}
