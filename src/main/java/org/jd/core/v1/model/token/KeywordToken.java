/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.token;

public class KeywordToken implements Token {
	public static final KeywordToken ABSTRACT = new KeywordToken("abstract");
	public static final KeywordToken ANNOTATION = new KeywordToken("@interface");
	public static final KeywordToken CLASS = new KeywordToken("class");
	public static final KeywordToken DEFAULT = new KeywordToken("default");
	public static final KeywordToken ENUM = new KeywordToken("enum");
	public static final KeywordToken IMPLEMENTS = new KeywordToken("implements");
	public static final KeywordToken INTERFACE = new KeywordToken("interface");
	public static final KeywordToken NATIVE = new KeywordToken("native");
	public static final KeywordToken PACKAGE = new KeywordToken("package");
	public static final KeywordToken PRIVATE = new KeywordToken("private");
	public static final KeywordToken PROTECTED = new KeywordToken("protected");
	public static final KeywordToken PUBLIC = new KeywordToken("public");
	public static final KeywordToken STATIC = new KeywordToken("static");
	public static final KeywordToken THROWS = new KeywordToken("throws");
	public static final KeywordToken FALSE = new KeywordToken("false");
	public static final KeywordToken INSTANCEOF = new KeywordToken("instanceof");
	public static final KeywordToken LENGTH = new KeywordToken("length");
	public static final KeywordToken NEW = new KeywordToken("new");
	public static final KeywordToken NULL = new KeywordToken("null");
	public static final KeywordToken THIS = new KeywordToken("this");
	public static final KeywordToken TRUE = new KeywordToken("true");
	public static final KeywordToken ASSERT = new KeywordToken("assert");
	public static final KeywordToken BREAK = new KeywordToken("break");
	public static final KeywordToken CASE = new KeywordToken("case");
	public static final KeywordToken CATCH = new KeywordToken("catch");
	public static final KeywordToken CONTINUE = new KeywordToken("continue");
	public static final KeywordToken DO = new KeywordToken("do");
	public static final KeywordToken ELSE = new KeywordToken("else");
	public static final KeywordToken FINAL = new KeywordToken("final");
	public static final KeywordToken FINALLY = new KeywordToken("finally");
	public static final KeywordToken FOR = new KeywordToken("for");
	public static final KeywordToken IF = new KeywordToken("if");
	public static final KeywordToken RETURN = new KeywordToken("return");
	public static final KeywordToken STRICT = new KeywordToken("strictfp");
	public static final KeywordToken SYNCHRONIZED = new KeywordToken("synchronized");
	public static final KeywordToken SWITCH = new KeywordToken("switch");
	public static final KeywordToken THROW = new KeywordToken("throw");
	public static final KeywordToken TRANSIENT = new KeywordToken("transient");
	public static final KeywordToken TRY = new KeywordToken("try");
	public static final KeywordToken VOLATILE = new KeywordToken("volatile");
	public static final KeywordToken WHILE = new KeywordToken("while");
    public static final KeywordToken BOOLEAN = new KeywordToken("boolean");
    public static final KeywordToken BYTE = new KeywordToken("byte");
    public static final KeywordToken CHAR = new KeywordToken("char");
    public static final KeywordToken DOUBLE = new KeywordToken("double");
    public static final KeywordToken EXPORTS = new KeywordToken("exports");
    public static final KeywordToken EXTENDS = new KeywordToken("extends");
    public static final KeywordToken FLOAT = new KeywordToken("float");
    public static final KeywordToken INT = new KeywordToken("int");
    public static final KeywordToken LONG = new KeywordToken("long");
    public static final KeywordToken MODULE = new KeywordToken("module");
    public static final KeywordToken OPEN = new KeywordToken("open");
    public static final KeywordToken OPENS = new KeywordToken("opens");
    public static final KeywordToken PROVIDES = new KeywordToken("provides");
    public static final KeywordToken REQUIRES = new KeywordToken("requires");
    public static final KeywordToken SHORT = new KeywordToken("short");
    public static final KeywordToken SUPER = new KeywordToken("super");
    public static final KeywordToken TO = new KeywordToken("to");
    public static final KeywordToken TRANSITIVE = new KeywordToken("transitive");
    public static final KeywordToken USES = new KeywordToken("uses");
    public static final KeywordToken VOID = new KeywordToken("void");
    public static final KeywordToken WITH = new KeywordToken("with");
    public static final KeywordToken IMPORT = new KeywordToken("import");

	protected String keyword;

	public KeywordToken(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public String toString() {
		return "KeywordToken{'" + keyword + "'}";
	}

	@Override
	public void accept(TokenVisitor visitor) {
		visitor.visit(this);
	}
}
