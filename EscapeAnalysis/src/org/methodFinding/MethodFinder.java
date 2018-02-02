package org.methodFinding;

import java.util.ArrayList;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.packagePrivateClasses.JavaClass;
import org.packagePrivateClasses.JavaClassList;
import org.packagePrivateClasses.JavaMethod;

public class MethodFinder extends MethodVisitor {

	JavaMethod _currentMethod;
	JavaClassList _packagePrivateClasses;

	ArrayList<JavaClass> _instantiatedPackagePrivateClasses = new ArrayList<JavaClass>();
	
	public MethodFinder(MethodVisitor mv, JavaMethod currentMethod, JavaClassList packagePrivateClasses) {
		super(Opcodes.ASM6, mv);
		
		_currentMethod = currentMethod;
		_packagePrivateClasses = packagePrivateClasses;
	}
	
	public ArrayList<JavaClass> instantiatedPackagePrivateClasses() {
		return _instantiatedPackagePrivateClasses;
	}
	
    /**
     *  Visits a type instruction. A type instruction is an instruction that takes the internal name of a class as parameter.
     */
	@Override
	public void visitTypeInsn(int opcode, String type) {

		//System.out.format("VisitTypeInsn: ");
		
		switch(opcode) {
			case Opcodes.NEW:
				//System.out.format("new %s()\n", type);
				
				JavaClass javaClass = _packagePrivateClasses.find(type);
				
				if(javaClass != null) {
					javaClass.addMethod(_currentMethod);
				}
				
				break;
			case Opcodes.ANEWARRAY:
				//System.out.format("new %s[]\n", type);
				break;
		}
		
		super.visitTypeInsn(opcode, type);
	}
	
	public void addMethod() {
		
	}
}
