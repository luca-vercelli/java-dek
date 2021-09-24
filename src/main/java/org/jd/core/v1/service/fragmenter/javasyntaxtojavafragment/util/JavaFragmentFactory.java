/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.util;

import java.util.List;

import org.jd.core.v1.model.javafragment.EndBlockFragment;
import org.jd.core.v1.model.javafragment.EndBlockInParameterFragment;
import org.jd.core.v1.model.javafragment.EndBodyFragment;
import org.jd.core.v1.model.javafragment.EndBodyInParameterFragment;
import org.jd.core.v1.model.javafragment.EndSingleStatementBlockFragment;
import org.jd.core.v1.model.javafragment.EndStatementsBlockFragment;
import org.jd.core.v1.model.javafragment.ImportsFragment;
import org.jd.core.v1.model.javafragment.JavaFragment;
import org.jd.core.v1.model.javafragment.SpaceSpacerFragment;
import org.jd.core.v1.model.javafragment.SpacerBetweenMembersFragment;
import org.jd.core.v1.model.javafragment.SpacerFragment;
import org.jd.core.v1.model.javafragment.StartBlockFragment;
import org.jd.core.v1.model.javafragment.StartBodyFragment;
import org.jd.core.v1.model.javafragment.StartSingleStatementBlockFragment;
import org.jd.core.v1.model.javafragment.StartStatementsBlockFragment;
import org.jd.core.v1.model.javafragment.StartStatementsDoWhileBlockFragment;
import org.jd.core.v1.model.javafragment.StartStatementsTryBlockFragment;
import org.jd.core.v1.model.javafragment.TokensFragment;

public class JavaFragmentFactory {
    public static void addSpacerAfterPackage(List<JavaFragment> fragments) {
        fragments.add(new SpacerFragment(0, 1, 1, 0, "Spacer after package"));
        fragments.add(new SpacerFragment(0, 1, 1, 1, "Second spacer after package"));
    }

    public static void addSpacerAfterImports(List<JavaFragment> fragments) {
        fragments.add(new SpacerFragment(0, 1, 1, 1, "Spacer after imports"));
    }

