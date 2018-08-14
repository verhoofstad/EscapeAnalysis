package org.asm.jvm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

/**
 * Represents the signature of a method consisting of its name, return type and argument types.
 */
public class MethodSignature {

    private String name;
    private String desc;
    private Type methodType;
    
    public MethodSignature(String name, String desc) {
        if(name == null) { throw new IllegalArgumentException("Parameter 'name' should not be null."); }
        if(desc == null) { throw new IllegalArgumentException("Parameter 'desc' should not be null."); }

        this.name = name;
        this.desc = desc;
        this.methodType = Type.getMethodType(desc);
    }
    
    public String name() {
        return this.name;
    }
    
    public Type returnType() {
        return this.methodType.getReturnType();
    }
    
    public Type[] argumentTypes() {
        return this.methodType.getArgumentTypes();
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode() ^ this.desc.hashCode() ^ 31;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MethodSignature)) {
            return false;
        }
        MethodSignature other = (MethodSignature)obj;
        return this.equals(other.name, other.desc);
    }
    
    public boolean equals(String name, String desc) {
        return this.name.equals(name) && this.desc.equals(desc);
    }
    
    @Override
    public String toString() {

        List<String> arguments = new ArrayList<String>();
        for (Type argumentType : this.argumentTypes()) {
            arguments.add(typeToString(argumentType));
        }

        return String.format("%s(%s):%s", this.name, String.join(",", arguments), typeToString(this.returnType()));
    }
        
    private static String typeToString(Type asmType) {
        switch (asmType.getSort()) {
        case Type.BOOLEAN:
        case Type.CHAR:
        case Type.BYTE:
        case Type.SHORT:
        case Type.INT:
        case Type.FLOAT:
        case Type.LONG:
        case Type.DOUBLE:
            return asmType.getClassName();
        case Type.ARRAY:
            try {
                return typeToString(asmType.getElementType()) + new String(new char[asmType.getDimensions()]).replace("\0", "[]");
            } catch (NullPointerException ex) {
                throw new Error("Null pointer exception.");
            }
        case Type.OBJECT:
            return asmType.getInternalName();
        case Type.VOID:
            return "V";
        default:
            throw new Error("Unexpected argument type for method.");
        }
    }
}
