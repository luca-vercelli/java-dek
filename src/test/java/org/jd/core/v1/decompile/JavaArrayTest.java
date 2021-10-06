/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.regex.PatternMaker;
import org.jd.core.v1.service.loader.ZipLoader;
import org.junit.Test;

public class JavaArrayTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	public void testJdk150Array() throws Exception {
		String internalClassName = "org/jd/core/test/Array";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.5.0.zip");
		Loader loader = new ZipLoader(is);
		String source = decompiler.decompile(loader, internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(": 13 */", "int[][] arrayOfInt1 = new int[1][];")));
		assertTrue(source.matches(PatternMaker.make(": 30 */", "int[][] arrayOfInt1 = { { 0, 1, 2")));

		assertTrue(source
				.matches(PatternMaker.make(": 52 */", "testException2(new Exception[][]", "{ { new Exception(\"1\")")));

		assertTrue(source.matches(PatternMaker.make(": 73 */", "testInt2(new int[][] { { 1,")));

		assertTrue(source.matches(PatternMaker.make(": 73 */", "testInt2(new int[][] { { 1,")));
		assertTrue(source.matches(PatternMaker.make(": 75 */", "testInt3(new int[][][] { { { 0, 1")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.5", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testJdk170Array() throws Exception {
		String internalClassName = "org/jd/core/test/Array";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
		Loader loader = new ZipLoader(is);
		String source = decompiler.decompile(loader, internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(": 12 */", "int[] i1 = new int[1];")));
		assertTrue(source.matches(PatternMaker.make(": 13 */", "int[][] i2 = new int[1][];")));
		assertTrue(source.matches(PatternMaker.make(": 14 */", "int[][][] i3 = new int[1][][];")));
		assertTrue(source.matches(PatternMaker.make(": 15 */", "int[][][] i4 = new int[1][2][];")));
		assertTrue(source.matches(PatternMaker.make(": 22 */", "String[][][][] s5 = new String[1][2][][];")));

		assertTrue(source.matches(PatternMaker.make(": 26 */", "byte[] b1 = { 1, 2 } ;")));
		assertTrue(source.matches(PatternMaker.make(": 27 */", "byte[][] b2 = { { 1, 2 } } ;")));
		assertTrue(source.matches(PatternMaker.make(": 28 */", "byte[][][][] b3 = { { { 3, 4 } } } ;")));

		assertTrue(source.matches(
				PatternMaker.make(": 48 */", "testException1(new Exception[]", "{ new Exception(\"1\") } );")));

		assertTrue(source.matches(PatternMaker.make(": 73 */", "testInt2(new int[][]", "{ { 1 } ,")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testInitializedArrayInTernaryOperator() throws Exception {
		@SuppressWarnings({ "unused", "rawtypes" })
		class InitializedArrayInTernaryOperator {
			Class[] test0(int i) {
				return (i == 0) ? new Class[] { Object.class } : null;
			}

			Class[] test2(int i) {
				return (i == 0) ? new Class[] { Object.class, String.class, Number.class } : null;
			}

			Class[][] test3(int i) {
				return (i == 0) ? new Class[][] { { Object.class }, { String.class, Number.class } } : null;
			}

			Class[] test4(int i) {
				return (i == 0) ? null : new Class[] { Object.class };
			}

			Class[] test5(int i) {
				return (i == 0) ? null : new Class[] { Object.class, String.class, Number.class };
			}

			Class[][] test6(int i) {
				return (i == 0) ? null : new Class[][] { { Object.class }, { String.class, Number.class } };
			}

			Class[] test7(int i) {
				return (i == 0) ? new Class[] { Object.class } : new Class[] { String.class, Number.class };
			}
		}

		String internalClassName = InitializedArrayInTernaryOperator.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source
				.matches(PatternMaker.make(" 81 */", "return (i == 0) ? new Class<?>[] { Object.class } : null;")));
		assertTrue(source.matches(PatternMaker.make(" 85 */",
				"return (i == 0) ? new Class<?>[] { Object.class, String.class, Number.class } : null;")));
		assertTrue(source.matches(PatternMaker.make(" 89 */",
				"return (i == 0) ? new Class[][] { { Object.class }, { String.class, Number.class } } : null;")));
		assertTrue(source
				.matches(PatternMaker.make(" 93 */", "return (i == 0) ? null : new Class<?>[] { Object.class };")));
		assertTrue(source.matches(PatternMaker.make(" 97 */",
				"return (i == 0) ? null : new Class<?>[] { Object.class, String.class, Number.class };")));
		assertTrue(source.matches(PatternMaker.make(" 101 */",
				"return (i == 0) ? null : new Class[][] { { Object.class }, { String.class, Number.class} };")));
		assertTrue(source.matches(PatternMaker.make(" 105 */",
				"return (i == 0) ? new Class<?>[] { Object.class } : new Class<?>[] { String.class, Number.class };")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
