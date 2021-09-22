/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.api;

import java.util.Map;

/**
 * An object that perform decompilation to a Printer
 *
 * Decompiled object shall be retrieved from Printer.
 */
public interface Decompiler {

	Printer decompile(Loader loader, Printer printer, String internalName) throws Exception;

	Printer decompile(Loader loader, Printer printer, String internalName, Map<String, Object> configuration)
			throws Exception;
}
