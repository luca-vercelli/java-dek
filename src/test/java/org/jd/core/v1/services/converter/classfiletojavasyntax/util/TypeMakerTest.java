package org.jd.core.v1.services.converter.classfiletojavasyntax.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jd.core.v1.model.javasyntax.type.ObjectType;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;
import org.jd.core.v1.service.loader.ClassPathLoader;
import org.junit.Test;

public class TypeMakerTest {

	TypeMaker typeMaker = new TypeMaker(new ClassPathLoader());

	@Test
	public void testIsAssignable() {

		ObjectType t1 = typeMaker.makeFromInternalTypeName(ArrayList.class.getName().replace(".", "/"));
		ObjectType t2 = typeMaker.makeFromInternalTypeName(List.class.getName().replace(".", "/"));

		assertTrue(typeMaker.isAssignable(new HashMap<>(), t1, t1));
		assertTrue(typeMaker.isAssignable(new HashMap<>(), t2, t1));
		assertFalse(typeMaker.isAssignable(new HashMap<>(), t1, t2));
	}
}
