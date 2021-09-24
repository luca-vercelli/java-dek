/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.api;

import java.io.IOException;

/**
 * An object that loads classes into memory as byte arrays
 */
public interface Loader {

	/**
	 * Return true if this Loader can load given class name
	 */
	boolean canLoad(String internalName);

	/**
	 * Load given class bytecode into a byte array
	 * @throws IOException 
	 */
	byte[] load(String internalName) throws IOException;
}
