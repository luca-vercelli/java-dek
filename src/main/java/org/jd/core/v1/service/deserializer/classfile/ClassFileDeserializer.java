/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.deserializer.classfile;

import static org.jd.core.v1.model.classfile.AccessType.ACC_SYNTHETIC;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jd.core.v1.api.Loader;
import org.jd.core.v1.api.Processor;
import org.jd.core.v1.model.classfile.ClassFile;
import org.jd.core.v1.model.classfile.ConstantPool;
import org.jd.core.v1.model.classfile.Field;
import org.jd.core.v1.model.classfile.Method;
import org.jd.core.v1.model.classfile.attribute.Annotation;
import org.jd.core.v1.model.classfile.attribute.Annotations;
import org.jd.core.v1.model.classfile.attribute.Attribute;
import org.jd.core.v1.model.classfile.attribute.AttributeAnnotationDefault;
import org.jd.core.v1.model.classfile.attribute.AttributeBootstrapMethods;
import org.jd.core.v1.model.classfile.attribute.AttributeCode;
import org.jd.core.v1.model.classfile.attribute.AttributeConstantValue;
import org.jd.core.v1.model.classfile.attribute.AttributeDeprecated;
import org.jd.core.v1.model.classfile.attribute.AttributeExceptions;
import org.jd.core.v1.model.classfile.attribute.AttributeInnerClasses;
import org.jd.core.v1.model.classfile.attribute.AttributeLineNumberTable;
import org.jd.core.v1.model.classfile.attribute.AttributeLocalVariableTable;
import org.jd.core.v1.model.classfile.attribute.AttributeLocalVariableTypeTable;
import org.jd.core.v1.model.classfile.attribute.AttributeMethodParameters;
import org.jd.core.v1.model.classfile.attribute.AttributeModule;
import org.jd.core.v1.model.classfile.attribute.AttributeModuleMainClass;
import org.jd.core.v1.model.classfile.attribute.AttributeModulePackages;
import org.jd.core.v1.model.classfile.attribute.AttributeParameterAnnotations;
import org.jd.core.v1.model.classfile.attribute.AttributeSignature;
import org.jd.core.v1.model.classfile.attribute.AttributeSourceFile;
import org.jd.core.v1.model.classfile.attribute.AttributeSynthetic;
import org.jd.core.v1.model.classfile.attribute.AttributeType;
import org.jd.core.v1.model.classfile.attribute.BootstrapMethod;
import org.jd.core.v1.model.classfile.attribute.CodeException;
import org.jd.core.v1.model.classfile.attribute.InnerClass;
import org.jd.core.v1.model.classfile.attribute.LineNumber;
import org.jd.core.v1.model.classfile.attribute.LocalVariable;
import org.jd.core.v1.model.classfile.attribute.LocalVariableType;
import org.jd.core.v1.model.classfile.attribute.MethodParameter;
import org.jd.core.v1.model.classfile.attribute.ModuleInfo;
import org.jd.core.v1.model.classfile.attribute.PackageInfo;
import org.jd.core.v1.model.classfile.attribute.ServiceInfo;
import org.jd.core.v1.model.classfile.attribute.UnknownAttribute;
import org.jd.core.v1.model.classfile.attribute.elementValue.ElementValue;
import org.jd.core.v1.model.classfile.attribute.elementValue.ElementValueAnnotationValue;
import org.jd.core.v1.model.classfile.attribute.elementValue.ElementValueArrayValue;
import org.jd.core.v1.model.classfile.attribute.elementValue.ElementValueClassInfo;
import org.jd.core.v1.model.classfile.attribute.elementValue.ElementValueEnumConstValue;
import org.jd.core.v1.model.classfile.attribute.elementValue.ElementValuePair;
import org.jd.core.v1.model.classfile.attribute.elementValue.ElementValuePrimitiveType;
import org.jd.core.v1.model.classfile.constant.Constant;
import org.jd.core.v1.model.classfile.constant.ConstantClass;
import org.jd.core.v1.model.classfile.constant.ConstantDouble;
import org.jd.core.v1.model.classfile.constant.ConstantFloat;
import org.jd.core.v1.model.classfile.constant.ConstantInteger;
import org.jd.core.v1.model.classfile.constant.ConstantLong;
import org.jd.core.v1.model.classfile.constant.ConstantMemberRef;
import org.jd.core.v1.model.classfile.constant.ConstantMethodHandle;
import org.jd.core.v1.model.classfile.constant.ConstantMethodType;
import org.jd.core.v1.model.classfile.constant.ConstantNameAndType;
import org.jd.core.v1.model.classfile.constant.ConstantPoolTag;
import org.jd.core.v1.model.classfile.constant.ConstantString;
import org.jd.core.v1.model.classfile.constant.ConstantUtf8;
import org.jd.core.v1.model.classfile.constant.ConstantValue;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.util.DefaultList;

