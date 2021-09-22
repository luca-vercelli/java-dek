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

public class CfrTest {
	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	// https://github.com/java-decompiler/jd-core/issues/34
	public void testFloatingPointCasting() throws Exception {

		@SuppressWarnings("unused")
		class FloatingPointCasting {
			private final long l = 9223372036854775806L;
			private final Long L = 9223372036854775806L;

			long getLong() {
				return 9223372036854775806L;
			}

			void test1() {
				long b = (long) (double) getLong();
				System.out.println(b == getLong()); // Prints "false"
			}

			void test2() {
				long b = (long) (double) l;
				System.out.println(b == l); // Prints "false"
			}

			void test3() {
				long b = (long) (double) L;
				System.out.println(b == L); // Prints "false"
			}
		}

		String internalClassName = FloatingPointCasting.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(": 60 */", "long b = (long)(double)")));
		assertTrue(source.matches(PatternMaker.make(": 65 */", "long b = Long.MAX_VALUE")));
		assertTrue(source.matches(PatternMaker.make(": 70 */", "long b = (long)(double)")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

}
