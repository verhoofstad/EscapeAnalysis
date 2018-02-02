package org.asm.jvm;

import java.io.File;
import java.util.Map;

import org.asm.ConvertOpcode;
import org.asm.JarClass;
import org.connectionGraph.EscapeStatementVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class StatementFilter2 extends MethodVisitor {

	private File jarFile;
	private JarClass currentClass;
	private String currentMethod;
	private int currentLine;
	
	private Map<String, Integer> patternCount;

	private EscapeStatementVisitor escapeStatementVisitor;
	private ByteCodeInstructionStack instructionStack;
	
	private Boolean verbose = false;
	private Boolean printPatterns = true; //146475, za 142449, zo 115289 

	
	public StatementFilter2(EscapeStatementVisitor escapeStatementVisitor, File jarFile, JarClass currentClass, String currentMethod, Map<String, Integer> patternCount) {
		super(Opcodes.ASM6);
		
		this.escapeStatementVisitor = escapeStatementVisitor;
		this.jarFile = jarFile;
		this.currentClass = currentClass;
		this.currentMethod = currentMethod;
		this.patternCount = patternCount;
		this.currentLine = 0;
	}
	
    /**
     *  Starts the visit of the method's code, if any (i.e. non abstract method).
     */
	@Override
	public void visitCode() {

		this.instructionStack = new ByteCodeInstructionStack();
		
		this.escapeStatementVisitor.visitConcreteMethod();
	}
	
    /**
     *  Visits a line number declaration.
     */
	@Override
	public void visitLineNumber(int line, Label start) {
		this.currentLine = line;
	}
	
	
    /**
     *  Visits the end of the method. 
     *  This method, which is the last one to be called, is used to inform the visitor that all the annotations and attributes of the method have been visited.
     */
	@Override
	public void visitEnd() {
		
		if(this.instructionStack != null) {
			this.escapeStatementVisitor.visitEnd();
		}
	}

    /**
     *  Visits a local variable instruction. A local variable instruction is an instruction that loads or stores the value of a local variable.
     *  
     *  @param opcode ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     */
	@Override
	public void visitVarInsn(int opcode, int var) {
		
		this.printInstruction(opcode, var);
		
		switch(opcode) {
		case Opcodes.ILOAD:
		case Opcodes.LLOAD:
		case Opcodes.FLOAD:
		case Opcodes.DLOAD:
			opcode = Opcodes.ILOAD;
			break;
		case Opcodes.ISTORE:
		case Opcodes.DSTORE:
		case Opcodes.FSTORE:
		case Opcodes.LSTORE:
			opcode = Opcodes.ISTORE;
			this.instructionStack.clear();
			break;
		}
		
		this.instructionStack.push(new ByteCodeInstruction(opcode, var));
		
		if(opcode == Opcodes.ASTORE) {
			
			Boolean regonized = false; 
			
			// Variable assignment from SPECIAL method invocation new, private, this, super
			if(this.instructionStack.endsWith("INVOKESPECIAL;ASTORE")) {
			
				// Handle p = new T()
				if(this.instructionStack.matches("NEW;DUP;INVOKESPECIAL;ASTORE")) {
					
					ByteCodeInstruction astore = this.instructionStack.pop();
					ByteCodeInstruction invokeSpecial = this.instructionStack.pop();
					ByteCodeInstruction dup = this.instructionStack.pop();
					ByteCodeInstruction newObj = this.instructionStack.pop();
					
					regonized = true;
					
					this.escapeStatementVisitor.visitNew(newObj.getType(), astore.getVarIndex());
				}
				
				// Handle p = new q(r.m());
				if(this.instructionStack.matches("NEW;DUP;ALOAD;INVOKEVIRTUAL;INVOKESPECIAL;ASTORE")) {
					regonized = true;
	
				}
				
				// Handle p = q.m()   , where m() is private or super()
				if(this.instructionStack.matches("ALOAD;INVOKESPECIAL;ASTORE")) {
					
					regonized = true;
				}
			}
			
			// Variable assignment from new array.
			if(this.instructionStack.endsWith("NEWARRAY;ASTORE")) {
			
				// Handle p = new value[short] 
				if(this.instructionStack.matches("SIPUSH;NEWARRAY;ASTORE")) {
					
					regonized = true;
				}
				
				// Handle p = new value[p.f.m()] 
				if(this.instructionStack.matches("ALOAD;GETFIELD;INVOKEVIRTUAL;NEWARRAY;ASTORE")) {
					
					regonized = true;
				}
				
				// Handle p = new value[p.f.f]
				if(this.instructionStack.matches("ALOAD;GETFIELD;GETFIELD;NEWARRAY;ASTORE")) {
					
					regonized = true;
				}
			}
			
			// Variable assignment from VIRTUAL method invocation.
			if(this.instructionStack.endsWith("INVOKEVIRTUAL;ASTORE")) {

				// Handle p = q.m();
				if(this.instructionStack.matches("ALOAD;INVOKEVIRTUAL;ASTORE")) {
					
					regonized = true;
				}
				
				// Handle p = q.f.m()
				if(this.instructionStack.matches("ALOAD;GETFIELD;INVOKEVIRTUAL;ASTORE")) {
					
					regonized = true;
				}
				
				// Handle p = q.m(0, 0)
				if(this.instructionStack.matches("ALOAD;GETFIELD;ICONST_0;ICONST_0;INVOKEVIRTUAL;ASTORE")) {
					
					regonized = true;
				}				
			}
				
			// Variable assignment from STATIC method invocation.
			if(this.instructionStack.endsWith("INVOKEVIRTUAL;ASTORE")) {

				// Handle p = T.m(q, r)
				if(this.instructionStack.matches("ALOAD;ALOAD;INVOKESTATIC;ASTORE")) {
					
					regonized = true;
				}
				
				// Handle p = T.m(q)
				if(this.instructionStack.matches("ALOAD;INVOKESTATIC;ASTORE")) {
					
					regonized = true;
				}
				
				// Handle p = T.m(q.m())
				if(this.instructionStack.matches("ALOAD;INVOKEVIRTUAL;INVOKESTATIC;ASTORE")) {
					
					regonized = true;
				}
				
				// Handle p = T.m(q.f, new T())  raster = Raster.createWritableRaster(sampleModel, new Point());
				if(this.instructionStack.matches("ALOAD;GETFIELD;NEW;DUP;INVOKESPECIAL;INVOKESTATIC;ASTORE")) {
					
	
				}
				
				// Handle p = T.m(ldc)
				if(this.instructionStack.matches("LDC;INVOKESTATIC;ASTORE")) {
					
				}
				
				// Handle p = T.m(q.f)
				if(this.instructionStack.matches("ALOAD;GETFIELD;INVOKESTATIC;ASTORE")) {
					
					
					regonized = true;
				}
				
				// Handle p = T.m(q.f, r)
				if(this.instructionStack.matches("ALOAD;GETFIELD;ALOAD;INVOKESTATIC;ASTORE")) {
					
					regonized = true;
				}
				
			}
			
			if(this.instructionStack.endsWith("INVOKEINTERFACE;ASTORE")) {
				
				// Handle p = q.m(r)
				if(this.instructionStack.matches("ALOAD;ALOAD;INVOKEINTERFACE;ASTORE")) {
					
				}
			}
			
			
			// Handle p = q
			if(this.instructionStack.matches("ALOAD;ASTORE")) {

				ByteCodeInstruction astore = this.instructionStack.pop();
				ByteCodeInstruction aload = this.instructionStack.pop();

				regonized = true;

				this.escapeStatementVisitor.visitAssignment(astore.getVarIndex(), aload.getVarIndex());
			}

			// Handle p = q.f
			if(this.instructionStack.matches("ALOAD;GETFIELD;ASTORE")) {

				ByteCodeInstruction astore = this.instructionStack.pop();
				ByteCodeInstruction getfield = this.instructionStack.pop();
				ByteCodeInstruction aload = this.instructionStack.pop();

				regonized = true;

				this.escapeStatementVisitor.visitAssignment(astore.getVarIndex(), aload.getVarIndex(), getfield.getName());
			}
			
			// Handle p = T.f
			if(this.instructionStack.matches("GETSTATIC;ASTORE")) {

				
				regonized = true;
			}
			
			
			// Handle p = null
			if(this.instructionStack.matches("ACONST_NULL;ASTORE")) {
				
				regonized = true;
			}

			// Throw; Catch
			if(this.instructionStack.matches("ATHROW;ASTORE")) {
				regonized = true;
			}
			// Return int; Catch
			if(this.instructionStack.matches("IRETURN;ASTORE")) {
				regonized = true;
			}
			
			/*
			if(!regonized) {
				this.printUnhandledPattern();
				this.incPattern(this.instructionStack.getInstructionTrail());
			}*/
			
			this.instructionStack.clear();
		}
	}
	
    /**
     *  Visits a field instruction. A field instruction is an instruction that loads or stores the value of a field of an object.
     *  
     *  @param opcode GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
     */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {

		if(this.verbose) {
			System.out.format("%s %s.%s\n", ConvertOpcode.toString(opcode), owner, name);
		}

		this.instructionStack.push(new ByteCodeInstruction(opcode, name));

		if(opcode == Opcodes.PUTFIELD) {

			Boolean regonized = false; 
			
			// Variable assignment from VIRTUAL method invocation.
			if(this.instructionStack.matches("INVOKEVIRTUAL;PUTFIELD")) {
				
				// Handle p.f = q.m()
				if(this.instructionStack.matches("ALOAD;ALOAD;INVOKEVIRTUAL;PUTFIELD")) {
					
					regonized = true;
				}
				
				// Handle p.f = q.f.m()
				if(this.instructionStack.matches("ALOAD;ALOAD;GETFIELD;INVOKEVIRTUAL;PUTFIELD")) {
					
					regonized = true;
				}
				
				// Handle p.f = q.f.m(r.f) 
				if(this.instructionStack.matches("ALOAD;ALOAD;GETFIELD;ALOAD;GETFIELD;INVOKEVIRTUAL;PUTFIELD")) {
					
					regonized = true;
				}
				
				
				// Handle p.f = q.f.m(int)
				if(this.instructionStack.matches("ALOAD;ALOAD;GETFIELD;ICONST_0;INVOKEVIRTUAL;PUTFIELD")) {
					
					regonized = true;
				}
				
			}
			
			
			

			// Handle p.f = q
			if(this.instructionStack.matches("ALOAD;ALOAD;PUTFIELD")) {
				
				ByteCodeInstruction putfield = this.instructionStack.pop();
				ByteCodeInstruction aloadQ = this.instructionStack.pop();
				ByteCodeInstruction aloadP = this.instructionStack.pop();

				regonized = true;

				this.escapeStatementVisitor.visitNonStaticFieldAssignment(aloadP.getVarIndex(), putfield.getName(), aloadQ.getVarIndex());
			}
			
			// Handle p.f = q.f
			if(this.instructionStack.matches("ALOAD;ALOAD;GETFIELD;PUTFIELD")) {
				
				regonized = true;
			}
			
			// Handle p.f = q.f.g
			if(this.instructionStack.matches("ALOAD;ALOAD;GETFIELD;GETFIELD;PUTFIELD")) {
				
				regonized = true;
			}
			
			// Handle p.f = null
			if(this.instructionStack.matches("ALOAD;ACONST_NULL;PUTFIELD")) {
				
				
				regonized = true;
			}
			
			// Handle p.f = q.f = null
			if(this.instructionStack.matches("ALOAD;ALOAD;ACONST_NULL;DUP_X1;PUTFIELD;PUTFIELD")) {
				

				regonized = true;
			}
			
			// Handle p.f = new T()
			if(this.instructionStack.matches("ALOAD;NEW;DUP;ALOAD;INVOKESPECIAL;PUTFIELD")) {
				
				regonized = true;
			}
			
			// Handle p.f = private(q, "string", r.sf)
			if(this.instructionStack.matches("ALOAD;ALOAD;ALOAD;LDC;GETSTATIC;INVOKESPECIAL;PUTFIELD")) {
				
				regonized = true;
			}
			
			// Handle p.f = new T(q, r, int)  (ex. this.bbwi = new ByteBufferWithInfo(orb, bufferManager, usePooledByteBuffers))
			if(this.instructionStack.matches("ALOAD;NEW;DUP;ALOAD;ALOAD;ILOAD;INVOKESPECIAL;PUTFIELD")) {

				
				
				regonized = true;
			}

			
			// Handle p.f = new T(0, 0, 0, 0) (ex. destinationRegion = new Rectangle(0, 0, 0, 0))
			if(this.instructionStack.matches("ALOAD;NEW;DUP;ICONST_0;ICONST_0;ICONST_0;ICONST_0;INVOKESPECIAL;PUTFIELD")) {
				
				regonized = true;
			}
			
			// Handle p.f = new value[q]   (ex. this.sourceBands = new int[this.numBands])
			if(this.instructionStack.matches("ALOAD;ILOAD;NEWARRAY;PUTFIELD")) {
				
				regonized = true;
			}
			
			// Handle p.f = new value[q.f]   (ex. this.sourceBands = new int[this.numBands])
			if(this.instructionStack.matches("ALOAD;ALOAD;GETFIELD;NEWARRAY;PUTFIELD")) {

				regonized = true;
			}
			
			

			
			// Handle p.f = new T(q.f, r.f, 0, null)   (ex. bi = new BufferedImage(colorModel, raster, false, null))
			if(this.instructionStack.matches("ALOAD;NEW;DUP;ALOAD;GETFIELD;ALOAD;ICONST_0;ACONST_NULL;INVOKESPECIAL;PUTFIELD")) {
				
				
				regonized = true;
			}
			
			
			// Handle p.f = p.m((int)q.f, r.f, s.f)  (ex. bi = readEmbedded((int)compression, bi, param)
			if(this.instructionStack.matches("ALOAD;ALOAD;ALOAD;GETFIELD;L2I;ALOAD;GETFIELD;ALOAD;INVOKESPECIAL;PUTFIELD")) {
				
				regonized = true;
			}
			

			// Handle p.f = new T(int)
			if(this.instructionStack.matches("ALOAD;NEW;DUP;ICONST_0;INVOKESPECIAL;PUTFIELD")) {
				
			}
			
			// Handle p.f = T.m()
			if(this.instructionStack.matches("ALOAD;INVOKESTATIC;PUTFIELD")) {
				
				regonized = true;
			}

			// Handle p.f = T.m(q, r.f)  (ex. this.colorModel = ImageUtil.createColorModel(colorSpace, this.sampleModel))
			if(this.instructionStack.matches("ALOAD;ALOAD;ALOAD;GETFIELD;INVOKESTATIC;PUTFIELD")) {
				
				regonized = true;
			}

			// Handle p.f = int/boolean
			if(this.instructionStack.matches("ICONST_0;PUTFIELD")
			    || this.instructionStack.matches("ILOAD;PUTFIELD") ) {
				
				// Can be ignored
				regonized = true;
			}
			
			
			// Handle p.f = new T() --> t = new T(), p.f = t
			if(this.instructionStack.matches("ALOAD;NEW;DUP;INVOKESPECIAL;PUTFIELD")) {
				
				
				
				regonized = true;
			}
			
			// Handle p.f--
			if(this.instructionStack.matches("ALOAD;DUP;GETFIELD;ICONST_0;ISUB;PUTFIELD")) {
				

				regonized = true;
			}
			
			
			
			if(!regonized) {
				this.printUnhandledPattern();
				this.incPattern(this.instructionStack.getInstructionTrail());
			}
			this.instructionStack.clear();
		}
		
		if(opcode == Opcodes.PUTSTATIC ) {

			Boolean regonized = false; 
			
			// Handle q.f = q
			if(this.instructionStack.matches("ALOAD;ALOAD;PUTSTATIC")) {
				
				ByteCodeInstruction putstatic = this.instructionStack.pop();
				ByteCodeInstruction aloadQ = this.instructionStack.pop();
				ByteCodeInstruction aloadP = this.instructionStack.pop();

				regonized = true;

				this.escapeStatementVisitor.visitStaticFieldAssignment(Type.getObjectType(owner), putstatic.getName(), aloadQ.getVarIndex());
			}

			// Handle T.f = new q()
			if(this.instructionStack.matches("NEW;DUP;ACONST_NULL;INVOKESPECIAL;PUTSTATIC")) {

				regonized = true;
			}
			
			// Handle T.f = new T().method()
			if(this.instructionStack.matches("NEW;DUP;INVOKESPECIAL;INVOKEVIRTUAL;PUTSTATIC")) {
			
				// private static DoubleByte.Decoder ms950 = (DoubleByte.Decoder)new MS950().newDecoder();
				
				regonized = true;
			}
			
			// Handle T.f = T.m(int, int, int)
			if(this.instructionStack.matches("ICONST_0;ICONST_0;ICONST_0;INVOKESTATIC;PUTSTATIC")) {
				
				regonized = true;
			}
			
			// public static final int kPreComputed_CodeBaseRMIChunked = RepositoryId.computeValueTag(true, RepositoryId.kSingleRepTypeInfo, true);
			
			if(!regonized) {
				this.printUnhandledPattern();
				this.incPattern(this.instructionStack.getInstructionTrail());
			}
			this.instructionStack.clear();
		}
	}
	

    /**
     *  Visits an IINC instruction.
     */
	@Override
	public void visitIincInsn(int var, int increment) {
		
		this.printInstruction(Opcodes.IINC);

		// Do not push on the instruction stack as it has no value for this analysis.
	}

	
    /**
     *  Visits a zero operand instruction.
     */
	@Override
	public void visitInsn(int opcode) {

		this.printInstruction(opcode);

		switch(opcode) {
		case Opcodes.ICONST_M1:
		case Opcodes.ICONST_0:
		case Opcodes.ICONST_1:
		case Opcodes.ICONST_2:
		case Opcodes.ICONST_3:
		case Opcodes.ICONST_4:
		case Opcodes.ICONST_5:
		case Opcodes.LCONST_0:
		case Opcodes.LCONST_1:
		case Opcodes.FCONST_0:
		case Opcodes.FCONST_1:
		case Opcodes.FCONST_2:
		case Opcodes.DCONST_0:
		case Opcodes.DCONST_1:
			opcode = Opcodes.ICONST_0;
			break;
		case Opcodes.IALOAD:
		case Opcodes.LALOAD:
		case Opcodes.FALOAD:
		case Opcodes.DALOAD:
		case Opcodes.BALOAD:
		case Opcodes.CALOAD:
		case Opcodes.SALOAD:
			opcode = Opcodes.IALOAD; 
			break;
		case Opcodes.IASTORE:
		case Opcodes.LASTORE:
		case Opcodes.FASTORE:
		case Opcodes.DASTORE:
		case Opcodes.BASTORE:
		case Opcodes.CASTORE:
		case Opcodes.SASTORE:
			opcode = Opcodes.IASTORE;
			break;
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
			opcode = Opcodes.IRETURN;
			break;
		}
		
		
		if(opcode == Opcodes.ATHROW) {
			return;
		}
		
		this.instructionStack.push(opcode);
		
		if(opcode == Opcodes.ARETURN) {
			
			if(this.instructionStack.matches("ALOAD;ARETURN")) {
				
				ByteCodeInstruction areturn = this.instructionStack.pop();
				ByteCodeInstruction aload = this.instructionStack.pop();

				this.escapeStatementVisitor.visitReturn(aload.getVarIndex());
			}
			
			this.instructionStack.clear();
		}
		
		if(opcode == Opcodes.RETURN) {
			
			this.instructionStack.clear();
		}
		
		if(opcode == Opcodes.AASTORE ) {
			
			Boolean regonized = false;
			
			if(!regonized) {
				this.printUnhandledPattern();
			}
			
			this.instructionStack.clear();
		}
	}

	
    /**
     *  Visits a method instruction. A method instruction is an instruction that invokes a method.
     *  
     *  @param opcode INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     *  
     */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

		if(this.verbose) {
			System.out.format("%s %s %s\n", ConvertOpcode.toString(opcode), owner, name);
		}
		
		
		// VIRTUAL method invocation
		if(this.instructionStack.endsWith("INVOKEVIRTUAL")) {
			
			Boolean regonized = false; 
			
			// Handle p.m();
			if(this.instructionStack.matches("ALOAD;INVOKEVIRTUAL")) {
				
				regonized = true;
			}

			// Handle p.m(q);
			if(this.instructionStack.matches("ALOAD;ALOAD;INVOKEVIRTUAL")) {
				
				regonized = true;
			}

			// Handle p.f.m()
			if(this.instructionStack.matches("ALOAD;GETFIELD;INVOKEVIRTUAL")) {
				
				regonized = true;
			}

			// Handle p.m(0)
			if(this.instructionStack.matches("ALOAD;ICONST_0;INVOKEVIRTUAL")) {
				
				regonized = true;
			}
			
			// Handle p.f.m(0, 0)
			if(this.instructionStack.matches("ALOAD;GETFIELD;ICONST_0;ICONST_0;INVOKEVIRTUAL")) {
				
				regonized = true;
			}
			
			if(!regonized) {
				this.printUnhandledPattern();
				this.incPattern(this.instructionStack.getInstructionTrail());
			}

			this.instructionStack.clear();
		}
		
		
		// STATIC method invocation.
		if(this.instructionStack.endsWith("INVOKESTATIC")) {

			Boolean regonized = false; 

			// Handle T.m(p, q, r, s)
			if(this.instructionStack.matches("ALOAD;ALOAD;ALOAD;ALOAD;INVOKESTATIC")) {
				
				regonized = true;
			}
			
			// Handle T.m(p, q, r)
			if(this.instructionStack.matches("ALOAD;ALOAD;ALOAD;INVOKESTATIC")) {
				
				regonized = true;
			}
			
			// Handle T.m(p, q)
			if(this.instructionStack.matches("ALOAD;ALOAD;INVOKESTATIC")) {
				
				regonized = true;
			}
			
			// Handle T.m(q)
			if(this.instructionStack.matches("ALOAD;INVOKESTATIC")) {
				
				regonized = true;
			}
			
			// Handle T.m(ldc)
			if(this.instructionStack.matches("LDC;INVOKESTATIC")) {
				
				regonized = true;
			}
			
			// Handle T.m(q.f)
			if(this.instructionStack.matches("ALOAD;GETFIELD;INVOKESTATIC")) {
				
				
				regonized = true;
			}
			
			// Handle p = T.m(q.f, r)
			if(this.instructionStack.matches("ALOAD;GETFIELD;ALOAD;INVOKESTATIC")) {
				
				regonized = true;
			}
			
			if(!regonized) {
				this.printUnhandledPattern();
				this.incPattern(this.instructionStack.getInstructionTrail());
			}
			this.instructionStack.clear();

		}
		
		if(this.instructionStack.endsWith("INVOKEINTERFACE")) {
			
			Boolean regonized = false; 

			// Handle p.m(r)
			if(this.instructionStack.matches("ALOAD;ALOAD;INVOKEINTERFACE")) {
				
				regonized = true;
			}
			
			if(!regonized) {
				this.printUnhandledPattern();
				this.incPattern(this.instructionStack.getInstructionTrail());
			}
			this.instructionStack.clear();

		}

		this.instructionStack.push(opcode);
	}
	
    /**
     *  Visits an instruction with a single int operand.
     */
	@Override
	public void visitIntInsn(int opcode, int operand) {

		if(this.verbose) {
			System.out.format("%s %s", ConvertOpcode.toString(opcode), operand);
		}
		this.instructionStack.push(opcode);
	}
	


    /**
     *  Visits an invokedynamic instruction.
     */
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {

		if(this.verbose) {
			System.out.format("VisitInvokeDynamicInsn: name: %s, desc: %s\n", name, desc);
		}

		this.instructionStack.push(Opcodes.INVOKEDYNAMIC);
	}

    /**
     *  Visits a jump instruction. A jump instruction is an instruction that may jump to another instruction.
     *  
     *  @param opcode IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL
     *  
     */
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		
		this.printInstruction(opcode);

		this.instructionStack.clear();
	}

    /**
     *  Visits a LDC instruction. Note that new constant types may be added in future versions of the Java Virtual Machine.
     *  To easily detect new constant types, implementations of this method should check for unexpected constant types, like this: 
     */
	@Override
	public void visitLdcInsn(Object cst) {

		this.printInstruction(Opcodes.LDC);

		this.instructionStack.push(Opcodes.LDC);
	}

    /**
     *  Visits a LOOKUPSWITCH instruction
     */
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		
		this.printInstruction(Opcodes.LOOKUPSWITCH);

		this.instructionStack.push(Opcodes.LOOKUPSWITCH);
	}

    /**
     *  Visits a MULTIANEWARRAY instruction.
     */
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		
		this.printInstruction(Opcodes.MULTIANEWARRAY);
	}

    /**
     *  Visits a TABLESWITCH instruction.
     */
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {

		this.printInstruction(Opcodes.TABLESWITCH);

		this.instructionStack.push(Opcodes.TABLESWITCH);
	}

    /**
     *  Visits a type instruction. A type instruction is an instruction that takes the internal name of a class as parameter.
     */
	@Override
	public void visitTypeInsn(int opcode, String type) {

		this.printInstruction(opcode, type);

		if(opcode == Opcodes.CHECKCAST) {
			// Do not push on the instruction stack as it has no value for this analysis.
			return;
		}
		
		this.instructionStack.push(new ByteCodeInstruction(opcode, Type.getObjectType(type)));
	}

	
	private void incPattern(String pattern) {
		if(this.patternCount.containsKey(pattern)) {
			this.patternCount.put(pattern, this.patternCount.get(pattern) + 1);
		} else {
			this.patternCount.put(pattern, 1);
		}
	}
	
	private void printUnhandledPattern() {
		if(this.printPatterns) {
			System.out.format("File:    %s\n", this.jarFile.toString());
			System.out.format("Class:   %s\n", this.currentClass.name());
			System.out.format("https://github.com/frohoff/jdk8u-jdk/blob/master/src/share/classes/%s\n", this.currentClass.name());
			System.out.format("Method:  %s (Line %s)\n", this.currentMethod, this.currentLine);
			System.out.format("Pattern: %s\n", this.instructionStack.getInstructionTrail());
			System.out.println("------------------------------------------------------------------");
		}
	}
	
	private void printInstruction(int opcode) {
		if(this.verbose) {
			System.out.format("%s\n", ConvertOpcode.toString(opcode));
			
			
		}
	}

	private void printInstruction(int opcode, int var) {
		if(this.verbose) {
			System.out.format("%s_%s\n", ConvertOpcode.toString(opcode), var);
		}
	}
	
	private void printInstruction(int opcode, String type) {
		if(this.verbose) {
			System.out.format("%s (%s)\n", ConvertOpcode.toString(opcode), type);
		}
	}

}
