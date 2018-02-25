package org.counting;

import org.asm.jvm.AccessFlags;
import org.dataSets.LibraryResult;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodCounter extends ClassVisitor {
	
	private LibraryResult result;
	
	public MethodCounter(LibraryResult result) {
		super(Opcodes.ASM6);
		
		this.result = result;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		AccessFlags accessFlags = new AccessFlags(access);
		
		this.result.all_methodCount++;
		
		if(accessFlags.isPublic()) {
			this.result.all_publicMethods++;
		} else if(accessFlags.isProtected()) {
			this.result.all_protectedMethods++;
		} else if(accessFlags.isPackagePrivate()) {
			this.result.all_packagePrivateMethods++;
		} else if(accessFlags.isPrivate()) {
			this.result.all_privateMethods++;
		} else {
			throw new Error();
		}
		return null;
	}
}
