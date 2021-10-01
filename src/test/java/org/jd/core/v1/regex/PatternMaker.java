/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;

/**
 * Utility used during tests to create regex patterns matching source code
 */
public class PatternMaker {

	/**
	 * Create a regexp matching the concatenation of first and next strings, in a
	 * multiline context.
	 */
	public static String make(String first, String... next) {
		StringBuilder sb = new StringBuilder("(?s).*");

		sb.append(replace(first));

		for (String s : next) {
			sb.append("[^\\n\\r]*").append(replace(s));
		}

		sb.append(".*");

		return sb.toString();
	}

	/**
	 * Create a regexp matching s, in a multiline context.
	 */
	public static String make(String s) {
		return "(?s).*" + replace(s) + ".*";
	}

	protected static String replace(String s) {
		return s.replace("[", "\\[") //
				.replace("]", "\\]") //
				.replace("(", "\\(") //
				.replace(")", "\\)") //
				.replace(".", "\\.") //
				.replace("?", "\\?") //
				.replace("+", "\\+") //
				.replace("*", "\\*") //
				.replace("|", "\\|") //
				.replace("^", "\\^") //
				.replace(" ", "[ ]*") //
				.replaceAll("\\s*\\{\\s*", "[^\\\\n\\\\r]*\\\\{[^\\\\n\\\\r]*") //
				.replaceAll("\\s*\\}\\s*", "[^\\\\n\\\\r]*\\\\}[^\\\\n\\\\r]*") //
				.replace(",", "[^\\n\\r]*,");
	}

	/**
	 * Custom JUnit Assertion. Fails if given line of source code does not match
	 * expected Java code.
	 * 
	 * @param line
	 * @param expected
	 * @param source
	 */
	public static void assertMatch(String source, String expected, int line) {
		if (line == 0) {
			assertMatch(expected, source);
			return;
		}
		boolean found = source.matches(make(": " + line + " */", expected));
		if (!found) {
			Pattern pattern = Pattern.compile("(?s).*:[ ]*" + line + "[ ]*\\*/([^\\n\\r]*).*");
			Matcher m = pattern.matcher(source);
			if (m.find()) {
				String msg = "expected: <" + expected + "> but was: <" + m.group(1) + ">";
				throw new AssertionFailedError(msg);
			} else {
				throw new AssertionFailedError("Line " + line + " not found");
			}
		}
	}

	/**
	 * Custom JUnit Assertion. Fails if no line of source code matches expected Java
	 * code.
	 * 
	 * @param line
	 * @param expected
	 * @param source
	 */
	public static void assertMatch(String source, String expected) {
		boolean found = source.matches(make(expected));
		if (!found) {
			String msg = "No lines of code match: " + expected;
			throw new AssertionFailedError(msg);
		}
	}

	/**
	 * Custom JUnit Assertion. Fails if some line of source code matches unexpected
	 * Java code.
	 * 
	 * @param line
	 * @param expected
	 * @param source
	 */
	public static void assertDontMatch(String source, String unexpected) {
		boolean found = source.matches(make(unexpected));
		if (found) {
			String msg = "Unexpected mathing code: " + unexpected;
			throw new AssertionFailedError(msg);
		}
	}
}
