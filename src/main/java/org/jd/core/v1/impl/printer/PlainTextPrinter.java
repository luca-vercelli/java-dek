/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.impl.printer;

import org.jd.core.v1.api.Printer;

/**
 * A Printer that stores Java source inside a StringBuilder. Retrieve final
 * source code using toString() method.
 */
public class PlainTextPrinter implements Printer {
	protected static final String TAB = "  ";
	protected static final String NEWLINE = "\n";
	protected static final int TEN = 10;

	protected int indentationCount;
	protected StringBuilder sb = new StringBuilder();
	protected int realLineNumber = 0;
	protected String format;

	protected boolean escapeUnicodeCharacters = false;
	protected boolean showLineNumbers = true;

	public PlainTextPrinter() {
	}

	public PlainTextPrinter(boolean escapeUnicodeCharacters) {
		this.escapeUnicodeCharacters = escapeUnicodeCharacters;
	}

	public PlainTextPrinter(boolean escapeUnicodeCharacters, boolean showLineNumbers) {
		this.escapeUnicodeCharacters = escapeUnicodeCharacters;
		this.showLineNumbers = showLineNumbers;
	}

	public void init() {
		sb.setLength(0);
		realLineNumber = 0;
		indentationCount = 0;
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	@Override
	public void start(int maxLineNumber, int majorVersion, int minorVersion) {
		this.indentationCount = 0;

		if (maxLineNumber == 0) {
			format = "%4d";
		} else {
			int width = 2;

			while (maxLineNumber >= TEN) {
				width++;
				maxLineNumber /= TEN;
			}

			format = "%" + width + "d";
		}
	}

	@Override
	public void end() {
	}

	@Override
	public void printText(String text) {
		if (escapeUnicodeCharacters) {
			for (int i = 0, len = text.length(); i < len; i++) {
				char c = text.charAt(i);

				if (c < 127) {
					sb.append(c);
				} else {
					int h = (c >> 12);

					sb.append("\\u");
					sb.append((char) ((h <= 9) ? (h + '0') : (h + ('A' - 10))));
					h = (c >> 8) & 15;
					sb.append((char) ((h <= 9) ? (h + '0') : (h + ('A' - 10))));
					h = (c >> 4) & 15;
					sb.append((char) ((h <= 9) ? (h + '0') : (h + ('A' - 10))));
					h = (c) & 15;
					sb.append((char) ((h <= 9) ? (h + '0') : (h + ('A' - 10))));
				}
			}
		} else {
			sb.append(text);
		}
	}

	@Override
	public void printNumericConstant(String constant) {
		sb.append(constant);
	}

	@Override
	public void printStringConstant(String constant, String ownerInternalName) {
		printText(constant);
	}

	@Override
	public void printKeyword(String keyword) {
		sb.append(keyword);
	}

	@Override
	public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
		printText(name);
	}

	@Override
	public void printReference(int type, String internalTypeName, String name, String descriptor,
			String ownerInternalName) {
		printText(name);
	}

	@Override
	public void indent() {
		this.indentationCount++;
	}

	@Override
	public void unindent() {
		if (this.indentationCount > 0)
			this.indentationCount--;
	}

	@Override
	public void startLine(int lineNumber) {
		printLineNumber(lineNumber);

		for (int i = 0; i < indentationCount; i++)
			sb.append(TAB);
	}

	@Override
	public void endLine() {
		sb.append(NEWLINE);
	}

	public void extraLine(int count) {
		while (count-- > 0) {
			printLineNumber(0);
			sb.append(NEWLINE);
		}
	}

	@Override
	public void startMarker(int type) {
	}

	@Override
	public void endMarker(int type) {
	}

	protected void printLineNumber(int lineNumber) {
		++realLineNumber;
		if (showLineNumbers) {
			sb.append("/*");
			sb.append(String.format(format, realLineNumber));
			sb.append(':');
			sb.append(String.format(format, lineNumber));
			sb.append(" */ ");
		}
	}
}
