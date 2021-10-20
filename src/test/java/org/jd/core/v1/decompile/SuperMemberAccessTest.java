/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.decompile;

import static org.jd.core.v1.regex.PatternMaker.assertMatch;
import static org.junit.Assert.assertTrue;

import org.jd.core.v1.TestDecompiler;
import org.jd.core.v1.compiler.CompilerUtil;
import org.jd.core.v1.compiler.JavaSourceFileObject;
import org.junit.Test;

public class SuperMemberAccessTest {

	protected TestDecompiler decompiler = new TestDecompiler();

	class TestPublicMembers {

		// bytecode has a constructor
		// TestClass(SuperMemberAccessTest paramSuperMemberAccessTest)
		// referencing "this" as this$0

		public String a;

		public String test() {
			return "";
		}

		public class Child extends TestPublicMembers {

			// bytecode has a constructor
			// public Child(SuperMemberAccessTest.TestClass this$0)

			public String a;

			public String test() {
				return "b";
			}

			String doSomething() {
				super.test(); // invokespecial test : ()Ljava/lang/String;
				return super.a; // getfield a : Ljava/lang/String; ?!?
			}

			String doSomeMore() {
				test(); // invokevirtual test : ()Ljava/lang/String;
				return a; // getfield a : Ljava/lang/String; ?!?
			}
		}
	}

	class TestPrivateMembers {

		// methods in bytecode:
		//
		// <init> (Lorg/jd/core/v1/decompile/SuperMemberAccessTest;)V
		// test ()Ljava/lang/String;
		// access$0 (Lorg/jd/core/v1/decompile/SuperMemberAccessTest$TestPrivateMembers;)Ljava/lang/String;
		// access$1 (Lorg/jd/core/v1/decompile/SuperMemberAccessTest$TestPrivateMembers;)Ljava/lang/String;
		// access$2 (Lorg/jd/core/v1/decompile/SuperMemberAccessTest$TestPrivateMembers;)Lorg/jd/core/v1/decompile/SuperMemberAccessTest

		private String a;

		private String test() {
			return "";
		}

		public class Child extends TestPrivateMembers {

			// methods in bytecode:
			//
			// <init> (Lorg/jd/core/v1/decompile/SuperMemberAccessTest$TestPrivateMembers;)V
			// test ()I
			// doSomething ()Ljava/lang/String;

			public int a;

			public int test() {
				return 1;
			}

			String doSomething() {
				super.test(); // invokestatic access$0 :
								// (Lorg/jd/core/v1/decompile/SuperMemberAccessTest$TestPrivateMembers;)Ljava/lang/String;
				return super.a; // invokestatic access$1 :
								// (Lorg/jd/core/v1/decompile/SuperMemberAccessTest$TestPrivateMembers;)Ljava/lang/String;
			}
		}
	}

	@Test
	public void testPublicSuperMembersAccess() throws Exception {

		String internalClassName = TestPublicMembers.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "super.test();", 46);
		assertMatch(source, "return super.a;", 47);
		assertMatch(source, "test();", 51);
		assertMatch(source, "return this.a;", 52);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	// https://github.com/java-decompiler/jd-core/issues/20 // FIXME
	public void testPrivateSuperMembersAccess() throws Exception {

		String internalClassName = TestPrivateMembers.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "super.test();", 83);
		assertMatch(source, "return super.a;", 85);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
