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
import org.jd.core.v1.impl.loader.ZipLoader;
import org.jd.core.v1.regex.PatternMaker;
import org.junit.Ignore;
import org.junit.Test;

public class JavaEnumTest {
	protected TestDecompiler decompiler = new TestDecompiler();

	@Test
	public void testJdk170Enum() throws Exception {
		String internalClassName = "org/jd/core/test/Enum";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.matches(
				PatternMaker.make(":  5 */", "SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;")));

		assertTrue(source.matches(PatternMaker.make(":  9 */", "MERCURY(3.303E23D, 2439700.0D),")));
		assertTrue(source.matches(PatternMaker.make(": 17 */", "URANUS(8.686E25D, 2.5559E7D),")));
		assertTrue(source.matches(PatternMaker.make(": 20 */", "NEPTUNE(1.024E26D, 2.4746E7D);")));
		assertTrue(source.indexOf("this.mass = mass;") != -1);
		assertTrue(source.matches(PatternMaker.make(": 27 */", "this.radius = radius;")));
		assertTrue(source
				.matches(PatternMaker.make(": 37 */", "return 6.673E-11D * this.mass / this.radius * this.radius;")));
		assertTrue(source.matches(PatternMaker.make(": 49 */", "double earthWeight = Double.parseDouble(args[0]);")));
		assertTrue(source.matches(PatternMaker.make(": 50 */", "double mass = earthWeight / EARTH.surfaceGravity();")));
		assertTrue(source.matches(PatternMaker.make(": 51 */", "for (Planet p : values()) {")));
		assertTrue(source.matches(PatternMaker.make(": 52 */",
				"System.out.printf(\"Your weight on %s is %f%n\", new Object[]", "{ p, p.surfaceWeight(mass) } );")));

		assertTrue(source.matches(PatternMaker.make("enum EmptyEnum {}")));

		assertTrue(source.indexOf("public static final enum") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testJdk901Enum() throws Exception {
		String internalClassName = "org/jd/core/test/Enum";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-9.0.1.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.matches(
				PatternMaker.make(":  5 */", "SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;")));

		assertTrue(source.matches(PatternMaker.make(":  9 */", "MERCURY(3.303E23D, 2439700.0D),")));
		assertTrue(source.matches(PatternMaker.make(": 17 */", "URANUS(8.686E25D, 2.5559E7D),")));
		assertTrue(source.matches(PatternMaker.make(": 20 */", "NEPTUNE(1.024E26D, 2.4746E7D);")));
		assertTrue(source.indexOf("this.mass = mass;") != -1);
		assertTrue(source.matches(PatternMaker.make(": 27 */", "this.radius = radius;")));
		assertTrue(source
				.matches(PatternMaker.make(": 37 */", "return 6.673E-11D * this.mass / this.radius * this.radius;")));
		assertTrue(source.matches(PatternMaker.make(": 49 */", "double earthWeight = Double.parseDouble(args[0]);")));
		assertTrue(source.matches(PatternMaker.make(": 50 */", "double mass = earthWeight / EARTH.surfaceGravity();")));
		assertTrue(source.matches(PatternMaker.make(": 51 */", "for (Planet p : values()) {")));
		assertTrue(source.matches(PatternMaker.make(": 52 */",
				"System.out.printf(\"Your weight on %s is %f%n\", new Object[]", "{ p, p.surfaceWeight(mass) } );")));

		assertTrue(source.matches(PatternMaker.make("enum EmptyEnum {}")));

		assertTrue(source.indexOf("public static final enum") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	public void testJdk1002Enum() throws Exception {
		String internalClassName = "org/jd/core/test/Enum";
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-10.0.2.zip");
		Loader loader = new ZipLoader(is);
		Map<String, Object> configuration = Collections.singletonMap("realignLineNumbers", Boolean.TRUE);
		String source = decompiler.decompile(loader, internalClassName, configuration);

		// Check decompiled source code
		assertTrue(source.matches(
				PatternMaker.make(":  5 */", "SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;")));

		assertTrue(source.matches(PatternMaker.make(":  9 */", "MERCURY(3.303E23D, 2439700.0D),")));
		assertTrue(source.matches(PatternMaker.make(": 17 */", "URANUS(8.686E25D, 2.5559E7D),")));
		assertTrue(source.matches(PatternMaker.make(": 20 */", "NEPTUNE(1.024E26D, 2.4746E7D);")));
		assertTrue(source.indexOf("this.mass = mass;") != -1);
		assertTrue(source.matches(PatternMaker.make(": 27 */", "this.radius = radius;")));
		assertTrue(source
				.matches(PatternMaker.make(": 37 */", "return 6.673E-11D * this.mass / this.radius * this.radius;")));
		assertTrue(source.matches(PatternMaker.make(": 49 */", "double earthWeight = Double.parseDouble(args[0]);")));
		assertTrue(source.matches(PatternMaker.make(": 50 */", "double mass = earthWeight / EARTH.surfaceGravity();")));
		assertTrue(source.matches(PatternMaker.make(": 51 */", "for (Planet p : values()) {")));
		assertTrue(source.matches(PatternMaker.make(": 52 */",
				"System.out.printf(\"Your weight on %s is %f%n\", new Object[]", "{ p, p.surfaceWeight(mass) } );")));

		assertTrue(source.matches(PatternMaker.make("enum EmptyEnum {}")));

		assertTrue(source.indexOf("public static final enum") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
	
	static enum EnumWithStaticMethod {
		
		ONE, TWO, THREE;
		
		public static EnumWithStaticMethod valueOf(int i) {
			switch(i) {
				case 1: return ONE;
				case 2: return TWO;
				case 3: return THREE;
				default: return null;
			}
		}
	}
	


	@Test
	@Ignore
	// https://github.com/java-decompiler/jd-core/issues/36 // FIXME
	public void testJavaEnumWithStaticMethod() throws Exception {

		String internalClassName = EnumWithStaticMethod.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		//assertTrue(source.matches(PatternMaker.make(": 29 */", "Integer intObj = 10;")));
		//assertTrue(source.matches(PatternMaker.make(": 30 */", "int i = intObj;")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
		
	}
}
