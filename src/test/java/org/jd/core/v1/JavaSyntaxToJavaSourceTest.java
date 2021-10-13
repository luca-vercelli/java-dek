/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1;

import static org.jd.core.v1.model.classfile.AccessType.ACC_BRIDGE;
import static org.jd.core.v1.model.classfile.AccessType.ACC_FINAL;
import static org.jd.core.v1.model.classfile.AccessType.ACC_PRIVATE;
import static org.jd.core.v1.model.classfile.AccessType.ACC_PUBLIC;
import static org.jd.core.v1.model.classfile.AccessType.ACC_STATIC;
import static org.jd.core.v1.model.classfile.AccessType.ACC_SYNTHETIC;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.jd.core.v1.model.javasyntax.CompilationUnit;
import org.jd.core.v1.model.javasyntax.declaration.BodyDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ClassDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ConstructorDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.EnumDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ExpressionVariableInitializer;
import org.jd.core.v1.model.javasyntax.declaration.FieldDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.FieldDeclarator;
import org.jd.core.v1.model.javasyntax.declaration.FormalParameter;
import org.jd.core.v1.model.javasyntax.declaration.FormalParameters;
import org.jd.core.v1.model.javasyntax.declaration.InterfaceDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.LocalVariableDeclarator;
import org.jd.core.v1.model.javasyntax.declaration.MemberDeclarations;
import org.jd.core.v1.model.javasyntax.declaration.MethodDeclaration;
import org.jd.core.v1.model.javasyntax.expression.ArrayExpression;
import org.jd.core.v1.model.javasyntax.expression.BinaryOperatorExpression;
import org.jd.core.v1.model.javasyntax.expression.BooleanExpression;
import org.jd.core.v1.model.javasyntax.expression.DoubleConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.Expressions;
import org.jd.core.v1.model.javasyntax.expression.FieldReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.IntegerConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.LengthExpression;
import org.jd.core.v1.model.javasyntax.expression.LocalVariableReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.MethodInvocationExpression;
import org.jd.core.v1.model.javasyntax.expression.NewExpression;
import org.jd.core.v1.model.javasyntax.expression.NullExpression;
import org.jd.core.v1.model.javasyntax.expression.ObjectTypeReferenceExpression;
import org.jd.core.v1.model.javasyntax.expression.ParenthesesExpression;
import org.jd.core.v1.model.javasyntax.expression.StringConstantExpression;
import org.jd.core.v1.model.javasyntax.expression.ThisExpression;
import org.jd.core.v1.model.javasyntax.statement.ExpressionStatement;
import org.jd.core.v1.model.javasyntax.statement.ForEachStatement;
import org.jd.core.v1.model.javasyntax.statement.IfStatement;
import org.jd.core.v1.model.javasyntax.statement.LocalVariableDeclarationStatement;
import org.jd.core.v1.model.javasyntax.statement.ReturnExpressionStatement;
import org.jd.core.v1.model.javasyntax.statement.ReturnStatement;
import org.jd.core.v1.model.javasyntax.statement.Statements;
import org.jd.core.v1.model.javasyntax.statement.SwitchStatement;
import org.jd.core.v1.model.javasyntax.type.ObjectType;
import org.jd.core.v1.model.javasyntax.type.PrimitiveType;
import org.jd.core.v1.model.javasyntax.type.Type;
import org.jd.core.v1.model.javasyntax.type.Types;
import org.jd.core.v1.model.message.CompileConfiguration;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.JavaVersion;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.JavaSyntaxToJavaFragmentProcessor;
import org.jd.core.v1.service.layouter.LayoutFragmentProcessor;
import org.jd.core.v1.service.loader.NopLoader;
import org.jd.core.v1.service.printer.PlainTextMetaPrinter;
import org.jd.core.v1.service.tokenizer.javafragmenttotoken.JavaFragmentToTokenProcessor;
import org.jd.core.v1.service.writer.WriteTokenProcessor;
import org.junit.Test;

