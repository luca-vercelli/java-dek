/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.classfile.constant;

import org.jd.core.v1.model.classfile.ConstantPool;

public class ConstantMethodType extends Constant {
    protected int descriptorIndex;

    public ConstantMethodType(int descriptorIndex) {
        super(ConstantPoolTag.CONSTANT_MethodType);
        this.descriptorIndex = descriptorIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public String getDescriptor(ConstantPool constants) {
        return constants.getConstantUtf8(descriptorIndex);
    }
}
