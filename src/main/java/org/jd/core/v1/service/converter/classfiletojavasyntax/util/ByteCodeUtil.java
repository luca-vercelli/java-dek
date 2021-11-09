/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.util;

import static org.jd.core.v1.service.converter.classfiletojavasyntax.util.ByteCodeConstants.*;

import org.jd.core.v1.model.classfile.ConstantPool;
import org.jd.core.v1.model.classfile.Method;
import org.jd.core.v1.model.classfile.attribute.AttributeCode;
import org.jd.core.v1.model.classfile.constant.ConstantMemberRef;
import org.jd.core.v1.model.classfile.constant.ConstantNameAndType;
import org.jd.core.v1.service.converter.classfiletojavasyntax.model.cfg.BasicBlock;

public class ByteCodeUtil {

    public static int searchNextOpcode(BasicBlock basicBlock, int maxOffset) {
        byte[] code = basicBlock.getControlFlowGraph().getMethod().<AttributeCode>getAttribute("Code").getCode();
        int offset = basicBlock.getFromOffset();
        int toOffset = basicBlock.getToOffset();

        if (toOffset > maxOffset) {
            toOffset = maxOffset;
        }

        for (; offset < toOffset; offset++) {
            int opcode = code[offset] & MASK;

            switch (opcode) {
            case BIPUSH:
            case LDC:
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
            case RET:
            case NEWARRAY:
                offset++;
                break;
            case SIPUSH:
            case LDC_W:
            case LDC2_W:
            case IINC:
            case GETSTATIC:
            case PUTSTATIC:
            case NEW:
            case GETFIELD:
            case PUTFIELD:
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case ANEWARRAY:
            case CHECKCAST:
            case INSTANCEOF:
                offset += 2;
                break;
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case GOTO:
            case IFNULL:
            case IFNONNULL:
                int deltaOffset = (short) (((code[++offset] & MASK) << 8) | (code[++offset] & MASK));

                if (deltaOffset > 0) {
                    offset += deltaOffset - 2 - 1;
                }
                break;
            case GOTO_W:
                deltaOffset = (((code[++offset] & MASK) << 24) | ((code[++offset] & MASK) << 16)
                        | ((code[++offset] & MASK) << 8) | (code[++offset] & MASK));

                if (deltaOffset > 0) {
                    offset += deltaOffset - 4 - 1;
                }
                break;
            case JSR:
                offset += 2;
                break;
            case MULTIANEWARRAY:
                offset += 3;
                break;
            case INVOKEINTERFACE:
            case INVOKEDYNAMIC:
                offset += 4;
                break;
            case JSR_W:
                offset += 4;
                break;
            case TABLESWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int low = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);
                int high = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (4 * (high - low + 1)) - 1;
                break;
            case LOOKUPSWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int count = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (8 * count) - 1;
                break;
            case WIDE:
                opcode = code[++offset] & MASK;

                if (opcode == IINC) {
                    offset += 4;
                } else {
                    offset += 2;
                }
                break;
            default:
                break;
            }
        }

