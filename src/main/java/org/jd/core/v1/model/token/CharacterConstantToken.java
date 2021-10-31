/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.token;

public class CharacterConstantToken implements Token {

	protected String ch;
	protected String ownerInternalName;

	public CharacterConstantToken(String ch, String ownerInternalName) {
		this.ch = ch;
		this.ownerInternalName = ownerInternalName;
	}

	public String getCharacter() {
		return ch;
	}

	public String getOwnerInternalName() {
		return ownerInternalName;
	}

	public String toString() {
		return "CharacterConstantToken{'" + ch + "'}";
	}

	@Override
	public void accept(TokenVisitor visitor) {
		visitor.visit(this);
	}
}
