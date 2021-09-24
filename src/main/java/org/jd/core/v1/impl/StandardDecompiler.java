/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.impl;

import org.jd.core.v1.api.Decompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Printer;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.converter.classfiletojavasyntax.ClassFileToJavaSyntaxProcessor;
import org.jd.core.v1.service.deserializer.classfile.DeserializeClassFileProcessor;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.JavaSyntaxToJavaFragmentProcessor;
import org.jd.core.v1.service.layouter.LayoutFragmentProcessor;
import org.jd.core.v1.service.tokenizer.javafragmenttotoken.JavaFragmentToTokenProcessor;
import org.jd.core.v1.service.writer.WriteTokenProcessor;

import java.io.IOException;
import java.util.Map;

/**
 * This is the main Decompiler implementation
 */
public class StandardDecompiler implements Decompiler {
	protected DeserializeClassFileProcessor deserializer = DeserializeClassFileProcessor.getInstance();
	protected ClassFileToJavaSyntaxProcessor converter = ClassFileToJavaSyntaxProcessor.getInstance();
	protected JavaSyntaxToJavaFragmentProcessor fragmenter = JavaSyntaxToJavaFragmentProcessor.getInstance();
	protected LayoutFragmentProcessor layouter = LayoutFragmentProcessor.getInstance();
	protected JavaFragmentToTokenProcessor tokenizer = JavaFragmentToTokenProcessor.getInstance();
	protected WriteTokenProcessor writer = WriteTokenProcessor.getInstance();

	@Override
	public Printer decompile(Loader loader, Printer printer, String internalName) throws IOException {
		Message message = new Message();

		message.setMainInternalTypeName(internalName);
		message.setLoader(loader);
		message.setPrinter(printer);

		decompile(message);

		return printer;
	}

	@Override
	public Printer decompile(Loader loader, Printer printer, String internalName, Map<String, Object> configuration)
			throws IOException {
		Message message = new Message();

		message.setMainInternalTypeName(internalName);
		message.setConfiguration(configuration);
		message.setLoader(loader);
		message.setPrinter(printer);

		decompile(message);
		return printer;
	}

	protected void decompile(Message message) throws IOException {
		this.deserializer.process(message);
		this.converter.process(message);
		this.fragmenter.process(message);
		this.layouter.process(message);
		this.tokenizer.process(message);
		this.writer.process(message);
	}
}