        if (offset <= maxOffset) {
            return code[offset] & MASK;
        } else {
            return 0;
        }
    }

    public static int getLastOpcode(BasicBlock basicBlock) {
        byte[] code = basicBlock.getControlFlowGraph().getMethod().<AttributeCode>getAttribute("Code").getCode();
        int offset = basicBlock.getFromOffset();
        int toOffset = basicBlock.getToOffset();

        if (offset >= toOffset) {
            return 0;
        }

        int lastOffset = offset;

        for (; offset < toOffset; offset++) {
            int opcode = code[offset] & MASK;

            lastOffset = offset;

            switch (opcode) {
            case BIPUSH:
            case LDC:
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
            case RET:
            case NEWARRAY:
                offset++;
                break;
            case SIPUSH:
            case LDC_W:
            case LDC2_W:
            case IINC:
            case GETSTATIC:
            case PUTSTATIC:
            case NEW:
            case GETFIELD:
            case PUTFIELD:
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case ANEWARRAY:
            case CHECKCAST:
            case INSTANCEOF:
                offset += 2;
                break;
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case GOTO:
            case IFNULL:
            case IFNONNULL:
                int deltaOffset = (short) (((code[++offset] & MASK) << 8) | (code[++offset] & MASK));

                if (deltaOffset > 0) {
                    offset += deltaOffset - 2 - 1;
                }
                break;
            case GOTO_W:
                deltaOffset = (((code[++offset] & MASK) << 24) | ((code[++offset] & MASK) << 16)
                        | ((code[++offset] & MASK) << 8) | (code[++offset] & MASK));

                if (deltaOffset > 0) {
                    offset += deltaOffset - 4 - 1;
                }
                break;
            case JSR:
                offset += 2;
                break;
            case MULTIANEWARRAY:
                offset += 3;
                break;
            case INVOKEINTERFACE:
            case INVOKEDYNAMIC:
                offset += 4;
                break;
            case JSR_W:
                offset += 4;
                break;
            case TABLESWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int low = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);
                int high = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (4 * (high - low + 1)) - 1;
                break;
            case LOOKUPSWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int count = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (8 * count) - 1;
                break;
            case WIDE:
                opcode = code[++offset] & MASK;

                if (opcode == IINC) {
                    offset += 4;
                } else {
                    offset += 2;
                }
                break;
            default:
                break;
            }
        }

        return code[lastOffset] & MASK;
    }

    public static int evalStackDepth(BasicBlock bb) {
        Method method = bb.getControlFlowGraph().getMethod();
        ConstantPool constants = method.getConstants();
        AttributeCode attributeCode = method.getAttribute("Code");
        byte[] code = attributeCode.getCode();
        return evalStackDepth(constants, code, bb);
    }

    public static int evalStackDepth(ConstantPool constants, byte[] code, BasicBlock bb) {
        ConstantMemberRef constantMemberRef;
        ConstantNameAndType constantNameAndType;
        String descriptor;
        int depth = 0;

        for (int offset = bb.getFromOffset(), toOffset = bb.getToOffset(); offset < toOffset; offset++) {
            int opcode = code[offset] & MASK;

            switch (opcode) {
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case LCONST_0:
            case LCONST_1:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case DCONST_0:
            case DCONST_1:
            case ILOAD_0:
            case ILOAD_1:
            case ILOAD_2:
            case ILOAD_3:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:
            case DUP:
            case DUP_X1:
            case DUP_X2:
                depth++;
                break;
            case BIPUSH:
            case LDC:
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
                offset++;
                depth++;
                break;
            case SIPUSH:
            case LDC_W:
            case LDC2_W:
            case JSR:
            case GETSTATIC:
            case NEW:
                offset += 2;
                depth++;
                break;
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case ISTORE_0:
            case ISTORE_1:
            case ISTORE_2:
            case ISTORE_3:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
            case POP:
            case IADD:
            case LADD:
            case FADD:
            case DADD:
            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
            case IREM:
            case LREM:
            case FREM:
            case DREM:
            case ISHL:
            case LSHL:
            case ISHR:
            case LSHR:
            case IUSHR:
            case LUSHR:
            case IAND:
            case LAND:
            case IOR:
            case LOR:
            case IXOR:
            case LXOR:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case MONITORENTER:
            case MONITOREXIT:
                depth--;
                break;
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case PUTSTATIC:
            case IFNULL:
            case IFNONNULL:
                offset += 2;
                depth--;
                break;
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
                offset++;
                depth--;
                break;
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                depth -= 3;
                break;
            case DUP2:
            case DUP2_X1:
            case DUP2_X2:
                depth += 2;
                break;
            case IINC:
            case GOTO:
            case GETFIELD:
            case ANEWARRAY:
            case CHECKCAST:
            case INSTANCEOF:
                offset += 2;
                break;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case PUTFIELD:
                offset += 2;
                depth -= 2;
                break;
            case POP2:
                depth -= 2;
                break;
            case RET:
            case NEWARRAY:
                offset++;
                break;
            case TABLESWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int low = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);
                int high = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (4 * (high - low + 1)) - 1;
                depth--;
                break;
            case LOOKUPSWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int count = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (8 * count) - 1;
                depth--;
                break;
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= 1 + countMethodParameters(descriptor);

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case INVOKESTATIC:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= countMethodParameters(descriptor);

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case INVOKEINTERFACE:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= 1 + countMethodParameters(descriptor);
                offset += 2; // Skip 'count' and one byte

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case INVOKEDYNAMIC:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= countMethodParameters(descriptor);
                offset += 2; // Skip 2 bytes

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case WIDE:
                opcode = code[++offset] & MASK;

                if (opcode == IINC) {
                    offset += 4;
                } else {
                    offset += 2;

                    switch (opcode) {
                    case ILOAD:
                    case LLOAD:
                    case FLOAD:
                    case DLOAD:
                    case ALOAD:
                        depth++;
                        break;
                    case ISTORE:
                    case LSTORE:
                    case FSTORE:
                    case DSTORE:
                    case ASTORE:
                        depth--;
                        break;
                    case RET:
                        break;
                    default:
                        break;
                    }
                }
                break;
            case MULTIANEWARRAY:
                offset += 3;
                depth += 1 - (code[offset] & MASK);
                break;
            case JSR_W:
                offset += 4;
                depth++;
            case GOTO_W:
                offset += 4;
                break;
            default:
                break;
            }
        }

        return depth;
    }

    public static int getMinDepth(BasicBlock bb) {
        Method method = bb.getControlFlowGraph().getMethod();
        ConstantPool constants = method.getConstants();
        AttributeCode attributeCode = method.getAttribute("Code");
        byte[] code = attributeCode.getCode();
        return getMinDepth(constants, code, bb);
    }

    private static int getMinDepth(ConstantPool constants, byte[] code, BasicBlock bb) {
        ConstantMemberRef constantMemberRef;
        ConstantNameAndType constantNameAndType;
        String descriptor;
        int depth = 0;
        int minDepth = 0;

        for (int offset = bb.getFromOffset(), toOffset = bb.getToOffset(); offset < toOffset; offset++) {
            int opcode = code[offset] & MASK;

            switch (opcode) {
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case LCONST_0:
            case LCONST_1:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case DCONST_0:
            case DCONST_1:
            case ILOAD_0:
            case ILOAD_1:
            case ILOAD_2:
            case ILOAD_3:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:
                depth++;
                break;
            case DUP:
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth += 2;
                break;
            case DUP_X1:
                depth -= 2;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth += 3;
                break;
            case DUP_X2:
                depth -= 3;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth += 4;
                break;
            case BIPUSH:
            case LDC:
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
                offset++;
                depth++;
                break;
            case SIPUSH:
            case LDC_W:
            case LDC2_W:
            case JSR:
            case GETSTATIC:
            case NEW:
                offset += 2;
                depth++;
                break;
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case IADD:
            case LADD:
            case FADD:
            case DADD:
            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
            case IREM:
            case LREM:
            case FREM:
            case DREM:
            case ISHL:
            case LSHL:
            case ISHR:
            case LSHR:
            case IUSHR:
            case LUSHR:
            case IAND:
            case LAND:
            case IOR:
            case LOR:
            case IXOR:
            case LXOR:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
                depth -= 2;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth++;
                break;
            case ISTORE_0:
            case ISTORE_1:
            case ISTORE_2:
            case ISTORE_3:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
            case POP:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case MONITORENTER:
            case MONITOREXIT:
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case PUTSTATIC:
            case IFNULL:
            case IFNONNULL:
                offset += 2;
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
                offset++;
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                depth -= 3;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case DUP2:
                depth -= 2;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth += 4;
                break;
            case DUP2_X1:
                depth -= 3;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth += 5;
                break;
            case DUP2_X2:
                depth -= 4;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth += 6;
                break;
            case IINC:
            case GOTO:
                offset += 2;
                break;
            case GETFIELD:
            case ANEWARRAY:
            case CHECKCAST:
            case INSTANCEOF:
                offset += 2;
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth++;
                break;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case PUTFIELD:
                offset += 2;
                depth -= 2;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case POP2:
                depth -= 2;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case RET:
                offset++;
                break;
            case NEWARRAY:
                offset++;
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth++;
                break;
            case TABLESWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int low = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);
                int high = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (4 * (high - low + 1)) - 1;
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case LOOKUPSWITCH:
                offset = (offset + 4) & 0xFFFC; // Skip padding
                offset += 4; // Skip default offset

                int count = ((code[offset++] & MASK) << 24) | ((code[offset++] & MASK) << 16)
                        | ((code[offset++] & MASK) << 8) | (code[offset++] & MASK);

                offset += (8 * count) - 1;
                depth--;
                if (minDepth > depth) {
                    minDepth = depth;
                }
                break;
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= 1 + countMethodParameters(descriptor);
                if (minDepth > depth) {
                    minDepth = depth;
                }

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case INVOKESTATIC:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= countMethodParameters(descriptor);
                if (minDepth > depth) {
                    minDepth = depth;
                }

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case INVOKEINTERFACE:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= 1 + countMethodParameters(descriptor);
                if (minDepth > depth) {
                    minDepth = depth;
                }
                offset += 2; // Skip 'count' and one byte

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case INVOKEDYNAMIC:
                constantMemberRef = constants.getConstant(((code[++offset] & MASK) << 8) | (code[++offset] & MASK));
                constantNameAndType = constants.getConstant(constantMemberRef.getNameAndTypeIndex());
                descriptor = constants.getConstantUtf8(constantNameAndType.getDescriptorIndex());
                depth -= countMethodParameters(descriptor);
                if (minDepth > depth) {
                    minDepth = depth;
                }
                offset += 2; // Skip 2 bytes

                if (descriptor.charAt(descriptor.length() - 1) != 'V') {
                    depth++;
                }
                break;
            case WIDE:
                opcode = code[++offset] & MASK;

                if (opcode == IINC) {
                    offset += 4;
                } else {
                    offset += 2;

                    switch (opcode) {
                    case ILOAD:
                    case LLOAD:
                    case FLOAD:
                    case DLOAD:
                    case ALOAD:
                        depth++;
                        break;
                    case ISTORE:
                    case LSTORE:
                    case FSTORE:
                    case DSTORE:
                    case ASTORE:
                        depth--;
                        if (minDepth > depth) {
                            minDepth = depth;
                        }
                        break;
                    case RET:
                        break;
                    default:
                        break;
                    }
                }
                break;
            case MULTIANEWARRAY:
                offset += 3;
                depth -= (code[offset] & MASK);
                if (minDepth > depth) {
                    minDepth = depth;
                }
                depth++;
                break;
            case JSR_W:
                offset += 4;
                depth++;
            case GOTO_W:
                offset += 4;
                break;
            default:
                break;
            }
        }

        return minDepth;
    }

    private static int countMethodParameters(String descriptor) {
        int count = 0;
        int i = 2;
        char c = descriptor.charAt(1);

        assert (descriptor.length() > 2) && (descriptor.charAt(0) == '(');

        while (c != ')') {
            while (c == '[') {
                c = descriptor.charAt(i++);
            }
            if (c == 'L') {
                do {
                    c = descriptor.charAt(i++);
                } while (c != ';');
            }
            c = descriptor.charAt(i++);
            count++;
        }

        return count;
    }
}
