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

public class Java9InterfaceTest {
    protected TestDecompiler decompiler = new TestDecompiler();

    @Test
    public void testJdk901InterfaceWithDefaultMethods() throws Exception {
        String internalClassName = "org/jd/core/test/InterfaceWithDefaultMethods";
        InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-9.0.1.zip");
        Loader loader = new ZipLoader(is);
        String source = decompiler.decompile(loader, internalClassName);

        // Check decompiled source code
        assertTrue(source.matches(PatternMaker.make("public interface InterfaceWithDefaultMethods")));
        assertTrue(source.matches(PatternMaker.make("void setTime(int paramInt1, int paramInt2, int paramInt3);")));
        assertTrue(source.matches(PatternMaker.make("LocalDateTime getLocalDateTime();")));
        assertTrue(source.matches(PatternMaker.make("static ZoneId getZoneId(String zoneString)")));
        assertTrue(source.matches(PatternMaker.make(": 24 */", "return unsafeGetZoneId(zoneString);")));
        assertTrue(source.matches(PatternMaker.make(": 26 */",
                "System.err.println(\"Invalid time zone: \" + zoneString + \"; using default time zone instead.\");")));
        assertTrue(source.matches(PatternMaker.make(": 27 */", "return ZoneId.systemDefault();")));
        assertTrue(source.matches(PatternMaker.make("default ZonedDateTime getZonedDateTime(String zoneString)")));
        assertTrue(source.matches(
                PatternMaker.make(": 32 */", "return getZonedDateTime(getLocalDateTime(), getZoneId(zoneString));")));
        assertTrue(source.matches(PatternMaker.make("private static ZoneId unsafeGetZoneId(String zoneString)")));
        assertTrue(source.matches(PatternMaker.make(": 36 */", "return ZoneId.of(zoneString);")));
        assertTrue(source.matches(PatternMaker
                .make("private ZonedDateTime getZonedDateTime(LocalDateTime localDateTime, ZoneId zoneId)")));
        assertTrue(source.matches(PatternMaker.make(": 40 */", "return ZonedDateTime.of(localDateTime, zoneId);")));

        // Recompile decompiled source code and check errors
        try {
            assertTrue(CompilerUtil.compile("1.9", new JavaSourceFileObject(internalClassName, source)));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("invalid source release: 1.9")) {
                System.err.println("testJdk901InterfaceWithDefaultMethods() need a Java SDK 9+");
            } else {
                assertTrue("Compilation failed: " + e.getMessage(), false);
            }
        }
    }
}
