/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.impl.loader;

import org.jd.core.v1.api.Loader;

/**
 * A Loader that loads nothing
 */
public class NopLoader implements Loader {
	@Override
	public byte[] load(String internalName) {
		return null;
	}

	@Override
	public boolean canLoad(String internalName) {
		return false;
	}
}
