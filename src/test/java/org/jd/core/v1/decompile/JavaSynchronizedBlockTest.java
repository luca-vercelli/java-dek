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

public class JavaSynchronizedBlockTest {
    protected TestDecompiler decompiler = new TestDecompiler();

    @Test
    public void testJdk170Synchronised() throws Exception {
        String internalClassName = "org/jd/core/test/Synchronized";
        InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
        Loader loader = new ZipLoader(is);
        String source = decompiler.decompile(loader, internalClassName);

        // Check decompiled source code
        assertTrue(source.matches(PatternMaker.make(":  11 */", "synchronized (paramStringBuilder)")));
        assertTrue(source.matches(PatternMaker.make(":  13 */", "inSynchronized();")));
        assertTrue(source.matches(PatternMaker.make(":  15 */", "return 2;")));

        assertTrue(source.matches(PatternMaker.make(":  20 */", "synchronized (paramStringBuilder)")));
        assertTrue(source.matches(PatternMaker.make(":  22 */", "inSynchronized();")));
        assertTrue(source.matches(PatternMaker.make(":  23 */", "return 2;")));

        assertTrue(source.matches(PatternMaker.make(":  29 */", "synchronized (paramStringBuilder)")));
        assertTrue(source.matches(PatternMaker.make(":  31 */", "inSynchronized();")));
        assertTrue(source.matches(PatternMaker.make(":   0 */", "return;")));

        assertTrue(source.matches(PatternMaker.make(":  73 */", "synchronized (paramStringBuilder)")));
        assertTrue(source.matches(PatternMaker.make(":  75 */", "inSynchronized();")));
        assertTrue(source.matches(PatternMaker.make(":  76 */", "throw new RuntimeException();")));

        assertTrue(source.matches(PatternMaker.make(":  95 */", "synchronized (s)")));
        assertTrue(source.matches(PatternMaker.make(":  97 */", "return subContentEquals(s);")));

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
    }

}