public class JavaSyntaxToJavaSourceTest {

	protected JavaSyntaxToJavaFragmentProcessor fragmenter = JavaSyntaxToJavaFragmentProcessor.getInstance();
	protected LayoutFragmentProcessor layouter = LayoutFragmentProcessor.getInstance();
	protected JavaFragmentToTokenProcessor tokenizer = JavaFragmentToTokenProcessor.getInstance();
	protected WriteTokenProcessor writer = WriteTokenProcessor.getInstance();

	@Test
	public void testClassDeclaration() throws Exception {
		ObjectType stringArrayType = new ObjectType("java/lang/String", "java.lang.String", "String", 1);
		ObjectType printStreamType = new ObjectType("java/io/PrintStream", "java.io.PrintStream", "PrintStream");

		CompilationUnit compilationUnit = new CompilationUnit(new ClassDeclaration(ACC_PUBLIC,
				"org/jd/core/v1/service/test/TokenWriterTest", "TokenWriterTest",
				new BodyDeclaration("org/jd/core/v1/service/test/TokenWriterTest", new MethodDeclaration(
						ACC_PUBLIC | ACC_STATIC, "main", PrimitiveType.TYPE_VOID,
						new FormalParameter(ObjectType.TYPE_STRING.createType(1), "args"), "([Ljava/lang/String;)V",
						new Statements(
								new IfStatement(new BinaryOperatorExpression(8, PrimitiveType.TYPE_BOOLEAN,
										new LocalVariableReferenceExpression(8, stringArrayType, "args"), "==",
										new NullExpression(8, stringArrayType), 9), ReturnStatement.RETURN),
								new LocalVariableDeclarationStatement(PrimitiveType.TYPE_INT,
										new LocalVariableDeclarator("i",
												new ExpressionVariableInitializer(new MethodInvocationExpression(10,
														PrimitiveType.TYPE_INT,
														new ThisExpression(10,
																new ObjectType(
																		"org/jd/core/v1/service/test/TokenWriterTest",
																		"org.jd.core.v1.service.writer.TokenWriterTest",
																		"TokenWriterTest")),
														"org/jd/core/v1/service/test/TokenWriterTest", "call",
														"(Ljava/lang/String;ILjava/util/Enumeration;C)I",
														new Expressions(new StringConstantExpression(11, "aaaa"),
																new LocalVariableReferenceExpression(12,
																		PrimitiveType.TYPE_INT, "b"),
																new NewExpression(13,
																		new ObjectType("java/util/Enumeration",
																				"java.util.Enumeration", "Enumeration"),
																		"()V",
																		new BodyDeclaration("java/util/Enumeration",
																				new MemberDeclarations(
																						new MethodDeclaration(
																								ACC_PUBLIC,
																								"hasMoreElements",
																								PrimitiveType.TYPE_BOOLEAN,
																								"()Z",
																								new ReturnExpressionStatement(
																										new BooleanExpression(
																												15,
																												false))),
																						new MethodDeclaration(
																								ACC_PUBLIC,
																								"nextElement",
																								ObjectType.TYPE_OBJECT,
																								"()Ljava/lang/Object;",
																								new ReturnExpressionStatement(
																										new NullExpression(
																												18,
																												ObjectType.TYPE_OBJECT)))))),
																new LocalVariableReferenceExpression(21,
																		PrimitiveType.TYPE_CHAR, "c")))))),
								new ExpressionStatement(new MethodInvocationExpression(22, PrimitiveType.TYPE_VOID,
										new FieldReferenceExpression(22, printStreamType,
												new ObjectTypeReferenceExpression(22,
														new ObjectType("java/lang/System", "java.lang.System",
																"System")),
												"java/lang/System", "out", "Ljava/io/PrintStream;"),
										"java/io/PrintStream", "println", "(I)V",
										new LocalVariableReferenceExpression(22, PrimitiveType.TYPE_INT, "i"))))))));

		PlainTextMetaPrinter printer = new PlainTextMetaPrinter();
		CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
		Message message = new Message();
		message.setCompilationUnit(compilationUnit);

		message.setLoader(new NopLoader());
		message.setPrinter(printer);
		message.setMainInternalTypeName("org/jd/core/v1/service/test/TokenWriterTest");
		message.setMaxLineNumber(22);
		message.setConfiguration(configuration);
		message.setMajorVersion(0);
		message.setMinorVersion(0);

		fragmenter.process(message);
		layouter.process(message);
		tokenizer.process(message);
		writer.process(message);

		String source = printer.toString();

		System.out.println("- - - - - - - - ");
		System.out.print(source);
		System.out.println("- - - - - - - - ");

		assertTrue(source.indexOf("/* 22: 22 */") != -1);
		assertTrue(source.indexOf("java.lang.System") == -1);
	}

