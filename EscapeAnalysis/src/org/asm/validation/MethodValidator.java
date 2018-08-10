package org.asm.validation;

import java.util.Set;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MethodValidator extends MethodVisitor {

    private Set<String> invokedTypes;
    private Set<String> instantiatedTypes;

    public MethodValidator(Set<String> invokedTypes, Set<String> instantiatedTypes) {
        super(Opcodes.ASM6);

        this.invokedTypes = invokedTypes;
        this.instantiatedTypes = instantiatedTypes;
    }

    /**
     * Visits a method instruction. A method instruction is an instruction that
     * invokes a method.
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

        owner = removeArrayNotation(owner);
        owner = removeReferenceNotation(owner);

        if (!isPrimitiveType(owner)) {
            this.invokedTypes.add(owner);
        }
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that takes
     * the internal name of a class as parameter.
     */
    @Override
    public void visitTypeInsn(int opcode, String type) {
        type = removeArrayNotation(type);
        type = removeReferenceNotation(type);

        if (!isPrimitiveType(type)) {
            this.instantiatedTypes.add(type);
        }
    }

    private String removeArrayNotation(String name) {

        while (name.startsWith("[")) {
            name = name.substring(1);
        }
        return name;
    }

    private String removeReferenceNotation(String name) {

        if (name.startsWith("L") && name.endsWith(";")) {
            name = name.substring(1).replace(";", "");
        }
        return name;
    }

    private boolean isPrimitiveType(String name) {

        switch (name) {
        case "B": // byte
        case "C": // char
        case "D": // double
        case "F": // float
        case "I": // int
        case "J": // long
        case "S": // short
        case "Z": // boolean
            return true;
        default:
            return false;
        }
    }
}
