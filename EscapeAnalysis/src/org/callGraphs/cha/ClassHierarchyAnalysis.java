package org.callGraphs.cha;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;
import org.callGraphs.CallGraph;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassHierarchyAnalysis extends JarFileSetVisitor {

	private ClassHierarchy classHierarchy;
	private AppliesToSets appliesToSets;
	private CallGraph callGraph;
	
	public ClassHierarchyAnalysis(ClassHierarchy classHierarchy) {
		this.classHierarchy = classHierarchy;
		this.callGraph = new CallGraph();
		this.appliesToSets = new AppliesToSets(this.classHierarchy);
	}
	
	public CallGraph callGraph() {
		return this.callGraph;
	}
	
	@Override
	public void visitPublicClass(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	@Override
	public void visitPackagePrivateClass(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	@Override
	public void visitPublicEnum(JarClass jarClass) {
		visitClass(jarClass);
	}

	@Override
	public void visitPackagePrivateEnum(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	private void visitClass(JarClass jarClass) {
		
		JavaType currentClass = this.classHierarchy.findClass(jarClass.name());
		
		if(currentClass != null) {
			CHAClassVisitor classVisitor = new CHAClassVisitor(currentClass, this.classHierarchy, this.appliesToSets, this.callGraph);
			
			jarClass.accept(classVisitor);
		} else {
			throw new Error("Could not find class " + jarClass.name());
		}
	}
	
	
	class CHAClassVisitor extends ClassVisitor
	{
		private JavaType currentClass;
		private ClassHierarchy classHierarchy;
		private AppliesToSets appliesToSets;
		private CallGraph callGraph;
		
		CHAClassVisitor(JavaType currentClass, ClassHierarchy classHierarchy, AppliesToSets appliesToSets, CallGraph callGraph){
			super(Opcodes.ASM6);
			
			this.currentClass = currentClass;
			this.classHierarchy = classHierarchy;
			this.appliesToSets = appliesToSets;
			this.callGraph = callGraph;
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

			JavaMethod currentMethod = this.currentClass.getMethod(name, desc);
			
			return new CHAMethodVisitor(currentMethod, this.classHierarchy, this.appliesToSets, this.callGraph);
		}
	}
	
	class CHAMethodVisitor extends MethodVisitor
	{
		private JavaMethod currentMethod;
		private ClassHierarchy classHierarchy;
		private AppliesToSets appliesToSets;
		private CallGraph callGraph;
		
		
	    public CHAMethodVisitor(JavaMethod currentMethod, ClassHierarchy classHierarchy, 
	    		AppliesToSets appliesToSets, CallGraph callGraph) {
			super(Opcodes.ASM6);
			
			this.currentMethod = currentMethod;
			this.classHierarchy = classHierarchy;
			this.appliesToSets = appliesToSets;
			this.callGraph = callGraph;
		}
		
	    /**
	     *  Visits a method instruction. A method instruction is an instruction that invokes a method.
	     *  
	     *  https://stackoverflow.com/questions/24510785/explanation-of-itf-parameter-of-visitmethodinsn-in-asm-5
	     *  
	     */
		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

			if(opcode == Opcodes.INVOKESPECIAL) {
				
				// Invocation of constructors, private methods, super() calls.
				
				JavaType declaredType = this.classHierarchy.findType(owner);
				
				if(declaredType != null) {
					if(name.equals("<init>")) {
						visitInvokeConstructor(declaredType, name, desc);
					} else {
						visitInvokeVirtual(declaredType, name, desc);
					}					
				}
				
			} else if(opcode == Opcodes.INVOKESTATIC) {
				
				JavaType declaredType = this.classHierarchy.findType(owner);
				
				if(declaredType != null) {
					visitInvokeStatic(declaredType, name, desc);
				}
				
			} else if(opcode == Opcodes.INVOKEVIRTUAL) {
				
				if(owner.startsWith("[")) {
					// Ignore array types for now...
					//System.out.format("Call on array type. Owner: %s, name: %s, desc: %s\n", owner, name, desc);
					return;
				}
								
				JavaType declaredType = this.classHierarchy.findType(owner);
				
				if(declaredType != null) {
					visitInvokeVirtual(declaredType, name, desc);
				}
				
			} else if(opcode == Opcodes.INVOKEINTERFACE) {
				
				JavaType declaredType = this.classHierarchy.findType(owner);
				
				if(declaredType != null) {
					visitInvokeVirtual(declaredType, name, desc);
				}
				
			} else {
				throw new Error("Invalid opcode.");
			}
			
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}
		
		private void visitInvokeVirtual(JavaType declaredType, String name, String desc) {
			
			if(declaredType.id().equals("java/lang/invoke/MethodHandle")) {
				return;
			}
			
			// If the declared type is an abstract class and the invoked method is defined
			// in an implemented interface, the class may not have a concrete implementation for 
			// that method.
			JavaMethodSet virtualTargets = appliesToSets.appliesTo(declaredType.coneSet(), name, desc);
			
			this.callGraph.addVirtualCallSite(this.currentMethod, virtualTargets);
		}
		
		private void visitInvokeStatic(JavaType declaredType, String name, String desc) {
			
			// Note that the static method can also reside in one of the super classes.
			JavaMethod staticTarget = declaredType.findStaticMethod(name, desc);
			
			this.callGraph.addStaticCallSite(this.currentMethod, staticTarget);
		}
		
		
		private void visitInvokeConstructor(JavaType declaredType, String name, String desc) {
			
			JavaMethod constructor = declaredType.getMethod(name, desc);
			
			this.callGraph.addStaticCallSite(this.currentMethod, constructor);
		}
	}
}