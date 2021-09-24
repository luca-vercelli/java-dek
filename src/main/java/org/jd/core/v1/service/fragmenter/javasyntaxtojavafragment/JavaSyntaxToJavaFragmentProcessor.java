/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment;

import java.util.List;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.fragment.Fragment;
import org.jd.core.v1.model.javafragment.ImportsFragment;
import org.jd.core.v1.model.javasyntax.CompilationUnit;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.visitor.CompilationUnitVisitor;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.visitor.SearchImportsVisitor;

/**
 * Convert a Java syntax model to a list of fragments.<br>
 * <br>
 *
 * Input: {@link org.jd.core.v1.model.javasyntax.CompilationUnit}<br>
 * Output: List<{@link org.jd.core.v1.model.fragment.Fragment}><br>
 */
public class JavaSyntaxToJavaFragmentProcessor implements Processor {

	/**
	 * Given a CompilationUnit, generate Fragments (or JavaFragment's ???)
	 */
	@Override
	public void process(Message message) {
		Loader loader = message.getLoader();
		String mainInternalTypeName = message.getMainInternalTypeName();
		int majorVersion = message.getMajorVersion();
		CompilationUnit compilationUnit = message.getCompilationUnit();

		SearchImportsVisitor importsVisitor = new SearchImportsVisitor(loader, mainInternalTypeName);
		importsVisitor.visit(compilationUnit);
		ImportsFragment importsFragment = importsVisitor.getImportsFragment();
		int maxLineNumber = importsVisitor.getMaxLineNumber();
		
		
		List<Fragment> fragments = generateFragments(loader, mainInternalTypeName, majorVersion, compilationUnit,
				importsFragment);
		
		message.setMaxLineNumber(maxLineNumber);
		message.setFragments(fragments);
	}

	/**
	 * Generate fragments
	 */
	protected List<Fragment> generateFragments(Loader loader, String mainInternalTypeName, int majorVersion,
			CompilationUnit compilationUnit, ImportsFragment importsFragment) {
		
		CompilationUnitVisitor visitor = new CompilationUnitVisitor(loader, mainInternalTypeName, majorVersion,
				importsFragment);
		visitor.visit(compilationUnit);
		List<Fragment> fragments = visitor.getFragments();
		return fragments;
	}

	private static JavaSyntaxToJavaFragmentProcessor instance = null;

	/**
	 * Get Singleton instance
	 */
	public static JavaSyntaxToJavaFragmentProcessor getInstance() {
		if (instance == null) {
			instance = new JavaSyntaxToJavaFragmentProcessor();
		}
		return instance;
	}
}
