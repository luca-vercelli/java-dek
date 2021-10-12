/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javasyntax.statement;

import org.jd.core.v1.util.DefaultList;

import java.util.List;

/**
 * A list of Statement that is itself a BaseStatement
 */
public class Statements extends DefaultList<Statement> implements BaseStatement {
	private static final long serialVersionUID = 505417745008819554L;

	public Statements() {
	}

	public Statements(int capacity) {
		super(capacity);
	}

	public Statements(List<Statement> list) {
		super(list);
		assert (list != null) && (list.size() > 1) : "Uses 'Statement' implementation instead";
	}

	public Statements(Statement statement, Statement... statements) {
		super(statement, statements);
		assert (statements != null) && (statements.length > 0) : "Uses 'Statement' implementation instead";
	}

	@Override
	public boolean isStatements() {
		return true;
	}

	@Override
	public void accept(StatementVisitor visitor) {
		visitor.visit(this);
	}
}
