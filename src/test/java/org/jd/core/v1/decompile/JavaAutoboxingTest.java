/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.junit.Assert.assertTrue;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.regex.PatternMaker;
import org.junit.Test;

public class JavaAutoboxingTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	// https://github.com/java-decompiler/jd-core/issues/14
	public void testAutoboxing() throws Exception {
		@SuppressWarnings("unused")
		class AutoboxingAndUnboxing {
			void test() {
				// You don't need autoboxing here
				Integer intObj = 10;
				int i = intObj;
			}

			void use(int i) {
			}

			void use(Integer i) {
			}

			Integer getInt() {
				return null;
			}

			void test2() {
				// Needed to call correct overloaded method
				use((Integer) 1);
				// Needed for null check
				getInt().intValue();
			}
			
			// FIXME https://github.com/java-decompiler/jd-core/issues/60
			/*int test3() {
				return Double.valueOf(0.0).intValue();
			}*/
		}

		String internalClassName = AutoboxingAndUnboxing.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(": 29 */", "Integer intObj = 10;")));
		assertTrue(source.matches(PatternMaker.make(": 30 */", "int i = intObj;")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
		
		// FIXME https://github.com/java-decompiler/jd-core/issues/14
	}
}
