/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jd.core.v1.api.Loader;

/**
 * A loader that loads classes from a given filesystem folder.
 */
public class DirectoryLoader implements Loader {
	protected File base;

	public static final int BUFFER_SIZE = 1024;

	public DirectoryLoader(File base) {
		this.base = base;
	}

	@Override
	public byte[] load(String internalName) throws IOException {
		File file = newFile(internalName);

		if (file.exists()) {
			try (FileInputStream in = new FileInputStream(file);
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[BUFFER_SIZE];
				int read = in.read(buffer);

				while (read > 0) {
					out.write(buffer, 0, read);
					read = in.read(buffer);
				}

				return out.toByteArray();
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean canLoad(String internalName) {
		return newFile(internalName).exists();
	}

	protected File newFile(String internalName) {
		return new File(base, internalName.replace('/', File.separatorChar) + ".class");
	}
}
