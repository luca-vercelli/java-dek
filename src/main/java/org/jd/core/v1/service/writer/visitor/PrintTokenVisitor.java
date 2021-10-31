/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.writer.visitor;

import java.util.List;

import org.jd.core.v1.api.Printer;
import org.jd.core.v1.model.javafragment.LineNumberTokensFragment.SearchLineNumberVisitor;
import org.jd.core.v1.model.token.BooleanConstantToken;
import org.jd.core.v1.model.token.CharacterConstantToken;
import org.jd.core.v1.model.token.DeclarationToken;
import org.jd.core.v1.model.token.EndBlockToken;
import org.jd.core.v1.model.token.EndMarkerToken;
import org.jd.core.v1.model.token.KeywordToken;
import org.jd.core.v1.model.token.LineNumberToken;
import org.jd.core.v1.model.token.NewLineToken;
import org.jd.core.v1.model.token.NumericConstantToken;
import org.jd.core.v1.model.token.ReferenceToken;
import org.jd.core.v1.model.token.StartBlockToken;
import org.jd.core.v1.model.token.StartMarkerToken;
import org.jd.core.v1.model.token.StringConstantToken;
import org.jd.core.v1.model.token.TextToken;
import org.jd.core.v1.model.token.Token;
import org.jd.core.v1.model.token.TokenVisitor;

/**
 * Print a list of tokens to a Printer.
 */
public class PrintTokenVisitor implements TokenVisitor {
	public static final int UNKNOWN_LINE_NUMBER = Printer.UNKNOWN_LINE_NUMBER;

	protected SearchLineNumberVisitor searchLineNumberVisitor = new SearchLineNumberVisitor();

	protected Printer printer;
	protected List<Token> tokens;
	protected int index;
	protected int newLineCount;

	/**
	 * Print a list of tokens to a Printer.
	 * 
	 * @param printer
	 * @param tokens
	 */
	public void start(Printer printer, List<Token> tokens) {
		this.printer = printer;
		this.tokens = tokens;
		this.index = 0;
		this.newLineCount = 0;
		printer.startLine(searchLineNumber());
	}

	public void end() {
		printer.endLine();
	}

	@Override
	public void visit(BooleanConstantToken token) {
		prepareNewLine();
		printer.printKeyword(token.getValue() ? "true" : "false");
		index++;
	}

	@Override
	public void visit(CharacterConstantToken token) {
		prepareNewLine();
		printer.printStringConstant('\'' + token.getCharacter() + '\'', token.getOwnerInternalName());
		index++;
	}

	@Override
	public void visit(DeclarationToken token) {
		prepareNewLine();
		printer.printDeclaration(token.getType(), token.getInternalTypeName(), token.getName(), token.getDescriptor());
		index++;
	}

	@Override
	public void visit(StartBlockToken token) {
		prepareNewLine();
		printer.printText(token.getText());
		printer.indent();
		if (token == StartBlockToken.START_RESOURCES_BLOCK) {
			printer.indent();
		}
		index++;
	}

	@Override
	public void visit(EndBlockToken token) {
		printer.unindent();
		if (token == EndBlockToken.END_RESOURCES_BLOCK) {
			printer.unindent();
		}
		prepareNewLine();
		printer.printText(token.getText());
		index++;
	}

	@Override
	public void visit(StartMarkerToken token) {
		prepareNewLine();
		printer.startMarker(token.getType());
		index++;
	}

	@Override
	public void visit(EndMarkerToken token) {
		prepareNewLine();
		printer.endMarker(token.getType());
		index++;
	}

	@Override
	public void visit(NewLineToken token) {
		newLineCount += token.getCount();
		index++;
	}

	@Override
	public void visit(KeywordToken token) {
		prepareNewLine();
		printer.printKeyword(token.getKeyword());
		index++;
	}

	@Override
	public void visit(LineNumberToken token) {
		index++;
	}

	@Override
	public void visit(NumericConstantToken token) {
		prepareNewLine();
		printer.printNumericConstant(token.getText());
		index++;
	}

	@Override
	public void visit(ReferenceToken token) {
		prepareNewLine();
		printer.printReference(token.getType(), token.getInternalTypeName(), token.getName(), token.getDescriptor(),
				token.getOwnerInternalName());
		index++;
	}

	@Override
	public void visit(StringConstantToken token) {
		prepareNewLine();
		printer.printStringConstant('"' + token.getText() + '"', token.getOwnerInternalName());
		index++;
	}

	@Override
	public void visit(TextToken token) {
		prepareNewLine();
		printer.printText(token.getText());
		index++;
	}

	protected void prepareNewLine() {
		if (newLineCount > 0) {
			printer.endLine();

			if (newLineCount > 2) {
				printer.extraLine(newLineCount - 2);
				newLineCount = 2;
			}

			if (newLineCount > 1) {
				printer.startLine(UNKNOWN_LINE_NUMBER);
				printer.endLine();
			}

			printer.startLine(searchLineNumber());
			newLineCount = 0;
		}
	}

	protected int searchLineNumber() {
		// Backward search
		searchLineNumberVisitor.reset();

		for (int i = index; i >= 0; i--) {
			tokens.get(i).accept(searchLineNumberVisitor);

			if (searchLineNumberVisitor.lineNumber != UNKNOWN_LINE_NUMBER) {
				return searchLineNumberVisitor.lineNumber;
			}
			if (searchLineNumberVisitor.newLineCounter > 0) {
				break;
			}
		}

		// Forward search
		searchLineNumberVisitor.reset();

		int size = tokens.size();

		for (int i = index; i < size; i++) {
			tokens.get(i).accept(searchLineNumberVisitor);

			if (searchLineNumberVisitor.lineNumber != UNKNOWN_LINE_NUMBER) {
				return searchLineNumberVisitor.lineNumber;
			}
			if (searchLineNumberVisitor.newLineCounter > 0) {
				break;
			}
		}

		return UNKNOWN_LINE_NUMBER;
	}
}
