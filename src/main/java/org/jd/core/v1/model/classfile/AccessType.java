package org.jd.core.v1.model.classfile;

/**
 * @see https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.1
 * 	    https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.5
 * 	    https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.6
 *      https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.7.24
 */
public enum AccessType {
	
	// Access flags for Class, Field, Method, Nested class, Module, Module Requires, Module Exports, Module Opens
    
    ACC_PUBLIC       (0x0001),  // C  F  M  N  .  .  .  .
    ACC_PRIVATE      (0x0002),  // .  F  M  N  .  .  .  .
    ACC_PROTECTED    (0x0004),  // .  F  M  N  .  .  .  .
    ACC_STATIC       (0x0008),  // C  F  M  N  .  .  .  .
    ACC_FINAL        (0x0010),  // C  F  M  N  .  .  .  .
    ACC_SYNCHRONIZED (0x0020),  // .  .  M  .  .  .  .  .
    ACC_SUPER        (0x0020),  // C  .  .  .  .  .  .  .
    ACC_OPEN         (0x0020),  // .  .  .  .  Mo .  .  .
    ACC_TRANSITIVE   (0x0020),  // .  .  .  .  .  MR .  .
    ACC_VOLATILE     (0x0040),  // .  F  .  .  .  .  .  .
    ACC_BRIDGE       (0x0040),  // .  .  M  .  .  .  .  .
    ACC_STATIC_PHASE (0x0040),  // .  .  .  .  .  MR .  .
    ACC_TRANSIENT    (0x0080),  // .  F  .  .  .  .  .  .
    ACC_VARARGS      (0x0080),  // .  .  M  .  .  .  .  .
    ACC_NATIVE       (0x0100),  // .  .  M  .  .  .  .  .
    ACC_INTERFACE    (0x0200),  // C  .  .  N  .  .  .  .
    ACC_ANONYMOUS    (0x0200),  // .  .  M  .  .  .  .  . // Custom flag
    ACC_ABSTRACT     (0x0400),  // C  .  M  N  .  .  .  .
    ACC_STRICT       (0x0800),  // .  .  M  .  .  .  .  .
    ACC_SYNTHETIC    (0x1000),  // C  F  M  N  Mo MR ME MO
    ACC_ANNOTATION   (0x2000),  // C  .  .  N  .  .  .  .
    ACC_ENUM         (0x4000),  // C  F  .  N  .  .  .  .
    ACC_MODULE       (0x8000),  // C  .  .  .  .  .  .  .
    ACC_MANDATED     (0x8000),  // .  .  .  .  Mo MR ME MO

    // Extension
    ACC_DEFAULT      (0x10000), // .  .  M  .  .  .  .  .
    ;
	
	int flag;
	
	private AccessType(int flag) {
		this.flag = flag;
	}
	
	public int getFlag() {
		return flag;
	}
}