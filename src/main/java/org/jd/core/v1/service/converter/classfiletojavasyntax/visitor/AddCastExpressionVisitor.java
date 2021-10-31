/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import static org.jd.core.v1.model.classfile.AccessType.ACC_BRIDGE;
import static org.jd.core.v1.model.classfile.AccessType.ACC_SYNTHETIC;
import static org.jd.core.v1.model.javasyntax.type.PrimitiveType.TYPE_BYTE;

import java.util.Map;

import org.jd.core.v1.model.javasyntax.AbstractJavaSyntaxVisitor;
import org.jd.core.v1.model.javasyntax.declaration.ArrayVariableInitializer;
import org.jd.core.v1.model.javasyntax.declaration.BaseMemberDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.BodyDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ConstructorDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ExpressionVariableInitializer;
import org.jd.core.v1.model.javasyntax.declaration.FieldDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.FieldDeclarator;
import org.jd.core.v1.model.javasyntax.declaration.LocalVariableDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.LocalVariableDeclarator;
import org.jd.core.v1.model.javasyntax.declaration.MethodDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.StaticInitializerDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.VariableInitializer;
import org.jd.core.v1.model.javasyntax.expression.BaseExpression;
import org.jd.core.v1.model.javasyntax.expression.BinaryOperatorExpression;
import org.jd.core.v1.model.javasyntax.expression.CastExpression;
import org.jd.core.v1.model.javasyntax.expression.ConstructorInvocationExpression;
import org.jd.core.v1.model.javasyntax.expression.ConstructorReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.DoubleConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.EnumConstantReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.Expression;
import org.jd.core.v1.model.javasyntax.expression.FieldReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.FloatConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.IntegerConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.LambdaIdentifiersExpression;
import org.jd.core.v1.model.javasyntax.expression.LocalVariableReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.LongConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.MethodInvocationExpression;
import org.jd.core.v1.model.javasyntax.expression.NewExpression;
import org.jd.core.v1.model.javasyntax.expression.NewInitializedArray;
import org.jd.core.v1.model.javasyntax.expression.NullExpression;
import org.jd.core.v1.model.javasyntax.expression.ObjectTypeReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.SuperConstructorInvocationExpression;
import org.jd.core.v1.model.javasyntax.expression.SuperExpression;
import org.jd.core.v1.model.javasyntax.expression.TernaryOperatorExpression;
import org.jd.core.v1.model.javasyntax.expression.ThisExpression;
import org.jd.core.v1.model.javasyntax.expression.TypeReferenceDotClassExpression;
import org.jd.core.v1.model.javasyntax.reference.InnerObjectReference;
import org.jd.core.v1.model.javasyntax.reference.ObjectReference;
import org.jd.core.v1.model.javasyntax.statement.BaseStatement;
import org.jd.core.v1.model.javasyntax.statement.BreakStatement;
import org.jd.core.v1.model.javasyntax.statement.ByteCodeStatement;
import org.jd.core.v1.model.javasyntax.statement.ContinueStatement;
import org.jd.core.v1.model.javasyntax.statement.ReturnExpressionStatement;
import org.jd.core.v1.model.javasyntax.statement.ThrowStatement;
import org.jd.core.v1.model.javasyntax.type.BaseType;
import org.jd.core.v1.model.javasyntax.type.BaseTypeArgument;
import org.jd.core.v1.model.javasyntax.type.InnerObjectType;
import org.jd.core.v1.model.javasyntax.type.ObjectType;
import org.jd.core.v1.model.javasyntax.type.Type;
import org.jd.core.v1.model.javasyntax.type.TypeArguments;
import org.jd.core.v1.model.javasyntax.type.TypeParameterWithTypeBounds;
import org.jd.core.v1.model.javasyntax.type.Types;
import org.jd.core.v1.model.javasyntax.type.WildcardExtendsTypeArgument;
import org.jd.core.v1.model.javasyntax.type.WildcardSuperTypeArgument;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileBodyDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileConstructorDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileMethodDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileStaticInitializerDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileConstructorInvocationExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileMethodInvocationExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileNewExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.expression.ClassFileSuperConstructorInvocationExpression;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;
import org.jd.core.v1.util.DefaultList;

