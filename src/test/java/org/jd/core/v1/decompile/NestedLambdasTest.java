/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.junit.Assert.assertTrue;
import static org.jd.core.v1.regex.PatternMaker.*;

import java.util.function.Function;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.junit.Test;

public class NestedLambdasTest {

    protected TestDecompiler decompiler = new TestDecompiler();

    static class TestClass {
        public Function<?, Function<?, ?>> test() {
            Function<?, Function<?, ?>> f = i1 -> i2 -> 1;
            return f;
        }
        // internally, this class has 4 methods:
        // <init>()V
        // test ()Ljava/util/function/Function
        // lambda$0 (Ljava/lang/Object;)Ljava/util/function/Function;
        // lambda$1 (Ljava/lang/Object;)Ljava/lang/Object;
    }

    @Test
    // https://github.com/java-decompiler/jd-core/issues/23
    public void testNestedLambdas() throws Exception {

        String internalClassName = TestClass.class.getName().replace('.', '/');
        String source = decompiler.decompile(internalClassName);

        // Check decompiled source code
        assertMatch(source, "public Function<?, Function<?, ?>> test () {");
        assertMatch(source, "Function<?, Function<?, ?>> f = i1 -> i2 -> 1;");

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));

    }
}
