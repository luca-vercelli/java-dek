package org.jd.core.v1.model.message;

import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;

public class CompileConfiguration {

	private boolean dumpOpcode;
	private TypeMaker typeMaker;
	private boolean realignLineNumbers;

	public boolean isDumpOpcode() {
		return dumpOpcode;
	}

	public CompileConfiguration setDumpOpcode(boolean dumpOpcode) {
		this.dumpOpcode = dumpOpcode;
		return this;
	}

	public TypeMaker getTypeMaker() {
		return typeMaker;
	}

	public CompileConfiguration setTypeMaker(TypeMaker typeMaker) {
		this.typeMaker = typeMaker;
		return this;
	}

	public boolean isRealignLineNumbers() {
		return realignLineNumbers;
	}

	public CompileConfiguration setRealignLineNumbers(boolean realignLineNumbers) {
		this.realignLineNumbers = realignLineNumbers;
		return this;
	}

}
