/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.message;

import java.util.List;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Printer;
import org.jd.core.v1.model.classfile.ClassFile;
import org.jd.core.v1.model.javafragment.JavaFragment;
import org.jd.core.v1.model.javasyntax.CompilationUnit;
import org.jd.core.v1.model.token.Token;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;

public class Message {

	private Printer printer;
	private Loader loader;
	private CompileConfiguration configuration = new CompileConfiguration();
	private String mainInternalTypeName;
	private Integer maxLineNumber;
	private Integer majorVersion;
	private Integer minorVersion;
	private Boolean containsByteCode;
	private Boolean showBridgeAndSynthetic;
	private TypeMaker typeMaker;
	private ClassFile classFile;
	private CompilationUnit compilationUnit;
	private List<JavaFragment> fragments;
	private List<Token> tokens;

	public CompileConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CompileConfiguration configuration) {
		if (configuration != null) {
			this.configuration = configuration;
		}
	}

	public Integer getMaxLineNumber() {
		return maxLineNumber;
	}

	public Integer getMaxLineNumber(int defVal) {
		return maxLineNumber != null ? maxLineNumber : defVal;
	}

	public void setMaxLineNumber(Integer maxLineNumber) {
		this.maxLineNumber = maxLineNumber;
	}

	public Integer getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}

	public Integer getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}

	public Printer getPrinter() {
		return printer;
	}

	public void setPrinter(Printer printer) {
		this.printer = printer;
	}

	public Loader getLoader() {
		return loader;
	}

	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	public String getMainInternalTypeName() {
		return mainInternalTypeName;
	}

	public void setMainInternalTypeName(String mainInternalTypeName) {
		this.mainInternalTypeName = mainInternalTypeName;
	}

	public TypeMaker getTypeMaker() {
		return typeMaker;
	}

	public void setTypeMaker(TypeMaker typeMaker) {
		this.typeMaker = typeMaker;
	}

	public Boolean getContainsByteCode() {
		return containsByteCode;
	}

	public Boolean getContainsByteCode(Boolean defVal) {
		return containsByteCode != null ? containsByteCode : defVal;
	}

	public void setContainsByteCode(Boolean containsByteCode) {
		this.containsByteCode = containsByteCode;
	}

	public Boolean getShowBridgeAndSynthetic() {
		return showBridgeAndSynthetic;
	}

	public Boolean getShowBridgeAndSynthetic(Boolean defVal) {
		return showBridgeAndSynthetic != null ? showBridgeAndSynthetic : defVal;
	}

	public void setShowBridgeAndSynthetic(Boolean showBridgeAndSynthetic) {
		this.showBridgeAndSynthetic = showBridgeAndSynthetic;
	}

	public List<JavaFragment> getFragments() {
		return fragments;
	}

	public void setFragments(List<JavaFragment> fragments) {
		this.fragments = fragments;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	public ClassFile getClassFile() {
		return classFile;
	}

	public void setClassFile(ClassFile classFile) {
		this.classFile = classFile;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

}
