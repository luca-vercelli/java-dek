
package org.jd.core.v1.decompile;

import static org.jd.core.v1.regex.PatternMaker.assertMatch;
import static org.junit.Assert.assertTrue;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.junit.Test;

public class JavaRecursionTest {
	protected TestDecompiler decompiler = new TestDecompiler();

	static class RecursiveClass {
		public static void a() {
			b();
		}

		public static void b() {
			c();
		}

		public static void c() {
			a();
		}
	}

	@Test
	public void testLongCasting() throws Exception {

		String internalClassName = RecursiveClass.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "b();", 17);
		assertMatch(source, "c();", 21);
		assertMatch(source, "a();", 25);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
