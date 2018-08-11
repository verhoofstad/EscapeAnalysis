package org.escapeAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaType;
import org.objectweb.asm.Type;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.VoidType;
import soot.ShortType;

public class SootFactory {

    public String getSootClassName(JavaType javaType) {
        return javaType.id().replace('/', '.');
    }
    
    public soot.SootMethod getSootMethod(soot.SootClass sootClass, JavaMethod method) {
        return sootClass.getMethod(method.name(), sootParameters(method), sootReturnType(method));
    }
    
    private static List<soot.Type> sootParameters(JavaMethod javaMethod) {
        List<soot.Type> parameterTypes = new ArrayList<soot.Type>();

        for (Type argumentType : javaMethod.signature().argumentTypes()) {
            parameterTypes.add(toSootType(argumentType));
        }

        return parameterTypes;
    }

    private static soot.Type sootReturnType(JavaMethod javaMethod) {
        return toSootType(javaMethod.signature().returnType());
    }

    private static soot.Type toSootType(Type asmType) {
        switch (asmType.getSort()) {
        case Type.VOID:
            return VoidType.v();
        case Type.BOOLEAN:
            return BooleanType.v();
        case Type.CHAR:
            return CharType.v();
        case Type.BYTE:
            return ByteType.v();
        case Type.SHORT:
            return ShortType.v();
        case Type.INT:
            return IntType.v();
        case Type.FLOAT:
            return FloatType.v();
        case Type.LONG:
            return LongType.v();
        case Type.DOUBLE:
            return DoubleType.v();
        case Type.ARRAY:
            return ArrayType.v(toSootType(asmType.getElementType()), asmType.getDimensions());
        case Type.OBJECT:
            return RefType.v(asmType.getClassName());
        case Type.METHOD:
            System.out.println("Error: Unexpected type. [method]");
            return null;
        }
        System.out.format("Error: Nothing found: %s\n", asmType.getSort());
        return null;
    }
}
