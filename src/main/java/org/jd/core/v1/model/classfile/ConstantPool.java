/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.classfile;

import org.jd.core.v1.model.classfile.constant.Constant;
import org.jd.core.v1.model.classfile.constant.ConstantClass;
import org.jd.core.v1.model.classfile.constant.ConstantPoolTag;
import org.jd.core.v1.model.classfile.constant.ConstantString;
import org.jd.core.v1.model.classfile.constant.ConstantUtf8;
import org.jd.core.v1.model.classfile.constant.ConstantValue;

/**
 * Store constant value. This table is stored at the beginning of the class
 * file.
 */
public class ConstantPool {
    protected Constant[] constants;

    public ConstantPool(Constant[] constants) {
        this.constants = constants;
    }

    @SuppressWarnings("unchecked")
    public <T extends Constant> T getConstant(int index) {
        return (T) constants[index];
    }

    public String getConstantTypeName(int index) {
        ConstantClass cc = (ConstantClass) constants[index];
        ConstantUtf8 cutf8 = (ConstantUtf8) constants[cc.getNameIndex()];
        return cutf8.getValue();
    }

    public String getConstantString(int index) {
        ConstantString constString = (ConstantString) constants[index];
        ConstantUtf8 cutf8 = (ConstantUtf8) constants[constString.getStringIndex()];
        return cutf8.getValue();
    }

    public String getConstantUtf8(int index) {
        ConstantUtf8 cutf8 = (ConstantUtf8) constants[index];
        return cutf8.getValue();
    }

    public ConstantValue getConstantValue(int index) {
        Constant constant = constants[index];

        if ((constant != null) && (constant.getTag() == ConstantPoolTag.CONSTANT_String)) {
            constant = constants[((ConstantString) constant).getStringIndex()];
        }

        return (ConstantValue) constant;
    }

    @Override
    public String toString() {
        return "ConstantPool";
    }
}
