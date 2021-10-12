/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.jd.core.v1.regex.PatternMaker.assertMatch;
import static org.junit.Assert.assertTrue;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.junit.Test;

public class SuperMemberAccessTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	class TestClass {
		private String a;

		private String test() {
			return "";
		}

		public class Child extends TestClass {
			public int a;

			public int test() {
				return 1;
			}

			String doSomething() {
				super.test();
				return super.a;
			}
		}
	}

	@Test
	// https://github.com/java-decompiler/jd-core/issues/20 // FIXME
	public void testSuperMembreAccess() throws Exception {

		String internalClassName = TestClass.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "return super.a;", 38);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));

	}
}
