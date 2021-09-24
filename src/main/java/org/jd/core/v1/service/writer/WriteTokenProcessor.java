/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.writer;

import java.util.List;

import org.jd.core.v1.api.Printer;
import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.model.token.Token;
import org.jd.core.v1.service.writer.visitor.PrintTokenVisitor;

/**
 * Write a list of tokens to a {@link org.jd.core.v1.api.Printer}.<br>
 * <br>
 *
 * Input: List<{@link org.jd.core.v1.model.token.Token}><br>
 * Output: -<br>
 */
public class WriteTokenProcessor implements Processor {

	/**
	 * Write a list of tokens to a Printer
	 */
	@Override
	public void process(Message message) {
		Printer printer = message.getPrinter();
		List<Token> tokens = message.getTokens();
		int maxLineNumber = message.getMaxLineNumber();
		int majorVersion = message.getMajorVersion();
		int minorVersion = message.getMinorVersion();
		process(printer, tokens, maxLineNumber, majorVersion, minorVersion);
	}

	/**
	 * Write a list of tokens to a Printer
	 */
	public void process(Printer printer, List<Token> tokens, int maxLineNumber, int majorVersion, int minorVersion) {
		PrintTokenVisitor visitor = new PrintTokenVisitor();

		printer.start(maxLineNumber, majorVersion, minorVersion);
		visitor.start(printer, tokens);

		for (Token token : tokens) {
			token.accept(visitor);
		}

		visitor.end();
		printer.end();
	}

	private static WriteTokenProcessor instance = null;

	/**
	 * Get Singleton instance
	 */
	public static WriteTokenProcessor getInstance() {
		if (instance == null) {
			instance = new WriteTokenProcessor();
		}
		return instance;
	}
}
