/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.visitor;

import static org.jd.core.v1.service.converter.classfiletojavasyntax.util.ByteCodeConstants.*;

import org.jd.core.v1.model.classfile.ConstantPool;
import org.jd.core.v1.model.classfile.Method;
import org.jd.core.v1.model.classfile.attribute.AttributeCode;
import org.jd.core.v1.model.classfile.constant.ConstantMemberRef;
import org.jd.core.v1.model.classfile.constant.ConstantNameAndType;
import org.jd.core.v1.model.javasyntax.AbstractJavaSyntaxVisitor;
import org.jd.core.v1.model.javasyntax.declaration.AnnotationDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.BodyDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ClassDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.ConstructorDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.EnumDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.InterfaceDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.MethodDeclaration;
import org.jd.core.v1.model.javasyntax.declaration.StaticInitializerDeclaration;
import org.jd.core.v1.model.javasyntax.type.Type;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileBodyDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.javasyntax.declaration.ClassFileMethodDeclaration;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;

public class UpdateBridgeMethodTypeVisitor extends AbstractJavaSyntaxVisitor {
    protected TypeMaker typeMaker;

    public UpdateBridgeMethodTypeVisitor(TypeMaker typeMaker) {
        this.typeMaker = typeMaker;
    }

    @Override
    public void visit(BodyDeclaration declaration) {
        ClassFileBodyDeclaration bodyDeclaration = (ClassFileBodyDeclaration) declaration;

        safeAcceptListDeclaration(bodyDeclaration.getMethodDeclarations());
        safeAcceptListDeclaration(bodyDeclaration.getInnerTypeDeclarations());
    }

    @Override
    public void visit(MethodDeclaration declaration) {
        if (declaration.isStatic() && declaration.getReturnedType().isObjectType()
                && declaration.getName().startsWith("access$")) {
            TypeMaker.TypeTypes typeTypes = typeMaker.makeTypeTypes(declaration.getReturnedType().getInternalName());

            if ((typeTypes != null) && (typeTypes.typeParameters != null)) {
                ClassFileMethodDeclaration cfmd = (ClassFileMethodDeclaration) declaration;
                Method method = cfmd.getMethod();
                byte[] code = method.<AttributeCode>getAttribute("Code").getCode();
                int offset = 0;
                int opcode = code[offset] & 255;

                while (((ILOAD <= opcode) && (opcode <= ALOAD_3)) || // ILOAD, LLOAD, FLOAD, DLOAD, ..., ILOAD_0 ...
                        // ILOAD_3,..., ALOAD_1, ..., ALOAD_3
                        ((DUP <= opcode) && (opcode <= SWAP))) { // DUP, ..., DUP2_X2, SWAP
                    opcode = code[++offset] & 255;
                }

                switch (opcode) {
                case GETSTATIC:
                case PUTSTATIC:
                case GETFIELD:
                case PUTFIELD:
                    int index = ((code[++offset] & 255) << 8) | (code[++offset] & 255);
                    ConstantPool constants = method.getConstants();
                    ConstantMemberRef constantMemberRef = constants.getConstant(index);
                    String typeName = constants.getConstantTypeName(constantMemberRef.getClassIndex());
                    ConstantNameAndType constantNameAndType = constants
                            .getConstant(constantMemberRef.getNameAndTypeIndex());
                    String name = constants.getConstantUtf8(constantNameAndType.getNameIndex());
                    String descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                    Type type = typeMaker.makeFieldType(typeName, name, descriptor);

                    // Update returned generic type of bridge method
                    typeMaker.setMethodReturnedType(typeName, cfmd.getName(), cfmd.getDescriptor(), type);
                    break;
                case INVOKEVIRTUAL:
                case INVOKESPECIAL:
                case INVOKESTATIC:
                case INVOKEINTERFACE:
                    index = ((code[++offset] & 255) << 8) | (code[++offset] & 255);
                    constants = method.getConstants();
                    constantMemberRef = constants.getConstant(index);
                    typeName = constants.getConstantTypeName(constantMemberRef.getClassIndex());
                    constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                    name = constants.getConstantUtf8(constantNameAndType.getNameIndex());
                    descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                    TypeMaker.MethodTypes methodTypes = typeMaker.makeMethodTypes(typeName, name, descriptor);

                    // Update returned generic type of bridge method
                    typeMaker.setMethodReturnedType(typeName, cfmd.getName(), cfmd.getDescriptor(),
                            methodTypes.returnedType);
                    break;
                default:
                    break;
                }
            }
        }
    }

    @Override
    public void visit(ConstructorDeclaration declaration) {
    }

    @Override
    public void visit(StaticInitializerDeclaration declaration) {
    }

    @Override
    public void visit(ClassDeclaration declaration) {
        safeAccept(declaration.getBodyDeclaration());
    }

    @Override
    public void visit(InterfaceDeclaration declaration) {
        safeAccept(declaration.getBodyDeclaration());
    }

    @Override
    public void visit(AnnotationDeclaration declaration) {
    }

    @Override
    public void visit(EnumDeclaration declaration) {
    }
}
