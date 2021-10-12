/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.regex.PatternMaker;
import org.jd.core.v1.service.loader.ZipLoader;
import org.junit.Test;

public class JavaTryCatchFinallyTest {
	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	public void testEclipseJavaCompiler321TryCatchFinally() throws Exception {
		String internalClassName = "org/jd/core/test/TryCatchFinally";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-eclipse-java-compiler-3.2.1.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.indexOf("catch (RuntimeException runtimeexception)") != -1);
		assertTrue(source.matches(PatternMaker.make("/*  45:  45 */", "inCatch1();")));
		assertTrue(source.matches(PatternMaker.make("/*  60:   0 */", "return;")));

		assertTrue(source.matches(PatternMaker.make(": 166 */", "return System.currentTimeMillis();")));

		// TODO assertTrue(source.matches(PatternMaker.make("/* 217:", "inCatch1();")));

		assertTrue(source.indexOf("/* 888: 888 */") != -1);

		assertTrue(source.indexOf("long l = System.currentTimeMillis(); return l;") == -1);
		assertTrue(source.indexOf("catch (RuntimeException null)") == -1);
		assertTrue(source.indexOf("Object object;") == -1);
		assertTrue(source.indexOf("RuntimeException runtimeexception4;") == -1);
		assertTrue(source.indexOf("Exception exception8;") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.5", new JavaSourceFileObject(internalClassName, source)));

		System.out.println(JavaTryCatchFinallyTest.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		System.out.println(System.getProperty("java.class.path"));
	}

	@Test
	public void testEclipseJavaCompiler370TryCatchFinally() throws Exception {
		String internalClassName = "org/jd/core/test/TryCatchFinally";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-eclipse-java-compiler-3.7.0.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.indexOf("catch (RuntimeException runtimeException)") != -1);
		assertTrue(source.matches(PatternMaker.make("/*  48:  48 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make("/*  60:   0 */", "return;")));

		assertTrue(source.matches(PatternMaker.make(": 166 */", "return System.currentTimeMillis();")));

		assertTrue(source.matches(PatternMaker.make(": 393 */", "inCatch1();")));
		assertTrue(source.matches(PatternMaker.make(": 395 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make(": 397 */", "inCatch3();")));
		assertTrue(source.matches(PatternMaker.make(": 399 */", "inFinally();")));
		assertTrue(source.indexOf("/* 400:   0]     inFinally();") == -1);

		assertTrue(source.matches(PatternMaker.make(": 424 */", "inTry();")));
		assertTrue(source.matches(PatternMaker.make(": 427 */", "inFinally();")));
		assertTrue(source.matches(PatternMaker.make(": 431 */", "inTryA();")));
		assertTrue(source.matches(PatternMaker.make(": 434 */", "inFinallyA();")));
		assertTrue(source.matches(PatternMaker.make(": 439 */", "inTryC();")));
		assertTrue(source.matches(PatternMaker.make(": 442 */", "inFinallyC();")));
		assertTrue(source.matches(PatternMaker.make(": 445 */", "inFinally();")));

		assertTrue(source.indexOf("/* 888: 888 */") != -1);

		assertTrue(source.indexOf("long l = System.currentTimeMillis(); return l;") == -1);
		assertTrue(source.indexOf("catch (RuntimeException null)") == -1);
		assertTrue(source.indexOf("Object object;") == -1);
		assertTrue(source.indexOf("RuntimeException runtimeexception4;") == -1);
		assertTrue(source.indexOf("Exception exception8;") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.5", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testEclipseJavaCompiler3130TryCatchFinally() throws Exception {
		String internalClassName = "org/jd/core/test/TryCatchFinally";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-eclipse-java-compiler-3.13.0.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.indexOf("catch (RuntimeException runtimeException)") != -1);
		assertTrue(source.matches(PatternMaker.make("/*  48:  48 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make("/*  60:   0 */", "return;")));

		assertTrue(source.matches(PatternMaker.make(": 166 */", "return System.currentTimeMillis();")));

		assertTrue(source.matches(PatternMaker.make(": 393 */", "inCatch1();")));
		assertTrue(source.matches(PatternMaker.make(": 395 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make(": 397 */", "inCatch3();")));
		assertTrue(source.matches(PatternMaker.make(": 399 */", "inFinally();")));

		assertTrue(source.matches(PatternMaker.make(": 424 */", "inTry();")));
		assertTrue(source.matches(PatternMaker.make(": 427 */", "inFinally();")));
		assertTrue(source.matches(PatternMaker.make(": 431 */", "inTryA();")));
		assertTrue(source.matches(PatternMaker.make(": 434 */", "inFinallyA();")));
		assertTrue(source.matches(PatternMaker.make(": 439 */", "inTryC();")));
		assertTrue(source.matches(PatternMaker.make(": 442 */", "inFinallyC();")));
		assertTrue(source.matches(PatternMaker.make(": 445 */", "inFinally();")));

		assertTrue(source.indexOf("/* 888: 888 */") != -1);

		assertTrue(source.indexOf("long l = System.currentTimeMillis(); return l;") == -1);
		assertTrue(source.indexOf("catch (RuntimeException null)") == -1);
		assertTrue(source.indexOf("Object object;") == -1);
		assertTrue(source.indexOf("RuntimeException runtimeexception4;") == -1);
		assertTrue(source.indexOf("Exception exception8;") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.5", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testJdk118TryCatchFinally() throws Exception {
		String internalClassName = "org/jd/core/test/TryCatchFinally";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.1.8.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.indexOf("catch (RuntimeException runtimeexception)") != -1);
		assertTrue(source.matches(PatternMaker.make("/*  48:  48 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make("/*  60:   0 */", "return;")));

		assertTrue(source.matches(PatternMaker.make(": 166 */", "return System.currentTimeMillis();")));

		assertTrue(source.matches(PatternMaker.make(": 393 */", "inCatch1();")));
		assertTrue(source.matches(PatternMaker.make(": 395 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make(": 397 */", "inCatch3();")));
		assertTrue(source.matches(PatternMaker.make(": 399 */", "inFinally();")));

		assertTrue(source.matches(PatternMaker.make(": 424 */", "inTry();")));
		assertTrue(source.matches(PatternMaker.make(": 427 */", "inFinally();")));
		assertTrue(source.matches(PatternMaker.make(": 431 */", "inTryA();")));
		assertTrue(source.matches(PatternMaker.make(": 434 */", "inFinallyA();")));
		assertTrue(source.matches(PatternMaker.make(": 439 */", "inTryC();")));
		assertTrue(source.matches(PatternMaker.make(": 442 */", "inFinallyC();")));
		assertTrue(source.matches(PatternMaker.make(": 445 */", "inFinally();")));

		assertTrue(source.indexOf("/* 902: 902 */") != -1);

		assertTrue(source.indexOf("long l = System.currentTimeMillis(); return l;") == -1);
		assertTrue(source.indexOf("catch (RuntimeException null)") == -1);
		assertTrue(source.indexOf("Object object;") == -1);
		assertTrue(source.indexOf("RuntimeException runtimeexception4;") == -1);
		assertTrue(source.indexOf("Exception exception8;") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.3", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testJdk131TryCatchFinally() throws Exception {
		String internalClassName = "org/jd/core/test/TryCatchFinally";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.3.1.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.indexOf("catch (RuntimeException runtimeexception)") != -1);
		assertTrue(source.matches(PatternMaker.make("/*  48:  48 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make("/*  60:   0 */", "return;")));

		assertTrue(source.matches(PatternMaker.make(": 166 */", "return System.currentTimeMillis();")));

		assertTrue(source.matches(PatternMaker.make(": 393 */", "inCatch1();")));
		assertTrue(source.matches(PatternMaker.make(": 395 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make(": 397 */", "inCatch3();")));
		assertTrue(source.matches(PatternMaker.make(": 399 */", "inFinally();")));

		assertTrue(source.matches(PatternMaker.make(": 424 */", "inTry();")));
		assertTrue(source.matches(PatternMaker.make(": 427 */", "inFinally();")));
		assertTrue(source.matches(PatternMaker.make(": 445 */", "inFinally();")));

		assertTrue(source.indexOf("/* 902: 902 */") != -1);

		assertTrue(source.indexOf("long l = System.currentTimeMillis(); return l;") == -1);
		assertTrue(source.indexOf("catch (RuntimeException null)") == -1);
		assertTrue(source.indexOf("Object object;") == -1);
		assertTrue(source.indexOf("RuntimeException runtimeexception4;") == -1);
		assertTrue(source.indexOf("Exception exception8;") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.3", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testJdk170TryCatchFinally() throws Exception {
		String internalClassName = "org/jd/core/test/TryCatchFinally";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.indexOf("catch (RuntimeException runtimeexception)") != -1);
		assertTrue(source.matches(PatternMaker.make("/*  48:  48 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make("/*  60:   0 */", "return;")));

		assertTrue(source.matches(PatternMaker.make(": 166 */", "return System.currentTimeMillis();")));

		assertTrue(source.matches(PatternMaker.make(": 192 */", "catch (RuntimeException e) {}")));
		assertTrue(source.matches(PatternMaker.make("/* 204:   0 */", "finally {}")));

		assertTrue(source.matches(PatternMaker.make(": 393 */", "inCatch1();")));
		assertTrue(source.matches(PatternMaker.make(": 395 */", "inCatch2();")));
		assertTrue(source.matches(PatternMaker.make(": 397 */", "inCatch3();")));
		assertTrue(source.matches(PatternMaker.make(": 399 */", "inFinally();")));

		assertTrue(source.matches(PatternMaker.make(": 424 */", "inTry();")));
		assertTrue(source.matches(PatternMaker.make(": 427 */", "inFinally();")));
		assertTrue(source.matches(PatternMaker.make(": 431 */", "inTryA();")));
		assertTrue(source.matches(PatternMaker.make(": 434 */", "inFinallyA();")));
		assertTrue(source.matches(PatternMaker.make(": 439 */", "inTryC();")));
		assertTrue(source.matches(PatternMaker.make(": 442 */", "inFinallyC();")));
		assertTrue(source.matches(PatternMaker.make(": 445 */", "inFinally();")));

		assertTrue(source.indexOf("/* 902: 902 */") != -1);

		assertTrue(source.indexOf("long l = System.currentTimeMillis(); return l;") == -1);
		assertTrue(source.indexOf("catch (RuntimeException null)") == -1);
		assertTrue(source.indexOf("Object object;") == -1);
		assertTrue(source.indexOf("RuntimeException runtimeexception4;") == -1);
		assertTrue(source.indexOf("Exception exception8;") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
	}
}
