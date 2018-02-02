package org.asm;

import org.asm.jvm.AccessFlags;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

class JarClassVisitor extends ClassVisitor
{
	private JarFileVisitor _visitor;
	private ClassReader _reader;
	
	public JarClassVisitor(JarFileVisitor visitor, ClassReader reader) {
		super(Opcodes.ASM6);
		
		_visitor = visitor;
		_reader = reader;
	}

	public void visit(int version, int access, String name,	String signature, String superName, String[] interfaces) {
		
		//System.out.format("Method: name: %s, signature %s, superName: %s\n", name, signature, superName);
		AccessFlags accessFlags = new AccessFlags(access);

		if(accessFlags.isEnum()) {
			
			if(accessFlags.isPublic()) {
				_visitor.visitPublicEnum(new JarClass(name, superName, interfaces, access, _reader));
			} else {
				_visitor.visitPackagePrivateEnum(new JarClass(name, superName, interfaces, access, _reader));
			}
		}
		
		if(accessFlags.isInterface()) {
			
			if(accessFlags.isPublic()) {
				_visitor.visitPublicInterface(new JarClass(name, superName, interfaces, access, _reader));
			} else {
				_visitor.visitPackagePrivateInterface(new JarClass(name, superName, interfaces, access, _reader));
			}
		}

		if(!accessFlags.isEnum() && !accessFlags.isInterface()) {
			
			if(accessFlags.isPublic()) {
				_visitor.visitPublicClass(new JarClass(name, superName, interfaces, access, _reader));
			} else {
				_visitor.visitPackagePrivateClass(new JarClass(name, superName, interfaces, access, _reader));
			}
		}
	}
}