public class AddCastExpressionVisitor extends AbstractJavaSyntaxVisitor {
	protected SearchFirstLineNumberVisitor searchFirstLineNumberVisitor = new SearchFirstLineNumberVisitor();

	protected TypeMaker typeMaker;
	protected Map<String, BaseType> typeBounds;
	protected Type returnedType;
	protected BaseType exceptionTypes;
	protected Type type;

	public AddCastExpressionVisitor(TypeMaker typeMaker) {
		this.typeMaker = typeMaker;
	}

	/**
	 * During visit, populate "typeBounds"
	 */
	@Override
	public void visit(BodyDeclaration declaration) {
		BaseMemberDeclaration memberDeclarations = declaration.getMemberDeclarations();

		if (memberDeclarations != null) {
			Map<String, BaseType> tb = typeBounds;

			typeBounds = ((ClassFileBodyDeclaration) declaration).getTypeBounds();
			memberDeclarations.accept(this);
			typeBounds = tb;
		}
	}

	/**
	 * During visit, populate "type"
	 */
	@Override
	public void visit(FieldDeclaration declaration) {
		if ((declaration.getFlags() & ACC_SYNTHETIC) == 0) {
			Type t = type;

			type = declaration.getType();
			declaration.getFieldDeclarators().accept(this);
			type = t;
		}
	}

	/**
	 * During visit, populate "type"
	 */
	@Override
	public void visit(FieldDeclarator declarator) {
		VariableInitializer variableInitializer = declarator.getVariableInitializer();

		if (variableInitializer != null) {
			int extraDimension = declarator.getDimension();

			if (extraDimension == 0) {
				variableInitializer.accept(this);
			} else {
				Type t = type;

				type = type.createType(type.getDimension() + extraDimension);
				variableInitializer.accept(this);
				type = t;
			}
		}
	}

	/**
	 * During visit, populate "typeBounds"
	 */
	@Override
	public void visit(StaticInitializerDeclaration declaration) {
		BaseStatement statements = declaration.getStatements();

		if (statements != null) {
			Map<String, BaseType> tb = typeBounds;

			typeBounds = ((ClassFileStaticInitializerDeclaration) declaration).getTypeBounds();
			statements.accept(this);
			typeBounds = tb;
		}
	}

	/**
	 * During visit, populate "typeBounds" and "exceptionTypes"
	 */
	@Override
	public void visit(ConstructorDeclaration declaration) {
		if ((declaration.getFlags() & (ACC_SYNTHETIC | ACC_BRIDGE)) == 0) {
			BaseStatement statements = declaration.getStatements();

			if (statements != null) {
				Map<String, BaseType> tb = typeBounds;
				BaseType et = exceptionTypes;

				typeBounds = ((ClassFileConstructorDeclaration) declaration).getTypeBounds();
				exceptionTypes = declaration.getExceptionTypes();
				statements.accept(this);
				typeBounds = tb;
				exceptionTypes = et;
			}
		}
	}

	/**
	 * During visit, populate "typeBounds", "returnedType", "exceptionTypes"
	 */
	@Override
	public void visit(MethodDeclaration declaration) {
		if ((declaration.getFlags() & (ACC_SYNTHETIC | ACC_BRIDGE)) == 0) {
			BaseStatement statements = declaration.getStatements();

			if (statements != null) {
				final Map<String, BaseType> tb = typeBounds;
				final Type rt = returnedType;
				final BaseType et = exceptionTypes;

				typeBounds = ((ClassFileMethodDeclaration) declaration).getTypeBounds();
				returnedType = declaration.getReturnedType();
				exceptionTypes = declaration.getExceptionTypes();
				statements.accept(this);
				typeBounds = tb;
				returnedType = rt;
				exceptionTypes = et;
			}
		}
	}

