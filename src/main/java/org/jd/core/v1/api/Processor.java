/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.api;

import java.io.IOException;

import org.jd.core.v1.model.message.Message;

public interface Processor {

    /**
     * Perform an elaboration phase on given Message
     * 
     * @param message
     * @throws IOException
     */
    public void process(Message message) throws IOException;
}