	@Test
	public void testInterfaceDeclaration() throws Exception {
		ObjectType cloneableType = new ObjectType("java/lang/Cloneable", "java.lang.Cloneable", "Cloneable");
		ObjectType stringType = new ObjectType("java/lang/String", "java.lang.String", "String");
		ObjectType listType = new ObjectType("java/util/List", "java.util.List", "List", stringType);

		CompilationUnit compilationUnit = new CompilationUnit(new InterfaceDeclaration(ACC_PUBLIC,
				"org/jd/core/v1/service/test/InterfaceTest", "InterfaceTest", new Types(listType, cloneableType)));

		PlainTextMetaPrinter printer = new PlainTextMetaPrinter();
		Message message = new Message();
		message.setCompilationUnit(compilationUnit);

		message.setLoader(new NopLoader());
		message.setPrinter(printer);
		message.setMainInternalTypeName("org/jd/core/v1/service/test/InterfaceTest");
		message.setMaxLineNumber(0);
		message.setMajorVersion(JavaVersion.JAVA5);
		message.setMinorVersion(0);

		fragmenter.process(message);
		layouter.process(message);
		tokenizer.process(message);
		writer.process(message);

		String source = printer.toString();

		System.out.println("- - - - - - - - ");
		System.out.print(source);
		System.out.println("- - - - - - - - ");

		assertTrue(source.indexOf("/*   1:   0 */ package org.jd.core.v1.service.test;") != -1);
		assertTrue(source.indexOf("interface InterfaceTest") != -1);
		assertTrue(source.indexOf("extends List") != -1);
		assertTrue(source.indexOf("<String") != -1);
		assertTrue(source.indexOf(", Cloneable") != -1);
	}

	@Test
	public void testEnumDayDeclaration() throws Exception {
		CompilationUnit compilationUnit = new CompilationUnit(
				new EnumDeclaration(null, ACC_PUBLIC, "org/jd/core/v1/service/test/Day", "Day", null,
						Arrays.asList(new EnumDeclaration.Constant("SUNDAY"), new EnumDeclaration.Constant("MONDAY"),
								new EnumDeclaration.Constant("TUESDAY"), new EnumDeclaration.Constant("WEDNESDAY"),
								new EnumDeclaration.Constant("THURSDAY"), new EnumDeclaration.Constant("FRIDAY"),
								new EnumDeclaration.Constant("SATURDAY")),
						null));

		PlainTextMetaPrinter printer = new PlainTextMetaPrinter();
		Message message = new Message();
		message.setCompilationUnit(compilationUnit);

		message.setLoader(new NopLoader());
		message.setPrinter(printer);
		message.setMainInternalTypeName("org/jd/core/v1/service/test/Day");
		message.setMaxLineNumber(0);
		message.setMajorVersion(0);
		message.setMinorVersion(0);

		fragmenter.process(message);
		layouter.process(message);
		tokenizer.process(message);
		writer.process(message);

		String source = printer.toString();

		System.out.println("- - - - - - - - ");
		System.out.print(source);
		System.out.println("- - - - - - - - ");

		assertTrue(source.indexOf(", MONDAY") != -1 || source.indexOf("-->MONDAY") != -1);
	}