    public static void addSpacerBeforeMainDeclaration(List<JavaFragment> fragments) {
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 5, "Spacer before main declaration"));
    }

    public static void addEndArrayInitializerInParameter(List<JavaFragment> fragments, StartBlockFragment start) {
        fragments.add(new EndBlockInParameterFragment(0, 0, 1, 20, "End array initializer", start));
        fragments.add(new SpaceSpacerFragment(0, 0, Integer.MAX_VALUE, 21, "End array initializer spacer in parameter"));
    }

    public static void addEndArrayInitializer(List<JavaFragment> fragments, StartBlockFragment start) {
        fragments.add(new EndBlockFragment(0, 0, Integer.MAX_VALUE, 20, "End array initializer", start));
    }

    public static void addEndSingleStatementMethodBody(List<JavaFragment> fragments, StartBodyFragment start) {
        fragments.add(new EndBodyFragment(0, 1, 1, 8, "End single statement method body", start));
    }

    public static void addEndMethodBody(List<JavaFragment> fragments, StartBodyFragment start) {
        fragments.add(new EndBodyFragment(0, 1, 1, 8, "End method body", start));
    }

    public static void addEndInstanceInitializerBlock(List<JavaFragment> fragments, StartBlockFragment start) {
        fragments.add(new EndBlockFragment(0, 1, 1, 8, "End anonymous method body", start));
    }

    public static void addEndTypeBody(List<JavaFragment> fragments, StartBodyFragment start) {
        fragments.add(new EndBodyFragment(0, 1, 1, 3, "End type body", start));
    }

    public static void addEndSubTypeBodyInParameter(List<JavaFragment> fragments, StartBodyFragment start) {
        fragments.add(new EndBodyInParameterFragment(0, 1, 1, 10, "End sub type body in parameter", start));
        fragments.add(new SpaceSpacerFragment(0, 0, Integer.MAX_VALUE, 13, "End sub type body spacer in parameter"));
    }

    public static void addEndSubTypeBody(List<JavaFragment> fragments, StartBodyFragment start) {
        fragments.add(new EndBodyFragment(0, 1, 1, 10, "End sub type body", start));
    }

    public static void addEndSingleStatementBlock(List<JavaFragment> fragments, StartSingleStatementBlockFragment start) {
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 19, "End single statement block spacer"));
        //fragments.add(new EndSingleStatementBlockFragment(0, 1, 2, 15, "End single statement block", start));
        fragments.add(new EndSingleStatementBlockFragment(0, 0, 1, 6, "End single statement block", start));
    }

    public static void addEndStatementsBlock(List<JavaFragment> fragments, StartStatementsBlockFragment.Group group) {
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 19, "End statement block spacer"));
        fragments.add(new EndStatementsBlockFragment(0, 1, 2, 15, "End statement block", group));
    }

    public static void addSpacerAfterEndStatementsBlock(List<JavaFragment> fragments) {
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 11, "Spacer after end statement block"));
    }

    public static void addEndStatementsInLambdaBlockInParameter(List<JavaFragment> fragments, StartBlockFragment start) {
        fragments.add(new EndBlockInParameterFragment(0, 1, 2, 15, "End statements in lambda block spacer in parameter", start));
        fragments.add(new SpaceSpacerFragment(0, 0, Integer.MAX_VALUE, 15, "End statements in lambda block spacer in parameter"));
    }

    public static void addEndStatementsInLambdaBlock(List<JavaFragment> fragments, StartBlockFragment start) {
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 15, "End statements in lambda block spacer"));
        fragments.add(new EndBlockFragment(0, 1, 2, 15, "End statements in lambda block spacer", start));
    }

    public static void addSpacerAfterMemberAnnotations(List<JavaFragment> fragments) {
        fragments.add(new SpaceSpacerFragment(0, 1, 1, 10, "Spacer after member annotations"));
    }

    public static void addSpacerAfterSwitchLabel(List<JavaFragment> fragments) {
        fragments.add(TokensFragment.START_DECLARATION_OR_STATEMENT_BLOCK);
        fragments.add(new SpaceSpacerFragment(0, 1, 1, 16, "Spacer after switch label"));
    }

    public static void addSpacerBetweenSwitchLabels(List<JavaFragment> fragments) {
        fragments.add(new SpaceSpacerFragment(0, 1, 1, 16, "Spacer between switch label"));
    }

    public static void addSpacerBeforeExtends(List<JavaFragment> fragments) {
        fragments.add(new SpaceSpacerFragment(0, 0, 1, 2, "Spacer before extends"));
    }

    public static void addSpacerBeforeImplements(List<JavaFragment> fragments) {
        fragments.add(new SpaceSpacerFragment(0, 0, 1, 2, "Spacer before implements"));
    }

    public static void addSpacerBetweenEnumValues(List<JavaFragment> fragments, int preferredLineCount) {
        fragments.add(TokensFragment.COMMA);
        fragments.add(new SpaceSpacerFragment(0, preferredLineCount, 1, 10, "Spacer between enum values"));
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 24, "Second spacer between enum values"));
    }

    public static void addSpacerBetweenFieldDeclarators(List<JavaFragment> fragments) {
        fragments.add(TokensFragment.COMMA);
        fragments.add(new SpacerFragment(0, 0, 1, 10, "Spacer between field declarators"));
    }

    public static void addSpacerBetweenMemberAnnotations(List<JavaFragment> fragments) {
        fragments.add(new SpaceSpacerFragment(0, 1, 1, 10, "Spacer between member annotations"));
    }

    public static void addSpacerBetweenMembers(List<JavaFragment> fragments) {
        fragments.add(new SpacerBetweenMembersFragment(0, 2, Integer.MAX_VALUE, 7, "Spacer between members"));
    }

    public static void addSpacerBetweenStatements(List<JavaFragment> fragments) {
        fragments.add(new SpaceSpacerFragment(0, 1, Integer.MAX_VALUE, 12, "Spacer between statements"));
    }

    public static void addSpacerBetweenSwitchLabelBlock(List<JavaFragment> fragments) {
        fragments.add(new SpacerFragment(0, 1, 1, 17, "Spacer between switch label block"));
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 11, "Spacer between switch label block 2"));
    }

    public static void addSpacerAfterSwitchBlock(List<JavaFragment> fragments) {
        fragments.add(TokensFragment.END_DECLARATION_OR_STATEMENT_BLOCK);
    }

    public static StartBlockFragment addStartArrayInitializerBlock(List<JavaFragment> fragments) {
        StartBlockFragment fragment = new StartBlockFragment(0, 0, Integer.MAX_VALUE, 20, "Start array initializer block");
        fragments.add(fragment);
        return fragment;
    }

    public static void addSpacerBetweenArrayInitializerBlock(List<JavaFragment> fragments) {
        fragments.add(TokensFragment.COMMA);
        fragments.add(new SpaceSpacerFragment(0, 0, Integer.MAX_VALUE, 20, "Spacer between array initializer block"));
    }

    public static void addNewLineBetweenArrayInitializerBlock(List<JavaFragment> fragments) {
        fragments.add(new SpacerFragment(0, 1, 1, 22, "New line between array initializer block"));
    }

    public static StartBodyFragment addStartSingleStatementMethodBody(List<JavaFragment> fragments) {
        StartBodyFragment fragment = new StartBodyFragment(0, 1, 2, 9, "Start single statement method body");
        fragments.add(fragment);
        return fragment;
    }

    public static StartBodyFragment addStartMethodBody(List<JavaFragment> fragments) {
        StartBodyFragment fragment = new StartBodyFragment(0, 1, 2, 9, "Start method body");
        fragments.add(fragment);
        return fragment;
    }

    public static StartBlockFragment addStartInstanceInitializerBlock(List<JavaFragment> fragments) {
        StartBlockFragment fragment = new StartBlockFragment(0, 1, 2, 9, "Start anonymous method body");
        fragments.add(fragment);
        return fragment;
    }

    public static StartBodyFragment addStartTypeBody(List<JavaFragment> fragments) {
        StartBodyFragment fragment = new StartBodyFragment(0, 1, 2, 4, "Start type body");
        fragments.add(fragment);
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 13, "Start type body spacer"));
        return fragment;
    }

    public static StartSingleStatementBlockFragment addStartSingleStatementBlock(List<JavaFragment> fragments) {
        StartSingleStatementBlockFragment fragment = new StartSingleStatementBlockFragment(0, 1, 2, 18, "Start single statement block");
        fragments.add(fragment);
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 23, "Start single statement block spacer"));
        return fragment;
    }

    public static StartStatementsBlockFragment.Group addStartStatementsBlock(List<JavaFragment> fragments) {
        StartStatementsBlockFragment fragment = new StartStatementsBlockFragment(0, 1, 2, 14, "Start statements block");
        fragments.add(fragment);
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 19, "Start statements block spacer"));
        return fragment.getGroup();
    }

    public static StartBlockFragment addStartStatementsInLambdaBlock(List<JavaFragment> fragments) {
        StartBlockFragment fragment = new StartBlockFragment(0, 1, 2, 14, "Start statements in lambda block");
        fragments.add(fragment);
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 14, "Start statements in lambda block spacer"));
        return fragment;
    }

    public static StartStatementsBlockFragment.Group addStartStatementsDoWhileBlock(List<JavaFragment> fragments) {
        StartStatementsDoWhileBlockFragment fragment = new StartStatementsDoWhileBlockFragment(0, 1, 2, 14, "Start statements do-while block");
        fragments.add(fragment);
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 14, "Start statements do-while block spacer"));
        return fragment.getGroup();
    }

    public static StartStatementsBlockFragment.Group addStartStatementsTryBlock(List<JavaFragment> fragments) {
        StartStatementsTryBlockFragment fragment = new StartStatementsTryBlockFragment(0, 1, 2, 14, "Start statements try block");
        fragments.add(fragment);
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 14, "Start statements try block spacer"));
        return fragment.getGroup();
    }

    public static void addStartStatementsBlock(List<JavaFragment> fragments, StartStatementsBlockFragment.Group group) {
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 23, "Start statements block pre spacer"));
        fragments.add(new StartStatementsBlockFragment(0, 1, 2, 14, "Start statements block", group));
        fragments.add(new SpacerFragment(0, 0, Integer.MAX_VALUE, 19, "Start statements block post spacer"));
    }

    public static ImportsFragment newImportsFragment() {
        return new ImportsFragment(0);
    }
}
