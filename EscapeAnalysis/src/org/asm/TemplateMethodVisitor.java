package org.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

public class TemplateMethodVisitor extends MethodVisitor {
	public TemplateMethodVisitor(int api) {
		super(api);
		// TODO Auto-generated constructor stub
	}
	
	public TemplateMethodVisitor(int api, MethodVisitor mv) {
		super(api, mv);
		// TODO Auto-generated constructor stub
	}
	

    /**
     *  Visits an annotation of this method.
     */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// TODO Auto-generated method stub
		System.out.format("VisitAnnotation: desc: %s, visible: %s\n", desc, visible);
		return super.visitAnnotation(desc, visible);
	}

    /**
     *  Visits the default value of this annotation interface method.
     */
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		// TODO Auto-generated method stub
		System.out.format("VisitAnnotationDefault\n");
		return super.visitAnnotationDefault();
	}

    /**
     *  Visits a non standard attribute of this method.
     */
	@Override
	public void visitAttribute(Attribute attr) {
		// TODO Auto-generated method stub
		System.out.format("VisitAttribute: attr: %s\n", attr.toString());
		super.visitAttribute(attr);
	}

    /**
     *  Starts the visit of the method's code, if any (i.e. non abstract method).
     */
	@Override
	public void visitCode() {
		// TODO Auto-generated method stub
		System.out.format("VisitCode\n");
		super.visitCode();
	}

    /**
     *  Visits the end of the method. 
     *  This method, which is the last one to be called, is used to inform the visitor that all the annotations and attributes of the method have been visited.
     */
	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		System.out.format("VisitEnd\n");
		super.visitEnd();
	}

    /**
     *  Visits a field instruction. A field instruction is an instruction that loads or stores the value of a field of an object.
     */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {

		String opcodeStr = "";

		switch(opcode) {
			case Opcodes.GETSTATIC:
				opcodeStr = "GETSTATIC";
				break;
			case Opcodes.PUTSTATIC:
				opcodeStr = "PUTSTATIC";
				break;
			case Opcodes.GETFIELD:
				opcodeStr = "GETFIELD";
				break;
			case Opcodes.PUTFIELD:
				opcodeStr = "PUTFIELD";
				break;
		}
		System.out.format("VisitFieldInsn: opcode: %s, owner: %s, name: %s, desc: %s\n", opcodeStr, owner, name, desc);
		super.visitFieldInsn(opcode, owner, name, desc);
	}

    /**
     *  Visits the current state of the local variables and operand stack elements. 
     *  This method must(*) be called just before any instruction i that follows an unconditional branch instruction such as GOTO or THROW, 
     *  that is the target of a jump instruction, or that starts an exception handler block. The visited types must describe 
     *  the values of the local variables and of the operand stack elements just before i is executed.
     */
	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitFrame\n");
		super.visitFrame(type, nLocal, local, nStack, stack);
	}

    /**
     *  Visits an IINC instruction.
     */
	@Override
	public void visitIincInsn(int var, int increment) {
		// TODO Auto-generated method stub
		System.out.format("VisitIincInsn: var: %s, increment: %s\n", var, increment);
		super.visitIincInsn(var, increment);
	}

    /**
     *  Visits a zero operand instruction.
     */
	@Override
	public void visitInsn(int opcode) {
		// TODO Auto-generated method stub
		System.out.format("VisitInsn: opcode: %s (%s)\n", ConvertOpcode.toString(opcode), opcode);
		super.visitInsn(opcode);
	}

    /**
     *  Visits an annotation on an instruction. This method must be called just after the annotated instruction. It can be called several times for the same instruction.
     */
	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitInsnAnnotation\n");
		return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
	}

    /**
     *  Visits an instruction with a single int operand.
     */
	@Override
	public void visitIntInsn(int opcode, int operand) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitIntInsn\n");
		super.visitIntInsn(opcode, operand);
	}

    /**
     *  Visits an invokedynamic instruction.
     */
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		// TODO Auto-generated method stub
		System.out.format("VisitInvokeDynamicInsn: name: %s, desc: %s\n", name, desc);
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
	}

    /**
     *  Visits a jump instruction. A jump instruction is an instruction that may jump to another instruction.
     */
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitJumpInsn\n");
		super.visitJumpInsn(opcode, label);
	}

    /**
     *  Visits a label. A label designates the instruction that will be visited just after it.
     */
	@Override
	public void visitLabel(Label label) {
		// TODO Auto-generated method stub
		System.out.format("VisitLabel: label: %s\n", label.toString());
		super.visitLabel(label);
	}

    /**
     *  Visits a LDC instruction. Note that new constant types may be added in future versions of the Java Virtual Machine.
     *  To easily detect new constant types, implementations of this method should check for unexpected constant types, like this: 
     */
	@Override
	public void visitLdcInsn(Object cst) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitLdcInsn\n");
		super.visitLdcInsn(cst);
	}

    /**
     *  Visits a line number declaration.
     */
	@Override
	public void visitLineNumber(int line, Label start) {
		// TODO Auto-generated method stub
		System.out.format("VsitLineNumber: line: %s\n", line);
		super.visitLineNumber(line, start);
	}

    /**
     *  Visits a local variable declaration.
     */
	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		// TODO Auto-generated method stub
		System.out.format("VisitLocalVariable: name: %s, desc: %s, signature: %s, index: %s\n", name, desc, signature, index);
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

    /**
     *  Visits an annotation on a local variable type.
     */
	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end,
			int[] index, String desc, boolean visible) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitLocalVariableAnnotation\n");
		return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
	}

    /**
     *  Visits a LOOKUPSWITCH instruction
     */
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitLookupSwitchInsn\n");
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

    /**
     *  Visits the maximum stack size and the maximum number of local variables of the method.
     */
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		// TODO Auto-generated method stub
		System.out.format("VisitMaxs: maxStack: %s, maxLocals: %s\n", maxStack, maxLocals);
		super.visitMaxs(maxStack, maxLocals);
	}

    /**
     *  Visits a method instruction. A method instruction is an instruction that invokes a method.
     */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

		String opcodeStr = "";
		
		switch(opcode) {
			case Opcodes.INVOKEVIRTUAL:
				opcodeStr = "InvokeVirtual";
				break;
			case Opcodes.INVOKESPECIAL:
				opcodeStr = "InvokeSpecial";
				break;
			case Opcodes.INVOKESTATIC:
				opcodeStr = "InvokeStatic";
				break;
			case Opcodes.INVOKEINTERFACE:
				opcodeStr = "InvokeInterface";
				break;
		}
		System.out.format("VisitMethodInsn: opcode: %s, owner: %s, name: %s, desc: %s, itf: %s\n", opcodeStr, owner, name, desc, itf);
		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}

    /**
     *  Visits a MULTIANEWARRAY instruction.
     */
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitMultiANewArrayInsn\n");
		super.visitMultiANewArrayInsn(desc, dims);
	}

    /**
     * 
     *  
     *  Will not be called if code wasn't compiled with '-parameter' option. 
     *  See also: https://stackoverflow.com/questions/42596536/visitparameter-of-methodvisitor-is-never-called
     */
	@Override
	public void visitParameter(String name, int access) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitParameter\n");
		super.visitParameter(name, access);
	}

    /**
     *  Visits an annotation of a parameter this method.
     */
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitParameterAnnotation\n");
		return super.visitParameterAnnotation(parameter, desc, visible);
	}

    /**
     *  Visits a TABLESWITCH instruction.
     */
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitTableSwitchInsn\n");
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

    /**
     *  Visits an annotation on an exception handler type. 
     *  This method must be called after the visitTryCatchBlock(org.objectweb.asm.Label, org.objectweb.asm.Label, org.objectweb.asm.Label, java.lang.String)
     *  for the annotated exception handler. It can be called several times for the same exception handler.
     */
	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitTryCatchAnnotation\n");
		return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
	}

    /**
     *  Visits a try catch block.
     */
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitTryCatchBlock\n");
		super.visitTryCatchBlock(start, end, handler, type);
	}

    /**
     *  Visits an annotation on a type in the method signature.
     */
	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		// TODO Auto-generated method stub
		System.out.format("METHOD visitTypeAnnotation\n");
		return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

    /**
     *  Visits a type instruction. A type instruction is an instruction that takes the internal name of a class as parameter.
     */
	@Override
	public void visitTypeInsn(int opcode, String type) {

		System.out.format("VisitTypeInsn: ");
		
		switch(opcode) {
			case Opcodes.NEW:
				System.out.format("new %s()\n", type);
				break;
			case Opcodes.ANEWARRAY:
				System.out.format("new %s[]\n", type);
				break;
			case Opcodes.CHECKCAST:
				System.out.format("%s\n", type);
				break;
			case Opcodes.INSTANCEOF:
				System.out.format("InstanceOf %s\n", type);
				break;
		}
		
		super.visitTypeInsn(opcode, type);
	}

    /**
     *  Visits a local variable instruction. A local variable instruction is an instruction that loads or stores the value of a local variable.
     */
	@Override
	public void visitVarInsn(int opcode, int var) {
		
		String opcodeStr = "";
		
		switch(opcode) {
			case Opcodes.ILOAD:
				opcodeStr = "ILOAD";
				break;
			case Opcodes.LLOAD:
				opcodeStr = "LLOAD";
				break;
			case Opcodes.FLOAD:
				opcodeStr = "FLOAD";
				break;
			case Opcodes.DLOAD:
				opcodeStr = "DLOAD";
				break;
			case Opcodes.ALOAD:
				opcodeStr = "ALOAD";
				break;
			case Opcodes.ISTORE:
				opcodeStr = "ISTORE";
				break;
			case Opcodes.LSTORE:
				opcodeStr = "LSTORE";
				break;
			case Opcodes.FSTORE:
				opcodeStr = "FSTORE";
				break;
			case Opcodes.DSTORE:
				opcodeStr = "DSTORE";
				break;
			case Opcodes.ASTORE:
				opcodeStr = "ASTORE";
				break;
			case Opcodes.RET:
				opcodeStr = "RET";
				break;
		}
		
		System.out.format("VisitVarInsn: opcode: %s, var: %s\n", opcodeStr, var);
		
		super.visitVarInsn(opcode, var);
	}
}