/**
 * Read a class file into a ClassFile object.
 * 
 * Lower level methods are in ClassFileReader.
 */
public class ClassFileDeserializer implements Processor {
    protected static final int[] EMPTY_INT_ARRAY = new int[0];

    public ClassFile loadClassFile(Loader loader, String internalTypeName) throws IOException {
        ClassFile classFile = innerLoadClassFile(loader, internalTypeName);

        if (classFile == null) {
            throw new IllegalArgumentException("Class '" + internalTypeName + "' could not be loaded");
        } else {
            return classFile;
        }
    }

    /**
     * Load main type (not inner types)
     * 
     * @param data
     * @return
     * @throws UTFDataFormatException
     */
    protected ClassFile loadClassFile(byte[] data) throws UTFDataFormatException {

        ClassFileReader reader = new ClassFileReader(data);

        int magic = reader.readInt();

        if (magic != ClassFileReader.JAVA_MAGIC_NUMBER) {
            throw new ClassFileFormatException("Invalid CLASS file");
        }

        int minorVersion = reader.readUnsignedShort();
        int majorVersion = reader.readUnsignedShort();

        ConstantPool constants = new ConstantPool(loadConstants(reader));

        int accessFlags = reader.readUnsignedShort();
        int thisClassIndex = reader.readUnsignedShort();
        int superClassIndex = reader.readUnsignedShort();

        String internalTypeName = constants.getConstantTypeName(thisClassIndex);
        String superTypeName = (superClassIndex == 0) ? null : constants.getConstantTypeName(superClassIndex);
        String[] interfaceTypeNames = loadInterfaces(reader, constants);
        Field[] fields = loadFields(reader, constants);
        Method[] methods = loadMethods(reader, constants);
        Map<String, Attribute> attributes = loadAttributes(reader, constants);

        return new ClassFile(majorVersion, minorVersion, accessFlags, internalTypeName, superTypeName,
                interfaceTypeNames, fields, methods, attributes);
    }

    protected ClassFile innerLoadClassFile(Loader loader, String internalTypeName) throws IOException {
        if (!loader.canLoad(internalTypeName)) {
            return null;
        }

        byte[] data = loader.load(internalTypeName);

        if (data == null) {
            return null;
        }

        // Load main type
        ClassFile classFile = loadClassFile(data);

        // Load inner types
        AttributeInnerClasses aic = classFile.getAttribute("InnerClasses");

        if (aic != null) {
            List<ClassFile> innerClassFiles = new DefaultList<>();
            String innerTypePrefix = internalTypeName + '$';

            for (InnerClass ic : aic.getInnerClasses()) {
                String innerTypeName = ic.getInnerTypeName();

                if (!internalTypeName.equals(innerTypeName)) {
                    if (internalTypeName.equals(ic.getOuterTypeName()) || innerTypeName.startsWith(innerTypePrefix)) {
                        ClassFile innerClassFile = innerLoadClassFile(loader, innerTypeName);
                        int flags = ic.getInnerAccessFlags();
                        int length;

                        if (innerTypeName.startsWith(innerTypePrefix)) {
                            length = internalTypeName.length() + 1;
                        } else {
                            length = innerTypeName.indexOf('$') + 1;
                        }

                        if (Character.isDigit(innerTypeName.charAt(length))) {
                            flags |= ACC_SYNTHETIC;
                        }

                        if (innerClassFile == null) {
                            // Inner class not found. Create an empty one.
                            innerClassFile = new ClassFile(classFile.getMajorVersion(), classFile.getMinorVersion(), 0,
                                    innerTypeName, "java/lang/Object", null, null, null, null);
                        }

                        innerClassFile.setOuterClassFile(classFile);
                        innerClassFile.setAccessFlags(flags);
                        innerClassFiles.add(innerClassFile);
                    }
                }
            }

            if (!innerClassFiles.isEmpty()) {
                classFile.setInnerClassFiles(innerClassFiles);
            }
        }

        return classFile;
    }

