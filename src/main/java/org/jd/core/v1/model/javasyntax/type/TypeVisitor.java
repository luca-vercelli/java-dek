/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javasyntax.type;

/**
 * Traverse a tree of Type's.
 * 
 * There is one visit() method for every Type subclass (primitive, object,
 * generics), plus one method handling list of types.
 */
public interface TypeVisitor {
	void visit(PrimitiveType type);

	void visit(ObjectType type);

	void visit(InnerObjectType type);

	void visit(Types types);

	void visit(GenericType type);
}
