/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import static org.jd.core.v1.model.javasyntax.type.ObjectType.TYPE_INTEGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.jd.core.v1.model.javasyntax.type.BaseType;
import org.jd.core.v1.model.javasyntax.type.GenericType;
import org.jd.core.v1.model.javasyntax.type.TypeArgument;
import org.junit.Test;

public class BindTypeParametersToTypeArgumentsVisitorTest {
	@Test
	public void testGenericTypeI0() throws Exception {
		HashMap<String, TypeArgument> bindings = new HashMap<>();

		bindings.put("I", TYPE_INTEGER);

		GenericType genericType = new GenericType("I", 0);

		BindTypesToTypesVisitor visitor = new BindTypesToTypesVisitor();

		visitor.setBindings(bindings);
		visitor.init();
		genericType.accept(visitor);
		BaseType baseType = visitor.getType();

		assertNotNull(baseType);
		assertEquals(baseType, TYPE_INTEGER);
	}

	@Test
	public void testGenericTypeI3() throws Exception {
		HashMap<String, TypeArgument> bindings = new HashMap<>();

		bindings.put("I", TYPE_INTEGER);

		GenericType genericType = new GenericType("I", 3);

		BindTypesToTypesVisitor visitor = new BindTypesToTypesVisitor();

		visitor.setBindings(bindings);
		visitor.init();
		genericType.accept(visitor);
		BaseType baseType = visitor.getType();

		assertNotNull(baseType);
		assertEquals(baseType, TYPE_INTEGER.createType(3));
	}
}