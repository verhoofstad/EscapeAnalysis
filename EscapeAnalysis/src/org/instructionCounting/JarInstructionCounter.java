package org.instructionCounting;

import java.util.HashMap;
import java.util.Map;

import org.asm.JarClass;
import org.asm.JarFileVisitor;

public class JarInstructionCounter extends JarFileVisitor {

	private Map<Integer, Integer> opcodeCount = new HashMap<Integer, Integer>();

	public JarInstructionCounter(Map<Integer, Integer> opcodeCount) {
		
		this.opcodeCount = opcodeCount;
	}
	
	public Map<Integer, Integer> getOpcodeCount() {
		return this.opcodeCount;
	}
	
	public void visitPublicClass(JarClass jarClass)	{
		visitClass(jarClass);
	}
	
	public void visitPackagePrivateClass(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	public void visitPublicEnum(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	public void visitPackagePrivateEnum(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	public void visitPublicInterface(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	public void visitPackagePrivateInterface(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	private void visitClass(JarClass jarClass) {
		
		ClassInstructionCounter instructionCounter = new ClassInstructionCounter(this.opcodeCount);
		
		jarClass.accept(instructionCounter);
		
	}
}
