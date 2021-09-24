/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.services.tokenizer.javafragmenttotoken;

import java.util.List;

import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.fragment.Fragment;
import org.jd.core.v1.model.javafragment.JavaFragment;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.services.tokenizer.javafragmenttotoken.visitor.TokenizeJavaFragmentTestVisitor;

public class TestTokenizeJavaFragmentProcessor implements Processor {

	@Override
	public void process(Message message) {
		List<Fragment> fragments = message.getFragments(); // JavaFragment's expected
		TokenizeJavaFragmentTestVisitor visitor = new TokenizeJavaFragmentTestVisitor(fragments.size() * 3);

		// Create tokens
		for (Fragment fragment : fragments) {
			((JavaFragment) fragment).accept(visitor);
		}

		message.setTokens(visitor.getTokens());
	}
}