    /**
     * Given a single class file, retrieve main type name
     * 
     * @param data
     * @return
     * @throws UTFDataFormatException
     */
    public String getMainTypeName(byte[] data) throws UTFDataFormatException {
        ClassFile classFile = loadClassFile(data);
        return classFile.getInternalTypeName();
    }

    protected Constant[] loadConstants(ClassFileReader reader) throws UTFDataFormatException {
        int count = reader.readUnsignedShort();

        if (count == 0) {
            return null;
        }

        Constant[] constants = new Constant[count];

        for (int i = 1; i < count; i++) {
            ConstantPoolTag tag = ConstantPoolTag.valueOf(reader.readByte());

            switch (tag) {
            case CONSTANT_Utf8:
                constants[i] = new ConstantUtf8(reader.readUTF8());
                break;
            case CONSTANT_Integer:
                constants[i] = new ConstantInteger(reader.readInt());
                break;
            case CONSTANT_Float:
                constants[i] = new ConstantFloat(reader.readFloat());
                break;
            case CONSTANT_Long:
                constants[i++] = new ConstantLong(reader.readLong());
                break;
            case CONSTANT_Double:
                constants[i++] = new ConstantDouble(reader.readDouble());
                break;
            case CONSTANT_Class:
            case CONSTANT_Module:
            case CONSTANT_Package:
                constants[i] = new ConstantClass(tag, reader.readUnsignedShort());
                break;
            case CONSTANT_String:
                constants[i] = new ConstantString(reader.readUnsignedShort());
                break;
            case CONSTANT_FieldRef:
            case CONSTANT_MethodRef:
            case CONSTANT_InterfaceMethodRef:
            case CONSTANT_Dynamic:
            case CONSTANT_InvokeDynamic:
                constants[i] = new ConstantMemberRef(tag, reader.readUnsignedShort(), reader.readUnsignedShort());
                break;
            case CONSTANT_NameAndType:
                constants[i] = new ConstantNameAndType(reader.readUnsignedShort(), reader.readUnsignedShort());
                break;
            case CONSTANT_MethodHandle:
                constants[i] = new ConstantMethodHandle(reader.readByte(), reader.readUnsignedShort());
                break;
            case CONSTANT_MethodType:
                constants[i] = new ConstantMethodType(reader.readUnsignedShort());
                break;
            default:
                throw new ClassFileFormatException("Invalid constant pool entry: " + tag);
            }
        }

        return constants;
    }

    protected String[] loadInterfaces(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        String[] interfaceTypeNames = new String[count];

        for (int i = 0; i < count; i++) {
            int index = reader.readUnsignedShort();
            interfaceTypeNames[i] = constants.getConstantTypeName(index);
        }

        return interfaceTypeNames;
    }

    protected Field[] loadFields(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        Field[] fields = new Field[count];

        for (int i = 0; i < count; i++) {
            int accessFlags = reader.readUnsignedShort();
            int nameIndex = reader.readUnsignedShort();
            int descriptorIndex = reader.readUnsignedShort();
            Map<String, Attribute> attributes = loadAttributes(reader, constants);

            String name = constants.getConstantUtf8(nameIndex);
            String descriptor = constants.getConstantUtf8(descriptorIndex);

            fields[i] = new Field(accessFlags, name, descriptor, attributes);
        }

        return fields;
    }

