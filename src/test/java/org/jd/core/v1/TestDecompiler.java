package org.jd.core.v1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.jd.core.v1.api.Decompiler;
import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Printer;
import org.jd.core.v1.impl.loader.ClassPathLoader;
import org.jd.core.v1.impl.printer.PlainTextPrinter;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.converter.classfiletojavasyntax.ClassFileToJavaSyntaxProcessor;
import org.jd.core.v1.service.deserializer.classfile.DeserializeClassFileProcessor;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.JavaSyntaxToJavaFragmentProcessor;
import org.jd.core.v1.service.layouter.LayoutFragmentProcessor;
import org.jd.core.v1.service.tokenizer.javafragmenttotoken.JavaFragmentToTokenProcessor;
import org.jd.core.v1.service.writer.WriteTokenProcessor;

/**
 * Similar to StandardDecompiler, with output to stdout
 * 
 * @author luca vercelli 2021
 *
 */
public class TestDecompiler implements Decompiler {
	protected DeserializeClassFileProcessor deserializer = new DeserializeClassFileProcessor();
	protected ClassFileToJavaSyntaxProcessor converter = new ClassFileToJavaSyntaxProcessor();
	protected JavaSyntaxToJavaFragmentProcessor fragmenter = new JavaSyntaxToJavaFragmentProcessor();
	protected LayoutFragmentProcessor layouter = new LayoutFragmentProcessor();
	protected JavaFragmentToTokenProcessor tokenizer = new JavaFragmentToTokenProcessor();
	protected WriteTokenProcessor writer = new WriteTokenProcessor();

	@Override
	public Printer decompile(Loader loader, Printer printer, String internalName) throws Exception {
		Message message = new Message();

		message.setMainInternalTypeName(internalName);
		message.setLoader(loader);
		message.setPrinter(printer);

		decompile(message);

		return printer;
	}

	@Override
	public Printer decompile(Loader loader, Printer printer, String internalName, Map<String, Object> configuration)
			throws Exception {
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

		String source = message.getPrinter().toString();
		assertTrue(source.indexOf("// Byte code:") == -1);
		printSource(source);
	}

	protected void printSource(String source) {
		System.out.println("- - - - - - - - ");
		System.out.println(source);
		System.out.println("- - - - - - - - ");
	}

	/**
	 * Convenience method for tests
	 */
	public String decompile(String internalName) throws Exception {
		Printer printer = new PlainTextPrinter();
		decompile(new ClassPathLoader(), printer, internalName);
		return printer.toString();
	}

	/**
	 * Convenience method for tests
	 */
	public String decompile(String internalName, Map<String, Object> configuration) throws Exception {
		Printer printer = new PlainTextPrinter();
		decompile(new ClassPathLoader(), printer, internalName, configuration);
		return printer.toString();
	}

	/**
	 * Convenience method for tests
	 */
	public String decompile(Loader loader, String internalName) throws Exception {
		Printer printer = new PlainTextPrinter();
		decompile(loader, printer, internalName);
		return printer.toString();
	}

	/**
	 * Convenience method for tests
	 */
	public String decompile(Loader loader, String internalName, Map<String, Object> configuration) throws Exception {
		Printer printer = new PlainTextPrinter();
		decompile(loader, printer, internalName, configuration);
		return printer.toString();
	}
}