	/**
	 * During visit, populate "returnedType"
	 */
	@Override
	public void visit(LambdaIdentifiersExpression expression) {
		BaseStatement statements = expression.getStatements();

		if (statements != null) {
			Type rt = returnedType;

			returnedType = ObjectType.TYPE_OBJECT;
			statements.accept(this);
			returnedType = rt;
		}
	}

	/**
	 * If return expression does not match "returnType", add a cast
	 */
	@Override
	public void visit(ReturnExpressionStatement statement) {
		statement.setExpression(updateExpression(returnedType, statement.getExpression(), false, true));
	}

	/**
	 * If thrown exception does not match "exceptionTypes", add a cast
	 */
	@Override
	public void visit(ThrowStatement statement) {
		if ((exceptionTypes != null) && (exceptionTypes.size() == 1)) {
			Type exceptionType = exceptionTypes.getFirst();

			if (exceptionType.isGenericType() && !statement.getExpression().getType().equals(exceptionType)) {
				statement.setExpression(addCastExpression(exceptionType, statement.getExpression()));
			}
		}
	}

	/**
	 * During visit, populate "type"
	 */
	@Override
	public void visit(LocalVariableDeclaration declaration) {
		Type t = type;

		type = declaration.getType();
		declaration.getLocalVariableDeclarators().accept(this);
		type = t;
	}

	/**
	 * During visit, populate "type"
	 */
	@Override
	public void visit(LocalVariableDeclarator declarator) {
		VariableInitializer variableInitializer = declarator.getVariableInitializer();

		if (variableInitializer != null) {
			int extraDimension = declarator.getDimension();

			if (extraDimension == 0) {
				variableInitializer.accept(this);
			} else {
				Type t = type;

				type = type.createType(type.getDimension() + extraDimension);
				variableInitializer.accept(this);
				type = t;
			}
		}
	}

	/**
	 * During visit, populate "type"
	 */
	@Override
	public void visit(ArrayVariableInitializer declaration) {
		if (type.getDimension() == 0) {
			acceptListDeclaration(declaration);
		} else {
			Type t = type;

			type = type.createType(type.getDimension() - 1);
			acceptListDeclaration(declaration);
			type = t;
		}
	}

	/**
	 * During visit of NewInitializedArray, populate "type".
	 * 
	 * Otherwise, if expression type does not match "type", add a cast.
	 */
	@Override
	public void visit(ExpressionVariableInitializer declaration) {
		Expression expression = declaration.getExpression();

		if (expression.isNewInitializedArray()) {
			NewInitializedArray nia = (NewInitializedArray) expression;
			Type t = type;

			type = nia.getType();
			nia.getArrayInitializer().accept(this);
			type = t;
		} else {
			declaration.setExpression(updateExpression(type, expression, false, true));
		}
	}

	@Override
	public void visit(SuperConstructorInvocationExpression expression) {
		BaseExpression parameters = expression.getParameters();

		if ((parameters != null) && (parameters.size() > 0)) {
			boolean unique = typeMaker.matchCount(expression.getObjectType().getInternalName(), "<init>",
					parameters.size(), true) <= 1;
			boolean forceCast = !unique && (typeMaker.matchCount(typeBounds,
					expression.getObjectType().getInternalName(), "<init>", parameters, true) > 1);
			expression.setParameters(
					updateParameters(((ClassFileSuperConstructorInvocationExpression) expression).getParameterTypes(),
							parameters, forceCast, unique));
		}
	}

	@Override
	public void visit(ConstructorInvocationExpression expression) {
		BaseExpression parameters = expression.getParameters();

		if ((parameters != null) && (parameters.size() > 0)) {
			boolean unique = typeMaker.matchCount(expression.getObjectType().getInternalName(), "<init>",
					parameters.size(), true) <= 1;
			boolean forceCast = !unique && (typeMaker.matchCount(typeBounds,
					expression.getObjectType().getInternalName(), "<init>", parameters, true) > 1);
			expression.setParameters(
					updateParameters(((ClassFileConstructorInvocationExpression) expression).getParameterTypes(),
							parameters, forceCast, unique));
		}
	}

