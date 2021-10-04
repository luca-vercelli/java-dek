/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.classfile.constant;

import org.jd.core.v1.model.classfile.ConstantPool;

public class ConstantMethodHandle extends Constant {
	protected int referenceKind;
	protected int referenceIndex;

	public ConstantMethodHandle(int referenceKind, int referenceIndex) {
		super(ConstantPoolTag.CONSTANT_MethodHandle);
		this.referenceKind = referenceKind;
		this.referenceIndex = referenceIndex;
	}

	public int getReferenceKind() {
		return referenceKind;
	}

	public int getReferenceIndex() {
		return referenceIndex;
	}

	public String getTypeName(ConstantPool constants) {
		ConstantMemberRef cmr = constants.getConstant(referenceIndex);
		return cmr.getTypeName(constants);
	}

	public String getName(ConstantPool constants) {
		ConstantMemberRef cmr = constants.getConstant(referenceIndex);
		return cmr.getName(constants);
	}

	public String getDescriptor(ConstantPool constants) {
		ConstantMemberRef cmr = constants.getConstant(referenceIndex);
		return cmr.getDescriptor(constants);
	}
}
