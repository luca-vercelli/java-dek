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
import org.junit.Ignore;
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
		assertTrue(source.matches(PatternMaker.make(": 36 */", "long b = (long)(double)")));
		assertTrue(source.matches(PatternMaker.make(": 41 */", "long b = Long.MAX_VALUE")));
		assertTrue(source.matches(PatternMaker.make(": 46 */", "long b = (long)(double)")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	static class LongCasting {
		public static long l (int x, int y) {
			long rc = ( (long)y << 32L) | x;
			return rc;
			}
	}

	@Test
	// FIXME https://github.com/java-decompiler/jd-core/issues/45
	public void testLongCasting() throws Exception {

		String internalClassName = LongCasting.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(":  0 */", "public static long l(int x, int y) {")));
		// assertTrue(source.matches(PatternMaker.make(": 65 */", "long rc = ( (long)y << 32L) | x;")));
		assertTrue(source.matches(PatternMaker.make(": 66 */", "return rc")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
	
	static class GenericParameterMethod {
	    static void use(Integer i) {
	        System.out.println("use(Integer)");
	    }
	    static <T> void use(T t) {
	        System.out.println("use(T)");
	    }
	    
	    public static void main(String... args) {
	        use(1);
	        use((Object) 1); // Calls use(T)
	    }
	}

	@Test
	@Ignore
	// FIXME https://github.com/java-decompiler/jd-core/issues/32
	public void testGenericsCast() throws Exception {

		String internalClassName = GenericParameterMethod.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		// assertTrue(source.matches(PatternMaker.make(": 35 */", "long b = (long)(double)")));
		// assertTrue(source.matches(PatternMaker.make(": 40 */", "long b = Long.MAX_VALUE")));
		assertTrue(source.matches(PatternMaker.make(": 95 */", "use((Object) 1);")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
