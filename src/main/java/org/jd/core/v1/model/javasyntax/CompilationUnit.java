/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javasyntax;

import org.jd.core.v1.model.javasyntax.declaration.BaseTypeDeclaration;

/**
 * Java Syntax tree. An object of this class represents a Java class organized
 * in a tree of declarations, statements, expressions.
 */
public class CompilationUnit {
	protected BaseTypeDeclaration typeDeclarations;

	public CompilationUnit(BaseTypeDeclaration typeDeclarations) {
		this.typeDeclarations = typeDeclarations;
	}

	public BaseTypeDeclaration getTypeDeclarations() {
		return typeDeclarations;
	}

	@Override
	public String toString() {
		return "CompilationUnit(" + (this.typeDeclarations != null ? typeDeclarations.toString() : "null") + ")";
	}
}
