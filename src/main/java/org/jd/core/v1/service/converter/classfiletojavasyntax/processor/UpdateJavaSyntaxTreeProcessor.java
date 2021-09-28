/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.processor;

import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.javasyntax.CompilationUnit;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;
import org.jd.core.v1.service.converter.classfiletojavasyntax.visitor.UpdateJavaSyntaxTreeStep0Visitor;
import org.jd.core.v1.service.converter.classfiletojavasyntax.visitor.UpdateJavaSyntaxTreeStep1Visitor;
import org.jd.core.v1.service.converter.classfiletojavasyntax.visitor.UpdateJavaSyntaxTreeStep2Visitor;

/**
 * Create statements, init fields, merge declarations.<br>
 * <br>
 *
 * Input: {@link CompilationUnit}<br>
 * Output: {@link CompilationUnit}<br>
 */
public class UpdateJavaSyntaxTreeProcessor implements Processor {

	protected UpdateJavaSyntaxTreeProcessor() {
	}

	/**
	 * Fill CompilationUnit object.
	 * 
	 * Update compilationUnit in place, by means of UpdateJavaSyntaxTreeStep
	 * visitors
	 */
	@Override
	public void process(Message message) {
		TypeMaker typeMaker = message.getTypeMaker();
		CompilationUnit compilationUnit = message.getCompilationUnit();

		new UpdateJavaSyntaxTreeStep0Visitor(typeMaker).visit(compilationUnit);
		new UpdateJavaSyntaxTreeStep1Visitor(typeMaker).visit(compilationUnit);
		new UpdateJavaSyntaxTreeStep2Visitor(typeMaker).visit(compilationUnit);
	}

	private static UpdateJavaSyntaxTreeProcessor instance = null;

	/**
	 * Get Singleton instance
	 */
	public static UpdateJavaSyntaxTreeProcessor getInstance() {
		if (instance == null) {
			instance = new UpdateJavaSyntaxTreeProcessor();
		}
		return instance;
	}
}