	@Test
	public void testEnumPlanetDeclaration() throws Exception {
		// Type cloneableType = new ObjectType("java/lang/Cloneable",
		// "java.lang.Cloneable", "Cloneable");
		Type stringType = ObjectType.TYPE_STRING;
		Type arrayOfStringType = stringType.createType(1);
		// Type listType = new ObjectType("java/util/List", "java.util.List", "List",
		// stringType);
		Type printStreamType = new ObjectType("java/io/PrintStream", "java.io.PrintStream", "PrintStream");
		Type planetType = new ObjectType("org/jd/core/v1/service/test/Planet", "org.jd.core.v1.service.test.Planet",
				"Planet");
		ThisExpression thisExpression = new ThisExpression(
				new ObjectType("org/jd/core/v1/service/test/Planet", "org.jd.core.v1.service.test.Planet", "Planet"));

		CompilationUnit compilationUnit = new CompilationUnit(new EnumDeclaration(ACC_PUBLIC,
				"org/jd/core/v1/service/test/Planet", "Planet",
				Arrays.asList(
						new EnumDeclaration.Constant("MERCURY",
								new Expressions(new DoubleConstantExpression(3.303e+23),
										new DoubleConstantExpression(2.4397e6))),
						new EnumDeclaration.Constant("VENUS",
								new Expressions(new DoubleConstantExpression(4.869e+24),
										new DoubleConstantExpression(6.0518e6))),
						new EnumDeclaration.Constant("EARTH",
								new Expressions(new DoubleConstantExpression(5.976e+24),
										new DoubleConstantExpression(6.37814e6)))),
				new BodyDeclaration("org/jd/core/v1/service/test/Planet", new MemberDeclarations(
						new FieldDeclaration(ACC_PRIVATE | ACC_FINAL, PrimitiveType.TYPE_DOUBLE,
								new FieldDeclarator("mass")),
						new FieldDeclaration(ACC_PRIVATE | ACC_FINAL, PrimitiveType.TYPE_DOUBLE,
								new FieldDeclarator("radius")),
						new ConstructorDeclaration(0,
								new FormalParameters(new FormalParameter(PrimitiveType.TYPE_DOUBLE, "mass"),
										new FormalParameter(PrimitiveType.TYPE_DOUBLE, "radius")),
								"(DD)V",
								new Statements(
										new ExpressionStatement(new BinaryOperatorExpression(0,
												PrimitiveType.TYPE_DOUBLE,
												new FieldReferenceExpression(PrimitiveType.TYPE_DOUBLE, thisExpression,
														"org/jd/core/v1/service/test/Planet", "mass", "D"),
												"=",
												new LocalVariableReferenceExpression(PrimitiveType.TYPE_DOUBLE, "mass"),
												16)),
										new ExpressionStatement(new BinaryOperatorExpression(0,
												PrimitiveType.TYPE_DOUBLE,
												new FieldReferenceExpression(PrimitiveType.TYPE_DOUBLE, thisExpression,
														"org/jd/core/v1/service/test/Planet", "radius", "D"),
												"=",
												new LocalVariableReferenceExpression(PrimitiveType.TYPE_DOUBLE,
														"radius"),
												16)))),
						new MethodDeclaration(ACC_PRIVATE, "mass", PrimitiveType.TYPE_DOUBLE, "()D",
								new ReturnExpressionStatement(new FieldReferenceExpression(PrimitiveType.TYPE_DOUBLE,
										thisExpression, "org/jd/core/v1/service/test/Planet", "mass", "D"))),
						new MethodDeclaration(ACC_PRIVATE, "radius", PrimitiveType.TYPE_DOUBLE, "()D",
								new ReturnExpressionStatement(new FieldReferenceExpression(PrimitiveType.TYPE_DOUBLE,
										thisExpression, "org/jd/core/v1/service/test/Planet", "radius", "D"))),
						new FieldDeclaration(ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
								PrimitiveType.TYPE_DOUBLE,
								new FieldDeclarator("G",
										new ExpressionVariableInitializer(new DoubleConstantExpression(6.67300E-11)))),
						new MethodDeclaration(0, "surfaceGravity", PrimitiveType.TYPE_DOUBLE, "()D",
								new ReturnExpressionStatement(new BinaryOperatorExpression(0, PrimitiveType.TYPE_DOUBLE,
										new FieldReferenceExpression(PrimitiveType.TYPE_DOUBLE, thisExpression,
												"org/jd/core/v1/service/test/Planet", "G", "D"),
										"*",
										new BinaryOperatorExpression(0, PrimitiveType.TYPE_DOUBLE,
												new FieldReferenceExpression(PrimitiveType.TYPE_DOUBLE, thisExpression,
														"org/jd/core/v1/service/test/Planet", "mass", "D"),
												"/",
												new ParenthesesExpression(new BinaryOperatorExpression(0,
														PrimitiveType.TYPE_DOUBLE,
														new FieldReferenceExpression(
																PrimitiveType.TYPE_DOUBLE, thisExpression,
																"org/jd/core/v1/service/test/Planet", "radius", "D"),
														"*",
														new FieldReferenceExpression(PrimitiveType.TYPE_DOUBLE,
																thisExpression, "org/jd/core/v1/service/test/Planet",
																"radius", "D"),
														4)),
												4),
										4))),
						new MethodDeclaration(0, "surfaceWeight", PrimitiveType.TYPE_DOUBLE,
								new FormalParameter(PrimitiveType.TYPE_DOUBLE, "otherMass"), "(D)D",
								new ReturnExpressionStatement(new BinaryOperatorExpression(0, PrimitiveType.TYPE_DOUBLE,
										new LocalVariableReferenceExpression(PrimitiveType.TYPE_DOUBLE, "otherMass"),
										"*",
										new MethodInvocationExpression(PrimitiveType.TYPE_DOUBLE, thisExpression,
												"org/jd/core/v1/service/test/Planet", "surfaceGravity", "()D"),
										4))),
						new MethodDeclaration(ACC_PUBLIC | ACC_STATIC, "surfaceWeight",
								PrimitiveType.TYPE_VOID, new FormalParameter(arrayOfStringType, "args"),
								"([Ljava/lan/String;)V",
								new Statements(
										new IfStatement(new BinaryOperatorExpression(
												0, PrimitiveType.TYPE_BOOLEAN,
												new LengthExpression(new LocalVariableReferenceExpression(
														arrayOfStringType, "args")),
												"!=", new IntegerConstantExpression(PrimitiveType.TYPE_INT, 1), 9),
												new Statements(
														new ExpressionStatement(new MethodInvocationExpression(
																PrimitiveType.TYPE_VOID,
																new FieldReferenceExpression(
																		printStreamType,
																		new ObjectTypeReferenceExpression(
																				new ObjectType("java/lang/System",
																						"java.lang.System", "System")),
																		"java/lang/System", "out",
																		"Ljava/io/PrintStream;"),
																"java/io/PrintStream", "println",
																"(Ljava/lang/String;)V",
																new StringConstantExpression(
																		"Usage: java Planet <earth_weight>"))),
														new ExpressionStatement(
																new MethodInvocationExpression(PrimitiveType.TYPE_VOID,
																		new ObjectTypeReferenceExpression(
																				new ObjectType("java/lang/System",
																						"java.lang.System", "System")),
																		"java/lang/System", "exit", "(I)V",
																		new IntegerConstantExpression(
																				PrimitiveType.TYPE_INT, -1))))),
										new LocalVariableDeclarationStatement(PrimitiveType.TYPE_DOUBLE,
												new LocalVariableDeclarator("earthWeight",
														new ExpressionVariableInitializer(
																new MethodInvocationExpression(
																		PrimitiveType.TYPE_DOUBLE,
																		new ObjectTypeReferenceExpression(
																				new ObjectType("java/lang/Double",
																						"java.lang.Double", "Double")),
																		"java/lang/Double", "parseDouble",
																		"(Ljava/lang/String;)D",
																		new ArrayExpression(
																				new LocalVariableReferenceExpression(
																						arrayOfStringType, "args"),
																				new IntegerConstantExpression(
																						PrimitiveType.TYPE_INT, 0)))))),
										new LocalVariableDeclarationStatement(PrimitiveType.TYPE_DOUBLE,
												new LocalVariableDeclarator("mass",
														new ExpressionVariableInitializer(new BinaryOperatorExpression(
																0, PrimitiveType.TYPE_DOUBLE,
																new LocalVariableReferenceExpression(
																		PrimitiveType.TYPE_DOUBLE, "earthWeight"),
																"/",
																new MethodInvocationExpression(
																		PrimitiveType.TYPE_DOUBLE,
																		new FieldReferenceExpression(planetType,
																				new ObjectTypeReferenceExpression(
																						new ObjectType(
																								"org/jd/core/v1/service/test/Planet",
																								"org.jd.core.v1.service.test.Planet",
																								"Planet")),
																				"org/jd/core/v1/service/test/Planet",
																				"EARTH",
																				"org/jd/core/v1/service/test/Planet"),
																		"org/jd/core/v1/service/test/Planet",
																		"surfaceGravity", "()D"),
																4)))),
										new ForEachStatement(planetType, "p",
												new MethodInvocationExpression(planetType,
														new ObjectTypeReferenceExpression(new ObjectType(
																"org/jd/core/v1/service/test/Planet",
																"org.jd.core.v1.service.test.Planet", "Planet")),
														"org/jd/core/v1/service/test/Planet", "values",
														"()[Lorg/jd/core/v1/service/test/Planet;"),
												new ExpressionStatement(new MethodInvocationExpression(
														PrimitiveType.TYPE_VOID,
														new FieldReferenceExpression(printStreamType,
																new ObjectTypeReferenceExpression(
																		new ObjectType("java/lang/System",
																				"java.lang.System", "System")),
																"java/lang/System", "out", "Ljava/io/PrintStream;"),
														"java/io/PrintStream", "printf",
														"(Ljava/lang/String;[Ljava/lang/Object;)V",
														new Expressions(
																new StringConstantExpression(
																		"Your weight on %s is %f%n"),
																new LocalVariableReferenceExpression(planetType, "p"),
																new MethodInvocationExpression(
																		PrimitiveType.TYPE_DOUBLE,
																		new LocalVariableReferenceExpression(planetType,
																				"p"),
																		"org/jd/core/v1/service/test/Planet",
																		"surfaceWeight", "(D)D",
																		new LocalVariableReferenceExpression(
																				PrimitiveType.TYPE_DOUBLE,
																				"mass"))))))))))));

		PlainTextMetaPrinter printer = new PlainTextMetaPrinter();
		Message message = new Message();
		message.setCompilationUnit(compilationUnit);

		message.setLoader(new NopLoader());
		message.setPrinter(printer);
		message.setMainInternalTypeName("org/jd/core/v1/service/test/Planet");
		message.setMaxLineNumber(0);
		message.setMajorVersion(0);
		message.setMinorVersion(0);

		fragmenter.process(message);
		layouter.process(message);
		JavaFragmentToTokenProcessor.getInstance().process(message);
		writer.process(message);

		String source = printer.toString();

		System.out.println("- - - - - - - - ");
		System.out.print(source);
		System.out.println("- - - - - - - - ");
	}

