/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.deserializer.classfile;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.classfile.ClassFile;
import org.jd.core.v1.model.message.Message;

/**
 * Create a ClassFile model from a loader and a internal type name.<br><br>
 *
 * Input:  -<br>
 * Output: {@link org.jd.core.v1.model.classfile.ClassFile}<br>
 */
public class DeserializeClassFileProcessor extends ClassFileDeserializer implements Processor {

    @Override
    public void process(Message message) throws Exception {
        Loader loader = message.getLoader();
        String internalTypeName = message.getMainInternalTypeName();
		ClassFile classFile = loadClassFile(loader, internalTypeName);

        message.setClassFile(classFile);
    }
}
