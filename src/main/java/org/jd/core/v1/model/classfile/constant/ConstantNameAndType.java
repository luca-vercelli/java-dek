/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.classfile.constant;

import org.jd.core.v1.model.classfile.ConstantPool;

public class ConstantNameAndType extends Constant {
    protected int nameIndex;
    protected int descriptorIndex;

    public ConstantNameAndType(int nameIndex, int descriptorIndex) {
        super(ConstantPoolTag.CONSTANT_NameAndType);
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public String getName(ConstantPool constants) {
        return constants.getConstantUtf8(nameIndex);
    }

    public String getDescriptor(ConstantPool constants) {
        return constants.getConstantUtf8(descriptorIndex);
    }
}