	@Override
	public void visit(MethodInvocationExpression expression) {
		BaseExpression parameters = expression.getParameters();

		if ((parameters != null) && (parameters.size() > 0)) {
			boolean unique = typeMaker.matchCount(expression.getInternalTypeName(), expression.getName(),
					parameters.size(), false) <= 1;
			boolean forceCast = !unique && (typeMaker.matchCount(typeBounds, expression.getInternalTypeName(),
					expression.getName(), parameters, false) > 1);
			
			// FIXME qua il problema è che parameterTypes è già Integer mentre dovrebbe essere Object, credo
			expression.setParameters(
					updateParameters(((ClassFileMethodInvocationExpression) expression).getParameterTypes(), parameters,
							forceCast, unique));
		}

		expression.getExpression().accept(this);
	}

	@Override
	public void visit(NewExpression expression) {
		BaseExpression parameters = expression.getParameters();

		if (parameters != null) {
			boolean unique = typeMaker.matchCount(expression.getObjectType().getInternalName(), "<init>",
					parameters.size(), true) <= 1;
			boolean forceCast = !unique && (typeMaker.matchCount(typeBounds,
					expression.getObjectType().getInternalName(), "<init>", parameters, true) > 1);
			expression.setParameters(updateParameters(((ClassFileNewExpression) expression).getParameterTypes(),
					parameters, forceCast, unique));
		}
	}

	/**
	 * During visit, populate "type"
	 */
	@Override
	public void visit(NewInitializedArray expression) {
		ArrayVariableInitializer arrayInitializer = expression.getArrayInitializer();

		if (arrayInitializer != null) {
			Type t = type;

			type = expression.getType();
			arrayInitializer.accept(this);
			type = t;
		}
	}

	@Override
	public void visit(FieldReferenceExpression expression) {
		Expression exp = expression.getExpression();

		if ((exp != null) && !exp.isObjectTypeReferenceExpression()) {
			Type type = typeMaker.makeFromInternalTypeName(expression.getInternalTypeName());

			if (type.getName() != null) {
				expression.setExpression(updateExpression(type, exp, false, true));
			}
		}
	}

	/**
	 * If operator is "=" and types do not match, add a cast
	 */
	@Override
	public void visit(BinaryOperatorExpression expression) {
		expression.getLeftExpression().accept(this);

		Expression rightExpression = expression.getRightExpression();

		if (expression.getOperator().equals("=")) {
			if (rightExpression.isMethodInvocationExpression()) {
				ClassFileMethodInvocationExpression mie = (ClassFileMethodInvocationExpression) rightExpression;

				if (mie.getTypeParameters() != null) {
					// Do not add cast expression if method contains type parameters
					rightExpression.accept(this);
					return;
				}
			}

			expression.setRightExpression(
					updateExpression(expression.getLeftExpression().getType(), rightExpression, false, true));
			return;
		}

		rightExpression.accept(this);
	}

	/**
	 * Add a cast if types do not match
	 */
	@Override
	public void visit(TernaryOperatorExpression expression) {
		Type expressionType = expression.getType();

		expression.getCondition().accept(this);
		expression.setTrueExpression(updateExpression(expressionType, expression.getTrueExpression(), false, true));
		expression.setFalseExpression(updateExpression(expressionType, expression.getFalseExpression(), false, true));
	}

	protected BaseExpression updateParameters(BaseType types, BaseExpression expressions, boolean forceCast,
			boolean unique) {
		if (expressions != null) {
			if (expressions.isList()) {
				DefaultList<Type> typeList = types.getList();
				DefaultList<Expression> expressionList = expressions.getList();

				for (int i = expressionList.size() - 1; i >= 0; i--) {
					expressionList.set(i, updateParameter(typeList.get(i), expressionList.get(i), forceCast, unique));
				}
			} else {
				expressions = updateParameter(types.getFirst(), expressions.getFirst(), forceCast, unique);
			}
		}

		return expressions;
	}

