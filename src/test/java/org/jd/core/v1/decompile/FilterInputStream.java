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
import org.junit.Test;

public class FilterInputStream extends java.io.FilterInputStream {

	public FilterInputStream() {
		super(null); // any value
	}

	protected TestDecompiler decompiler = new TestDecompiler();


	@Test
	// https://github.com/java-decompiler/jd-core/issues/38
	public void testSuperClassCollideName() throws Exception {

		String internalClassName = FilterInputStream.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertTrue(source.matches(PatternMaker.make(":  0 */", "public class FilterInputStream extends java.io.FilterInputStream {")));

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
		
	}
}
