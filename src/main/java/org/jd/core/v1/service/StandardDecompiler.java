/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service;

import java.io.IOException;

import org.jd.core.v1.api.Decompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Printer;
import org.jd.core.v1.model.message.CompileConfiguration;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.converter.classfiletojavasyntax.processor.ConvertClassFileProcessor;
import org.jd.core.v1.service.deserializer.classfile.ClassFileDeserializer;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.JavaSyntaxToJavaFragmentProcessor;
import org.jd.core.v1.service.layouter.LayoutFragmentProcessor;
import org.jd.core.v1.service.tokenizer.javafragmenttotoken.JavaFragmentToTokenProcessor;
import org.jd.core.v1.service.writer.WriteTokenProcessor;

/**
 * This is the main Decompiler implementation
 */
public class StandardDecompiler implements Decompiler {
	protected ClassFileDeserializer deserializer = ClassFileDeserializer.getInstance();
	protected ConvertClassFileProcessor converter = ConvertClassFileProcessor.getInstance();
	protected JavaSyntaxToJavaFragmentProcessor fragmenter = JavaSyntaxToJavaFragmentProcessor.getInstance();
	protected LayoutFragmentProcessor layouter = LayoutFragmentProcessor.getInstance();
	protected JavaFragmentToTokenProcessor tokenizer = JavaFragmentToTokenProcessor.getInstance();
	protected WriteTokenProcessor writer = WriteTokenProcessor.getInstance();

	protected StandardDecompiler() {
	}

	@Override
	public Printer decompile(Loader loader, Printer printer, String internalName) throws IOException {
		return this.decompile(loader, printer, internalName, null);
	}

	@Override
	public Printer decompile(Loader loader, Printer printer, String internalName, CompileConfiguration configuration)
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
		// message.getConfiguration().setDumpOpcode(true);
		this.deserializer.process(message);
		this.converter.process(message);
		this.fragmenter.process(message);

		// Maybe you want to debug this:
		// ((ClassFileClassDeclaration)message.getCompilationUnit().getTypeDeclarations()).getBodyDeclaration().getMemberDeclarations()

		this.layouter.process(message);
		this.tokenizer.process(message);
		this.writer.process(message);
	}

	private static StandardDecompiler instance = null;

	/**
	 * Get Singleton instance
	 */
	public static StandardDecompiler getInstance() {
		if (instance == null) {
			instance = new StandardDecompiler();
		}
		return instance;
	}
}
