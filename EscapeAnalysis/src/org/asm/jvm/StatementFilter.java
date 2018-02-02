package org.asm.jvm;

import java.util.ArrayList;

import org.asm.ConvertOpcode;
import org.asm.jvm.values.*;
import org.connectionGraph.EscapeStatementVisitor;
import org.connectionGraph.nodes.Node;
import org.connectionGraph.nodes.ValueNode;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

/*
 * 
 */
public class StatementFilter extends MethodVisitor {

	private String className;
	private String methodDescriptor;
	private int access;

	private Frame frame;
	private ArrayList<ObjectInstance> heap;

	private Type methodType;
	private AccessFlags methodFlags;
	
	private EscapeStatementVisitor escapeStatementViaitor;
	
	public StatementFilter(String className, int access, String methodDescriptor) {
		super(Opcodes.ASM6);
		
		this.className = className;
		this.access = access;
		this.methodDescriptor = methodDescriptor;
	}
	
    /**
     *  Starts the visit of the method's code, if any (i.e. non abstract method).
     */
	@Override
	public void visitCode() {
		
		System.out.format("visitCode\n");

		// Initialize statement filter only if the method has code.
		this.methodType = Type.getMethodType(this.methodDescriptor);
		this.methodFlags = new AccessFlags(this.access);
		this.frame = new Frame();
		this.heap = new ArrayList<ObjectInstance>();

		ArrayList<Value> parameterValues = new ArrayList<Value>();
		
		// On instance method invocation, local variable 0 is always used to pass a reference to the object
		// on which the instance method is being invoked (this in the Java programming language).
		if(!methodFlags.isStatic()) {
			
			ObjectInstance thisObject = new ObjectInstance(this.className);
			// Add the this object to the 
			
			parameterValues.add(new ReferenceValue(thisObject));
		}
	
		// Any parameters are subsequently passed in consecutive local variables starting from local variable 1. 
		for(Type argType : methodType.getArgumentTypes()) {
			
			switch(argType.getSort()) {
			case Type.ARRAY:
			case Type.OBJECT:
				ObjectInstance parameterObject = new ObjectInstance(argType.getInternalName());
				parameterValues.add(new ReferenceValue(parameterObject));
				break;
			case Type.BOOLEAN:
				parameterValues.add(BooleanValue.getInstance());
				break;
			case Type.CHAR:
				parameterValues.add(CharValue.getInstance());
				break;
			case Type.INT:
				parameterValues.add(IntValue.getInstance());
				break;
			case Type.SHORT:
				parameterValues.add(ShortValue.getInstance());
				break;
			case Type.BYTE:
				parameterValues.add(ByteValue.getInstance());
				break;
			case Type.FLOAT:
				parameterValues.add(FloatValue.getInstance());
				break;
			case Type.DOUBLE:
				// A value of type long or type double occupies two consecutive local variables.  
				parameterValues.add(DoubleValue.getInstance());
				parameterValues.add(DoubleValue.getInstance());
				break;
			case Type.LONG:
				// A value of type long or type double occupies two consecutive local variables.  
				parameterValues.add(LongValue.getInstance());
				parameterValues.add(LongValue.getInstance());
				break;
			}
		}
		
		
		this.frame.initializeLocalVariables(parameterValues);
		
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
				ReferenceValue objectRef2 = (ReferenceValue)this.frame.popFromStack();
				this.frame.pushValueOntoOperandStack(new FieldName(name));
				opcodeStr = "GETFIELD";
				break;
			case Opcodes.PUTFIELD:
				
				ReferenceValue objectRef3 = (ReferenceValue)this.frame.popFromStack();
				Value value3 = (Value)this.frame.popFromStack();
				
			
				
				opcodeStr = "PUTFIELD";
				break;
		}
		System.out.format("VisitFieldInsn: opcode: %s, owner: %s, name: %s, desc: %s\n", opcodeStr, owner, name, desc);
		super.visitFieldInsn(opcode, owner, name, desc);
	}


    /**
     *  Visits a zero operand instruction.
     */
	@Override
	public void visitInsn(int opcode) {
		// TODO Auto-generated method stub
		System.out.format("VisitInsn: opcode: %s (%s)\n", ConvertOpcode.toString(opcode), opcode);

		switch(opcode) {
		case Opcodes.NOP: // Do nothing
			break;
		case Opcodes.ACONST_NULL: // Push the null object reference onto the operand stack.
			this.frame.pushValueOntoOperandStack(ReferenceValue.getNullReference());
			break;
		case Opcodes.ICONST_M1: // Push int constant onto the operand stack. 
		case Opcodes.ICONST_0:
		case Opcodes.ICONST_1:
		case Opcodes.ICONST_2: 
		case Opcodes.ICONST_3:
		case Opcodes.ICONST_4:
		case Opcodes.ICONST_5:
			this.frame.pushValueOntoOperandStack(IntValue.getInstance());
			break;
		case Opcodes.LCONST_0: // Push the long constant onto the operand stack. 
		case Opcodes.LCONST_1:
			this.frame.pushValueOntoOperandStack(LongValue.getInstance());
			break;
		case Opcodes.FCONST_0: // Push the float constant onto the operand stack. 
		case Opcodes.FCONST_1:
		case Opcodes.FCONST_2:
			this.frame.pushValueOntoOperandStack(FloatValue.getInstance());
			break;
		case Opcodes.DCONST_0: // Push the double constant onto the operand stack. 
		case Opcodes.DCONST_1:
			this.frame.pushValueOntoOperandStack(DoubleValue.getInstance());
			break;
		case Opcodes.IALOAD: // Load int from array.
			this.frame.popFromStack();
			this.frame.popFromStack();
			this.frame.pushValueOntoOperandStack(IntValue.getInstance());
			break;
		case Opcodes.BALOAD: // Load byte or boolean from array
			this.frame.popFromStack();
			this.frame.popFromStack();
			// Since we do not keep track of any value types, we push an arbitrary value.
			this.frame.pushValueOntoOperandStack(ByteValue.getInstance());
			break;
		case Opcodes.CALOAD: // Load char from array 
			this.frame.popFromStack();
			this.frame.popFromStack();
			// Since we do not keep track of any value types, we push an arbitrary value.
			this.frame.pushValueOntoOperandStack(CharValue.getInstance());
			break;
		case Opcodes.SALOAD: // Load short from array 
			this.frame.popFromStack();
			this.frame.popFromStack();
			// Since we do not keep track of any value types, we push an arbitrary value.
			this.frame.pushValueOntoOperandStack(ShortValue.getInstance());
			break;
		case Opcodes.LALOAD: // Load long from array
			this.frame.popFromStack();
			this.frame.popFromStack();
			// Since we do not keep track of any value types, we push an arbitrary value.
			this.frame.pushValueOntoOperandStack(LongValue.getInstance());
			break;
		case Opcodes.FALOAD: // Load float from array 
			this.frame.popFromStack();
			this.frame.popFromStack();
			// Since we do not keep track of any value types, we push an arbitrary value.
			this.frame.pushValueOntoOperandStack(FloatValue.getInstance());
			break;
		case Opcodes.DALOAD: // Load double from array 
			this.frame.popFromStack();
			this.frame.popFromStack();
			// Since we do not keep track of any value types, we push an arbitrary value.
			this.frame.pushValueOntoOperandStack(DoubleValue.getInstance());
			break;

		case Opcodes.AALOAD: // Load reference from array 

			break;
			
		case Opcodes.LASTORE: // Store into long array
		case Opcodes.FASTORE: // Store into float array 
		case Opcodes.DASTORE: // Store into double array
		case Opcodes.IASTORE: // Store into int array 
		case Opcodes.BASTORE: // Store into byte or boolean array
		case Opcodes.CASTORE: // Store into char array 
		case Opcodes.SASTORE: // Store into short array 
			this.frame.popFromStack(); // Arrayref
			this.frame.popFromStack(); // index
			this.frame.popFromStack(); // value
			break;
			
		case Opcodes.AASTORE: // Store into reference array. 
			
			break;
		case Opcodes.POP: // Pop the top operand stack value
			this.frame.popFromStack(); // value
			break;
		case Opcodes.POP2: 
			this.frame.popFromStack(); // value
			break;
		case Opcodes.DUP: // Duplicate the top operand stack value 
			Value value = this.frame.popFromStack(); // value
			this.frame.pushValueOntoOperandStack(value);
			this.frame.pushValueOntoOperandStack(value);
			break;
		case Opcodes.DUP_X1: // Duplicate the top operand stack value and insert two values down.
			Value value1 = this.frame.popFromStack();
			Value value2 = this.frame.popFromStack();
			this.frame.pushValueOntoOperandStack(value1);
			this.frame.pushValueOntoOperandStack(value2);
			this.frame.pushValueOntoOperandStack(value1);
			break;
		case Opcodes.DUP_X2: // Duplicate the top operand stack value and insert two or three values down.
			Value dupx2_value1 = this.frame.popFromStack();
			Value dupx2_value2 = this.frame.popFromStack();
			Value dupx2_value3 = this.frame.popFromStack();
			this.frame.pushValueOntoOperandStack(dupx2_value1);
			this.frame.pushValueOntoOperandStack(dupx2_value3);
			this.frame.pushValueOntoOperandStack(dupx2_value2);
			this.frame.pushValueOntoOperandStack(dupx2_value1);
			break;
		case Opcodes.DUP2: 
		case Opcodes.DUP2_X1: 
		case Opcodes.DUP2_X2: 
		case Opcodes.SWAP: 
		case Opcodes.IADD: 
		case Opcodes.LADD: 
		case Opcodes.FADD: 
		case Opcodes.DADD: 
		case Opcodes.ISUB: 
		case Opcodes.LSUB: 
		case Opcodes.FSUB: 
		case Opcodes.DSUB: 
		case Opcodes.IMUL: 
		case Opcodes.LMUL: 
		case Opcodes.FMUL: 
		case Opcodes.DMUL: 
		case Opcodes.IDIV: 
		case Opcodes.LDIV: 
		case Opcodes.FDIV:
		case Opcodes.DDIV: 
		case Opcodes.IREM: 
		case Opcodes.LREM: 
		case Opcodes.FREM: 
		case Opcodes.DREM: 
		case Opcodes.INEG: 
		case Opcodes.LNEG:
		case Opcodes.FNEG: 
		case Opcodes.DNEG:
		case Opcodes.ISHL:
		case Opcodes.LSHL: 
		case Opcodes.ISHR: 
		case Opcodes.LSHR: 
		case Opcodes.IUSHR: 
		case Opcodes.LUSHR: 
		case Opcodes.IAND: 
		case Opcodes.LAND: 
		case Opcodes.IOR: 
		case Opcodes.LOR: 
		case Opcodes.IXOR: 
		case Opcodes.LXOR: 
		case Opcodes.I2L: 
		case Opcodes.I2F: 
		case Opcodes.I2D: 
		case Opcodes.L2I: 
		case Opcodes.L2F: 
		case Opcodes.L2D: 
		case Opcodes.F2I: 
		case Opcodes.F2L:
		case Opcodes.F2D: 
		case Opcodes.D2I: 
		case Opcodes.D2L: 
		case Opcodes.D2F:
		case Opcodes.I2B: 
		case Opcodes.I2C: 
		case Opcodes.I2S:
		case Opcodes.LCMP: 
		case Opcodes.FCMPL:
		case Opcodes.FCMPG: 
		case Opcodes.DCMPL: 
		case Opcodes.DCMPG: 
		case Opcodes.IRETURN:
		case Opcodes.LRETURN: 
		case Opcodes.FRETURN: 
		case Opcodes.DRETURN: 
		case Opcodes.ARETURN: 
		case Opcodes.RETURN: 
		case Opcodes.ARRAYLENGTH: 
		case Opcodes.ATHROW: 
		case Opcodes.MONITORENTER: 
		case Opcodes.MONITOREXIT:
			break;
	}
		
	
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
