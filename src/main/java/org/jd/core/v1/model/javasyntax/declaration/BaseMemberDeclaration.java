/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.model.javasyntax.declaration;

import org.jd.core.v1.util.Base;

public interface BaseMemberDeclaration extends Declaration, Base<MemberDeclaration> {

    /**
     * This is only true in a ClassDeclaration... uhm...
     */
    default boolean isClassDeclaration() {
        return false;
    }
}
