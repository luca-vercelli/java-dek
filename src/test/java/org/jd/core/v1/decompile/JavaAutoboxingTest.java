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

public class JavaAutoboxingTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	public void testAutoboxing() throws Exception {

		@SuppressWarnings("unused")
		class AutoboxingAndUnboxing {

			// https://github.com/java-decompiler/jd-core/issues/14
			void testUnnededAutoboxing() {
				// You don't need autoboxing here
				Integer intObj = 10;
				int i = intObj;
			}

			// https://github.com/java-decompiler/jd-core/issues/60
			int testRequiredAutoboxing() {
				return Double.valueOf(0.0).intValue();
			}

			Integer testReturn() {
				return 2;
			}
		}

		String internalClassName = AutoboxingAndUnboxing.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code

		assertMatch(source, "Integer intObj = 10;", 31);
		assertMatch(source, "int i = intObj;", 32);
		assertMatch(source, "return Double.valueOf(0.0D).intValue();", 37);
		assertMatch(source, "return 2;", 41);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	// FIXME
	// https://github.com/java-decompiler/jd-core/issues/14#issuecomment-584100313
	public void testAutoboxingOverload() throws Exception {

		@SuppressWarnings("unused")
		class AutoboxingOverload {

			void use(int i) {
			}

			void use(Integer i) {
			}

			void use(Double d) {
			}

			void testOverload() {
				// Needed to call correct overloaded method
				use((Integer) 1);
				// No need for box
				use(0.0D);
			}
		}

		String internalClassName = AutoboxingOverload.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "use(Integer.valueOf(1));", 78);
		assertMatch(source, "use(0.0D);", 80);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
