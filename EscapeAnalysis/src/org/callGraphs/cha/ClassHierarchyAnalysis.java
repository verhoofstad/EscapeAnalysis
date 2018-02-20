package org.callGraphs.cha;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;
import org.callGraphs.CallGraph;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaInterface;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaType;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassHierarchyAnalysis extends JarFileSetVisitor {

	private ClassHierarchy classHierarchy;
	private CallGraph callGraph;
	
	public ClassHierarchyAnalysis(ClassHierarchy classHierarchy) {
		this.classHierarchy = classHierarchy;
		this.callGraph = new CallGraph();
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
		
		JavaClass currentClass = this.classHierarchy.findClass(jarClass.name());
		
		if(currentClass != null) {
			CHAClassVisitor classVisitor = new CHAClassVisitor(currentClass, this.classHierarchy, this.callGraph);
			
			jarClass.accept(classVisitor);
		} else {
			throw new Error();
		}
	}
	
	
	class CHAClassVisitor extends ClassVisitor
	{
		private JavaClass currentClass;
		private ClassHierarchy classHierarchy;
		private CallGraph callGraph;
		
		CHAClassVisitor(JavaClass currentClass, ClassHierarchy classHierarchy, CallGraph callGraph){
			super(Opcodes.ASM6);
			
			this.currentClass = currentClass;
			this.classHierarchy = classHierarchy;
			this.callGraph = callGraph;
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

			JavaMethod currentMethod = this.currentClass.getMethod(name, desc);
			
			return new CHAMethodVisitor(currentMethod, this.classHierarchy, this.callGraph);
		}
	}
	
	class CHAMethodVisitor extends MethodVisitor
	{
		private JavaMethod currentMethod;
		private ClassHierarchy classHierarchy;
		private CallGraph callGraph;
		
	    public CHAMethodVisitor(JavaMethod currentMethod, ClassHierarchy classHierarchy, CallGraph callGraph) {
			super(Opcodes.ASM6);
			
			this.currentMethod = currentMethod;
			this.classHierarchy = classHierarchy;
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
				
				// Invocation of constructors, private methods, super() calls,
				
				if(!itf) {
					JavaClass targetClass = this.classHierarchy.getClass(owner);
					JavaMethod target = targetClass.findMethodUpwards(name, desc);
					
					if(target != null) {
						this.callGraph.addStaticCallSite(this.currentMethod, target);
					} else {
						System.out.format("Cannot find special method on class: %s\n", JavaMethod.toName(owner, name, desc));
					}
				} else {
					
					JavaInterface targetInterface = this.classHierarchy.getInterface(owner);
					JavaMethod target = targetInterface.findNonAbstractMethodUpwards(name, desc);
					
					if(target != null) {
						this.callGraph.addStaticCallSite(this.currentMethod, target);
					} else {
						System.out.format("Cannot find special method on interface: %s\n", JavaMethod.toName(owner, name, desc));
					}
				}
			} else if(opcode == Opcodes.INVOKESTATIC) {
				
				if(!itf) {
					JavaClass targetClass = this.classHierarchy.getClass(owner);
					// Note that the static method can also reside in one of the super classes.
					JavaMethod target = targetClass.getMethodUpwards(name, desc);
					
					this.callGraph.addStaticCallSite(this.currentMethod, target);
				} else {

					JavaInterface targetInterface = this.classHierarchy.getInterface(owner);
					// Note that the static method can also reside in one of the super interfaces.
					JavaMethod target = targetInterface.findNonAbstractMethodUpwards(name, desc);
					
					if(target != null) {
						this.callGraph.addStaticCallSite(this.currentMethod, target);
					} else {
						System.out.format("Cannot find static method on interface: %s\n", JavaMethod.toName(owner, name, desc));
					}
				}
				
			} else if(opcode == Opcodes.INVOKEVIRTUAL) {
				
				if(owner.startsWith("[")) {
					// Ignore array types for now...
					//System.out.format("Call on array type. Owner: %s, name: %s, desc: %s\n", owner, name, desc);
					return;
				}
				
				JavaClass declaredType = this.classHierarchy.getClass(owner);

				List<JavaMethod> virtualTargets = new ArrayList<JavaMethod>();

				JavaMethod virtualTarget1 = declaredType.findMethodUpwards(name, desc);
				
				// If the declared type is an abstract class and the invoked method is defined
				// in an implemented interface, the class may not have a concrete implementation for 
				// that method.
				if(virtualTarget1 != null) {
					virtualTargets.add(virtualTarget1);
				} else {
					if(!declaredType.isAbstract()) {
						printWarning("Cannot find virtual method " + JavaMethod.toName(owner, name, desc));
					}
				}

				for(JavaMethod virtualTarget : declaredType.findMethodsDownwards(name, desc)) {
					virtualTargets.add(virtualTarget);
				}
				
				if(virtualTargets.size() > 0) {
					this.callGraph.addVirtualCallSite(this.currentMethod, virtualTargets);
				} else {
					if(!declaredType.isAbstract()) {
						printWarning("No virtual targets found for " + JavaMethod.toName(owner, name, desc));
					}
				}
				
				if(itf) { throw new Error("Class expected"); }
				
			} else if(opcode == Opcodes.INVOKEINTERFACE) {
				
				JavaInterface declaredType = this.classHierarchy.getInterface(owner);
				
				visitInvokeVirtual(declaredType, name, desc);
				
			} else {
				throw new Error("Invalid opcode.");
			}
			
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}
		
		private void visitInvokeVirtual(JavaType declaredType, String name, String desc) {
			
		}
		
		private void visitInvokeStatic(JavaType declaredType, String name, String desc) {
			
			
			
		}
		
		
		private void printWarning(String warning) {
			System.out.format("Warning: %s\n   in %s\n", warning, this.currentMethod.id());
		}
	}
}