	@Test
	public void testSwitch() throws Exception {
		CompilationUnit compilationUnit = new CompilationUnit(new ClassDeclaration(ACC_PUBLIC,
				"org/jd/core/v1/service/test/SwitchTest", "SwitchTest",
				new BodyDeclaration("org/jd/core/v1/service/test/SwitchTest", new MethodDeclaration(
						ACC_PUBLIC | ACC_STATIC, "translate", PrimitiveType.TYPE_INT,
						new FormalParameter(PrimitiveType.TYPE_INT, "i"), "(I)Ljava/lang/String;",
						new SwitchStatement(new LocalVariableReferenceExpression(PrimitiveType.TYPE_INT, "i"),
								Arrays.asList(
										(SwitchStatement.Block) new SwitchStatement.LabelBlock(
												new SwitchStatement.ExpressionLabel(new IntegerConstantExpression(
														PrimitiveType.TYPE_INT, 0)),
												new Statements(
														new LocalVariableDeclarationStatement(ObjectType.TYPE_STRING,
																new LocalVariableDeclarator("zero",
																		new ExpressionVariableInitializer(
																				new StringConstantExpression("zero")))),
														new ReturnExpressionStatement(
																new LocalVariableReferenceExpression(
																		ObjectType.TYPE_STRING, "zero")))),
										new SwitchStatement.MultiLabelsBlock(Arrays.asList(
												(SwitchStatement.Label) new SwitchStatement.ExpressionLabel(
														new IntegerConstantExpression(PrimitiveType.TYPE_INT, 1)),
												new SwitchStatement.ExpressionLabel(
														new IntegerConstantExpression(PrimitiveType.TYPE_INT, 2))),
												new ReturnExpressionStatement(
														new StringConstantExpression("one or two"))),
										new SwitchStatement.LabelBlock(SwitchStatement.DEFAULT_LABEL,
												new ReturnExpressionStatement(
														new StringConstantExpression("other")))))))));

		PlainTextMetaPrinter printer = new PlainTextMetaPrinter();
		Message message = new Message();
		message.setCompilationUnit(compilationUnit);

		message.setLoader(new NopLoader());
		message.setPrinter(printer);
		message.setMainInternalTypeName("org/jd/core/v1/service/test/SwitchTest");
		message.setMaxLineNumber(0);
		message.setMajorVersion(0);
		message.setMinorVersion(0);

		fragmenter.process(message);
		layouter.process(message);
		tokenizer.process(message);
		writer.process(message);

		String source = printer.toString();

		System.out.println("- - - - - - - - ");
		System.out.print(source);
		System.out.println("- - - - - - - - ");

		assertTrue(source.indexOf("switch (i)") != -1);
	}

