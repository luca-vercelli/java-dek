/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import static org.jd.core.v1.model.classfile.AccessType.*;

import java.util.List;

import org.jd.core.v1.model.classfile.Method;
import org.jd.core.v1.model.classfile.attribute.AttributeCode;
import org.jd.core.v1.model.javasyntax.AbstractJavaSyntaxVisitor;
import org.jd.core.v1.model.javasyntax.declaration.AnnotationDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.BodyDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ClassDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ConstructorDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.EnumDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.FieldDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.InterfaceDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.MethodDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.StaticInitializerDeclaration;
import org.jd.core.v1.model.javasyntax.statement.ByteCodeStatement;
import org.jd.core.v1.model.javasyntax.type.Type;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.cfg.ControlFlowGraph;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileBodyDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileConstructorOrMethodDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.ByteCodeWriter;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.ControlFlowGraphGotoReducer;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.ControlFlowGraphLoopReducer;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.ControlFlowGraphMaker;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.ControlFlowGraphReducer;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.ExceptionUtil;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.LocalVariableMaker;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.StatementMaker;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;

/**
 * Add FormalParameters, variables, statements
 */
public class CreateInstructionsVisitor extends AbstractJavaSyntaxVisitor {
	protected TypeMaker typeMaker;
	protected boolean dumpOpcode;

	/**
	 * Constructor
	 * 
	 * @param typeMaker
	 * @param dumpOpcode if true, print opcode instead of statements
	 */
	public CreateInstructionsVisitor(TypeMaker typeMaker, boolean dumpOpcode) {
		this.typeMaker = typeMaker;
		this.dumpOpcode = dumpOpcode;
	}

	@Override
	public void visit(AnnotationDeclaration declaration) {
		safeAccept(declaration.getBodyDeclaration());
	}

	/**
	 * Add FormalParameters, variables, statements
	 */
	@Override
	public void visit(BodyDeclaration declaration) {
		ClassFileBodyDeclaration bodyDeclaration = (ClassFileBodyDeclaration) declaration;

		// Parse byte code
		List<ClassFileConstructorOrMethodDeclaration> methods = bodyDeclaration.getMethodDeclarations();

		if (methods != null) {

			// Synthetic and Bridge methods first, in reverse order because of possibly
			// nested lambdas
			for (int i = methods.size() - 1; i >= 0; --i) {
				ClassFileConstructorOrMethodDeclaration method = methods.get(i);
				acceptSyntheticMethod(method);
			}

			// Then, all other methods
			for (ClassFileConstructorOrMethodDeclaration method : methods) {
				if ((method.getFlags() & (ACC_SYNTHETIC | ACC_BRIDGE)) == 0) {
					method.accept(this);
				}
			}
		}
	}

	private void acceptSyntheticMethod(ClassFileConstructorOrMethodDeclaration method) {
		if ((method.getFlags() & (ACC_SYNTHETIC | ACC_BRIDGE)) != 0) {
			method.accept(this);
		} else if ((method.getFlags() & (ACC_STATIC | ACC_BRIDGE)) == ACC_STATIC) {
			if (method.getMethod().getName().startsWith("access$")) {
				// Accessor -> bridge method
				method.setFlags(method.getFlags() | ACC_BRIDGE);
				method.accept(this);
			}
		} else if (method.getParameterTypes() != null) {
			if (method.getParameterTypes().isList()) {
				for (Type type : method.getParameterTypes()) {
					if (type.isObjectType() && (type.getName() == null)) {
						// Synthetic type in parameters -> synthetic method
						method.setFlags(method.getFlags() | ACC_SYNTHETIC);
						method.accept(this);
						break;
					}
				}
			} else {
				Type type = method.getParameterTypes().getFirst();
				if (type.isObjectType() && (type.getName() == null)) {
					// Synthetic type in parameters -> synthetic method
					method.setFlags(method.getFlags() | ACC_SYNTHETIC);
					method.accept(this);
				}
			}
		}
	}

	@Override
	public void visit(FieldDeclaration declaration) {
	}

	@Override
	public void visit(ConstructorDeclaration declaration) {
		createParametersVariablesAndStatements((ClassFileConstructorOrMethodDeclaration) declaration, true);
	}

	@Override
	public void visit(MethodDeclaration declaration) {
		createParametersVariablesAndStatements((ClassFileConstructorOrMethodDeclaration) declaration, false);
	}

	@Override
	public void visit(StaticInitializerDeclaration declaration) {
		createParametersVariablesAndStatements((ClassFileConstructorOrMethodDeclaration) declaration, false);
	}

	protected void createParametersVariablesAndStatements(ClassFileConstructorOrMethodDeclaration comd,
			boolean constructor) {
		Method method = comd.getMethod();
		AttributeCode attributeCode = method.getAttribute("Code");
		LocalVariableMaker localVariableMaker = new LocalVariableMaker(typeMaker, comd, constructor);

		if (attributeCode == null) {
			localVariableMaker.make(false, typeMaker);
		} else {
			StatementMaker statementMaker = new StatementMaker(typeMaker, localVariableMaker, comd);
			boolean containsLineNumber = (attributeCode.getAttribute("LineNumberTable") != null);

			try {
				boolean decompileSuccess = false;
				if (!dumpOpcode) {
					ControlFlowGraph cfg = ControlFlowGraphMaker.make(method);

					if (cfg != null) {
						ControlFlowGraphGotoReducer.reduce(cfg);
						ControlFlowGraphLoopReducer.reduce(cfg);

						if (ControlFlowGraphReducer.reduce(cfg)) {
							comd.setStatements(statementMaker.make(cfg));
							decompileSuccess = true;
						}
					}
				}
				if (!decompileSuccess) {
					comd.setStatements(new ByteCodeStatement(ByteCodeWriter.write("// ", method)));
				}
			} catch (Exception e) {
				assert ExceptionUtil.printStackTrace(e);
				comd.setStatements(new ByteCodeStatement(ByteCodeWriter.write("// ", method)));
			}

			localVariableMaker.make(containsLineNumber, typeMaker);
		}

		comd.setFormalParameters(localVariableMaker.getFormalParameters());

		if (comd.getClassFile().isInterface()) {
			comd.setFlags(comd.getFlags() & ~(ACC_PUBLIC | ACC_ABSTRACT));
		}
	}

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
		safeAccept(declaration.getBodyDeclaration());
	}
}
