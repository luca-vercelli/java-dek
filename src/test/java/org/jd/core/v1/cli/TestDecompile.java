package org.jd.core.v1.cli;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import org.jd.cli.CliMain;
import org.junit.Ignore;
import org.junit.Test;

public class TestDecompile {

    @Test
    @Ignore
    // This test halts because of System.exit()
    public void testDecompileZip() throws URISyntaxException {
        File fileSrc = new File(TestDecompile.class.getResource("/zip/data-java-jdk-1.8.0.zip").toURI());
        File fileDstFolder = new File("target" + File.separator + "tmp");
        CliMain.main(new String[] { "-d", fileDstFolder.getPath(), fileSrc.getPath() });

        String destFilePath = "target/tmp/org/jd/core/test/Basic.class".replace("/", File.separator);
        File fileDst = new File(destFilePath);
        assertTrue("No decompiled file: " + fileDst.getAbsolutePath(), fileDst.exists());
        assertTrue("Decompiled file empty: " + fileDst.getAbsolutePath(), fileDst.length() > 0);
    }
}
