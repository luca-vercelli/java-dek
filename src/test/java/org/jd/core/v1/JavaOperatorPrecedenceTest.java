/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1;

import static org.junit.Assert.assertTrue;

import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.regex.PatternMaker;
import org.junit.Test;

public class JavaOperatorPrecedenceTest {
	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	public void testNewOperatorPrecedence() throws Exception {
		@SuppressWarnings("unused")
		class NewOperatorPrecedence {
			public void test() {
			}

			public /* static */ void main(String... args) {
				new NewOperatorPrecedence().test();
			}
		}

		String internalClassName = NewOperatorPrecedence.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(": 28 */", "new NewOperatorPrecedence().test();")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