	@Test
	public void testBridgeAndSyntheticAttributes() throws Exception {
		CompilationUnit compilationUnit = new CompilationUnit(new ClassDeclaration(ACC_PUBLIC,
				"org/jd/core/v1/service/test/SyntheticAttributeTest", "SyntheticAttributeTest",
				new BodyDeclaration("org/jd/core/v1/service/test/SyntheticAttributeTest",
						new MemberDeclarations(
								new FieldDeclaration(ACC_PUBLIC | ACC_BRIDGE,
										PrimitiveType.TYPE_INT, new FieldDeclarator("i")),
								new MethodDeclaration(ACC_PUBLIC | ACC_BRIDGE,
										"testBridgeAttribute", PrimitiveType.TYPE_VOID, "()V"),
								new MethodDeclaration(ACC_PUBLIC | ACC_SYNTHETIC,
										"testSyntheticAttribute", PrimitiveType.TYPE_VOID, "()V")))));

		PlainTextMetaPrinter printer = new PlainTextMetaPrinter();
		Message message = new Message();
		message.setCompilationUnit(compilationUnit);

		message.setLoader(new NopLoader());
		message.setPrinter(printer);
		message.setMainInternalTypeName("org/jd/core/v1/service/test/SyntheticAttributeTest");
		message.setMaxLineNumber(0);
		message.setMajorVersion(0);
		message.setMinorVersion(0);

		fragmenter.process(message);
		layouter.process(message);
		tokenizer.process(message);
		writer.process(message);

		String source = printer.toString();

		System.out.println("- - - - - - - - ");
		System.out.print(source);
		System.out.println("- - - - - - - - ");

		assertTrue(source.indexOf("/* bridge */") == -1);
		assertTrue(source.indexOf("/* synthetic */") == -1);
	}
}
