/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.jd.core.v1.regex.PatternMaker.assertMatch;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.jd.core.v1.service.loader.ZipLoader;
import org.junit.Test;

public class JavaLambdaTest {
    protected TestDecompiler decompiler = new TestDecompiler();

    @Test
    public void testJdk180Lambda() throws Exception {
        String internalClassName = "org/jd/core/test/Lambda";
        InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.8.0.zip");
        Loader loader = new ZipLoader(is);
        String source = decompiler.decompile(loader, internalClassName);

        // Check decompiled source code
        assertMatch(source, "list.stream().filter(s -> (s != null)).forEach(s -> System.out.println(s));", 20);
        assertMatch(source, "Predicate<String> filter = s -> (s.length() == length);");
        assertMatch(source, "Consumer<String> println = s -> System.out.println(s);");
        assertMatch(source, "list.stream().filter(filter).forEach(println);", 27);
        assertMatch(source, "((Map)list.stream()", 31);
        assertMatch(source, ".collect(Collectors.toMap(lambda -> lambda.index, Function.identity())))", 32);
        assertMatch(source, ".forEach((key, value) ->", 33);
        assertMatch(source, "Thread thread = new Thread(() -> {", 48);
        assertMatch(source, "Consumer<String> staticMethodReference = String::valueOf;", 58);
        assertMatch(source, "BiFunction<String, String, Integer> methodReference = String::compareTo;", 59);
        assertMatch(source, "Supplier<String> instanceMethodReference = s::toString;", 60);
        assertMatch(source, "Supplier<String> constructorReference = String::new;", 61);
        assertMatch(source, "MethodType mtToString = MethodType.methodType(String.class);", 65);
        assertMatch(source, "MethodType mtSetter = MethodType.methodType(void.class, Object.class);", 66);
        assertMatch(source,
                "MethodType mtStringComparator = MethodType.methodType(int[].class, String.class, new Class<?>[]"
                        + "{ String.class",
                67);

        // Recompile decompiled source code and check errors
        assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
    }
}
