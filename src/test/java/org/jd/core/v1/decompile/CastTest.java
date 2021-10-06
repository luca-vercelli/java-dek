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

public class CastTest {
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
		assertMatch(source, "long b = (long)(double)", 35);
		assertMatch(source, "long b = Long.MAX_VALUE", 40);
		assertMatch(source, "long b = (long)(double)", 45);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	static class LongCasting {
		public static long l(int x, int y) {
			long rc = ((long) y << 32L) | x;
			return rc;
		}
	}

	@Test
	// https://github.com/java-decompiler/jd-core/issues/45
	public void testLongCasting() throws Exception {

		String internalClassName = LongCasting.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "public static long l(int x, int y) {");
		assertMatch(source, "long rc = (long)y << 32L | (long)x;", 64);
		assertMatch(source, "return rc", 65);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	static class GenericParameterMethod {
		static void use(Integer i) {
		}

		static <T> void use(T t) {
		}

		public static void main(String... args) {
			use(1);
			use((Object) 1); // Calls use(T)
		}
	}

	@Test
	// FIXME https://github.com/java-decompiler/jd-core/issues/32
	public void testGenericsCast() throws Exception {

		String internalClassName = GenericParameterMethod.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		assertMatch(source, "use((Object) 1);", 96);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
