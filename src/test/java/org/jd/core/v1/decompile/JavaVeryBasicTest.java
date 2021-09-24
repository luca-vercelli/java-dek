package org.jd.core.v1.decompile;

import static org.junit.Assert.assertTrue;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.junit.Test;

/**
 * This is mainly for didactic purpose
 * 
 * @author luca-vercelli 2021
 *
 */
public class JavaVeryBasicTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	class TestClass {
		public int v = 3;

		public void doSomething(int w) {
			v = w;
		}
	}

	@Test
	public void testVerySimpleClass() throws Exception {
		String internalClassName = TestClass.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source.indexOf("public int v = 3;") >= 0);
		assertTrue(source.indexOf("public void doSomething(int w) {") >= 0);
		assertTrue(source.indexOf("this.v = w;") >= 0);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
	}

}
