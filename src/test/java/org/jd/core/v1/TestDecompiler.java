package org.jd.core.v1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Printer;
import org.jd.core.v1.impl.StandardDecompiler;
import org.jd.core.v1.impl.loader.ClassPathLoader;
import org.jd.core.v1.impl.printer.PlainTextPrinter;
import org.jd.core.v1.model.message.Message;

/**
 * Similar to StandardDecompiler, with output to stdout
 * 
 * @author luca vercelli 2021
 *
 */
public class TestDecompiler extends StandardDecompiler {

	@Override
	protected void decompile(Message message) throws IOException {
		super.decompile(message);

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
