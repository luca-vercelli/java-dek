/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.model.message.CompileConfiguration;
import org.jd.core.v1.regex.PatternMaker;
import org.jd.core.v1.service.loader.ZipLoader;
import org.junit.Test;

public class JavaSwitchTest {
    protected TestDecompiler decompiler = new TestDecompiler();

    @Test
    public void testJdk170Switch() throws Exception {
        String internalClassName = "org/jd/core/test/Switch";
        InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
        Loader loader = new ZipLoader(is);
        CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
        String source = decompiler.decompile(loader, internalClassName, configuration);

        // Check decompiled source code
        assertTrue(source.matches(PatternMaker.make("/*  15:  15 */", "switch (i)")));
        assertTrue(source.matches(PatternMaker.make("/*  16:   0 */", "case 0:")));
        assertTrue(source.matches(PatternMaker.make("/*  17:  17 */", "System.out.println(\"0\");")));
        assertTrue(source.matches(PatternMaker.make("/*  18:   0 */", "break;")));

        assertTrue(source.matches(PatternMaker.make("/*  34:   0 */", "case 0:")));
        assertTrue(source.matches(PatternMaker.make("/*  35:  35 */", "System.out.println(\"0\");")));
        assertTrue(source.matches(PatternMaker.make("/*  36:   0 */", "case 1:")));

        assertTrue(source.matches(PatternMaker.make("/*  56:   0 */", "default:")));

        assertTrue(source.matches(PatternMaker.make("/* 110:   0 */", "break;")));
        assertTrue(source.matches(PatternMaker.make("/* 111:   0 */", "case 1:")));
        assertTrue(source.matches(PatternMaker.make("/* 112: 112 */", "System.out.println(\"1\");")));
        assertTrue(source.matches(PatternMaker.make("/* 113: 113 */", "throw new RuntimeException(\"boom\");")));

        assertTrue(source.matches(PatternMaker.make("/* 134:   0 */", "return;")));

        assertTrue(source.matches(PatternMaker.make("/* 171:   0 */", "case 3:")));
        assertTrue(source.matches(PatternMaker.make("/* 172:   0 */", "case 4:")));
        assertTrue(source.matches(PatternMaker.make("/* 173: 173 */", "System.out.println(\"3 or 4\");")));
        assertTrue(source.matches(PatternMaker.make("/* 174:   0 */", "break;")));

        assertTrue(source.matches(PatternMaker.make("/* 265:   0 */", "case 1:")));
        assertTrue(source.matches(PatternMaker.make("/* 266:   0 */", "break;")));
        assertTrue(source.matches(PatternMaker.make("/* 267:   0 */", "default:")));

        assertTrue(source.matches(PatternMaker.make("/* 283:   0 */", "case 1:")));
        assertTrue(source.matches(PatternMaker.make("/* 284:   0 */", "case 2:")));
        assertTrue(source.matches(PatternMaker.make("/* 285:   0 */", "case 3:")));
        assertTrue(source.matches(PatternMaker.make("/* 286:   0 */", "break;")));
        assertTrue(source.matches(PatternMaker.make("/* 288:   0 */", "default:")));

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
    }

    @Test
    public void testJdk170AdvancedSwitch() throws Exception {
        String internalClassName = "org/jd/core/test/AdvancedSwitch";
        InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
        Loader loader = new ZipLoader(is);
        CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
        String source = decompiler.decompile(loader, internalClassName, configuration);

        // Check decompiled source code
        assertTrue(source.matches(PatternMaker.make("/* 13: 13 */", "A,", "B,", "C;")));

        assertTrue(source.matches(PatternMaker.make("/* 19: 19 */", "switch (te)")));
        assertTrue(source.matches(PatternMaker.make("/* 20:  0 */", "case A:")));
        assertTrue(source.matches(PatternMaker.make("/* 22:  0 */", "case B:")));
        assertTrue(source.matches(PatternMaker.make("/* 25:  0 */", "case C:")));

        assertTrue(source.matches(PatternMaker.make("/* 39:  0 */", "case A:")));
        assertTrue(source.matches(PatternMaker.make("/* 40:  0 */", "case B:")));
        assertTrue(source.matches(PatternMaker.make("/* 41: 41 */", "System.out.println(\"A or B\");")));

        assertTrue(source.matches(PatternMaker.make("/* 56: 56 */", "switch (str)")));
        assertTrue(source.matches(PatternMaker.make("/* 57:  0 */", "case \"One\":")));
        assertTrue(source.matches(PatternMaker.make("/* 58: 58 */", "System.out.println(1);")));

        assertTrue(source.matches(PatternMaker.make("/* 78:  0 */", "case \"One\":")));
        assertTrue(source.matches(PatternMaker.make("/* 79:  0 */", "case \"POe\":")));
        assertTrue(source.matches(PatternMaker.make("/* 80: 80 */", "System.out.println(\"'One' or 'POe'\");")));

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.7", new JavaSourceFileObject(internalClassName, source)));
    }

    @Test
    public void testEclipseJavaCompiler321Switch() throws Exception {
        String internalClassName = "org/jd/core/test/Switch";
        InputStream is = this.getClass().getResourceAsStream("/zip/data-java-eclipse-java-compiler-3.2.1.zip");
        Loader loader = new ZipLoader(is);
        CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
        String source = decompiler.decompile(loader, internalClassName, configuration);

        // Check decompiled source code
        assertTrue(source.indexOf("/* 239: 239 */") != -1);

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.5", new JavaSourceFileObject(internalClassName, source)));
    }

    @Test
    public void testEclipseJavaCompiler3130Switch() throws Exception {
        String internalClassName = "org/jd/core/test/Switch";
        InputStream is = this.getClass().getResourceAsStream("/zip/data-java-eclipse-java-compiler-3.13.0.zip");
        Loader loader = new ZipLoader(is);
        CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
        String source = decompiler.decompile(loader, internalClassName, configuration);

        // Check decompiled source code
        assertTrue(source.indexOf("/* 239: 239 */") != -1);

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.5", new JavaSourceFileObject(internalClassName, source)));
    }

    @Test
    // Test switch-enum in an other switch-enum
    public void testBstMutationResult() throws Exception {
        String internalClassName = "com/google/common/collect/BstMutationResult";
        Class<?> mainClass = com.google.common.collect.Collections2.class;
        InputStream is = new FileInputStream(
                Paths.get(mainClass.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile());
        Loader loader = new ZipLoader(is);
        CompileConfiguration configuration = new CompileConfiguration().setRealignLineNumbers(true);
        String source = decompiler.decompile(loader, internalClassName, configuration);

        // Check decompiled source code
        assertTrue(source.indexOf("N resultLeft, resultRight;") != -1);
        assertTrue(source.indexOf("assert false;") == -1);
        assertTrue(source
                .matches(PatternMaker.make("/* 131:", "resultLeft = liftOriginalRoot.childOrNull(BstSide.LEFT);")));
        assertTrue(source.matches(PatternMaker.make("/* 134:", "case LEFT:")));

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
    }

}
