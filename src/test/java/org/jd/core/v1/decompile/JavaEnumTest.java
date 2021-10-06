/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.jd.core.v1.regex.PatternMaker.assertDontMatch;
import static org.jd.core.v1.regex.PatternMaker.assertMatch;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.service.loader.ZipLoader;
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
		assertMatch(source, "SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;", 5);
		assertMatch(source, "MERCURY(3.303E23D, 2439700.0D),", 9);
		assertMatch(source, "URANUS(8.686E25D, 2.5559E7D),", 17);
		assertMatch(source, "NEPTUNE(1.024E26D, 2.4746E7D);", 20);
		assertMatch(source, "this.mass = mass;");
		assertMatch(source, "this.radius = radius;", 27);
		assertMatch(source, "return 6.673E-11D * this.mass / this.radius * this.radius;", 37);
		assertMatch(source, "double earthWeight = Double.parseDouble(args[0]);", 49);
		assertMatch(source, "double mass = earthWeight / EARTH.surfaceGravity();", 50);
		assertMatch(source, "for (Planet p : values()) {", 51);
		assertMatch(source,
				"System.out.printf(\"Your weight on %s is %f%n\", new Object[] { p, p.surfaceWeight(mass) } );", 52);
		assertMatch(source, "enum EmptyEnum {}");
		assertDontMatch(source, "public static final enum");

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
		assertMatch(source, "SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;", 5);
		assertMatch(source, "MERCURY(3.303E23D, 2439700.0D),", 9);
		assertMatch(source, "URANUS(8.686E25D, 2.5559E7D),", 17);
		assertMatch(source, "NEPTUNE(1.024E26D, 2.4746E7D);", 20);
		assertMatch(source, "this.mass = mass;");
		assertMatch(source, "this.radius = radius;", 27);
		assertMatch(source, "return 6.673E-11D * this.mass / this.radius * this.radius;", 37);
		assertMatch(source, "double earthWeight = Double.parseDouble(args[0]);", 49);
		assertMatch(source, "double mass = earthWeight / EARTH.surfaceGravity();", 50);
		assertMatch(source, "for (Planet p : values()) {", 51);
		assertMatch(source,
				"System.out.printf(\"Your weight on %s is %f%n\", new Object[] { p, p.surfaceWeight(mass) } );", 52);
		assertMatch(source, "enum EmptyEnum {}");
		assertDontMatch(source, "public static final enum");

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
		assertMatch(source, "SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;", 5);
		assertMatch(source, "MERCURY(3.303E23D, 2439700.0D),", 9);
		assertMatch(source, "URANUS(8.686E25D, 2.5559E7D),", 17);
		assertMatch(source, "NEPTUNE(1.024E26D, 2.4746E7D);", 20);
		assertMatch(source, "this.mass = mass;");
		assertMatch(source, "this.radius = radius;", 27);
		assertMatch(source, "return 6.673E-11D * this.mass / this.radius * this.radius;", 37);
		assertMatch(source, "double earthWeight = Double.parseDouble(args[0]);", 49);
		assertMatch(source, "double mass = earthWeight / EARTH.surfaceGravity();", 50);
		assertMatch(source, "for (Planet p : values()) {", 51);
		assertMatch(source,
				"System.out.printf(\"Your weight on %s is %f%n\", new Object[] { p, p.surfaceWeight(mass) } );", 52);
		assertMatch(source, "enum EmptyEnum {}");
		assertDontMatch(source, "public static final enum");

		assertTrue(source.indexOf("public static final enum") == -1);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	static enum EnumWithStaticMethod {

		ONE, TWO, THREE;

		public static EnumWithStaticMethod valueOf(int i) {
			switch (i) {
			case 1:
				return ONE;
			case 2:
				return TWO;
			case 3:
				return THREE;
			default:
				return null;
			}
		}
	}

	@Test
	// https://github.com/java-decompiler/jd-core/issues/36
	public void testJavaEnumWithStaticMethod() throws Exception {

		String internalClassName = EnumWithStaticMethod.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code

		assertMatch(source, "public static EnumWithStaticMethod valueOf(int i) {");

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));

	}
}
