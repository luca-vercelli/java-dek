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

			public int a;

			public String test() {
				return "b";
			}

			String doSomething() {
				super.test();
				return super.a;
			}
		}
	}

	class TestPrivateMembers {

		// bytecode has a constructor
		// TestClass(SuperMemberAccessTest paramSuperMemberAccessTest)
		// referencing "this" as this$0

		private String a;

		private String test() {
			return "";
		}

		public class Child extends TestPrivateMembers {

			// bytecode has a constructor
			// public Child(SuperMemberAccessTest.TestClass this$0)
			// there are 2 synthetic methods access$0 and access$1 referencing private super
			// fields

			public int a;

			public String test() {
				return "b";
			}

			String doSomething() {
				super.test(); // referenced as access$0
				return super.a; // referenced as access$1
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

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}

	@Test
	// https://github.com/java-decompiler/jd-core/issues/20 // FIXME
	public void testPrivateSuperMembersAccess() throws Exception {

		String internalClassName = TestPublicMembers.class.getName().replace('.', '/');
		String source = decompiler.decompile(internalClassName);

		// Check decompiled source code
		assertMatch(source, "super.test();", 78);
		assertMatch(source, "return super.a;", 79);

		// Recompile decompiled source code and check errors
		assertTrue(CompilerUtil.compile("1.8", new JavaSourceFileObject(internalClassName, source)));
	}
}