	private Expression updateParameter(Type type, Expression expression, boolean forceCast, boolean unique) {
		expression = updateExpression(type, expression, forceCast, unique);

		if (type == TYPE_BYTE) {
			if (expression.isIntegerConstantExpression()) {
				expression = new CastExpression(TYPE_BYTE, expression);
			} else if (expression.isTernaryOperatorExpression()) {
				Expression exp = expression.getTrueExpression();

				if (exp.isIntegerConstantExpression() || exp.isTernaryOperatorExpression()) {
					expression = new CastExpression(TYPE_BYTE, expression);
				} else {
					exp = expression.getFalseExpression();

					if (exp.isIntegerConstantExpression() || exp.isTernaryOperatorExpression()) {
						expression = new CastExpression(TYPE_BYTE, expression);
					}
				}
			}
		}

		return expression;
	}

	/**
	 * If type is not expression.getType(), add a cast, OR TRANSFORM IT (FIXME)
	 */
	private Expression updateExpression(Type type, Expression expression, boolean forceCast, boolean unique) {
		if (expression.isNullExpression()) {
			if (forceCast) {
				searchFirstLineNumberVisitor.init();
				expression.accept(searchFirstLineNumberVisitor);
				expression = new CastExpression(searchFirstLineNumberVisitor.getLineNumber(), type, expression);
			}
		} else {
			Type expressionType = expression.getType();

			if (!expressionType.equals(type)) {
				if (type.isObjectType()) {
					if (expressionType.isObjectType()) {
						ObjectType objectType = (ObjectType) type;
						ObjectType expressionObjectType = (ObjectType) expressionType;

						if (forceCast && !objectType.rawEquals(expressionObjectType)) {
							// Force disambiguation of method invocation => Add cast
							if (expression.isNewExpression()) {
								ClassFileNewExpression ne = (ClassFileNewExpression) expression;
								ne.setObjectType(ne.getObjectType().createType(null));
							}
							expression = addCastExpression(objectType, expression);
						} else if (!ObjectType.TYPE_OBJECT.equals(type)
								&& !typeMaker.isAssignable(typeBounds, objectType, expressionObjectType)) {
							BaseTypeArgument ta1 = objectType.getTypeArguments();
							BaseTypeArgument ta2 = expressionObjectType.getTypeArguments();
							Type t = type;

							if ((ta1 != null) && (ta2 != null) && !ta1.isTypeArgumentAssignableFrom(typeBounds, ta2)) {
								// Incompatible typeArgument arguments => Add cast
								t = objectType.createType(null);
							}
							expression = addCastExpression(t, expression);
						}
					} else if (expressionType.isPrimitiveType() && forceCast) {
						expression = addCastExpression(type, expression);
					} else if (expressionType.isGenericType() && !ObjectType.TYPE_OBJECT.equals(type)) {
						expression = addCastExpression(type, expression);
					}
				} else if (type.isGenericType()) {
					if (expressionType.isObjectType() || expressionType.isGenericType()) {
						expression = addCastExpression(type, expression);
					}
				}
			}

			if (expression.isCastExpression()) {
				Type ceExpressionType = expression.getExpression().getType();

				if (type.isObjectType() && ceExpressionType.isObjectType()) {
					ObjectType ot1 = (ObjectType) type;
					ObjectType ot2 = (ObjectType) ceExpressionType;

					if (ot1.equals(ot2)) {
						// Remove cast expression
						expression = expression.getExpression();
					} else if (unique && typeMaker.isAssignable(typeBounds, ot1, ot2)) {
						// Remove cast expression
						expression = expression.getExpression();
					}
				}
			}

			expression.accept(this);
		}

		return expression;
	}

	private Expression addCastExpression(Type type, Expression expression) {
		if (expression.isCastExpression()) {
			if (type.equals(expression.getExpression().getType())) {
				return expression.getExpression();
			} else {
				CastExpression ce = (CastExpression) expression;

				ce.setType(type); // FIXME not sure of this
				return ce;
			}
		} else {
			searchFirstLineNumberVisitor.init();
			expression.accept(searchFirstLineNumberVisitor);
			return new CastExpression(searchFirstLineNumberVisitor.getLineNumber(), type, expression);
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
	public void visit(LocalVariableReferenceExpression expression) {
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
	public void visit(Types list) {
	}

	@Override
	public void visit(TypeParameterWithTypeBounds type) {
	}
}
