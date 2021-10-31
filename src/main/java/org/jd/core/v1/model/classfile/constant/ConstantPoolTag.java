package org.jd.core.v1.model.classfile.constant;

/**
 * @see https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.4
 */
public enum ConstantPoolTag {
    CONSTANT_Utf8(1), //
    CONSTANT_Integer(3), //
    CONSTANT_Float(4), //
    CONSTANT_Long(5), //
    CONSTANT_Double(6), //
    CONSTANT_Class(7), //
    CONSTANT_String(8), //
    CONSTANT_FieldRef(9), //
    CONSTANT_MethodRef(10), //
    CONSTANT_InterfaceMethodRef(11), //
    CONSTANT_NameAndType(12), //
    CONSTANT_MethodHandle(15), //
    CONSTANT_MethodType(16), //
    CONSTANT_Dynamic(17), //
    CONSTANT_InvokeDynamic(18), //
    CONSTANT_Module(19), //
    CONSTANT_Package(20), //
    ;

    protected byte tag;
    private static final ConstantPoolTag[] array = new ConstantPoolTag[21];

    static {
        for (int i = 0; i < array.length; ++i) {
            array[i] = null;
        }
        for (ConstantPoolTag constant : ConstantPoolTag.values()) {
            array[constant.tag] = constant;
        }
    }

    ConstantPoolTag(int tag) {
        this.tag = (byte) tag;
    }

    public byte getTag() {
        return tag;
    }

    public static ConstantPoolTag valueOf(byte readByte) {
        return array[readByte];
    }
}