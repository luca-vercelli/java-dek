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

public class JavaMethodOverloadingTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	// https://github.com/java-decompiler/jd-core/issues/33
	public void testArrayMethodOverloading() throws Exception {

		@SuppressWarnings("unused")
		class ArrayMethodOverloading {
			void use(Object[] o) {
			}

			void use(Object o) {
			}

			void test1() {
				use("string");
			}

			void test2() {
				use((Object) new Object[] { "" });
			}

			void test3() {
				use(null);
			}

			void test4() {
				use((Object) null);
			}
		}

		String internalClassName = ArrayMethodOverloading.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(": 34 */", "use(\"string\");")));
		assertTrue(source.matches(PatternMaker.make(": 38 */", "use((Object)new Object[] { \"\" });")));
		assertTrue(source.matches(PatternMaker.make(": 42 */", "use((Object[])null);")));
		assertTrue(source.matches(PatternMaker.make(": 46 */", "use((Object)null);")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

}
