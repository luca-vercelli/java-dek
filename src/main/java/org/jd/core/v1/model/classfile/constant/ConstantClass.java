/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.classfile.constant;

import org.jd.core.v1.model.classfile.ConstantPool;

public class ConstantClass extends Constant {
    protected int nameIndex;

    public ConstantClass(ConstantPoolTag tag, int nameIndex) {
        super(tag);
        this.nameIndex = nameIndex;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public String getName(ConstantPool constants) {
        return constants.getConstantUtf8(nameIndex);
    }
}
