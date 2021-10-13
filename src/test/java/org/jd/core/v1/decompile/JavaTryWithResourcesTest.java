/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.jd.core.v1.regex.PatternMaker.assertMatch;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.model.message.CompileConfiguration;
import org.jd.core.v1.service.loader.ZipLoader;
import org.junit.Test;

public class JavaTryWithResourcesTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	public void testJdk170TryWithResources() throws Exception {
		String internalClassName = "org/jd/core/test/TryWithResources";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
		Loader loader = new ZipLoader(is);
		CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertMatch(source, "try (FileInputStream input = new FileInputStream(path))", 12);

		assertMatch(source, "try (FileInputStream input = new FileInputStream(path))", 49);
		assertMatch(source, "e.printStackTrace();", 57);
		assertMatch(source, "System.out.println(\"finally\");", 59);

		assertMatch(source, "try(FileInputStream input = new FileInputStream(pathIn);", 121);
		assertMatch(source, "BufferedInputStream bufferedInput = new BufferedInputStream(input);", 122);
		assertMatch(source, "FileOutputStream output = new FileOutputStream(pathOut);", 123);
		assertMatch(source, "BufferedOutputStream bufferedOutput = new BufferedOutputStream(output))", 124);
		assertMatch(source, "if (data == -7)", 132);
		assertMatch(source, "return 1;", 133);
		assertMatch(source, "return 2;", 142);
		assertMatch(source, "e.printStackTrace();", 144);
		assertMatch(source, "e.printStackTrace();", 150);
		assertMatch(source, "System.out.println(\"finally, before loop\");", 152);
		assertMatch(source, "System.out.println(\"finally, after loop\");", 156);
		assertMatch(source, "System.out.println(\"finally\");", 159);
		assertMatch(source, "return 3;", 162);

		assertMatch(source, "/* 162: 162 */");

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testJdk180TryWithResources() throws Exception {
		String internalClassName = "org/jd/core/test/TryWithResources";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.8.0.zip");
		Loader loader = new ZipLoader(is);
		CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertMatch(source, "try (FileInputStream input = new FileInputStream(path))", 12);

		assertMatch(source, "try (FileInputStream input = new FileInputStream(path))", 49);
		assertMatch(source, "e.printStackTrace();", 57);
		assertMatch(source, "System.out.println(\"finally\");", 59);

		assertMatch(source, "try(FileInputStream input = new FileInputStream(pathIn);", 121);
		assertMatch(source, "BufferedInputStream bufferedInput = new BufferedInputStream(input);", 122);
		assertMatch(source, "FileOutputStream output = new FileOutputStream(pathOut);", 123);
		assertMatch(source, "BufferedOutputStream bufferedOutput = new BufferedOutputStream(output))", 124);
		assertMatch(source, "if (data == -7)", 132);
		assertMatch(source, "return 1;", 133);
		assertMatch(source, "return 2;", 142);
		assertMatch(source, "e.printStackTrace();", 144);
		assertMatch(source, "e.printStackTrace();", 150);
		assertMatch(source, "System.out.println(\"finally, before loop\");", 152);
		assertMatch(source, "System.out.println(\"finally, after loop\");", 156);
		assertMatch(source, "System.out.println(\"finally\");", 159);
		assertMatch(source, "return 3;", 162);

		assertMatch(source, "/* 162: 162 */");

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	// derived from org/jd/core/test/TryWithResources
	static class TestClass1 {

		public void t(String pathIn, String pathOut) {
			try (FileInputStream input = new FileInputStream(pathIn);
					FileOutputStream output = new FileOutputStream(pathOut)) {
				System.out.println("inside try");
			} catch (IOException e) {
				System.out.println("inside catch");
			} finally {
				System.out.println("inside finally");
			}
		}
	}

	@Test
	public void testTryWithResourceBasic() throws Exception {

		String internalClassName = TestClass1.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "try (FileInputStream input = new FileInputStream(pathIn);");
		assertMatch(source, "FileOutputStream output = new FileOutputStream(pathOut)) {");
		assertMatch(source, "} catch (IOException e) {");
		assertMatch(source, "} finally {");

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	static class TestClass implements AutoCloseable {
		@Override
		public void close() {
		}

		static int test() {
			try (TestClass obj = new TestClass()) {
				return 1;
			}
		}
	}

	@Test
	// https://github.com/java-decompiler/jd-core/issues/23 // FIXME
	public void testTryWithResource() throws Exception {

		String internalClassName = TestClass.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "try (TestClass obj = new TestClass()) {", 138);
		assertMatch(source, "return 1;", 139);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
