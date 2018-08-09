package org.asm.jvm;

public class MethodDescriptor {
    
    public static String noArgsAndReturnVoid = "()V";

    public static String justReturnsObject = "()Ljava/lang/Object;";
            
    public static String readObjectDescriptor = "(Ljava/io/ObjectInputStream;)V";
    
    public static String writeObjectDescriptor = "(Ljava/io/ObjectOutputStream;)V";
    
    public static String readObjectInputDescriptor = "(Ljava/io/ObjectInput;)V";

    public static String writeObjectOutputDescriptor = "(Ljava/io/ObjectOutput;)V";
}