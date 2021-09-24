/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.tokenizer.javafragmenttotoken;

import java.util.List;

import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.javafragment.JavaFragment;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.model.token.Token;
import org.jd.core.v1.service.tokenizer.javafragmenttotoken.visitor.TokenizeJavaFragmentVisitor;

/**
 * Convert a list of fragments to a list of tokens.<br>
 * <br>
 *
 * Input: List<{@link org.jd.core.v1.model.fragment.Fragment}><br>
 * Output: List<{@link org.jd.core.v1.model.token.Token}><br>
 */
public class JavaFragmentToTokenProcessor implements Processor {

	/**
	 * Convert a list of JavaFragment's to a list of tokens
	 */
	@Override
	public void process(Message message) {
		@SuppressWarnings("unchecked")
		List<JavaFragment> fragments = (List<JavaFragment>) (List<?>) message.getFragments(); // warning expects a List
																								// of JavaFragment !
		List<Token> list = process(fragments);
		message.setTokens(list);
	}

	/**
	 * Convert a list of JavaFragment's to a list of tokens
	 */
	public List<Token> process(List<JavaFragment> fragments) {
		TokenizeJavaFragmentVisitor visitor = new TokenizeJavaFragmentVisitor(fragments.size() * 3);

		// Create tokens
		for (JavaFragment fragment : fragments) {
			fragment.accept(visitor);
		}

		return visitor.getTokens();
	}
}
