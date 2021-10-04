/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.impl.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jd.core.v1.api.Loader;

/**
 * A Loader that loads classes from classpath.
 */
public class ClassPathLoader implements Loader {

	public static final int BUFFER_SIZE = 1024 * 2;

	@Override
	public byte[] load(String internalName) throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/" + internalName + ".class");

		if (is == null) {
			return null;
		} else {
			try (InputStream in = is; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[BUFFER_SIZE];
				int read = in.read(buffer);

				while (read > 0) {
					out.write(buffer, 0, read);
					read = in.read(buffer);
				}

				return out.toByteArray();
			}
		}
	}

	@Override
	public boolean canLoad(String internalName) {
		return this.getClass().getResource("/" + internalName + ".class") != null;
	}
}
