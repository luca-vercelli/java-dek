/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.tokenizer.javafragmenttotoken;

import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.fragment.Fragment;
import org.jd.core.v1.model.javafragment.JavaFragment;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.tokenizer.javafragmenttotoken.visitor.TokenizeJavaFragmentVisitor;

import java.util.List;

/**
 * Convert a list of fragments to a list of tokens.<br><br>
 *
 * Input:  List<{@link org.jd.core.v1.model.fragment.Fragment}><br>
 * Output: List<{@link org.jd.core.v1.model.token.Token}><br>
 */
public class JavaFragmentToTokenProcessor implements Processor {

    @Override
    public void process(Message message) throws Exception {
        List<Fragment> fragments = message.getFragments(); // warning expects a List of JavaFragment !
        TokenizeJavaFragmentVisitor visitor = new TokenizeJavaFragmentVisitor(fragments.size() * 3);

        // Create tokens
        for (Fragment fragment : fragments) {
            ((JavaFragment)fragment).accept(visitor);
        }

        message.setTokens(visitor.getTokens());
    }
}