    protected Method[] loadMethods(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }
        Method[] methods = new Method[count];

        for (int i = 0; i < count; i++) {
            int accessFlags = reader.readUnsignedShort();
            int nameIndex = reader.readUnsignedShort();
            int descriptorIndex = reader.readUnsignedShort();
            Map<String, Attribute> attributes = loadAttributes(reader, constants);

            String name = constants.getConstantUtf8(nameIndex);
            String descriptor = constants.getConstantUtf8(descriptorIndex);

            methods[i] = new Method(accessFlags, name, descriptor, attributes, constants);
        }

        return methods;
    }

    // Warning: we are assuming 1 attribute only of each type
    protected Map<String, Attribute> loadAttributes(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        Map<String, Attribute> attributes = new HashMap<>();

        for (int i = 0; i < count; i++) {
            int attributeNameIndex = reader.readUnsignedShort();
            int attributeLength = reader.readInt();

            Constant constant = constants.getConstant(attributeNameIndex);

            int offsetBefore = reader.offset;
            if (constant.getTag() == ConstantPoolTag.CONSTANT_Utf8) {
                String name = ((ConstantUtf8) constant).getValue();
                AttributeType type;
                try {
                    type = AttributeType.valueOf(name);
                } catch (IllegalArgumentException exc) {
                    System.err.println("Unknown attribute: " + name);
                    attributes.put(name, new UnknownAttribute());
                    reader.skip(attributeLength);
                    continue;
                }

                switch (type) {
                case AnnotationDefault:
                    attributes.put(name, new AttributeAnnotationDefault(loadElementValue(reader, constants)));
                    break;
                case BootstrapMethods:
                    attributes.put(name, new AttributeBootstrapMethods(loadBootstrapMethods(reader)));
                    break;
                case Code:
                    attributes.put(name, new AttributeCode(reader.readUnsignedShort(), reader.readUnsignedShort(),
                            loadCode(reader), loadCodeExceptions(reader), loadAttributes(reader, constants)));
                    break;
                case ConstantValue:
                    if (attributeLength < 2) {
                        throw new ClassFileFormatException("Invalid attribute length");
                    }
                    attributes.put(name, new AttributeConstantValue(loadConstantValue(reader, constants)));
                    break;
                case Deprecated:
                    attributes.put(name, new AttributeDeprecated());
                    break;
                case Exceptions:
                    attributes.put(name, new AttributeExceptions(loadExceptionTypeNames(reader, constants)));
                    break;
                case InnerClasses:
                    attributes.put(name, new AttributeInnerClasses(loadInnerClasses(reader, constants)));
                    break;
                case LocalVariableTable:
                    LocalVariable[] localVariables = loadLocalVariables(reader, constants);
                    if (localVariables != null) {
                        attributes.put(name, new AttributeLocalVariableTable(localVariables));
                    }
                    break;
                case LocalVariableTypeTable:
                    attributes.put(name,
                            new AttributeLocalVariableTypeTable(loadLocalVariableTypes(reader, constants)));
                    break;
                case LineNumberTable:
                    attributes.put(name, new AttributeLineNumberTable(loadLineNumbers(reader)));
                    break;
                case MethodParameters:
                    attributes.put(name, new AttributeMethodParameters(loadParameters(reader, constants)));
                    break;
                case Module:
                    attributes.put(name,
                            new AttributeModule(constants.getConstantTypeName(reader.readUnsignedShort()),
                                    reader.readUnsignedShort(), constants.getConstantUtf8(reader.readUnsignedShort()),
                                    loadModuleInfos(reader, constants), loadPackageInfos(reader, constants),
                                    loadPackageInfos(reader, constants), loadConstantClassNames(reader, constants),
                                    loadServiceInfos(reader, constants)));
                    break;
                case ModulePackages:
                    attributes.put(name, new AttributeModulePackages(loadConstantClassNames(reader, constants)));
                    break;
                case ModuleMainClass:
                    attributes.put(name,
                            new AttributeModuleMainClass(constants.getConstant(reader.readUnsignedShort())));
                    break;
                case RuntimeInvisibleAnnotations:
                case RuntimeVisibleAnnotations:
                    Annotation[] annotations = loadAnnotations(reader, constants);
                    if (annotations != null) {
                        attributes.put(name, new Annotations(annotations));
                    }
                    break;
                case RuntimeInvisibleParameterAnnotations:
                case RuntimeVisibleParameterAnnotations:
                    attributes.put(name,
                            new AttributeParameterAnnotations(loadParameterAnnotations(reader, constants)));
                    break;
                case Signature:
                    if (attributeLength < 2) {
                        throw new ClassFileFormatException("Invalid attribute length");
                    }
                    attributes.put(name, new AttributeSignature(constants.getConstantUtf8(reader.readUnsignedShort())));
                    break;
                case SourceFile:
                    if (attributeLength < 2) {
                        throw new ClassFileFormatException("Invalid attribute length");
                    }
                    attributes.put(name,
                            new AttributeSourceFile(constants.getConstantUtf8(reader.readUnsignedShort())));
                    break;
                case Synthetic:
                    attributes.put(name, new AttributeSynthetic());
                    break;
                default:
                    attributes.put(name, new UnknownAttribute());
                    reader.skip(attributeLength);
                }
            } else {
                throw new ClassFileFormatException("Invalid " + i + "th attribute");
            }
            int diffOffset = reader.offset - offsetBefore;
            if (diffOffset > attributeLength) {
                System.err.println("Warning: hidden bytes within attribute");
                reader.skip(attributeLength - diffOffset);
            }
        }

        return attributes;
    }

    protected ElementValue loadElementValue(ClassFileReader reader, ConstantPool constants) {
        int type = reader.readByte();

        switch (type) {
        case 'B': // byte
        case 'D': // double
        case 'F': // float
        case 'I': // int
        case 'J': // long
        case 'S': // short
        case 'Z': // boolean
        case 'C': // char
        case 's': // String ... well it's not primitive...
            int constValueIndex = reader.readUnsignedShort();
            ConstantValue constValue = (ConstantValue) constants.getConstant(constValueIndex);
            return new ElementValuePrimitiveType(type, constValue);
        case 'e': // enum
            int descriptorIndex = reader.readUnsignedShort();
            String descriptor = constants.getConstantUtf8(descriptorIndex);
            int constNameIndex = reader.readUnsignedShort();
            String constName = constants.getConstantUtf8(constNameIndex);
            return new ElementValueEnumConstValue(descriptor, constName);
        case 'c': // class
            int classInfoIndex = reader.readUnsignedShort();
            String classInfo = constants.getConstantUtf8(classInfoIndex);
            return new ElementValueClassInfo(classInfo);
        case '@': // annotation
            int typeIndex = reader.readUnsignedShort();
            descriptor = constants.getConstantUtf8(typeIndex);
            return new ElementValueAnnotationValue(
                    new Annotation(descriptor, loadElementValuePairs(reader, constants)));
        case '[': // array
            return new ElementValueArrayValue(loadElementValues(reader, constants));
        default:
            throw new ClassFileFormatException("Invalid element value type: " + type);
        }
    }

    protected ElementValuePair[] loadElementValuePairs(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        ElementValuePair[] pairs = new ElementValuePair[count];

        for (int i = 0; i < count; i++) {
            int elementNameIndex = reader.readUnsignedShort();
            String elementName = constants.getConstantUtf8(elementNameIndex);
            pairs[i] = new ElementValuePair(elementName, loadElementValue(reader, constants));
        }

        return pairs;
    }

    protected ElementValue[] loadElementValues(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        ElementValue[] values = new ElementValue[count];
        for (int i = 0; i < count; i++) {
            values[i] = loadElementValue(reader, constants);
        }

        return values;
    }

    protected BootstrapMethod[] loadBootstrapMethods(ClassFileReader reader) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        BootstrapMethod[] values = new BootstrapMethod[count];
        for (int i = 0; i < count; i++) {
            int bootstrapMethodRef = reader.readUnsignedShort();
            int numBootstrapArguments = reader.readUnsignedShort();
            int[] bootstrapArguments;

            if (numBootstrapArguments == 0) {
                bootstrapArguments = EMPTY_INT_ARRAY;
            } else {
                bootstrapArguments = new int[numBootstrapArguments];
                for (int j = 0; j < numBootstrapArguments; j++) {
                    bootstrapArguments[j] = reader.readUnsignedShort();
                }
            }

            values[i] = new BootstrapMethod(bootstrapMethodRef, bootstrapArguments);
        }

        return values;
    }

    protected byte[] loadCode(ClassFileReader reader) {
        int codeLength = reader.readInt();
        if (codeLength == 0) {
            return null;
        }

        byte[] code = new byte[codeLength];
        reader.readFully(code);

        return code;
    }

    protected CodeException[] loadCodeExceptions(ClassFileReader reader) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        CodeException[] codeExceptions = new CodeException[count];

        for (int i = 0; i < count; i++) {
            codeExceptions[i] = new CodeException(i, reader.readUnsignedShort(), reader.readUnsignedShort(),
                    reader.readUnsignedShort(), reader.readUnsignedShort());
        }

        return codeExceptions;
    }

    protected ConstantValue loadConstantValue(ClassFileReader reader, ConstantPool constants) {
        int constantValueIndex = reader.readUnsignedShort();

        return constants.getConstantValue(constantValueIndex);
    }

    protected String[] loadExceptionTypeNames(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        String[] exceptionTypeNames = new String[count];

        for (int i = 0; i < count; i++) {
            int exceptionClassIndex = reader.readUnsignedShort();
            exceptionTypeNames[i] = constants.getConstantTypeName(exceptionClassIndex);
        }

        return exceptionTypeNames;
    }

    protected InnerClass[] loadInnerClasses(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        InnerClass[] innerClasses = new InnerClass[count];

        for (int i = 0; i < count; i++) {
            int innerTypeIndex = reader.readUnsignedShort();
            int outerTypeIndex = reader.readUnsignedShort();
            int innerNameIndex = reader.readUnsignedShort();
            int innerAccessFlags = reader.readUnsignedShort();

            String innerTypeName = constants.getConstantTypeName(innerTypeIndex);
            String outerTypeName = (outerTypeIndex == 0) ? null : constants.getConstantTypeName(outerTypeIndex);
            String innerName = (innerNameIndex == 0) ? null : constants.getConstantUtf8(innerNameIndex);

            innerClasses[i] = new InnerClass(innerTypeName, outerTypeName, innerName, innerAccessFlags);
        }

        return innerClasses;
    }

    protected LocalVariable[] loadLocalVariables(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        LocalVariable[] localVariables = new LocalVariable[count];

        for (int i = 0; i < count; i++) {
            int startPc = reader.readUnsignedShort();
            int length = reader.readUnsignedShort();
            int nameIndex = reader.readUnsignedShort();
            int descriptorIndex = reader.readUnsignedShort();
            int index = reader.readUnsignedShort();

            String name = constants.getConstantUtf8(nameIndex);
            String descriptor = constants.getConstantUtf8(descriptorIndex);

            localVariables[i] = new LocalVariable(startPc, length, name, descriptor, index);
        }

        return localVariables;
    }

    protected LocalVariableType[] loadLocalVariableTypes(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        LocalVariableType[] localVariables = new LocalVariableType[count];

        for (int i = 0; i < count; i++) {
            int startPc = reader.readUnsignedShort();
            int length = reader.readUnsignedShort();
            int nameIndex = reader.readUnsignedShort();
            int descriptorIndex = reader.readUnsignedShort();
            int index = reader.readUnsignedShort();

            String name = constants.getConstantUtf8(nameIndex);
            String descriptor = constants.getConstantUtf8(descriptorIndex);

            localVariables[i] = new LocalVariableType(startPc, length, name, descriptor, index);
        }

        return localVariables;
    }

    protected LineNumber[] loadLineNumbers(ClassFileReader reader) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        LineNumber[] lineNumbers = new LineNumber[count];

        for (int i = 0; i < count; i++) {
            lineNumbers[i] = new LineNumber(reader.readUnsignedShort(), reader.readUnsignedShort());
        }

        return lineNumbers;
    }

    protected MethodParameter[] loadParameters(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedByte();
        if (count == 0) {
            return null;
        }

        MethodParameter[] parameters = new MethodParameter[count];

        for (int i = 0; i < count; i++) {
            int nameIndex = reader.readUnsignedShort();

            String name = constants.getConstantUtf8(nameIndex);

            parameters[i] = new MethodParameter(name, reader.readUnsignedShort());
        }

        return parameters;
    }

    protected ModuleInfo[] loadModuleInfos(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        ModuleInfo[] moduleInfos = new ModuleInfo[count];

        for (int i = 0; i < count; i++) {
            int moduleInfoIndex = reader.readUnsignedShort();
            int moduleFlag = reader.readUnsignedShort();
            int moduleVersionIndex = reader.readUnsignedShort();

            String moduleInfoName = constants.getConstantTypeName(moduleInfoIndex);
            String moduleVersion = (moduleVersionIndex == 0) ? null : constants.getConstantUtf8(moduleVersionIndex);

            moduleInfos[i] = new ModuleInfo(moduleInfoName, moduleFlag, moduleVersion);
        }

        return moduleInfos;
    }

    protected PackageInfo[] loadPackageInfos(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        PackageInfo[] packageInfos = new PackageInfo[count];

        for (int i = 0; i < count; i++) {
            int packageInfoIndex = reader.readUnsignedShort();
            int packageFlag = reader.readUnsignedShort();

            String packageInfoName = constants.getConstantTypeName(packageInfoIndex);

            packageInfos[i] = new PackageInfo(packageInfoName, packageFlag, loadConstantClassNames(reader, constants));
        }

        return packageInfos;
    }

    protected String[] loadConstantClassNames(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        String[] names = new String[count];

        for (int i = 0; i < count; i++) {
            names[i] = constants.getConstantTypeName(reader.readUnsignedShort());
        }

        return names;
    }

    protected ServiceInfo[] loadServiceInfos(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        ServiceInfo[] services = new ServiceInfo[count];

        for (int i = 0; i < count; i++) {
            services[i] = new ServiceInfo(constants.getConstantTypeName(reader.readUnsignedShort()),
                    loadConstantClassNames(reader, constants));
        }

        return services;
    }

    protected Annotation[] loadAnnotations(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedShort();
        if (count == 0) {
            return null;
        }

        Annotation[] annotations = new Annotation[count];

        for (int i = 0; i < count; i++) {
            int descriptorIndex = reader.readUnsignedShort();
            String descriptor = constants.getConstantUtf8(descriptorIndex);
            annotations[i] = new Annotation(descriptor, loadElementValuePairs(reader, constants));
        }

        return annotations;
    }

    protected Annotations[] loadParameterAnnotations(ClassFileReader reader, ConstantPool constants) {
        int count = reader.readUnsignedByte();
        if (count == 0) {
            return null;
        }

        Annotations[] parameterAnnotations = new Annotations[count];

        for (int i = 0; i < count; i++) {
            Annotation[] annotations = loadAnnotations(reader, constants);
            if (annotations != null) {
                parameterAnnotations[i] = new Annotations(annotations);
            }
        }

        return parameterAnnotations;
    }

    /**
     * Create a ClassFile model from a loader and a internal type name
     * 
     * @throws IOException
     */
    @Override
    public void process(Message message) throws IOException {
        Loader loader = message.getLoader();
        String internalTypeName = message.getMainInternalTypeName();
        ClassFile classFile = loadClassFile(loader, internalTypeName);

        message.setClassFile(classFile);
    }

    private static ClassFileDeserializer instance = null;

    /**
     * Get Singleton instance
     */
    public static ClassFileDeserializer getInstance() {
        if (instance == null) {
            instance = new ClassFileDeserializer();
        }
        return instance;
    }
}
