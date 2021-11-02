/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.service.deserializer.classfile.ClassFileDeserializer;

/**
 * A Loader that loads a single class file, possibly placed in a bad filesystem
 * location, possibly including inner classes.
 * 
 * The main problem is retrieving package name before loading the class.
 */
public class SingleFileLoader implements Loader {

    public static final int BUFFER_SIZE = 1024 * 2;
    private File file;
    private String internalTypeName;
    private String innerTypesPrefix;

    public SingleFileLoader(String file) throws IOException {
        this(new File(file));
    }

    public SingleFileLoader(File file) throws IOException {
        this.file = file;
        ClassFileDeserializer deserializer = ClassFileDeserializer.getInstance();
        internalTypeName = deserializer.getMainTypeName(load(file));
        innerTypesPrefix = internalTypeName + '$';
    }

    private byte[] load(File file) throws IOException {
        try (InputStream in = new FileInputStream(file); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read = in.read(buffer);

            while (read > 0) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }

            return out.toByteArray();
        }
    }

    @Override
    public byte[] load(String internalName) throws IOException {
        if (internalName != null) {
            if (internalName.equals(this.internalTypeName)) {
                return load(file);
            }
            if (internalName.startsWith(this.innerTypesPrefix)) {
                String fname = internalName.substring(internalName.lastIndexOf("/") + 1) + ".class";
                File innerTypeFile = new File(file.getParent(), fname);
                return load(innerTypeFile);
            }
        }
        return null;

    }

    @Override
    public boolean canLoad(String internalName) {
        return internalName != null && (internalName.equals(this.internalTypeName)
                || (internalName.startsWith(this.internalTypeName + '$')));
    }

    public File getFile() {
        return file;
    }

    public String getInternalTypeName() {
        return internalTypeName;
    }

}
