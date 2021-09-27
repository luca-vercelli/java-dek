/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.deserializer.classfile;

import java.io.IOException;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.classfile.ClassFile;
import org.jd.core.v1.model.message.Message;

/**
 * Create a ClassFile model from a loader and a internal type name.<br>
 * <br>
 *
 * Input: -<br>
 * Output: {@link org.jd.core.v1.model.classfile.ClassFile}<br>
 */
public class DeserializeClassFileProcessor extends ClassFileDeserializer implements Processor {

	protected DeserializeClassFileProcessor() {
	}

	/**
	 * Create a ClassFile model from a loader and a internal type name
	 * 
	 * @throws IOException
	 */
	@Override
	public void process(Message message) throws IOException {
		Loader loader = message.getLoader();
		String internalTypeName = message.getMainInternalTypeName();
		ClassFile classFile = process(loader, internalTypeName);

		message.setClassFile(classFile);
	}

	/**
	 * Create a ClassFile model from a loader and a internal type name
	 * 
	 * @param loader
	 * @param internalTypeName
	 * @return
	 * @throws IOException
	 */
	public ClassFile process(Loader loader, String internalTypeName) throws IOException {
		ClassFile classFile = loadClassFile(loader, internalTypeName);
		return classFile;
	}

	private static DeserializeClassFileProcessor instance = null;

	/**
	 * Get Singleton instance
	 */
	public static DeserializeClassFileProcessor getInstance() {
		if (instance == null) {
			instance = new DeserializeClassFileProcessor();
		}
		return instance;
	}
}
