/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax;

import java.util.Map;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.converter.classfiletojavasyntax.processor.ConvertClassFileProcessor;
import org.jd.core.v1.service.converter.classfiletojavasyntax.processor.UpdateJavaSyntaxTreeProcessor;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;

/**
 * Convert ClassFile model to Java syntax model.<br>
 * <br>
 *
 * Input: {@link org.jd.core.v1.model.classfile.ClassFile}<br>
 * Output: {@link org.jd.core.v1.model.javasyntax.CompilationUnit}<br>
 *
 * @see ConvertClassFileProcessor
 */
public class ClassFileToJavaSyntaxProcessor implements Processor {

	/**
	 * Given a ClassFile, create TypeMaker and CompilationUnit
	 */
	@Override
	public void process(Message message) {
		Loader loader = message.getLoader();
		Map<String, Object> configuration = message.getConfiguration();

		TypeMaker typeMaker = createTypeMaker(loader, configuration);

		message.setTypeMaker(typeMaker);
		ConvertClassFileProcessor.getInstance().process(message);
		UpdateJavaSyntaxTreeProcessor.getInstance().process(message);
	}

	private TypeMaker createTypeMaker(Loader loader, Map<String, Object> configuration) {
		TypeMaker typeMaker = null;

		if (configuration == null) {
			typeMaker = new TypeMaker(loader);
		} else {

			try {
				typeMaker = (TypeMaker) configuration.get("typeMaker");

				if (typeMaker == null) {
					// Store the heavy weight object 'typeMaker' in 'configuration' to reuse it
					configuration.put("typeMaker", typeMaker = new TypeMaker(loader));
				}
			} catch (Exception e) {
				if (typeMaker == null) {
					typeMaker = new TypeMaker(loader);
				}
			}

		}
		return typeMaker;
	}

	private static ClassFileToJavaSyntaxProcessor instance = null;

	/**
	 * Get Singleton instance
	 */
	public static ClassFileToJavaSyntaxProcessor getInstance() {
		if (instance == null) {
			instance = new ClassFileToJavaSyntaxProcessor();
		}
		return instance;
	}
}
